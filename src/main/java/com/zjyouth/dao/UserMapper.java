package com.zjyouth.dao;

import com.zjyouth.common.ServerResponse;
import com.zjyouth.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUsername(String username);//检查用户名是否存在

    int checkEmail(String email);//检查email是否存在

    //用户登录  多个参数一定要写mybatis的注解 @Param("username")
    User selectLogin(@Param("username") String username,@Param("password") String password);

    //用户名找问题
    String selectQuestionByUsername(String username);

    //多个参数一定要写mybatis的注解 @Param("username")
    //@Param("username") 要一一对应的
    int checkAnswer(@Param("username")String username ,@Param("question")String question,@Param("answer")String answer);

    //根据用户名修改密码
    int updatePasswordByUsername(@Param("username")String username ,@Param("passwordNew")String passwordNew);


    //防止横向越权，要校验一下这个用户的旧密码，一定要指定是这个用户,因为我们会查询一个count(1),
    //如果不指定id，那么结构就是true  count>0;
    //int checkPassword (@Param("passwordOld")String passwordOld,@Param("passwordNew") String passwordNew,@Param("user") User user);
    int checkPassword (@Param(value = "password")String password,@Param("userId") Integer userId);

    int checkEmailByUserId (@Param(value = "email")String email,@Param("userId") Integer userId);


    List<User> selectList();


    //    <!--  统计用户数量  -->
    int getUserCount();

    //    <!--  通过用户名查找 用户 -->
    User selectByUsername(String username);

}