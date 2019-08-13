package com.mianmian.redisDemo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author : zhangyi
 * @Date : 2019-08-11 17:16
 */
@Getter
@AllArgsConstructor
public enum ErrorCodeEnum {

    OK("OK", ""),
    FAIL("FAIL", "操作失败"),
    BIZ_PARAM_ERR("BIZ_PARAM_ERR", "请求参数错误"),
    BIZ_FAIL("BIZ_FAIL", "业务处理失败");

    private String code;
    private String msg;

    public String getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

}

