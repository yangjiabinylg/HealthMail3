package com.zjyouth.common;

/**
 * Created by Administrator on 2018/6/13.
 *
 *     响应编码的枚举类
 */
public enum ResponseCode {
    //      code , 描述
    SUCCESS(0,"SUCCESS"),//成功           code   ,描述
    ERROR(1,"ERROR"),//出错   服务器出错??
    NEED_LOGIN(10,"NEED_LOGIN"),//需要登录
    ILLEGAL_ARGUMENT(2,"ILLEGAL_ARGUMENT");//非法参数


    private final int code;
    private final String desc;

    ResponseCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
