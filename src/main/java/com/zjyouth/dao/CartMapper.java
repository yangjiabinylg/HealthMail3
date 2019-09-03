package com.zjyouth.dao;

import com.zjyouth.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {


    //根据主键删除
    int deleteByPrimaryKey(Integer id);

    //全部插入
    int insert(Cart record);

    //插入不为空的字段
    int insertSelective(Cart record);

    //主键查找
    Cart selectByPrimaryKey(Integer id);

    //主键更新   有判空
    int updateByPrimaryKeySelective(Cart record);

    //主键更新   没有判空
    int updateByPrimaryKey(Cart record);


    /**
     *      其他的都是一样的  都是使用mapper  一样   不讲了
     */

    /**
     *   @Param("userId") Integer userId   如果只有一个参数时可以不加注解的 @Param("userId")
     */
    Cart selectCartByUserIdProductId(@Param("userId") Integer userId , @Param("productId") Integer productId);


    /**
     *    这个用户的购物车  产品
     *    如果只有一个参数时可以不加注解的 @Param("userId")
     */
    List<Cart> selectCartByUserId(Integer userId);

    /**
     *    购物车中的item是不是全选
     */
    int selectCartProductCheckedStatusByUserId(Integer userId);

    /**
     *     删除  商品列表
     */
    int deleteByUserIdProductIds(@Param("userId") Integer userId , @Param("productIdList") List<String> productIdList);


    /**
     *     根据用户id 和选中不选中    设置全选和全不选
     */
    //int checkdOrUncheckedAllProduct(@Param("userId") Integer userId , @Param("checked") Integer checked);

    /* 合并一起用好了    单个更新和全部选中一起用就差个
          <if test="productId != null">
          and product_id = #{productId}
        </if>
     */
    int checkdOrUncheckedProduct(@Param("userId") Integer userId , @Param("checked") Integer checked, @Param("productId") Integer productId);


     //获取购物车 产品总数  显示在页面右上方
    int selectCartProductCount(@Param("userId") Integer userId);


    ////从购物车中获取数据  获取该用户勾选的所有的商品列表  新增一个sql
    List<Cart> selectCheckedCartByUserId(Integer userId);

}