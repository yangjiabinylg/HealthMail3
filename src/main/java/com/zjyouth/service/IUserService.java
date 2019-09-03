package com.zjyouth.service;

import com.github.pagehelper.PageInfo;
import com.zjyouth.common.ServerResponse;
import com.zjyouth.pojo.User;

/**
 * Created by Administrator on 2018/6/13.
 *
 *          controller层调用的是Service层的接口
 */
public interface IUserService {

        ServerResponse<User> login(String username, String password);

        ServerResponse<String> register(User user);

        ServerResponse<String> checkValid(String str,String type);

        ServerResponse selectQuestion(String username);

        ServerResponse<String> checkAnswer(String username ,String question,String answer);

        ServerResponse<String> forgetRestPassword(String username,String passwordNew,String forgetToken);

      /**
       *     登录状态下更新密码
       */
      public ServerResponse<String> resetPassword (String passwordOld,String passwordNew,User user);

      ServerResponse<User> updateInformation(User user);

      ServerResponse<User> getInformation(Integer userId);

    /**
     *  检查是不是管理员
     *
     * @param user
     * @return
     */
     ServerResponse checkAdminRole(User user);

     ServerResponse<PageInfo> getUserList(int pageNum, int pageSize);

     int getUserCount();

}