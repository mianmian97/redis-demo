package com.mianmian.redisDemo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author : zhangyi
 * @Date : 2019-08-11 20:06
 */

@Getter
@AllArgsConstructor
public enum SortTypeEnum {

    SCORE("score:"),
    TIME("time:");

    private String value;

    public String getValue() {
        return this.value;
    }
}
