package com.zjyouth.dao;

import com.zjyouth.common.ServerResponse;
import com.zjyouth.pojo.Shipping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    //为了防止横向越权  我们要使用  userId  和   shippingId  两个组合进行删除收货地址的操作
    int deleteByShipppingIdUserId(@Param("userId") Integer userId,@Param("shippingId") Integer shippingId);


    //和删除一样  更新也是有越权的问题的
    //shipping.setUserId(userId);//这里必须要从新赋值  使用从session中取出来的userid
    //不能使用shipping自己传递过来的  他可能是乱传递的（有可能是空）  也是会将别人的地址更新掉的
    int updateByShipping(Shipping shipping);


    //查询单个 收货地址的详情 查询要判断越权的问题  收货地址对越权问题表现的比较的突出
    Shipping selectByShippingIdUserId(@Param("userId") Integer userId,@Param("shippingId") Integer shippingId);


    //分页的list
    List<Shipping> selectByUserId(@Param("userId") Integer userId);

}