package com.mianmian.redisDemo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 文章dto
 *
 * @Author : zhangyi
 * @Date : 2019-08-11 17:20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDTO {

    private Long articleId;

    private String title;

    private Long sendTime;

    private Long posterId;

    private Long votes;
}
