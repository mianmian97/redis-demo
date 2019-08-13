package com.mianmian.redisDemo.controller;

import com.mianmian.redisDemo.domain.Resp;
import com.mianmian.redisDemo.dto.ArticleDTO;
import com.mianmian.redisDemo.enums.SortTypeEnum;
import com.mianmian.redisDemo.util.LocalDateTimeUtil;
import com.mianmian.redisDemo.util.RedisUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author : zhangyi
 * @Date : 2019-08-11 16:38
 */

@Api(value = "VoteController ")
@RestController
@RequestMapping("/vote")
public class VoteController {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    StringRedisTemplate redisTemplate;

    private final static String ARTICLE_PR = "article:";

    private final static String VOTE_PR = "voted:";

    private final static String USER_PR = "user:";

    private final static String TIME_SORT_PR = "time:";

    private final static String SCORE_SORT_PR = "score:";

    //文章投票截止时间（秒）
    private final static Integer ONE_WEEK_IN_SECOND = 7 * 24 * 60 * 60 * 1000;

    //评为优秀的文章需要的票数
    private final static Integer SUPPORT_VOTE_COUNT = 200;

    //每获得一票增加的分数
    private final static Integer VOTE_SCTRE = (24 * 60 * 60 * 1000) / SUPPORT_VOTE_COUNT;

    /**
     * 投票接口
     *
     * @param articleId 文章id
     * @param userId    作者id
     * @return resp
     */
    @PostMapping("/")
    public Resp vote(@RequestParam Long articleId,
                     @RequestParam("userId") Long userId) {

        Long sendTime = Long.valueOf(redisUtil.hget(ARTICLE_PR + articleId, "sendTime"));

        if (sendTime < LocalDateTimeUtil.getTimestampOfDateTime(LocalDateTime.now()) - ONE_WEEK_IN_SECOND) {
            return Resp.paramErr("投票已过期！");
        }

        if (redisTemplate.opsForSet().add(VOTE_PR + articleId, USER_PR + userId) == 0) {
            return Resp.paramErr("该用户已投过票！");
        }

        //文章获得投票增加分值
        redisTemplate.opsForZSet().incrementScore(SCORE_SORT_PR, ARTICLE_PR + articleId, VOTE_SCTRE);
        //增加票数
        redisTemplate.opsForHash().increment(ARTICLE_PR + articleId, "votes", 1);

        return Resp.ok();
    }

    @GetMapping("/article/list")
    public Resp getArticleList(@RequestParam SortTypeEnum sortTypeEnum) {

        Set articleIdSet = redisTemplate.opsForZSet().reverseRange(sortTypeEnum.getValue(), 0, -1);
        List<String> articleIds = (List<String>) articleIdSet.stream().map(set -> {
            return String.valueOf(set).split(":")[1];
        }).collect(Collectors.toList());
        List<ArticleDTO> articleDTOS = new ArrayList<>();

        articleIds.forEach(id -> {
            String key = ARTICLE_PR + id;

            ArticleDTO articleDTO = ArticleDTO.builder()
                    .articleId(Long.valueOf(id))
                    .posterId(Long.valueOf(redisUtil.hget(key, "posterId")))
                    .sendTime(Long.valueOf(redisUtil.hget(key, "sendTime")))
                    .title(redisUtil.hget(key, "title"))
                    .votes(Long.valueOf(redisUtil.hget(key, "votes")))
                    .build();
            articleDTOS.add(articleDTO);
        });
        return Resp.ok(articleDTOS);
    }

    /**
     * 新增文章接口
     *
     * @param articleDTO 文章dto
     * @return resp
     */
    @PostMapping("/create/article")
    public Resp createArticle(@RequestBody ArticleDTO articleDTO) {

        if (Objects.nonNull(articleDTO)) {

            String articleId = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));

            //事务控制
            SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
                @Override
                public <K, V> Object execute(RedisOperations<K, V> redisOperations) throws DataAccessException {
                    String key = ARTICLE_PR + articleId;
                    redisOperations.multi();

                    redisOperations.opsForHash().put((K) key, "title", articleDTO.getTitle());
                    redisOperations.opsForHash().put((K) key, "sendTime", String.valueOf(LocalDateTimeUtil.getTimestampOfDateTime(LocalDateTime.now())));
                    redisOperations.opsForHash().put((K) key, "posterId", String.valueOf(articleDTO.getPosterId()));
                    redisOperations.opsForHash().put((K) key, "votes", "1");

                    //将文章作者添加至已投票用户集合中
                    redisTemplate.opsForSet().add(VOTE_PR + articleId, USER_PR + articleDTO.getPosterId());
                    //已投票用户记录设置过期时间
                    redisTemplate.expire(VOTE_PR + articleId, ONE_WEEK_IN_SECOND, TimeUnit.MILLISECONDS);

                    //将文章添加到根据时间排序和分数排序的两个zSet中
                    redisTemplate.opsForZSet().add(TIME_SORT_PR, key, LocalDateTimeUtil.getTimestampOfDateTime(LocalDateTime.now()));
                    redisTemplate.opsForZSet().add(SCORE_SORT_PR, key, LocalDateTimeUtil.getTimestampOfDateTime(LocalDateTime.now()) + VOTE_SCTRE);

                    return redisOperations.exec();
                }
            };

            redisTemplate.execute(sessionCallback);

            return Resp.ok(articleId);
        }
        return Resp.ok();
    }


}
