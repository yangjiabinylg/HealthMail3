package com.zjyouth.service;

import com.zjyouth.common.ServerResponse;
import com.zjyouth.vo.CartVo;

/**
 * Created by Administrator on 2018/6/17.
 */
public interface ICartService {

    ServerResponse<CartVo> add(Integer userId, Integer productId , Integer count );

    ServerResponse<CartVo> update(Integer userId,Integer productId ,Integer count );

    ServerResponse<CartVo> deleteProduct(Integer userId , String productIds );

    ServerResponse<CartVo> list(Integer userId);

    //4个接口用  全选 全不选  单个选中   单个不选中
    ServerResponse<CartVo> selectOrUnselect(Integer userId,Integer productId,Integer checked);

    //获取购物车 产品总数  显示在页面右上方
    ServerResponse<Integer> getCartProductCount(Integer userId);
}