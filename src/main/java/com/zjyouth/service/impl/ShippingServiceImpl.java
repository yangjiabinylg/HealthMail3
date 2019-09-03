package com.zjyouth.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.zjyouth.common.ServerResponse;
import com.zjyouth.dao.ShippingMapper;
import com.zjyouth.dao.UserMapper;
import com.zjyouth.pojo.Shipping;
import com.zjyouth.pojo.User;
import com.zjyouth.service.IShippingService;
import com.zjyouth.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/6/18.
 */
@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    @Autowired
    private UserMapper userMapper;//这里报错没事的 我们使用mybatis插件的注解




    public ServerResponse add(Integer userId,Shipping shipping){
        //我们没有要求前端传 用户的id  我们自己设置一下就好了
        shipping.setUserId(userId);
        int rowCount = shippingMapper.insert(shipping);

        //我们和前端预定好 在插入数据以后立即拿到  刚刚自动生成的主键
        //默认的sql 返回的是影响的数据的行数   需要修改一下
        //useGeneratedKeys="true" keyProperty="id"   加上这个在sql配置中
        //这样shipping 里面就会有自增主键的id了
        if(rowCount > 0 ){
            Map result = Maps.newHashMap();//这里没有必要创建一个对象 放数据  直接使用map返回
            result.put("shippingId",shipping.getId());
            return ServerResponse.createBySuccess("地址新建成功",result);
        }
        return ServerResponse.createByErrorMessage("新建地址失败");
    }


    public  ServerResponse addApp(String username, Shipping shipping){
        User user = userMapper.selectByUsername(username);
        if(user == null){
            return ServerResponse.createByErrorMessage("没有该用户");
        }
        //我们没有要求前端传 用户的id  我们自己设置一下就好了
        shipping.setUserId(user.getId());
        int rowCount = shippingMapper.insert(shipping);

        //我们和前端预定好 在插入数据以后立即拿到  刚刚自动生成的主键
        //默认的sql 返回的是影响的数据的行数   需要修改一下
        //useGeneratedKeys="true" keyProperty="id"   加上这个在sql配置中
        //这样shipping 里面就会有自增主键的id了
        if(rowCount > 0 ){
            Map result = Maps.newHashMap();//这里没有必要创建一个对象 放数据  直接使用map返回
            result.put("shippingId",shipping.getId());
            return ServerResponse.createBySuccess("地址新建成功",result);
        }
        return ServerResponse.createByErrorMessage("新建地址失败");
    }


    ///int resultCount = shippingMapper.deleteByPrimaryKey(shippingId);
    //这样就ok了  但是有横向越权的问题   比如你登录之后 传入别人的id 也一样删除
    //我们这里是没有限制的   所以这个dao就不可以使用
    //自己写一个吧
    //为了防止横向越权  我们要使用  userId  和   shippingId  两个组合进行删除收货地址的操作
    public ServerResponse<String> del(Integer userId,Integer shippingId){
        //int resultCount = shippingMapper.deleteByPrimaryKey(shippingId);
        //这样就ok了  但是有横向越权的问题   比如你登录之后 传入别人的id 也一样删除
        //我们这里是没有限制的   所以这个dao就不可以使用
        //自己写一个吧

        //为了防止横向越权  我们要使用  userId  和   shippingId  两个组合进行删除收货地址的操作
        int resultCount = shippingMapper.deleteByShipppingIdUserId(userId,shippingId);
        if(resultCount > 0){
            return  ServerResponse.createBySuccess("删除地址成功");
        }
        return ServerResponse.createByErrorMessage("删除地址失败");
    }



    public ServerResponse<String> delApp(String username,Integer shippingId){

        //int resultCount = shippingMapper.deleteByPrimaryKey(shippingId);
        //这样就ok了  但是有横向越权的问题   比如你登录之后 传入别人的id 也一样删除
        //我们这里是没有限制的   所以这个dao就不可以使用
        //自己写一个吧

        User user = userMapper.selectByUsername(username);
        //为了防止横向越权  我们要使用  userId  和   shippingId  两个组合进行删除收货地址的操作
        int resultCount = shippingMapper.deleteByShipppingIdUserId(user.getId(),shippingId);
        if(resultCount > 0){
            return  ServerResponse.createBySuccess("删除地址成功");
        }
        return ServerResponse.createByErrorMessage("删除地址失败");


    }


    //和删除一样  更新也是有越权的问题的
    public ServerResponse  update(Integer userId,Shipping shipping){
        shipping.setUserId(userId);//这里必须要从新赋值  使用从session中取出来的userid
        //不能使用shipping自己传递过来的  他可能是乱传递的（有可能是空）  也是会将别人的地址更新掉的
        int resultCount = shippingMapper.updateByShipping(shipping);
        if(resultCount > 0){
            return  ServerResponse.createBySuccess("更新地址成功");
        }
        return ServerResponse.createByErrorMessage("更新地址失败");
    }


    public ServerResponse  updateApp(String username,Shipping shipping){
        User user = userMapper.selectByUsername(username);
        shipping.setUserId(user.getId());//这里必须要从新赋值  使用从session中取出来的userid
        //不能使用shipping自己传递过来的  他可能是乱传递的（有可能是空）  也是会将别人的地址更新掉的
        int resultCount = shippingMapper.updateByShipping(shipping);
        if(resultCount > 0){
            return  ServerResponse.createBySuccess("更新地址成功");
        }
        return ServerResponse.createByErrorMessage("更新地址失败");
    }



    //查询单个 收货地址的详情 查询要判断越权的问题  收货地址对越权问题表现的比较的突出
    public ServerResponse<Shipping>  select(Integer userId,Integer shippingId){
        //为了防止横向越权  我们要使用  userId  和   shippingId  两个组合进行删除收货地址的操作
        Shipping shipping = shippingMapper.selectByShippingIdUserId(userId,shippingId);
        if(shipping == null){
            return  ServerResponse.createByErrorMessage("无法查询到该地址");
        }
        return ServerResponse.createBySuccess("查询单个地址成功",shipping);
    }


    //最后一个是分页的list的接口
    public ServerResponse<PageInfo>  list(Integer userId, int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList = shippingMapper.selectByUserId(userId);
        PageInfo pageInfo = new PageInfo(shippingList);//如果是二次封装list在进行设置
        //pageInfo.setList(list);
        return ServerResponse.createBySuccess(pageInfo);
    }

    //最后一个是分页的list的接口
    public ServerResponse<PageInfo>  listApp(User user, int pageNum,int pageSize){

//        User user = userMapper.selectByUsername(username);
//        if(user == null){
//            return ServerResponse.createByErrorMessage("没有该用户");
//        }
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList = shippingMapper.selectByUserId(user.getId());;
        PageInfo pageResult = new PageInfo(shippingList);//别搞错了是填充得失dao层的 list、
        //如果是二次封装list在进行设置
        //List<OrderVo> orderVoList = assembleOrderVoList(orderList, userId);//组装一下适合前端展示的view object
        //pageResult.setList(orderVoList);//新封装的list只是覆盖一下原来的  适合前端展示
        return ServerResponse.createBySuccess(pageResult);
    }

    public User getUser(String username){
        User user = userMapper.selectByUsername(username);
        if(user == null){
            return null;
        }
        return user;
    }



//    //  用户个人中心中   订单列表   要分页的
//    public ServerResponse<PageInfo> getOrderList(Integer userId ,int pageNum , int pageSize){
//        PageHelper.startPage(pageNum,pageSize);
//        List<Order> orderList = orderMapper.selectByUserId(userId);
//        PageInfo pageResult = new PageInfo(orderList);//别搞错了是填充得失dao层的 list、
//        List<OrderVo> orderVoList = assembleOrderVoList(orderList, userId);//组装一下适合前端展示的view object
//        pageResult.setList(orderVoList);//新封装的list只是覆盖一下原来的  适合前端展示
//        return ServerResponse.createBySuccess(pageResult);
//    }






}