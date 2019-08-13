package com.mianmian.redisDemo.domain;

/**
 * @Author : zhangyi
 * @Date : 2019-08-11 17:15
 */

import com.mianmian.redisDemo.enums.ErrorCodeEnum;
import lombok.Data;

/**
 * 统一返回封装类
 *
 * @param <T>
 */
@Data
public class Resp<T> {
    private String code;
    private String msg;
    private T data;

    public Resp() {

    }

    public Resp(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Resp(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Resp(ErrorCodeEnum codeEnum) {
        this.code = codeEnum.getCode();
        this.msg = codeEnum.getMsg();
    }

    public Resp(ErrorCodeEnum codeEnum, String msg) {
        this.code = codeEnum.getCode();
        this.msg = msg;
    }

    public Resp(ErrorCodeEnum codeEnum, T data) {
        this.code = codeEnum.getCode();
        this.msg = codeEnum.getMsg();
        this.data = data;
    }

    public static Resp ok() {
        return new Resp(ErrorCodeEnum.OK);
    }

    public static Resp ok(ErrorCodeEnum codeEnum) {
        return new Resp(codeEnum);
    }

    public static <T> Resp<T> ok(T data) {
        return new Resp<>(ErrorCodeEnum.OK, data);
    }

    public static <T> Resp<T> ok(ErrorCodeEnum codeEnum, T data) {
        return new Resp<>(codeEnum, data);
    }

    public static Resp fail() {
        return new Resp(ErrorCodeEnum.FAIL);
    }

    public static Resp okOrFail(boolean flag) {
        return flag ? ok() : fail();
    }

    public static <T> Resp<T> okOrFail(boolean flag, T data) {
        return flag ? ok(data) : fail();
    }


    public static Resp paramErr(String msg) {
        return new Resp(ErrorCodeEnum.BIZ_PARAM_ERR, msg);
    }

    public static Resp serviceErr(String msg) {
        return new Resp(ErrorCodeEnum.BIZ_FAIL, msg);
    }

    public static Resp fail(ErrorCodeEnum err) {
        return new Resp(err);
    }
}

