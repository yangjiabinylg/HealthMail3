package com.zjyouth.controller.portal;

import com.zjyouth.common.Const;
import com.zjyouth.common.ResponseCode;
import com.zjyouth.common.ServerResponse;
import com.zjyouth.pojo.User;
import com.zjyouth.service.ICartService;
import com.zjyouth.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2018/6/17.
 */
@Controller
@RequestMapping("/cart/")
public class CartController {

    @Autowired
    private ICartService iCartService;

    //添加购物产品   返回整个列表  计算好总价   计算好item总价  是否选中  是否全选
    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse<CartVo> add(HttpSession session, Integer count, Integer productId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        //判断用户是否登录
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode()
                    ,ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.add(user.getId(),productId,count);
    }


    //  add 写完剩下的 都是调用这个方法  进行各种校验  校验数据正确性
    //改变购物车中的数量    加号  减号   人工修改数量      上面的写好  剩下的就简单了
    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse<CartVo> update(HttpSession session, Integer count, Integer productId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        //判断用户是否登录
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode()
                    ,ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.update(user.getId(),productId,count);
    }

    //购物车中删除产品   写完add    就很简单了   String productIds  可能删除多个和前端与约定好 使用,进行分割 45,66,99,9
    @RequestMapping("delete_product.do")
    @ResponseBody
    public ServerResponse<CartVo> deleteProduct(HttpSession session , String productIds){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        //判断用户是否登录
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode()
                    ,ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.deleteProduct(user.getId(),productIds);
    }


    //写完增删改    下个查   参数只要登录用户就行了    写完add   list是很好写的   购物车就不做分页了
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<CartVo> list(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        //判断用户是否登录
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode()
                    ,ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.list(user.getId());
    }


    //全选（Service层分装共用方法调用就好了）//4个接口用同一个dao  全选 全不选  单个选中   单个不选中
    //全反选（Service层分装共用方法调用就好了）//4个接口用同一个dao  全选 全不选  单个选中   单个不选中


    //单独选（Service层分装共用方法调用就好了）//4个接口用同一个dao  全选 全不选  单个选中   单个不选中
    //单独反选（Service层分装共用方法调用就好了）//4个接口用同一个dao  全选 全不选  单个选中   单个不选中


    //查询当前用户的购物车里面的产品的数量(放在页面的右上角)  如果一个产品有10个,那么数量就是10
    //不是按照种类算  按照总数算   现在电商都是这样  我们和他们一样好了







    //全选（Service层分装共用方法调用就好了）   //4个接口用同一个dao  全选 全不选  单个选中   单个不选中
    //写完增删改    下个查   参数只要登录用户就行了    写完add   list是很好写的   购物车就不做分页了
    @RequestMapping("select_all.do")
    @ResponseBody
    public ServerResponse<CartVo> selectAll(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        //判断用户是否登录
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode()
                    ,ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnselect(user.getId(),null,Const.Cart.CHECKED);
    }
    //全反选（Service层分装共用方法调用就好了）  //4个接口用  全选 全不选  单个选中   单个不选中
    @RequestMapping("un_select_all.do")
    @ResponseBody
    public ServerResponse<CartVo> unSelectAll(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        //判断用户是否登录
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode()
                    ,ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnselect(user.getId(),null,Const.Cart.UN_CHECKED);
    }

    //单独选（Service层分装共用方法调用就好了）   //4个接口用  全选 全不选  单个选中   单个不选中
    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse<CartVo> select(HttpSession session,Integer productId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        //判断用户是否登录
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode()
                    ,ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnselect(user.getId(),productId,Const.Cart.CHECKED);
    }
    //单独反选（Service层分装共用方法调用就好了）   //4个接口用  全选 全不选  单个选中   单个不选中
    @RequestMapping("un_select.do")
    @ResponseBody
    public ServerResponse<CartVo> unSelect(HttpSession session,Integer productId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        //判断用户是否登录
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode()
                    ,ResponseCode.NEED_LOGIN.getDesc());
        }//    ServerResponse<CartVo> selectOrUnselect(Integer userId,Integer productId,Integer checked);
        return iCartService.selectOrUnselect(user.getId(),productId,Const.Cart.UN_CHECKED);
    }


    //查询当前用户的购物车里面的产品的数量(放在页面的右上角)  如果一个产品有10个,那么数量就是10
    //不是按照种类算  按照总数算   现在电商都是这样  我们和他们一样好了
    @RequestMapping("get_cart_product_count.do")
    @ResponseBody
    public ServerResponse<Integer> getCartProductCount(HttpSession session ){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        //判断用户是否登录
        if(user == null){
            return ServerResponse.createBySuccess(0);//购物车未登录的话就显示  0 就好了
        }
        return iCartService.getCartProductCount(user.getId());
    }



}