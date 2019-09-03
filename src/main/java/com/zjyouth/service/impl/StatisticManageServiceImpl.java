package com.zjyouth.service.impl;

import com.google.common.collect.Interner;
import com.google.common.collect.Maps;
import com.zjyouth.common.ServerResponse;
import com.zjyouth.dao.OrderMapper;
import com.zjyouth.dao.ProductMapper;
import com.zjyouth.dao.UserMapper;
import com.zjyouth.pojo.Shipping;
import com.zjyouth.service.IStatisticManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/8/7.
 */

//@Service("iShippingService")
//public class ShippingServiceImpl implements IShippingService {
@Service("iStatisticManageService")
public class StatisticManageServiceImpl implements IStatisticManageService {

    @Autowired
    private UserMapper userMapper;//这里报错没事的 我们使用mybatis插件的注解

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ProductMapper productMapper;




    //查询单个 收货地址的详情 查询要判断越权的问题  收货地址对越权问题表现的比较的突出
    public ServerResponse<Map> getBaseCount(){
//  public ServerResponse pay(Long orderNo,Integer userId,String path){

        Integer userCount =  userMapper.getUserCount();
        Integer orderCount =  orderMapper.getOrderCount();
        Integer productCount =  productMapper.getProductCount();
        if(userCount == null ){
            return  ServerResponse.createByErrorMessage("无法查询到用户数量");
        }
        if(orderCount == null ){
            return  ServerResponse.createByErrorMessage("无法查询到订单数量");
        }
        if(productCount == null ){
            return  ServerResponse.createByErrorMessage("无法查询到产品数量");
        }
        Map<String ,Integer> resultMap = Maps.newHashMap();
        //  Map map = new HashMap();
        resultMap.put("userCount",userCount);
        resultMap.put("orderCount",orderCount);
        resultMap.put("productCount",productCount);

        return ServerResponse.createBySuccess("查询基础数据成功",resultMap);

    }





}