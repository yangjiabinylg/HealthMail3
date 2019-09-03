package com.zjyouth.controller.app;

import com.github.pagehelper.PageInfo;
import com.zjyouth.common.Const;
import com.zjyouth.common.ResponseCode;
import com.zjyouth.common.ServerResponse;
import com.zjyouth.pojo.Shipping;
import com.zjyouth.pojo.User;
import com.zjyouth.service.IShippingService;
import com.zjyouth.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2018/6/18.
 *
 *      收货地址  5个接口  增删改查    还有一个查询集合  列表页分页
 */
@Controller
@RequestMapping("/app/shipping/")
public class ShippingAppController {

    @Autowired
    private IShippingService iShippingService;




    //springmvc  对象绑定数据的 方式    不用我们一个字段一个字段的写   不然会写的很长的参数列表
    @RequestMapping("add.do")
    @ResponseBody
//    public ServerResponse add(HttpSession session , Shipping shipping){
    public ServerResponse add(String username , Shipping shipping){
//        User user = (User) session.getAttribute(Const.CURRENT_USER);
//        //判断用户是否登录
//        if(user == null){
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
//                    ResponseCode.NEED_LOGIN.getDesc());
//        }
//        return iShippingService.add(user.getId(),shipping);
        return iShippingService.addApp(username,shipping);
    }

    @RequestMapping("del.do")
    @ResponseBody
    public ServerResponse del(String username , Integer shippingId){
//    public ServerResponse del(HttpSession session , Integer shippingId){
//        User user = (User) session.getAttribute(Const.CURRENT_USER);
//        //判断用户是否登录
//        if(user == null){
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode()
//                    ,ResponseCode.NEED_LOGIN.getDesc());
//        }
//        return iShippingService.del(user.getId(),shippingId);

        return iShippingService.delApp( username ,shippingId);
    }


    @RequestMapping("update.do")
    @ResponseBody
    //public ServerResponse update(HttpSession session , Shipping shipping){
    public ServerResponse update(String username , Shipping shipping){
//        User user = (User) session.getAttribute(Const.CURRENT_USER);
//        //判断用户是否登录
//        if(user == null){
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
//        }
//        return iShippingService.update(user.getId(),shipping);

        return iShippingService.updateApp(username,shipping);
    }


    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse<Shipping> select(HttpSession session , Integer shippingId ){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        //判断用户是否登录
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.select(user.getId(),shippingId);
    }


    //最后一个是分页的list的接口
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "20") int pageSize,
                                         //HttpSession session  ){
                                         String username  ){
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //判断用户是否登录
//        if(user == null){
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
//        }
        User user = iShippingService.getUser(username);
        if(user == null){
            return ServerResponse.createByErrorMessage("没有该用户");
        }
        return iShippingService.listApp(user,pageNum,pageSize);
    }


    /**
     *     收发货地址全部完成了
     */


}