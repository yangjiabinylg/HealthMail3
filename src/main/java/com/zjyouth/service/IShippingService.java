package com.zjyouth.service;

import com.github.pagehelper.PageInfo;
import com.zjyouth.common.ServerResponse;
import com.zjyouth.pojo.Shipping;
import com.zjyouth.pojo.User;

/**
 * Created by Administrator on 2018/6/18.
 */
public interface IShippingService {

    ServerResponse add(Integer userId, Shipping shipping);

    ServerResponse addApp(String username, Shipping shipping);

    ///int resultCount = shippingMapper.deleteByPrimaryKey(shippingId);
    //这样就ok了  但是有横向越权的问题   比如你登录之后 传入别人的id 也一样删除
    //我们这里是没有限制的   所以这个dao就不可以使用
    //自己写一个吧
    //为了防止横向越权  我们要使用  userId  和   shippingId  两个组合进行删除收货地址的操作
    ServerResponse<String> del(Integer userId,Integer shippingId);

    ServerResponse<String> delApp(String username,Integer shippingId);

    ServerResponse  update(Integer userId,Shipping shipping);

    ServerResponse  updateApp(String username,Shipping shipping);

    //查询单个 收货地址的详情 查询要判断越权的问题  收货地址对越权问题表现的比较的突出
    ServerResponse<Shipping>  select(Integer userId,Integer shippingId);


    //最后一个是分页的list的接口
    ServerResponse<PageInfo>  list(Integer userId, int pageNum, int pageSize);

    public User getUser(String username);
    ServerResponse<PageInfo>  listApp(User user, int pageNum, int pageSize);
}