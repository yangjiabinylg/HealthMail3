package com.zjyouth.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.zjyouth.common.Const;
import com.zjyouth.common.ServerResponse;
import com.zjyouth.common.TokenCache;
import com.zjyouth.dao.UserMapper;
import com.zjyouth.pojo.Product;
import com.zjyouth.pojo.User;
import com.zjyouth.service.IUserService;
import com.zjyouth.utils.MD5Util;
import com.zjyouth.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2018/6/13.
 */
@Service("iUserService")//这个类声明为service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;//这里报错没事的 我们使用mybatis插件的注解

    @Override
    public ServerResponse<User> login(String username, String password) {

        int resultCount = userMapper.checkUsername(username);
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("用户名不存在");//无法登录
        }

        //todo  密码登录MD5;  登录要用加密后的进行比较
        String md5Password = MD5Util.MD5EncodeUtf8(password);


        User user = userMapper.selectLogin(username,md5Password);
        if(user == null){//用户名不存在上一个判断已经返回了   这里只可能密码错误
            return ServerResponse.createByErrorMessage("密码错误");
        }
        // 密码就不要传递了   返回一个空就好了
        user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
        //为什么这里要设置空  加密的密码返回有意义吗
        return ServerResponse .createBySuccess("登录成功",user);
    }


    public ServerResponse<String> register(User user){
//        int resultCount = userMapper.checkUsername(user.getUsername());
//        if(resultCount > 0){
//            return ServerResponse.createByErrorMessage("用户名已经存在");
//        }
//        resultCount = userMapper.checkEmail(user.getEmail());
//        if(resultCount > 0){
//            return ServerResponse.createByErrorMessage("email已经存在");
//        }

        //重构代码
        ServerResponse<String> validResponse = this.checkValid(user.getUsername(), Const.USERNAME);
        if(!validResponse.isSuccess()){//
            return validResponse;//用户名验证不通过
        }
        validResponse = this.checkValid(user.getEmail(), Const.EMAIL);
        if(!validResponse.isSuccess()){
            return validResponse;//邮箱验证不通过
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5加密  原密码MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultCount = userMapper.insert(user);
        if(resultCount == 0){//没插入成功  db出错
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }




    /**   检查用户名和email的有效性
     *
     * @param str
     * @param type    区分是用户名还是email   使用静态常量
     * @return
     */
    public ServerResponse<String> checkValid(String str,String type){
        //org.apache.commons.lang3.StringUtils.isNoneBlank()  认为空格是false；空格认为是没值的
        //org.apache.commons.lang3.StringUtils.isNotEmpty()   认为空格是true   空格认为是有值的
        if(org.apache.commons.lang3.StringUtils.isNoneBlank(type)){//type不是空才开始校验  空格认为是没值的
            //开始校验
            if(Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUsername(str);
                if(resultCount > 0){
                    return ServerResponse.createByErrorMessage("用户名已经存在");
                }
            }
            if(Const.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                if(resultCount > 0){
                    return ServerResponse.createByErrorMessage("email已经存在");
                }
            }
        }else{
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }



    // 忘记密码
    public ServerResponse selectQuestion(String username){
        //这个就不这么写了
        //int resultCount = userMapper.checkUsername(user.getUsername());

        ServerResponse  validResponse = this.checkValid(username, Const.USERNAME);
        if(validResponse.isSuccess()){//这里就是个取非的问题（对照不重构的代码号理解多了--我）
            // true（已经有这个用户名） == true
            //用户不存在   好好想想 不能直接返回validResponse
            return ServerResponse.createByErrorMessage("用户不存在");//这里就是个取非的问题
            //不能直接返回validResponse（return ServerResponse.createByErrorMessage("用户名已经存在");）
            // return ServerResponse.createByErrorMessage("用户名已经存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if(org.apache.commons.lang3.StringUtils.isNoneBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("找回密码的问题是空的，用户注册是没有填写找回密码的问题");
    }




    /*
    *    校验问题答案是不是正确的    使用本地缓存存储用户信息
    */
    public ServerResponse<String> checkAnswer(String username ,String question,String answer){
        //Service层用到 瓜娃  瓜娃缓存  做这个token  利用这个有效期  搞定这个
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if(resultCount > 0){
            //说明问题及问题答案是这个用户的  并且是正确的
            String forgetToken = UUID.randomUUID().toString();//这个可以认为是不会重复的
            //这个token放到本地chache中设置这个的有效期
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);

            //TOKEN_PREFIX  重构代码
            // String token = TokenCache.getKey("token_"+username);
            //String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题的答案错误");
    }


    /**
     *    在登录状态下，重置密码
     */
    public ServerResponse<String> forgetRestPassword(String username,String passwordNew,String forgetToken){

        //没有传递token
        if(org.apache.commons.lang3.StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数错误，必须传递Token ");
        }

        //用户不存在   排除username = null  String token = TokenCache.getKey("token_"+null);
        ServerResponse  validResponse = this.checkValid(username, Const.USERNAME);
        if(validResponse.isSuccess()){//这里就是个取非的问题（对照不重构的代码号理解多了--我）   true（已经有这个用户名） == true
            //用户不存在   好好想想 不能直接返回validResponse
            return ServerResponse.createByErrorMessage("用户不存在");//这里就是个取非的问题
        }

        //没找到缓存token或者没有缓存token
        //TOKEN_PREFIX  重构代码
        // String token = TokenCache.getKey("token_"+username);
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if(org.apache.commons.lang3.StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token无效或者过期");
        }


        //如果你使用的是StringUtils  null 这种情况是不需要考虑的
        //      String a = null;
        //      if("abc".equals(a)){
        //      }
        if(org.apache.commons.lang3.StringUtils.equals(forgetToken,token)){
            //各种校验都通过了
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username, md5Password);

            if(rowCount > 0 ){
                return ServerResponse.createBySuccessMessage("修改密码成功");
                //只有这一种情况是成功的  其他都是失败
            }
        }else{
            return ServerResponse.createByErrorMessage("token错误，请重新获取重置密码的token");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }


    /**
     *     登录状态下更新密码
     */
    public ServerResponse<String> resetPassword (String passwordOld,String passwordNew,User user){
        //防止横向越权，要校验一下这个用户的旧密码，一定要指定是这个用户,因为我们会查询一个count(1),
        //如果不指定id，那么结构就是true  count>0;
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("旧密码错误");
        }

        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);//只更新不为空的字段 //选择性的更新   不是全部更新
        //int updateCount2 = userMapper.updateByPrimaryKey(user); //全部更新
        if(updateCount > 0){
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("密码更新失败");

    }


    public ServerResponse<User> updateInformation(User user){
        //username是不能被更新的
        //email也要进行一个校验，校验新的email是不是已经存在，并且存在的email如果相同的话，不能是我们当前的用户的
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount > 0){// count(1)>0 这个是这个email已经被人注册
            return ServerResponse.createByErrorMessage("email已存在,请更换email再尝试更新");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);//只更新不为空字段
        if(updateCount > 0 ){
            return ServerResponse.createBySuccess("更新个人信息成功",updateUser);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }


    public ServerResponse<User> getInformation(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
        //为什么这里要设置空  加密的密码返回有意义吗
        return ServerResponse.createBySuccess(user);
    }

//    public static void main(String[] args) {
//        //330a89e6-499f-4f50-a124-e9d745d86e3b 这个可以认为是不会重复的
//        System.out.println(UUID.randomUUID().toString());
//    }



    /**
     *  检查是不是管理员   backend  后台的接口   检查是不是管理员
     *
     * @param user
     * @return
     */
    public ServerResponse checkAdminRole(User user){
        if(user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }



    /**
     *      使用mybatis的分页插件
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ServerResponse<PageInfo> getUserList(int pageNum, int pageSize){
        //1.startPage--start讲一下mybatis分页的使用方法
        //2.填充自己的sql查询逻辑
        //3.pageHelper-收尾

        //1.startPage--start
        PageHelper.startPage(pageNum,pageSize);


        //2.填充自己的sql查询逻辑
        /*
            order by id asc; 不能加 ;
            mybatis的分页插件会自动加上 limit 10 offset 1 这部分是mybatis自动加上的

            Product 这么多信息是不用加载的   重写一个对象要显示什么  传递什么
            对productListItem 对象进行封装
        */
        List<User> userList = userMapper.selectList();//很多字段列表是不需要显示的
//        List<ProductListVo> productListVoArrayList = Lists.newArrayList();//封装成
//        for(Product productItem : productList){
//            //封装一个适合listItem的对象  减少数据传输的数据量
//            ProductListVo productListVo = assembleProductListVo(productItem);
//            productListVoArrayList.add(productListVo);
//        }


        //3.pageHelper-收尾   这里myabtis 会自动进行分页处理
        // mybatis的分页插件会自动加上 limit 10 offset 1 这部分是mybatis自动加上的
        PageInfo pageResult = new PageInfo(userList);//自动加limit是在这里加的
//        pageResult.setList(productListVoArrayList);//封装成适合  列表显示的结果  去除多余的字段
        return ServerResponse.createBySuccess(pageResult);

    }





    /**
     *
     */
    public int getUserCount(){
        List<User> userList = userMapper.selectList();
        int userCount = userList.size();
        return userCount;
    }



}