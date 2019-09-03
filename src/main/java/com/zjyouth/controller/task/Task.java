package com.zjyouth.controller.task;

/**
 * Created by Administrator on 2018/8/23.
 */

import com.zjyouth.common.Const;
import com.zjyouth.pojo.Order;
import com.zjyouth.service.IOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class Task {

    @Autowired
    private IOrderService iOrderService;

    private static final Logger logger = LoggerFactory.getLogger(Task.class);

    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    //"0 0-5 14 * * ?" 在每天下午2点到下午2:05期间的每1分钟触发
    //@Scheduled(cron = "0 0 2 * * ?")　　//每天凌晨两点执行
    //@Scheduled(cron = "0 18-48 16 * * ?")
//    @Scheduled(cron = "0 0 23 * * ?")
//    void doSomethingWith(){
////        //logger.info("定时任务开始......");
////        System.out.println("定时任务开始......");
////        long begin = System.currentTimeMillis();
////
////        List<Order> orderListTask = iOrderService.getOrderListTask();
////        //执行数据库操作了哦...
////         // todo  24h间隔定时 遍历查询快递信息
////        for(int i = 0 ; i <orderListTask.size();i++){
////            if(orderListTask.get(i).getLogisticsId() != null){
////                iOrderService.updateLogisticData(orderListTask.get(i));
////                System.out.println("24h间隔定时 遍历查询快递信息"+i+" "+orderListTask.get(i));
////            }
////        }
////         // todo  取消订单 交易关闭  closetime  endtime  好了
////
////
////
////
////         // todo  发货30天交易结束   endtime
////        long nd = 1000 * 24 * 60 * 60;
////        for(int i = 0 ; i <orderListTask.size();i++){
////            if(orderListTask.get(i).getSendTime() != null){
////                // 获得两个时间的毫秒时间差异
////                long diff =new Date().getTime() - orderListTask.get(i).getSendTime().getTime()   ;
////                // 计算差多少天
////                long day = diff / nd;
////                if(day >= 1 ){
////                    orderListTask.get(i).setEndTime(new Date());
////                    orderListTask.get(i).setStatus(Const.OrderStatusEnum.ORDER_SUCCESS.getCode());
////                    System.out.println("发货30天交易结束  交易成功   endtime"+i+" "+ orderListTask.get(i));
////                }
////                iOrderService.updateOrderTask(orderListTask.get(i));
////            }
////
////        }
////
////        long end = System.currentTimeMillis();
////        //logger.info("定时任务结束，共耗时：[" + (end-begin) / 1000 + "]秒");
////        System.out.println(System.currentTimeMillis()+"定时任务结束，共耗时：[" + (end-begin) / 1000 + "]秒");
////        System.out.println("qqq定时执行任务  执行当前时间"+formatter.format(Calendar.getInstance().getTime()));
//    }





    //如果需要以固定速率执行，只要将注解中指定的属性名称改成fixedRate即可，
    // 以下方法将以一个固定速率5s来调用一次执行，这个周期是以上一个任务开始时间为基准
    // ，从上一任务开始执行后5s再次调用：
    //@Scheduled(fixedRate = 1000*60 )
    @Scheduled(fixedRate = 1000*60*60*24 )
    //@Scheduled(fixedRate = 1000 )
    public void doSomething() {
        // something that should execute periodically
        // todo  2h未支付  关闭订单   closetime
        //System.out.println("每分钟执行一次定时执行任务  执行当前时间"+formatter.format(Calendar.getInstance().getTime()));


        //logger.info("定时任务开始......");
        System.out.println("定时任务开始......");
        logger.info("定时任务开始......");
        long begin = System.currentTimeMillis();





        List<Order> orderListTask = iOrderService.getOrderListTask();
        // 获得两个时间的毫秒时间差异
        //long diff =new Date().getTime() - orderListTask.get(i).getSendTime().getTime()   ;
        //long nd = 1000 * 24 * 60 * 60;


        //执行数据库操作了哦...
        // todo  5h间隔定时 遍历查询快递信息
        for(int i = 0 ; i <orderListTask.size();i++){
            // 获得两个时间的毫秒时间差异
            long diff =new Date().getTime() - orderListTask.get(i).getUpdateTime().getTime() ;
            if(diff >= 1000 * 60 * 60 * 10  ){// todo  还是有问题
                if(orderListTask.get(i).getLogisticsId() != null){
                    orderListTask.get(i).setUpdateTime(new Date());
                    iOrderService.updateLogisticData(orderListTask.get(i));
                    System.out.println("5h间隔定时 遍历查询快递信息"+i+" "+orderListTask.get(i));
                    logger.info("5h间隔定时 遍历查询快递信息"+i+" "+orderListTask.get(i));
                }
            }
        }

        // todo  发货30天交易结束   endtime
        long nd = 1000 * 24 * 60 * 60 * 30;
        for(int i = 0 ; i <orderListTask.size();i++){
            if(orderListTask.get(i).getSendTime() != null){//已经发货了
                // 获得两个时间的毫秒时间差异
                long diff =new Date().getTime() - orderListTask.get(i).getSendTime().getTime()   ;
                // 计算差多少天
                long day = diff / nd;
                //if(day >= 30 ){
                if(day >= 1 ){
                    orderListTask.get(i).setEndTime(new Date());
                    orderListTask.get(i).setStatus(Const.OrderStatusEnum.ORDER_SUCCESS.getCode());
                    System.out.println("发货30天交易结束  交易成功   endtime"+i+" "+ orderListTask.get(i));
                    logger.info("发货30天交易结束  交易成功   endtime"+i+" "+ orderListTask.get(i));
                }
                iOrderService.updateOrderTask(orderListTask.get(i));
            }

        }




        // todo  2h为支付订单 订单关闭 交易关闭
        for(int i = 0 ; i <orderListTask.size();i++){
            if(orderListTask.get(i).getStatus() == 10){//下了订单但是没有支付
                // 获得两个时间的毫秒时间差异
                long diff =new Date().getTime() - orderListTask.get(i).getCreateTime().getTime()   ;
                //
                //if(diff >= 1000*60*60*2 ){
                if(diff >= 1000*60*60*24 && orderListTask.get(i).getCloseTime() == null ){  //超过2h
                    orderListTask.get(i).setEndTime(new Date());
                    orderListTask.get(i).setCloseTime(new Date());
                    orderListTask.get(i).setStatus(Const.OrderStatusEnum.ORDER_CLOSE.getCode());
                    System.out.println("订单超过2h未支付 订单关闭   endtime"+i+" "+ orderListTask.get(i));
                    logger.info("订单超过2h未支付 订单关闭   endtime"+i+" "+ orderListTask.get(i));
                }
                iOrderService.updateOrderTask(orderListTask.get(i));
            }

        }







        long end = System.currentTimeMillis();
        //logger.info("定时任务结束，共耗时：[" + (end-begin) / 1000 + "]秒");
        System.out.println(System.currentTimeMillis()+"定时任务结束，共耗时：[" + (end-begin) / 1000 + "]秒");
        logger.info((System.currentTimeMillis()+"定时任务结束，共耗时：[" + (end-begin) / 1000 + "]秒"));
    }


}