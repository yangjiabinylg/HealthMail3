package com.zjyouth.controller.portal;

import com.zjyouth.common.Const;
import com.zjyouth.common.ResponseCode;
import com.zjyouth.common.ServerResponse;
import com.zjyouth.pojo.User;
import com.zjyouth.service.IUserService;
import org.omg.CORBA.Object;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 *    portal 门户的意思
 *
 *    这里编写   普通用户的所有操作接口
 */
@Controller
@RequestMapping("/user/")//全部是user下面的
public class UserController {

    @Autowired
    private IUserService iUserService;


    /**
     *   用户登录
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "login.do" , method = RequestMethod.POST)
    @ResponseBody//将数据直接序列化为jason  自动通过SpringMVC插件jackson插件
    public ServerResponse<User> login(String username, String password , HttpSession session){

        //调用service-->mybatis->dao     ctrl+t 鼠标  找实现类
        ServerResponse<User> response = iUserService.login(username, password);
        if(response.isSuccess()){
            //session的    key  --  value    登录成功  放到session中用户信息
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    /**
     *    退出登录接口 很好写     移除session就好了   把session的key删除掉
     */
    @RequestMapping(value = "logout.do" , method = RequestMethod.POST)
    @ResponseBody//将数据直接序列化为jason  自动通过SpringMVC插件
    public ServerResponse<User> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        //删除就可以了  session就可以了
        return ServerResponse.createBySuccess();
    }

    /**
     *    退出登录接口 很好写     移除session就好了   把session的key删除掉
     */
    @RequestMapping(value = "register.do" , method = RequestMethod.POST)
    @ResponseBody//将数据直接序列化为jason  自动通过SpringMVC插件
    //public ServerResponse<String> register(String username , String password)
    public ServerResponse<String> register(User user){
        return iUserService.register(user);
    }

    /**   检查用户名和email的有效性
     *
     * @param str
     * @param type    区分是用户名还是email
     * @return
     */
    @RequestMapping(value = "check_valid.do" , method = RequestMethod.POST)
    @ResponseBody//将数据直接序列化为jason  自动通过SpringMVC插件
    public ServerResponse<String> checkValid(String str,String type){
        return iUserService.checkValid(str,type);
    }


    @RequestMapping(value = "get_user_info.do" , method = RequestMethod.POST)
    @ResponseBody//将数据直接序列化为jason  自动通过SpringMVC插件
    public ServerResponse<User> getUserInfo(HttpSession session){
        // 没有进行数据库操作  userMapper.select    用户信息除了密码都在session中
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user != null){
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息");
    }

    /**
     *      密码有2中   1.登录状态下的  重置密码  新密码  旧密码
     *      2.密码提示问题  密码提示答案  重置密码
     */

    /**
     *     通过密码提示问题找密码       密码提示问题的获取
     */
    @RequestMapping(value = "forget_get_question.do" , method = RequestMethod.POST)
    @ResponseBody//将数据直接序列化为jason  自动通过SpringMVC插件
    public ServerResponse<String> forgetGetQuestion(String username){
        return iUserService.selectQuestion(username);
    }

    /*
     *    校验问题答案是不是正确的
     */
    @RequestMapping(value = "forget_check_answer.do" , method = RequestMethod.POST)
    @ResponseBody//将数据直接序列化为jason  自动通过SpringMVC插件
    public ServerResponse<String> forgetCheckAnswer(String username ,String question,String answer){
        //Service层用到 瓜娃  瓜娃缓存  做这个token  利用这个有效期  搞定这个
        return iUserService.checkAnswer(username, question, answer);
    }


    /**
     *      非登录状态下，重置密码
     */
    @RequestMapping(value = "forget_reset_password.do" , method = RequestMethod.POST)
    @ResponseBody//将数据直接序列化为jason  自动通过SpringMVC插件
    public ServerResponse<String> forgetRestPassword(String username,String passwordNew,String forgetToken){
        return iUserService.forgetRestPassword(username,passwordNew,forgetToken);
    }


    /**
     *      登录状态下，重置密码
     *      HttpSession session  判断是否登录
     */
    @RequestMapping(value = "reset_password.do" , method = RequestMethod.POST)
    @ResponseBody//将数据直接序列化为jason  自动通过SpringMVC插件
    public ServerResponse<String> resetPassword(HttpSession session,String passwordOld,String passwordNew){
        //return iUserService.forgetRestPassword(username,passwordNew,forgetToken);
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMessage("用户未登陆");
        }
        //防止横向越权，要校验一下这个用户的旧密码，一定要指定是这个用户,因为我们会查询一个count(1),
        //如果不指定id，那么结构就是true  count>0;
        return iUserService.resetPassword(passwordOld,passwordNew,user);
    }


    /**
     *    更新个人信息的接口  新的个人信息要放到session中  返回给前端  可以直接更新到页面上
     */
    @RequestMapping(value = "update_information.do" , method = RequestMethod.POST)
    @ResponseBody//将数据直接序列化为jason  自动通过SpringMVC插件
    public ServerResponse<User> updateInformation(HttpSession session,User user) {
        //return iUserService.forgetRestPassword(username,passwordNew,forgetToken);
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        //User user 形参是没有userId的
        user.setId(currentUser.getId());//不能更新
        user.setUsername(currentUser.getUsername());//不能更新
        ServerResponse<User> response = iUserService.updateInformation(user);
        if(response.isSuccess()){
            //更新成功  session 放更新后的数据
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response ;//更新失败

    }

    /**
     *     获取个人信息   当没有登录就 强制登录
     */
    @RequestMapping(value = "get_information.do" , method = RequestMethod.POST)
    @ResponseBody//将数据直接序列化为jason  自动通过SpringMVC插件
    public ServerResponse<User> getInformation(HttpSession session) {

        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMessage
                    (ResponseCode.NEED_LOGIN.getCode(),"未登录，需要强制登录status=10") ;
        }
        return iUserService.getInformation(currentUser.getId());
    }


}