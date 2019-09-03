package com.zjyouth.controller.backend;

import com.zjyouth.common.Const;
import com.zjyouth.common.ResponseCode;
import com.zjyouth.common.ServerResponse;
import com.zjyouth.pojo.User;
import com.zjyouth.service.IOrderService;
import com.zjyouth.service.IProductService;
import com.zjyouth.service.IStatisticManageService;
import com.zjyouth.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/8/1.
 */
@Controller
@RequestMapping("/manage/statistic")
public class StatisticManageController {

    @Autowired
    private IUserService iUserService;

//    @Autowired
//    private IProductService iProductService;



//    @Autowired
//    private IOrderService iOrderService;

    @Autowired
    private IStatisticManageService iStatisticManageService;




    @RequestMapping(value = "base_count.do" ,method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Map> baseCount( HttpSession session){

        //以后会用springmvc的拦截器将所有的权限进行归一化
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage
                    (ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录");
        }
        //校验一下是否是管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //是管理员
            return iStatisticManageService.getBaseCount();
        }else{
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }

    }







    //    @RequestMapping(value = "base_count.do" ,method = RequestMethod.GET)
//    @ResponseBody
//    public ServerResponse<Map> baseCount( HttpSession session){
//
////        ServerResponse<User> response = iUserService.login(username, password);
////        if(response.isSuccess()){
////            //有这个用户
////            User user = response.getData();
////            if(user.getRole() == Const.Role.ROLE_ADMIN){
////                //说明登录的是管理员
////                session.setAttribute(Const.CURRENT_USER,user);
////                return response;
////            }else{
////                return ServerResponse.createByErrorMessage("不是管理员,无法登录");
////            }
////
////        }
//
//
//        Map map = new HashMap();
//        map.put("userCount",iUserService.getUserCount());
//        map.put("productCount",iProductService.getProductCount());
//        map.put("orderCount",iOrderService.getOrderCount());
//
//        ServerResponse<Map> response = ServerResponse.createBySuccess(map);;
//        return  response;//根本没这个用户
//
//    }



}