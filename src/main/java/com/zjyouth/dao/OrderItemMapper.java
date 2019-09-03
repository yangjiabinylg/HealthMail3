package com.zjyouth.dao;

import com.zjyouth.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);

    //根据用户id  查询出所有的   订单详情的列表
    List<OrderItem> getByOrderNoUserId(@Param("orderNo") Long orderNo,@Param("userId") Integer userId);

    //管理员  查询出所有的   订单详情的列表
    List<OrderItem> getByOrderNo(@Param("orderNo") Long orderNo);



    //mybatis 批量插入订单item   一个订单有多个订单item
    void batchInsert(@Param("orderItemList") List<OrderItem> orderItemList);

}