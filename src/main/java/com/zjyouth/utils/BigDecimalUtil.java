package com.zjyouth.utils;

import java.math.BigDecimal;

/**
 * Created by geely
 *
 *      BigDecimal的字符串构造方法可可以不丢失精度
 *      数据库是double 转字符串好麻烦  对这个工具类进行封装
 *      强调一下  商业计算一定要使用BigDecimal
 */
public class BigDecimalUtil {

    private BigDecimalUtil(){//不能被外部实例化

    }


    /**
     *    不会丢失精度的加法
     * @param v1
     * @param v2
     * @return
     */
    public static BigDecimal add(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2);//加
    }

    public static BigDecimal sub(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2);//减
    }

    public static BigDecimal mul(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2);//乘法
    }

    public static BigDecimal div(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2,2,BigDecimal.ROUND_HALF_UP);//四舍五入,保留2位小数

        //除不尽的情况
    }





}
