package com.mmall.test;

import org.junit.Test;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2018/6/17.
 */
public class BigDecimalTest {


    /**
     *    购物车丢失精度的问题    很可能导致 下不了订单   或者对账有问题
     *
     *    0.060000000000000005   0.06
          0.5800000000000001     0.58
          401.49999999999994     401.5
          1.2329999999999999     1.233
     */
    @Test
    public void test(){
        System.out.println(0.05+0.01);
        System.out.println(1.0-0.42);
        System.out.println(4.015*100);
        System.out.println(123.3/100);
    }

    /**
     *
     */
    @Test
    public void test2(){
        BigDecimal b1 = new BigDecimal(0.05);
        BigDecimal b2 = new BigDecimal(0.01);
        System.out.println(b1.add(b2));//0.06000000000000000298372437868010820238851010799407958984375

    }


    /**
     *v       一定要记住要使用的是  字符串构造器  不然是没有效果的
     */
    @Test
    public void test3(){
        BigDecimal b1 = new BigDecimal("0.05");
        BigDecimal b2 = new BigDecimal("0.01");
        System.out.println(b1.add(b2));//0.06

    }

}