package com.zjyouth.service;

import com.github.pagehelper.PageInfo;
import com.zjyouth.common.ServerResponse;
import com.zjyouth.pojo.Logistics;
import com.zjyouth.pojo.Order;
import com.zjyouth.pojo.User;
import com.zjyouth.vo.OrderVo;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/6/21.
 */
public interface IOrderService {

    ServerResponse pay(Long orderNo, Integer userId, String path);
    ServerResponse payApp(Long orderNo,Integer userId );

    ServerResponse aliCallback(Map<String ,String> params);

    //查询订单是否付款
    //前台轮询查询订单状态   扫描成功后  前端会调用这个接口   如果付款成功就跳转成功页面
    ServerResponse queryOrderPayStatus(Integer userId,Long orderNo) ;

    ServerResponse  createOrder(Integer userId,Integer shippingId);

    ServerResponse<String > cancel(Integer userId,Long orderNo);

    //下面是获取购物车中选中的商品
    ServerResponse getOrderCartProduct(Integer userId);

    //获取订单的详情  用户界面
    ServerResponse<OrderVo>  getOrderDetail(Integer userId, Long orderNo);

    public void   updateLogisticData(Order order) ;

    public void   updateOrderTask(Order order) ;



    //  用户个人中心中   订单列表   要分页的
    ServerResponse<PageInfo> getOrderList(Integer userId , int pageNum , int pageSize);

    //  用户个人中心中   订单列表   要分页的
    ServerResponse<PageInfo> getOrderlistNeedPay(Integer userId , int pageNum , int pageSize);




    //管理员接口
    //backend  后台管理员的service接口  管理员获取订单列表
    ServerResponse<PageInfo> manageList(int pageNum , int pageSize);


    public List<Order>getOrderListTask();


    //管理员接口   获取订单详情
    ServerResponse<OrderVo> manageDetail(Long orderNo);
    // 管理员接口   //后台  按订单号查找订单详情    管理员使用
    //一期这里暂时使用精确匹配   不过一期先做分页（为二期做准备）   二期多条件模糊查询
    ServerResponse<PageInfo> manageSearch(Long orderNo,int pageNum,int pageSize);
    /**
     *      管理员   订单发货   返回String 发货成功  或者失败就好了
     */
//    ServerResponse<String> manageSendGoods(Long orderNo);
    ServerResponse<String>  manageSendGoods(Long orderNo , Logistics logistics);


    public int getOrderCount();


    public ServerResponse<PageInfo> manageSearchByUsername(String username,int pageNum,int pageSize);

    public User getUser(String username);

}