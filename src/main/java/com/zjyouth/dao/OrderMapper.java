package com.zjyouth.dao;

import com.zjyouth.pojo.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);




    //orderNo 和 userId查询一下订单有没有  多个参数要写Param注解
    Order selectByUserIdAndOrderNo(@Param("userId") Integer userId, @Param("orderNo") Long orderNo);

    //按照订单号查找订单  不是主键自增字段
    Order selectByOrderNo(Long orderNo);

    //查找该用户的所有订单要分页
    List<Order> selectByUserId(Integer userId);

    //查找该用户的所有订单要分页
    List<Order> selectByUserIdNeedPay(Integer userId);



    //查找所有订单要分页   管理员使用的接口
    List<Order> selectAllOrder();



    //    <!--  统计订单数量  -->
    int getOrderCount();


    //查找该用户的所有订单要分页
    List<Order> selectManageOrderByUserId(Integer userId);


}