package com.zjyouth.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.zjyouth.common.Const;
import com.zjyouth.common.ResponseCode;
import com.zjyouth.common.ServerResponse;
import com.zjyouth.dao.CartMapper;
import com.zjyouth.dao.ProductMapper;
import com.zjyouth.pojo.Cart;
import com.zjyouth.pojo.Product;
import com.zjyouth.service.ICartService;
import com.zjyouth.service.ICatetoryService;
import com.zjyouth.utils.BigDecimalUtil;
import com.zjyouth.utils.PropertiesUtil;
import com.zjyouth.vo.CartProductVo;
import com.zjyouth.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Administrator on 2018/6/17.
 */
@Service("iCartService")
public class CartServiceImpl implements ICartService {


    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;


    //相向购物车添加产品
    public ServerResponse<CartVo> add(Integer userId,Integer productId ,Integer count ){
        if(productId == null || count == null){//有一个为空就说明参数错误    2个参数都必须传递
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode()
                    ,ResponseCode.NEED_LOGIN.getDesc());
        }

        //先查询一下  有的话更新  没有就插入数据
        Cart cart = cartMapper.selectCartByUserIdProductId(userId,productId);
        if(cart == null){
            //没有查到数据  新增这条数据
            //这个产品不再购物车里面，需要新增一个这个产品的记录
            Cart cartItem = new Cart();
            cartItem.setQuantity(count );
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);
            cartMapper.insert(cartItem);
        }else{
            //这个产品以及在购物车里面了
            // 如果产品已经存在，数量相加
            count = cart.getQuantity() + count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        /*
                 购物车是不可能这么简单的  我们需要封装一个  比较通用的方法
                 1.需要一个 产品item的vo  需要封装一下
                 2.list<item>  封装
         */
        //重新从db获取最新的产品列表    重构代码
        //CartVo cartVo  = this.getCartVoLimit(userId);
        //return ServerResponse.createBySuccess(cartVo);
        return  this.list(userId);//调用上的list  封装好了

    }



    //改变购物车的数量  //改变购物车中的数量    加号  减号   人工修改数量      上面的写好  剩下的就简单了
    public ServerResponse<CartVo> update(Integer userId,Integer productId ,Integer count ){
        if(productId == null || count == null){//有一个为空就说明参数错误
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }

        //先查询一下  有的话更新
        Cart cart = cartMapper.selectCartByUserIdProductId(userId,productId);
        if(cart != null){
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKeySelective(cart);
        //重新从db获取最新的产品列表    重构代码
        //CartVo cartVo  = this.getCartVoLimit(userId);
        //return ServerResponse.createBySuccess(cartVo);
        return  this.list(userId);//调用上的list  封装好了
    }


    //删除购物车里面的  items
    public ServerResponse<CartVo> deleteProduct(Integer userId , String productIds ){
        //介绍一下 瓜娃中的split方法 直接搞定
        //不然我们要讲split转成数组  然后有遍历数据  添加到集合中
        List<String> productList = Splitter.on(",").splitToList(productIds);
        if(CollectionUtils.isEmpty(productList)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }

        cartMapper.deleteByUserIdProductIds(userId,productList);
        //重新从db获取最新的产品列表    重构代码
        //CartVo cartVo  = this.getCartVoLimit(userId);
        //return ServerResponse.createBySuccess(cartVo);
        return  this.list(userId);//调用上的list  封装好了

    }




    /**
     *    对购物车进行限制   有效性判断  是否购物数量超出库存等等
     *
     *
     *    全选 反选 都调用这个方法
     */
    private CartVo getCartVoLimit(Integer userId){
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        List<CartProductVo> cartProductVoList = Lists.newArrayList();

        BigDecimal cartTotalPrice = new BigDecimal("0");//购物总价

        if(CollectionUtils.isNotEmpty(cartList)){//购物车不为空
            for(Cart cartItem : cartList){
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(cartItem.getUserId());
                cartProductVo.setProductId(cartItem.getProductId());

                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                if(product != null){
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());

                    //判断库存
                    int buyLimitCount = 0;
                    if(product.getStock() >= cartItem.getQuantity()){//购买数量小于等于库存
                        //库存充足的时候
                        buyLimitCount = cartItem.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    }else{//购买数量大于库存
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        //购物车中更新有效库存   要购买的数量大于库存  要重置
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cartItem.getId());
                        cartForQuantity.setQuantity(buyLimitCount);//更新最多可以购买   库存的数量
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    //计算总价  这个是  item 的总价不是整个购物车的总价    item*单价 = item的总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue()
                            ,cartProductVo.getQuantity().doubleValue()));
                    cartProductVo.setProductChecked(cartItem.getChecked());
                }
                if(cartItem.getChecked() == Const.Cart.CHECKED){//这个item是选中的
                    //如果已经勾选  增加到整个的购物车总价中
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue()
                            ,cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(this.getAllCheckedStatus(userId));//全选是true
//        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix.http"));
        return cartVo;
    }

    private boolean getAllCheckedStatus(Integer userId){
        if(userId == null){//没这个人
            return false;
        }
        //cartMapper.selectCartProductCheckedStatusByUserId(userId)
        //返回大于0说明有未勾选的  等于0没有未选中的（就是全选了）
        //sql  是查询有没有未勾选的
       return cartMapper.selectCartProductCheckedStatusByUserId(userId ) == 0;//等于0没有未选中的（就是全选了）
    }

    //写完add   list超级好写  购物车就不做分页了
    public ServerResponse<CartVo> list(Integer userId){
        //重新从db获取最新的产品列表
        CartVo cartVo  = this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }


    // 调用上的list   重置全选和全不选
    // 4个接口用  全选 全不选  单个选中   单个不选中
    public ServerResponse<CartVo> selectOrUnselect(Integer userId,Integer productId,Integer checked){
        cartMapper.checkdOrUncheckedProduct(userId,checked,productId);
        return  this.list(userId);//调用上的list  封装好了
    }


    //获取购物车 产品总数  显示在页面右上方
    public ServerResponse<Integer> getCartProductCount(Integer userId){
        if(userId == null){
            return ServerResponse.createBySuccess(0);
        }
        return  ServerResponse.createBySuccess(cartMapper.selectCartProductCount(userId));
    }



}