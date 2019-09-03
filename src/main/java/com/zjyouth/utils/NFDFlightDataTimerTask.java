//package com.zjyouth.utils;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.TimerTask;
///**
// * Created by Administrator on 2018/8/23.
// */
///**
// * 在 TimerManager 这个类里面，大家一定要注意 时间点的问题。如果你设定在凌晨2点执行任务。但你是在2点以后
// *发布的程序或是重启过服务，那这样的情况下，任务会立即执行，而不是等到第二天的凌晨2点执行。为了，避免这种情况
// *发生，只能判断一下，如果发布或重启服务的时间晚于定时执行任务的时间，就在此基础上加一天。
// * @author wls
// *
// */
//public class NFDFlightDataTimerTask extends TimerTask {
//    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    @Override
//    public void run() {
//        try {
//            //在这里写你要执行的内容
//            System.out.println("yjb定时执行任务  执行当前时间"+formatter.format(Calendar.getInstance().getTime()));
//            // todo  24h间隔定时 遍历查询快递信息
//
//            // todo  取消订单 交易关闭  closetime  endtime  好了
//
//            // todo  发货30天交易关闭   endtime
//
//            // todo  发货30天交易关闭   closetime
//
//
//
//        } catch (Exception e) {
//            System.out.println("-------------解析信息发生异常--------------");
//        }
//    }
//
//}