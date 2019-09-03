package com.zjyouth.controller.app;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayRequest;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.demo.trade.config.Configs;

import com.alipay.demo.trade.model.builder.AlipayHeartbeatSynRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.zjyouth.common.Const;
import com.zjyouth.common.ResponseCode;
import com.zjyouth.common.ServerResponse;

import com.zjyouth.dao.UserMapper;
import com.zjyouth.pojo.User;
import com.zjyouth.service.ICartService;
import com.zjyouth.service.IOrderService;
import com.zjyouth.utils.PropertiesAlipayUtil;
import com.zjyouth.utils.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by Administrator on 2018/6/21.
 *
 *       订单和支付联系很紧密   而且 支付的接口没几个  就放一起了
 */
@Controller
@RequestMapping("/app/order/")
public class OrderAppController {

    //打印日志用于查错
    private static final Logger logger = LoggerFactory.getLogger(OrderAppController.class);


    @Autowired
    private IOrderService iOrderService;

    @Autowired
    private ICartService iCartService;


    @Autowired
    private UserMapper userMapper;//这里报错没事的 我们使用mybatis插件的注解


    /*****************************************订单接口****************************************************/
   /*
    *    创建订单
    */

    /**
     *
     *
     * @param shippingId     收货地址id
     * @return
     */
    @RequestMapping("create.do")
    @ResponseBody
    //public ServerResponse create(HttpSession session , Integer shippingId){
    public ServerResponse create(String username , Integer productId, Integer count, Integer shippingId){
//        User user = (User) session.getAttribute(Const.CURRENT_USER);
//        //判断用户是否登录
//        if(user == null){
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
//        }

        User user = userMapper.selectByUsername(username);
        iCartService.add(user.getId(),productId,count);
        return iOrderService.createOrder(user.getId(),shippingId);

    }



    //取消订单  在未付款的情况下 取消订单
    @RequestMapping("cancel.do")
    @ResponseBody
    public ServerResponse cancel(String username , Long orderNo){


        User user = userMapper.selectByUsername(username);
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
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
    public ServerResponse detail(String username ,Long orderNo ){
        User user = iOrderService.getUser(username);
        if(user == null){
            return ServerResponse.createByErrorMessage("没有该用户");
        }
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //判断用户是否登录
//        if(user == null){
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
//        }
        return iOrderService.getOrderDetail(user.getId(),orderNo);
    }


    //户个人中心中   订单列表   要分页的
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse list(String username , @RequestParam(value = "pageNum" ,defaultValue = "1") int pageNum, @RequestParam(value = "pageSize" ,defaultValue = "10")int pageSize ){
//        User user = (User) session.getAttribute(Const.CURRENT_USER);
//        //判断用户是否登录
//        if(user == null){
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
//        }
//        return iOrderService.getOrderList(user.getId(),pageNum,pageSize);


        User user = iOrderService.getUser(username);
        if(user == null){
            return ServerResponse.createByErrorMessage("没有该用户");
        }

        return iOrderService.getOrderList(user.getId(),pageNum,pageSize);
    }

    //户个人中心中   订单列表   要分页的
    @RequestMapping("listNeedPay.do")
    @ResponseBody
    public ServerResponse listNeedPay(String username , @RequestParam(value = "pageNum" ,defaultValue = "1") int pageNum, @RequestParam(value = "pageSize" ,defaultValue = "10")int pageSize ){
//        User user = (User) session.getAttribute(Const.CURRENT_USER);
//        //判断用户是否登录
//        if(user == null){
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
//        }
//        return iOrderService.getOrderList(user.getId(),pageNum,pageSize);


        User user = iOrderService.getUser(username);
        if(user == null){
            return ServerResponse.createByErrorMessage("没有该用户");
        }

        return iOrderService.getOrderlistNeedPay(user.getId(),pageNum,pageSize);
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
        String path = request.getSession().getServletContext().getRealPath("upload");
        //String path = request.getSession().getServletContext().getRealPath("upload/");这里是没有斜线的  加上看起来怪怪的
        return iOrderService.pay(orderNo,user.getId(),path);
    }




    @RequestMapping("payApp.do")
    @ResponseBody
    public Object payApp(String username , Long orderNo){


        User user = (User) iOrderService.getUser(username);
        //判断用户是否登录
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.payApp(orderNo,user.getId() );


    }

    @RequestMapping("alipay_callback.do")
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request){

        System.out.println("alipay_callback 回调了"+request);




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

        params.remove("sign_type");//sign_type不参与签名
        try {
//            boolean alipoayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());
//            boolean alipoayRSACheckedV2 = AlipaySignature.rsaCheckV1(params,
//                    PropertiesAlipayUtil.getProperty("public_key"),"utf-8", "RSA2");

//             验签只需要使用到支付宝公钥 ，而不是使用应用公钥！
            boolean alipoayRSACheckedV2 = AlipaySignature.rsaCheckV1(params,
                    PropertiesAlipayUtil.getProperty("alipay_public_key"),"utf-8", "RSA2");
//            if(!alipoayRSACheckedV2){
//                return ServerResponse.createByErrorMessage("验证不通过，非法请求，在恶意请求我就报网警了");
//            }
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
    /**
     * 要求外部订单号必须唯一。
     * @return
     */
    public static String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
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