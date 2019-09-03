package com.zjyouth.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.zjyouth.common.Const;
import com.zjyouth.common.ResponseCode;
import com.zjyouth.common.ServerResponse;
import com.zjyouth.pojo.User;
import com.zjyouth.service.IOrderService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Created by Administrator on 2018/6/21.
 *
 *       订单和支付联系很紧密   而且 支付的接口没几个  就放一起了
 */
@Controller
@RequestMapping("/order/")
public class OrderController {

    //打印日志用于查错
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);


    @Autowired
    private IOrderService iOrderService;


    /*****************************************订单接口****************************************************/
   /*
    *    创建订单
    */

    /**
     *
     * @param session
     * @param shippingId     收货地址id
     * @return
     */
    @RequestMapping("create.do")
    @ResponseBody
    public ServerResponse create(HttpSession session , Integer shippingId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        //判断用户是否登录
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
         return iOrderService.createOrder(user.getId(),shippingId);
    }



    //取消订单  在未付款的情况下 取消订单
    @RequestMapping("cancel.do")
    @ResponseBody
    public ServerResponse cancel(HttpSession session , Long orderNo){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        //判断用户是否登录
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.cancel(user.getId(),orderNo);
    }



    //获取购物车中已经选中的商品详情  主要是总价  显示现在选中的商品的总价
    @RequestMapping("get_order_cart_product.do")
    @ResponseBody
    public ServerResponse getOrderCartProduct(HttpSession session ){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        //判断用户是否登录
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderCartProduct(user.getId());
    }






    //前台  用户个人中心中   订单列表    订单详情   发货功能开发



    //订单的详情
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse detail(HttpSession session ,Long orderNo ){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        //判断用户是否登录
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderDetail(user.getId(),orderNo);
    }


    //户个人中心中   订单列表   要分页的
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse list(HttpSession session , @RequestParam(value = "pageNum" ,defaultValue = "1") int pageNum, @RequestParam(value = "pageSize" ,defaultValue = "10")int pageSize ){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        //判断用户是否登录
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderList(user.getId(),pageNum,pageSize);
    }

    /**
     *    用户端的查看订单详情和  订单list页面写完了   下面开始写管理员的接口
     *
     *    在后台写    把前后台的接口分离 也是为了 以后做项目分离的时候 扩展时候使用的
     */


















    /*************************************支付宝支付接口********************************************************/


    /**
     *
     *
     * @param session   判断用户登录
     * @param orderNo   支付时的订单号
     * @param request   获取servlet的上下文  拿到upload的文件夹
     *                   将自动生成的二维码， 上传到ftp服务器上，返回给前端二维码的url，前端进行展示
     *                   进行扫码支付
     *
     * @return
     */
    @RequestMapping("pay.do")
    @ResponseBody
    public ServerResponse pay(HttpSession session , Long orderNo, HttpServletRequest request){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        //判断用户是否登录
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        //HttpServletRequest request   获取servlet的上下文，拿到upload文件夹，然后将二维码传递到ftp服务器上
        //返回二维码的图片地址  前端显示图片扫描支付
        String path = request.getSession().getServletContext().getRealPath("upload");
       // String path = request.getSession().getServletContext().getRealPath("upload");//强调一下没有/  xxx/upload
        path = "c:\\mmmaill\\upload\\";

        //String path = request.getSession().getServletContext().getRealPath("upload/");这里是没有斜线的  加上看起来怪怪的
        return iOrderService.pay(orderNo,user.getId(),path);
    }


    @RequestMapping("alipay_callback.do")
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request){
        //返回object  因为可能不止是字符串   这个要返回 alipay 按要求的格式返回
        //支付宝的回调会放到request中供 我们自己取  只要一个参数就好了

        //自己从新组装map
        Map<String, String> params = Maps.newHashMap();
        //支付宝将回调放request中是数组
        Map<String, String[]> parameterParams = request.getParameterMap();
        for(Iterator iterator = parameterParams.keySet().iterator(); iterator.hasNext();){
            String key = (String) iterator.next();
            String[] values = parameterParams.get(key);//支付宝将回调放request中是数组
            String valueStr = "";
            for(int i = 0; i < values.length ; i++){//遍历数组  拼接之后    1,2,3,4
                valueStr = (i == values.length -1)? valueStr + values[i]: valueStr + values[i]+",";
            }
            ////自己从新组装map
            params.put(key,valueStr);
        }
        logger.info("支付宝回调，sign:{},trade_status:{},参数:{}",params.get("sign"),params.get("trade_status"),params.toString());

        //验证是不是我们的订单号   总价是不是对的  数量是不是对的 等等
        //非常重要，验证回调的正确性，是不是支付宝发的，并且能还要避免重复通知,这个说了好多次了ppt里面
        //我们使用rsa2   256位的进行验证  ctrl+shift+t 查class   ctrl+o 查方法

        params.remove("sign_type");
        try {
            boolean alipoayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());
            if(!alipoayRSACheckedV2){
                return ServerResponse.createByErrorMessage("验证不通过，非法请求，在恶意请求我就报网警了");
            }
        } catch (AlipayApiException e) {
             logger.error("支付宝验证回调异常",e);
        }

        //todo  验证各种数据  如果正确  减少库存 增加订单  修改状态  在service层做     你们下去自己做吧


        ServerResponse serverResponse = iOrderService.aliCallback(params);
        if(serverResponse.isSuccess()){
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;
    }


    //前台轮询查询订单状态   扫描成功后  前端会调用这个接口   如果付款成功就跳转成功页面
    @RequestMapping("query_order_pay_status.do")
    @ResponseBody
    public ServerResponse<Boolean> queryOrderPayStatus(HttpSession session , Long orderNo, HttpServletRequest request){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        //判断用户是否登录
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        //return iOrderService.queryOrderPayStatus(user.getId(),orderNo);
        //我们和前端约定好了 返回的是布尔值  这里还要稍微改改
        ServerResponse serverResponse = iOrderService.queryOrderPayStatus(user.getId(), orderNo);
        if(serverResponse.isSuccess()){
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);
    }


    /**
     *    对接支付宝的3个接口就完成了
     *
     *    1.支付
     *    2.查询订单支付状态
     *    3.支付宝回调（这个不是我们调用  是支付宝调用   但是我们没有部署到云服务器上只能  使用natapp进行内网穿透了）
     *    要记得启动natapp客户端  输入token  natapp -authtoken=17dfcd16d4cc900c        账户：中国8250    密码：
     */


}