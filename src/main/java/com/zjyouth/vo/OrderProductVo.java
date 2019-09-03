package com.zjyouth.vo;

import com.zjyouth.pojo.OrderItem;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Administrator on 2018/6/22.
 *
 *      下面是获取购物车中选中的商品
 */
public class OrderProductVo {


    private List<OrderItemVo> orderItemVoList;
    private BigDecimal productTotalPrice;//总价
    private String imageHost;//ftp服务器图片前缀


    public List<OrderItemVo> getOrderItemVoList() {
        return orderItemVoList;
    }

    public void setOrderItemVoList(List<OrderItemVo> orderItemVoList) {
        this.orderItemVoList = orderItemVoList;
    }

    public BigDecimal getProductTotalPrice() {
        return productTotalPrice;
    }

    public void setProductTotalPrice(BigDecimal productTotalPrice) {
        this.productTotalPrice = productTotalPrice;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }
}