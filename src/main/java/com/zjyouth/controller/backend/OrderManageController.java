package com.zjyouth.controller.backend;

import com.github.pagehelper.PageInfo;
import com.zjyouth.common.Const;
import com.zjyouth.common.ResponseCode;
import com.zjyouth.common.ServerResponse;
import com.zjyouth.pojo.Logistics;
import com.zjyouth.pojo.User;
import com.zjyouth.service.IOrderService;
import com.zjyouth.service.IUserService;
import com.zjyouth.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2018/6/22.
 *
 *      在后台写    把前后台的接口分离 也是为了 以后做项目分离的时候 扩展时候使用的
 */

@Controller
@RequestMapping("/manage/order")
public class OrderManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IOrderService iOrderService;



    //在后台写    把前后台的接口分离 也是为了 以后做项目分离的时候 扩展时候使用的
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(HttpSession session , @RequestParam(value = "pageNum" ,defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize" ,defaultValue = "10")int pageSize ){
        //以后会用springmvc的拦截器将所有的权限进行归一化
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage
                    (ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录");
        }
        //校验一下是否是管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //是管理员
            return iOrderService.manageList(pageNum, pageSize );
        }else{
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }




    //后台  订单详情页面    管理员使用
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<OrderVo> orderDetail(HttpSession session , Long orderNo ){
        //以后会用springmvc的拦截器将所有的权限进行归一化
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage
                    (ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录");
        }
        //校验一下是否是管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //是管理员
            return iOrderService.manageDetail(orderNo );
        }else{
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }



    //后台  按订单号查找订单详情    管理员使用
    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> orderSearch(HttpSession session , Long orderNo ,
                                                @RequestParam(value = "pageNum" ,defaultValue = "1") int pageNum,
                                                @RequestParam(value = "pageSize" ,defaultValue = "10")int pageSize  ){
        //以后会用springmvc的拦截器将所有的权限进行归一化
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage
                    (ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录");
        }
        //校验一下是否是管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //是管理员
            return iOrderService.manageSearch(orderNo ,pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

    //后台  按订用户名查找订单详情    管理员使用
    @RequestMapping("searchByUsername.do")
    @ResponseBody
    public ServerResponse<PageInfo> orderSearchByUsername(HttpSession session , String username ,
                                                          @RequestParam(value = "pageNum" ,defaultValue = "1") int pageNum,
                                                          @RequestParam(value = "pageSize" ,defaultValue = "10")int pageSize  ){
        //以后会用springmvc的拦截器将所有的权限进行归一化
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage
                    (ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录");
        }
        //校验一下是否是管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //是管理员
            return iOrderService.manageSearchByUsername(username ,pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }


    /**
     *      管理员   订单发货
     *
     *      返回String 发货成功  或者失败就好了
     */
    @RequestMapping("send_goods.do")
    @ResponseBody
    public ServerResponse<String> orderSendGoods(HttpSession session , Long orderNo , Logistics logistics ){
        //以后会用springmvc的拦截器将所有的权限进行归一化
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage
                    (ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录");
        }
        //校验一下是否是管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //是管理员
            return iOrderService.manageSendGoods(orderNo,   logistics );

        }else{
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }




}