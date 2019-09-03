package com.zjyouth.common;

/**
 * Created by Administrator on 2018/8/8.
 */
public enum LogisticsCompanyEnum {
    SF("顺丰", 1, "shunfeng"),
    ZT("中通", 2, "zhongtong"),
    ST("申通", 3, "shentong"),
    YT("圆通", 4, "yuantong"),
    HT("汇通", 5, "huitongkuaidi"),
    YD("韵达", 6, "yunda"),
    YZ("邮政包裹/平邮", 7, "youzhengguonei"),
    EMS("EMS", 8, "ems"),
    TT("天天", 9, "tiantian"),
    DB("德邦", 10, "debangwuliu");

    private final String name;//顺丰
    private final int value;//1
    private final String type;//shunfeng

    LogisticsCompanyEnum(String name, int value, String type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public String getType() {
        return type;
    }


    public static LogisticsCompanyEnum valueOf(int value ){
        for(LogisticsCompanyEnum paymentTypeEnum : values()){
            if(paymentTypeEnum.getValue() == value){
                return paymentTypeEnum;
            }
        }
        throw new RuntimeException("没有找到对应的枚举类");
    }

    public static LogisticsCompanyEnum typeOf(String type ){
        for(LogisticsCompanyEnum paymentTypeEnum : values()){
            if(paymentTypeEnum.getType() .equals(type) ){
                return paymentTypeEnum;
            }
        }
        throw new RuntimeException("没有找到对应的枚举类");
    }



}
