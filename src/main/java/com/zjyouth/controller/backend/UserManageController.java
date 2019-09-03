package com.zjyouth.controller.backend;

import com.zjyouth.common.Const;
import com.zjyouth.common.ResponseCode;
import com.zjyouth.common.ServerResponse;
import com.zjyouth.pojo.User;
import com.zjyouth.service.IUserService;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2018/6/14.
 *
 *
 *    这里编写所有的 管理员操作接口
 *
 */
@Controller
@RequestMapping("/manage/user")
public class UserManageController {


    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "login.do" ,method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username , String password , HttpSession session , HttpServletResponse httpResponse){

        ServerResponse<User> response = iUserService.login(username, password);
        if(response.isSuccess()){
            //有这个用户
            User user = response.getData();
            if(user.getRole() == Const.Role.ROLE_ADMIN){
                //说明登录的是管理员

                session.setAttribute(Const.CURRENT_USER,user);


                return response;
            }else{
                return ServerResponse.createByErrorMessage("不是管理员,无法登录");
            }

        }
        return  response;//根本没这个用户
    }


    /**
     *       获取产品列表   要做分页 参数有
     *       mybatis分页插件  通过该aop分页插件  实现分页
     *       com.github.pagehelper
     *
     *       不传入参数就默认是   第一页    一页10条记录
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse getList(HttpSession session,
                                  @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                  @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize ){
        //所有的后台管理都是要强制登录的
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //填充业务   属性读取 vo的建立
            //return iProductService.manageProductDetail(productId);
            return iUserService.getUserList(pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("非管理员 ,无权限操作");
        }
    }


}