package com.zjyouth.service.impl;

import ch.qos.logback.core.rolling.helper.CompressionMode;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayResponse;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.zjyouth.common.Const;
import com.zjyouth.common.LogisticsCompanyEnum;
import com.zjyouth.common.ServerResponse;
import com.zjyouth.dao.*;
import com.zjyouth.pojo.*;
import com.zjyouth.service.IOrderService;
import com.zjyouth.utils.*;
import com.zjyouth.vo.OrderItemVo;
import com.zjyouth.vo.OrderProductVo;
import com.zjyouth.vo.OrderVo;
import com.zjyouth.vo.ShippingVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * Created by Administrator on 2018/6/21.
 */
@Service("iOrderService")
public class OrderServiceImpl implements IOrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private PayInfoMapper payInfoMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ShippingMapper shippingMapper;

    @Autowired
    private UserMapper userMapper;


    @Autowired
    private LogisticsMapper logisticsMapper;


    //订单号   用户id    二维码上传到哪里的路径
    //map承载数据  就不特地封装对象了   传递前端订单号  二维码上传到哪里的路径
    public ServerResponse pay(Long orderNo,Integer userId,String path){
        Map<String ,String> resultMap = Maps.newHashMap();

        //orderNo 和 userId查询一下订单有没有  多个参数要写Param注解
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if(order == null){
            return ServerResponse.createByErrorMessage("用户没有该订单");
        }
        resultMap.put("orderNo",String.valueOf(order.getOrderNo()));
        //组装生成支付宝订单的各种参数  这里参考demo的例子


        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        //String outTradeNo = "tradeprecreate" + System.currentTimeMillis()//生成一个不会重复的订单号  这里是毫秒数模拟
        //        + (long) (Math.random() * 10000000L);
        String outTradeNo = order.getOrderNo().toString();//用自己的订单号

        //主题
        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        //String subject = "xxx品牌xxx门店当面付扫码消费";
        String subject = new StringBuilder().append("康体汇健康工程,订单号:").append(outTradeNo).toString();

        //总金额   一期是不考虑打折的
        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();//订单的总价

        //打折金额  可以不填写 一期是不考虑打折的
        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        //商家的id  我们用默认好了  不修改
        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        //String body = "购买商品3件共20.00元";
        String body = new StringBuilder().append("订单").append(outTradeNo).append("购买商品共").append(totalAmount).append("元").toString();

        //售货员id用于  销量的绩效   售货员1,2,3  3个人  根据售货员登录的账户将传入的id  记账用的
        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";


        //店铺编号   如果是连锁店就用到   这个店户编号
        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        //扩展字段   就不修改了
        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();

        /**
         *      List<OrderItem> orderItemList  这个是订单明细
         *      是在下一个章节讲的   支付和订单很紧密  为了保证流程还是按电商的流程来
         *      如果先讲订单还要给支付留个口  再讲支付的业务填充进去  怕大家理解起来困难    还是按照电商的流程来讲比较好
         *      里面的支付写不了    支付放最后也不合适  还是先讲支付吧
         */

        //先去写个接口  查询出所有的 订单详情
        List<OrderItem> orderItemList = orderItemMapper.getByOrderNoUserId(orderNo,userId);
        for(OrderItem orderItem : orderItemList){
            // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
            //GoodsDetail goods1 = GoodsDetail.newInstance("goods_id001", "xxx小面包", 1000, 1);
            GoodsDetail goods = GoodsDetail.newInstance(orderItem.getProductId().toString(), orderItem.getProductName(),
                    BigDecimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue(),new Double(100).doubleValue()).longValue(),
                    orderItem.getQuantity());
            goodsDetailList.add(goods);
        }

        // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
        //GoodsDetail goods1 = GoodsDetail.newInstance("goods_id001", "xxx小面包", 1000, 1);
        // 创建好一个商品后添加至商品明细列表
        //goodsDetailList.add(goods1);

        // 继续创建并添加第一条商品信息，用户购买的产品为“黑人牙刷”，单价为5.00元，购买了两件
        //GoodsDetail goods2 = GoodsDetail.newInstance("goods_id002", "xxx牙刷", 500, 2);
        //goodsDetailList.add(goods2);

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                //用户支付后支付宝主动跳转的页面
                //怎么才能接收到这个回调呢   natapp
                //这个也可以是远程debug
                //这个是线上沙箱里面有配置  复制
                //.setNotifyUrl("http://www.heppymmall.com/order/alipay_callback.do")//目前还没写
                .setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))//目前还没写
                //.setNotifyUrl("http://www.test-notify-url.com")//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);

        /**    一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *     Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        //静态块    加载支付宝配置文件
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        //初始化交易Service
        AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

        //支付宝返回的结果
        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS://下单成功
                logger.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                //下单成功  生成二维码  返回前端
                File folder = new File(path);
                //file判断存不存在   给与写权限   目录创建出来   保证文件夹是有的不然二维码文件放哪里
                if(!folder.exists()){//file判断存不存在
                    folder.setWritable(true);//给与写权限
                    folder.mkdirs();//目录创建出来
                }

                // 需要修改为运行机器上的路径      %是对订单号进行替换
                //String filePath = String.format("/Users/sudo/Desktop/qr-%s.png", response.getOutTradeNo());
                //细节细节细节   xxxxx/uploadqr-xxx.png   订单号会替换%s
                String qrPath = String.format(path+"/qr-%s.png", response.getOutTradeNo());
                //订单号会替换 %s  生成文件名
                String  qrFileName = String.format("qr-%s.png", response.getOutTradeNo());
                //瓜娃提供的方法   支付宝已经封装好了  调用瓜娃的内容
                //Zxing这个工具类生成一个二维码
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);

                File targetFile = new File(path,qrFileName);
                try {
                    FTPUtil.uploadFile(Lists.newArrayList(targetFile));//上传图片至图片服务器
                } catch (IOException e) {
                    logger.error("上传二维码异常",e);
                }
                logger.info("qrpath:"+qrPath);
//                String qrUrl = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFile.getName();
                String qrUrl = PropertiesUtil.getProperty("ftp.server.http.prefix.http")+targetFile.getName();
                logger.info("qrUrl:" +  qrUrl);
                resultMap.put("qrUrl",qrUrl);
                return ServerResponse.createBySuccess(resultMap);
            //Zxing这个工具类生成一个二维码
            //ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);
            //break;
            case FAILED:
                logger.error("支付宝预下单失败!!!");
                return ServerResponse.createByErrorMessage("支付宝预下单失败!!!");
            //break;

            case UNKNOWN:
                logger.error("系统异常，预下单状态未知!!!");
                return ServerResponse.createByErrorMessage("系统异常，预下单状态未知!!!");
            //break;

            default:
                logger.error("不支持的交易状态，交易返回异常!!!");
                return ServerResponse.createByErrorMessage("不支持的交易状态，交易返回异常!!!");
            //break;
        }
    }






    //订单号   用户id    二维码上传到哪里的路径
    //map承载数据  就不特地封装对象了   传递前端订单号  二维码上传到哪里的路径
    public ServerResponse payApp(Long orderNo,Integer userId ){

        Map<String ,String> resultMap = Maps.newHashMap();

        //orderNo 和 userId查询一下订单有没有  多个参数要写Param注解
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if(order == null){
            return ServerResponse.createByErrorMessage("用户没有该订单");
        }

        try{
            AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipaydev.com/gateway.do",
                    PropertiesAlipayUtil.getProperty("appid"),PropertiesAlipayUtil.getProperty("private_key"),
                    "json","UTF-8",
                    PropertiesAlipayUtil.getProperty("public_key"),"RSA2");


            AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
            AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
            model.setOutTradeNo(String.valueOf(order.getOrderNo())  );// 订单号。
            model.setTimeoutExpress("120m");// 设置未付款支付宝交易的超时时间，一旦超时，该笔交易就会自动被关闭。
            // 当用户进入支付宝收银台页面（不包括登录页面），会触发即刻创建支付宝交易，此时开始计时。
            // 取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。
            // 该参数数值不接受小数点， 如 1.5h，可转换为 90m。
            model.setTotalAmount( order.getPayment().toString());// 订单总金额，单位为元，精确到小数点后两位，
            // 取值范围[0.01,100000000]这里调试每次支付1分钱，在项目上线前应将此处改为订单的总金额
            model.setProductCode("QUICK_MSECURITY_PAY");// 销售产品码，商家和支付宝签约的产品码，为固定值QUICK_MSECURITY_PAY
            request.setBizModel(model);
            request.setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url.app"));// 设置后台异步通知的地址，
            // 在手机端支付成功后支付宝会通知后台，手机端的真实支付结果依赖于此地址
            // 根据不同的产品

            model.setBody(new StringBuilder().append("订单").append(String.valueOf(order.getOrderNo()))
                    .append("购买商品共").append(order.getPayment().toString())
                    .append("元").toString());// 对一笔交易的具体描述信息。如果是多种商品，请将商品描述字符串累加传给body。
            model.setSubject(new StringBuilder().append("康体汇健康工程,订单号:").append(String.valueOf(order.getOrderNo())).toString());

            //break;
            // 这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            // 可以直接给客户端请求，无需再做处理。
//            orders.setAliPayOrderString(response.getBody());
//            baseResult.setData(orders);


            resultMap.put("orderString",response.getBody());
            return ServerResponse.createBySuccess(resultMap);
        }
        catch (Exception e)
        {
            e.printStackTrace();
//            baseResult.setState(-999);
//            baseResult.setMsg("程序异常!");
//            baseResult.setSuccess(false);
//            logger.error(e.getMessage());
            System.out.println("程序异常!");
            return ServerResponse.createByErrorMessage("支付宝下单失败!!!");
        }

    }










    //这个就是日志上的应答    之前看的日志就是这里了
    //简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            logger.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                logger.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            logger.info("body:" + response.getBody());
        }
    }




    public ServerResponse aliCallback(Map<String ,String> params){
        //todo  验证各种数据  如果正确  减少库存 增加订单  修改状态  在service层做     你们下去自己做吧
        //订单主键  订单号拿出来
        Long orderNo = Long.parseLong(params.get("out_trade_no"));
        //支付宝的交易号 也要保持一下做一下持久化  保存到payinfo表里面
        String tradeNo = params.get("trade_no");
        //交易状态
        String tradStatus = params.get("trade_status");
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order == null){//查询一下支付宝传递过来的订单号  我们数据库是否有
            return ServerResponse.createByErrorMessage("非严选商城订单，回调忽略");
        }
        if(order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()){
            //这个状态大于等于20 说明已经支付了钱  那就是我们的事了   支付宝回调过了
            return ServerResponse.createBySuccess("支付宝重复调用");
        }
        if(Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradStatus)){//交易成功
            order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));//保存交易时间
            order.setStatus(Const.OrderStatusEnum.PAID.getCode());//订单状态修改为已付款
            orderMapper.updateByPrimaryKeySelective(order);//
        }

        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());//订单主键  订单号拿出来
        payInfo.setPayPlatform(Const.PayPlatformEnum.ALIPAY.getCode());//一期只支持alipay
        payInfo.setPlatformNumber(tradeNo);//支付宝的交易号 也要保持一下做一下持久化  保存到payinfo表里面
        payInfo.setPlatformStatus(tradStatus);

        payInfoMapper.insert(payInfo);
        return ServerResponse.createBySuccess();

    }



    //查询订单是否付款
    //前台轮询查询订单状态   扫描成功后  前端会调用这个接口   如果付款成功就跳转成功页面
    public ServerResponse queryOrderPayStatus(Integer userId,Long orderNo) {
        //orderNo 和 userId查询一下订单有没有  多个参数要写Param注解
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("用户没有该订单");
        }
        if(order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()){
            //这个状态大于等于20 说明已经支付了钱(包括未发货，已发货 ，订单成功这些   )   那就是我们的事了   支付宝回调过了
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

    ///////////////////////////////////////上面是支付宝接口/////////////////////////////////////////////////////////////


    ////////////////////////////////////////////下面是订单接口////////////////////////////////////////////////////////

    //创建订单
    public ServerResponse  createOrder(Integer userId,Integer shippingId){

        //从购物车中获取数据  获取该用户勾选的所有的商品列表  新增一个sqlMapper
        List<Cart> cartList = cartMapper.selectCheckedCartByUserId(userId);

        //计算这个订单的总价  写个方法调用一下<List<OrderItem>>
        ServerResponse serverResponse = this.getCartOrderItem(userId,cartList);
        if(!serverResponse.isSuccess()){//如果获取失败
            return serverResponse;//直接返回错误
        }
        //计算订单总价
        List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();
        BigDecimal payment = this.getOrderTotalPrice(orderItemList);

        if(CollectionUtils.isEmpty(orderItemList)){
            return ServerResponse.createByErrorMessage("购物车为空");
        }
        //生成订单  写个私有方法
        Order order = this.assembleOrder(userId,shippingId,payment);
        if(order == null){
            return ServerResponse.createByErrorMessage("生成订单错误");
        }
        for(OrderItem orderItem : orderItemList ){
            orderItem.setOrderNo(order.getOrderNo());//所有的订单的item都属于同一个订单的编号
        }

        //mybatis 批量插入订单item   一个订单有多个订单item
        orderItemMapper.batchInsert(orderItemList);

        //生成成功，我们要减少我们产品的库存
        reduceProductStock(orderItemList);

        //清理一下购物车
        cleanCart(cartList);

        //封装一个vo对象  返回前端  包括时间戳转成时间  支付的订单号  金额 状态
        //包含订单信息和订单明细信息和 收货人信息
        //组装返回前端的订单预览界面   下一步就是支付了
        OrderVo orderVo = assembleOrderVo(order, orderItemList);
        return ServerResponse.createBySuccess(orderVo);
    }


    //组装返回前端的订单预览界面   下一步就是支付了
    //包含订单信息和订单明细信息和 收货人信息
    private OrderVo assembleOrderVo(Order order ,List<OrderItem> orderItemList){
        //组装返回前端的订单预览界面   下一步就是支付了
        OrderVo orderVo = new OrderVo();
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentType(order.getPaymentType());
        //"在线支付"    不能这么硬编码的
        orderVo.setPaymentTypeDesc(Const.PaymentTypeEnum.codeOf(order.getPaymentType()).getValue());

        orderVo.setPostage(order.getPostage());
        orderVo.setStatus(order.getStatus());
        orderVo.setStatusDesc(Const.OrderStatusEnum.codeOf(order.getStatus()).getValue());

        orderVo.setShippingId(order.getShippingId());
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        if(shipping != null){
            orderVo.setReceiverName(shipping.getReceiverName());
            orderVo.setShippingVo(assembleShippingVo(shipping));
        }

        orderVo.setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
        orderVo.setSendTime(DateTimeUtil.dateToStr(order.getSendTime()));
        orderVo.setEndTime(DateTimeUtil.dateToStr(order.getEndTime()));
        orderVo.setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()));
        orderVo.setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()));

//        orderVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        orderVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix.http"));

        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        for(OrderItem orderItem : orderItemList){
            OrderItemVo orderItemVo = assembleOrderItemVo(orderItem);
            orderItemVoList.add(orderItemVo);
        }
        orderVo.setOrderItemVoList(orderItemVoList);
        return orderVo;
    }

    //组装orderItemVo对象
    private OrderItemVo assembleOrderItemVo(OrderItem orderItem){
        OrderItemVo orderItemVo = new OrderItemVo();
        orderItemVo.setOrderNo(orderItem.getOrderNo());
        orderItemVo.setProductId(orderItem.getProductId());
        orderItemVo.setProductName(orderItem.getProductName());
        orderItemVo.setProductImage(orderItem.getProductImage());
        orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
        orderItemVo.setQuantity(orderItem.getQuantity());
        orderItemVo.setTotalPrice(orderItem.getTotalPrice());
        orderItemVo.setCreateTime(DateTimeUtil.dateToStr(orderItem.getCreateTime()));
        return orderItemVo;
    }

    //组装收货人信息
    private ShippingVo assembleShippingVo(Shipping shipping){
        ShippingVo shippingVo = new ShippingVo();
        shippingVo.setReceiverName(shipping.getReceiverName());
        shippingVo.setReceiverAddress(shipping.getReceiverAddress());
        shippingVo.setReceiverProvince(shipping.getReceiverProvince());
        shippingVo.setReceiverCity(shipping.getReceiverCity());
        shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVo.setReceiverMobile(shipping.getReceiverMobile());
        shippingVo.setReceiverZip(shipping.getReceiverZip());
        shippingVo.setReceiverPhone(shipping.getReceiverPhone());
        return  shippingVo;
    }

    //清理一下购物车
    private void cleanCart(List<Cart> cartList){
        for(Cart cart : cartList){
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
    }

    // //生成成功，我们要减少我们产品的库存
    private void reduceProductStock(List<OrderItem> orderItemList){
        for(OrderItem orderItem : orderItemList){
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock() - orderItem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
    }

    //生成订单  写个私有方法
    private Order assembleOrder(Integer userId ,Integer shippingId ,BigDecimal payment){
        Order order = new Order();
        //生成订单号  用主键不行 竞争对象一看就知道你到底有多少单
        //设计的好以后做分布式  高并发  都是没问题的
        //1期就简单点就是时间戳取余了
        //long orderNo =  generateOrderNo(shippingId);
        long orderNo =  generateOrderNo(userId);
        order.setOrderNo(orderNo);
        order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());//订单状态未付款
        //运费是扩展用的  一期就全部包邮   设置为0
        order.setPostage(0);
        //目前只支持线上付款  货到付款不支持
        order.setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode());
        order.setPayment(payment);
        order.setUserId(userId);
        order.setShippingId(shippingId);
        //发货时间就不写了 这个是后期加的  到时候更新就好了 付钱以后的事情

        //发货时间等等
        //付款时间等等

        int rowCount = orderMapper.insert(order);
        if( rowCount > 0 ){
            return order;
        }
        return null;//插入失败
    }

    //生成订单号
    //订单号生成的规则  二期会讲订单号怎么生成比较好  订单号怎么分库分表  多数据源 扩展做好准备
    //订单号在分布式中是怎么生成的   高并发的情况下 如何生成    shippingId
    //private  long generateOrderNo(Integer userId){
    private  long generateOrderNo(Integer userId){
        long currentTime = System.currentTimeMillis();
        // return currentTime + currentTime % 9;//1期就简单点就是时间戳取余了  并发可能会相同时间点击可能会重复
        //我们数据库是订单号唯一索引  有个人会下单失败
        //return currentTime +new Random().nextInt(1000)+userId+ new Random().nextInt(100);//一期并发不多但是原理还是有的

        //String temp = String.valueOf(currentTime) +  String.valueOf(new Random().nextInt(1000)) +  String.valueOf(userId)+ String.valueOf(new Random().nextInt(100));
        //String temp = String.valueOf(currentTime) +   String.valueOf(shippingId);
        //return Long.parseLong(temp);//一期并发不多但是原理还是有的
        return currentTime +userId+ new Random().nextInt(100);//一期并发不多但是原理还是有的
    }

    //计算订单所有item的总价
    private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList){
        BigDecimal payMent = new BigDecimal("0");
        // item总价 =  item单价 * item数量
        // 订单总价 =  订单总价 + item总价
        for(OrderItem orderItem  : orderItemList){
            payMent =  BigDecimalUtil.add(payMent.doubleValue() ,orderItem.getTotalPrice().doubleValue());
        }
        return payMent;
    }

    //计算这个订单的总价  写个方法调用一下  <List<OrderItem>>
    private ServerResponse getCartOrderItem(Integer userId,List<Cart> cartList){
        List<OrderItem> orderItemList = Lists.newArrayList();
        if(CollectionUtils.isEmpty(cartList)){
            return ServerResponse.createByErrorMessage("购物车为空");
        }

        //校验购物车中的数据   包括产品的状态和数量
        for(Cart cartItem : cartList){
            OrderItem orderItem = new OrderItem();
            Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
            if(Const.ProductStatusEnum.ON_SALE.getCode() != product.getStatus()){
                //购物车中产品是不是在售状态
                return ServerResponse.createByErrorMessage("产品 "+product.getName()+" 不是在线售卖状态，下架了");
            }
            //校验库存
            if(cartItem.getQuantity() > product.getStock()){
                return ServerResponse.createByErrorMessage("产品 "+product.getName()+" 库存不足");
            }
            //校验通过组装订单item项目
            orderItem.setUserId(userId);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            //产品价格是变动的  需要持久化当时购买的价格
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            //计算单项item的总价    item总价 = item单价 * item数量
            orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartItem.getQuantity()));
            orderItemList.add(orderItem);
        }
        return ServerResponse.createBySuccess(orderItemList);
    }

    ////////////////////////////////////////////上面都是创建订单接口////////////////////////////////////////////////////////

    ////////////////////////////////////////////下面是取消订单接口////////////////////////////////////////////////////////
    //取消订单这个就比较简单了
    public ServerResponse<String > cancel(Integer userId,Long orderNo){
        Order order = orderMapper.selectByUserIdAndOrderNo(userId,orderNo);
        if(order == null){
            return ServerResponse.createByErrorMessage("该用户此订单不存在");
        }
        if(order.getStatus() !=  Const.OrderStatusEnum.NO_PAY.getCode()){
            //二期会有付款后对接支付宝退款   一期支付宝支付就不能退款了
            return ServerResponse.createByErrorMessage("已付款,无法取消订单");
        }
        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(Const.OrderStatusEnum.CANCELED.getCode());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
        updateOrder.setEndTime(new Date());
        updateOrder.setCloseTime(new Date());

        int row = orderMapper.updateByPrimaryKeySelective(updateOrder);
        if(row > 0 ){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }



    ////////////////////////////////////////////下面是获取购物车中选中的商品////////////////////////////////////////////////////////
    public ServerResponse getOrderCartProduct(Integer userId){
        OrderProductVo orderProductVo = new OrderProductVo();
        //从购物车中获取数据
        List<Cart> cartList = cartMapper.selectCheckedCartByUserId(userId);
        ServerResponse serverResponse = getCartOrderItem(userId, cartList);
        if(!serverResponse.isSuccess()){
            return serverResponse;
        }
        List<OrderItem> orderItemList  =  (List<OrderItem>) serverResponse.getData();

        List<OrderItemVo> orderItemVoList = Lists.newArrayList();

        BigDecimal payment = new BigDecimal("0");
        for(OrderItem orderItem : orderItemList){
            payment = BigDecimalUtil.add(payment.doubleValue(),orderItem.getTotalPrice().doubleValue());
            orderItemVoList.add(assembleOrderItemVo(orderItem));
        }
        orderProductVo.setProductTotalPrice(payment);
        orderProductVo.setOrderItemVoList(orderItemVoList);
//        orderProductVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        orderProductVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix.http"));
        return  ServerResponse.createBySuccess(orderProductVo);
    }






//    //获取订单的详情  用户界面
//    public ServerResponse<OrderVo>  getOrderDetail(Integer userId,Long orderNo){
//        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
//
//        if(order != null){
//            List<OrderItem> orderItemList = orderItemMapper.getByOrderNoUserId(orderNo,userId);
//            OrderVo orderVo = assembleOrderVo(order,orderItemList);
//            return ServerResponse.createBySuccess(orderVo);
//        }
//        return ServerResponse.createByErrorMessage("没有找到该订单");
//    }


    //获取订单的详情  用户界面
    public ServerResponse<OrderVo>  getOrderDetail(Integer userId,Long orderNo) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        Logistics logistics = null;
        if (order != null) {

            if (order.getLogisticsId() != null) {
                logistics = logisticsMapper.selectByPrimaryKey(order.getLogisticsId());
                String logisticsDetail = "";
                try {
                    logisticsDetail = LogisticsUtil.getLogisticsDetail(logistics.getLogisticsCode(), logistics.getLogisticsNo());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                logistics.setLogisticsDetail(logisticsDetail);
                LogisticsDetail logisticsDetail1 = new Gson().fromJson(logisticsDetail, LogisticsDetail.class);
                /*
                status	查询结果状态：
                0：物流单暂无结果，
                1：查询成功，
                2：接口出现异常，
//// {"message":"快递公司参数异常：单号不存在或者已经过期","nu":"","ischeck":"0","condition":"","com":"","status":"201","state":"0","data":[]}
////{"message":"ok","nu":"220192092873","ischeck":"0","condition":"00","com":"zhongtong","status":"200","s
                */
                if (logisticsDetail1.getStatus().equals("200")) {
                    logisticsMapper.updateByPrimaryKey(logistics);
                } else {
                    logistics = logisticsMapper.selectByPrimaryKey(order.getLogisticsId());
                }
            }

            List<OrderItem> orderItemList = orderItemMapper.getByOrderNoUserId(orderNo, userId);
            OrderVo orderVo = assembleOrderVo(order, orderItemList);
            orderVo.setLogistics(logistics);
            return ServerResponse.createBySuccess(orderVo);
        }
        return ServerResponse.createByErrorMessage("没有找到该订单");
    }


    public void   updateLogisticData(Order order) {
        Logistics logistics = null;
        if (order.getLogisticsId() != null) {
            logistics = logisticsMapper.selectByPrimaryKey(order.getLogisticsId());
            String logisticsDetail = "";
            try {
                logisticsDetail = LogisticsUtil.getLogisticsDetail(logistics.getLogisticsCode(), logistics.getLogisticsNo());
            } catch (Exception e) {
                e.printStackTrace();
            }
            logistics.setLogisticsDetail(logisticsDetail);
            LogisticsDetail logisticsDetail1 = new Gson().fromJson(logisticsDetail, LogisticsDetail.class);
                /*
                status	查询结果状态：
                0：物流单暂无结果，
                1：查询成功，
                2：接口出现异常，
//// {"message":"快递公司参数异常：单号不存在或者已经过期","nu":"","ischeck":"0","condition":"","com":"","status":"201","state":"0","data":[]}
////{"message":"ok","nu":"220192092873","ischeck":"0","condition":"00","com":"zhongtong","status":"200","s
                */
            if (logisticsDetail1.getStatus().equals("200")) {
                logisticsMapper.updateByPrimaryKey(logistics);
            } else {
                logistics = logisticsMapper.selectByPrimaryKey(order.getLogisticsId());
            }
        }

    }

    public void   updateOrderTask(Order order) {

        orderMapper.updateByPrimaryKeySelective(order);

    }



    //  用户个人中心中   订单列表   要分页的
    public ServerResponse<PageInfo> getOrderList(Integer userId ,int pageNum , int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList = orderMapper.selectByUserId(userId);
        PageInfo pageResult = new PageInfo(orderList);//别搞错了是填充得失dao层的 list、
        List<OrderVo> orderVoList = assembleOrderVoList(orderList, userId);//组装一下适合前端展示的view object
        pageResult.setList(orderVoList);//新封装的list只是覆盖一下原来的  适合前端展示
        return ServerResponse.createBySuccess(pageResult);
    }

    public ServerResponse<PageInfo> getOrderlistNeedPay(Integer userId ,int pageNum , int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList = orderMapper.selectByUserIdNeedPay(userId);
        PageInfo pageResult = new PageInfo(orderList);//别搞错了是填充得失dao层的 list、
        List<OrderVo> orderVoList = assembleOrderVoList(orderList, userId);//组装一下适合前端展示的view object
        pageResult.setList(orderVoList);//新封装的list只是覆盖一下原来的  适合前端展示
        return ServerResponse.createBySuccess(pageResult);
    }


    private List<OrderVo> assembleOrderVoList( List<Order> orderList,Integer userId  ){
        List<OrderVo> orderVoList = Lists.newArrayList();
        for(Order order : orderList){
            List<OrderItem> orderItemList = Lists.newArrayList();
            if(userId == null){
                //todo 管理员 查询的是时候  不需要传userId   方法重用  管理员和一般用户一样的
                orderItemList = orderItemMapper.getByOrderNo(order.getOrderNo() );
            }else{
                orderItemList = orderItemMapper.getByOrderNoUserId(order.getOrderNo(),userId);
            }
            OrderVo orderVo = assembleOrderVo(order,orderItemList);
            orderVoList.add(orderVo);
        }
        return orderVoList;
    }



    //backend  后台管理员的service接口  获取订单列表
    public ServerResponse<PageInfo> manageList(int pageNum , int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList = orderMapper.selectAllOrder();
        PageInfo pageResult = new PageInfo(orderList);//别搞错了是填充得失dao层的 list、
        List<OrderVo> orderVoList = assembleOrderVoList(orderList,null);//组装一下适合前端展示的view object
        pageResult.setList(orderVoList);//新封装的list只是覆盖一下原来的  适合前端展示
        return ServerResponse.createBySuccess(pageResult);
    }

    public List<Order> getOrderListTask(){

        List<Order> orderList = orderMapper.selectAllOrder();

        return orderList;
    }


    /**
     * https://www.kuaidi100.com/openapi/api_post.shtml
     *
     com	物流公司编号
     nu	物流单号
     time	每条跟踪信息的时间
     context	每条跟综信息的描述
     state	快递单当前的状态 ：　
     0：在途，即货物处于运输过程中；
     1：揽件，货物已由快递公司揽收并且产生了第一条跟踪信息；
     2：疑难，货物寄送过程出了问题；
     3：签收，收件人已签收；
     4：退签，即货物由于用户拒签、超区等原因退回，而且发件人已经签收；
     5：派件，即快递正在进行同城派件；
     6：退回，货物正处于退回发件人的途中；
     该状态还在不断完善中，若您有更多的参数需求，欢迎发邮件至 kuaidi@kingdee.com 提出。
     status	查询结果状态：
     0：物流单暂无结果，
     1：查询成功，
     2：接口出现异常，
     message	无意义，请忽略
     condition	无意义，请忽略
     ischeck	无意义，请忽略
     */
    //管理员接口   获取订单详情
    public ServerResponse<OrderVo> manageDetail(Long orderNo){
        Order order = orderMapper.selectByOrderNo(orderNo);
        Logistics logistics = null;
        if(order != null){
            if(  order.getLogisticsId() != null){
                logistics = logisticsMapper.selectByPrimaryKey(order.getLogisticsId());
                String logisticsDetail = "";
                try {
                    logisticsDetail = LogisticsUtil.getLogisticsDetail(logistics.getLogisticsCode(), logistics.getLogisticsNo());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                logistics.setLogisticsDetail(logisticsDetail);
                LogisticsDetail logisticsDetail1 = new Gson().fromJson(logisticsDetail, LogisticsDetail.class);
                /*
                status	查询结果状态：
                0：物流单暂无结果，
                1：查询成功，
                2：接口出现异常，
//// {"message":"快递公司参数异常：单号不存在或者已经过期","nu":"","ischeck":"0","condition":"","com":"","status":"201","state":"0","data":[]}
////{"message":"ok","nu":"220192092873","ischeck":"0","condition":"00","com":"zhongtong","status":"200","s
                */
                if(logisticsDetail1.getStatus().equals("200") ){
                    logisticsMapper.updateByPrimaryKey(logistics);
                }else{
                    logistics = logisticsMapper.selectByPrimaryKey(order.getLogisticsId());
                }

            }

            List<OrderItem> orderItemList = orderItemMapper.getByOrderNo(orderNo);
            OrderVo orderVo = assembleOrderVo(order,orderItemList);
            orderVo.setLogistics(logistics);
            return ServerResponse.createBySuccess(orderVo);
        }
        return ServerResponse.createByErrorMessage("订单不存在");
    }


    // //后台  按订单号查找订单详情    管理员使用
    //一期这里暂时使用精确匹配   不过一期先做分页（为二期做准备）   二期多条件模糊查询
    public ServerResponse<PageInfo> manageSearch(Long orderNo,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        Order order = orderMapper.selectByOrderNo(orderNo);//精确匹配   只能找到一个   但是为了 配合二期这里做了分页
        if(order != null){
            List<OrderItem> orderItemList = orderItemMapper.getByOrderNo(orderNo);
            OrderVo orderVo = assembleOrderVo(order, orderItemList);
            PageInfo pageResult = new PageInfo(Lists.newArrayList(order));//必须要list  转换一下
            pageResult.setList(Lists.newArrayList(orderVo));
            return ServerResponse.createBySuccess(pageResult);
        }
        return ServerResponse.createByErrorMessage("订单不存在");
    }

    // //后台  按订单号查找订单详情    管理员使用
    //一期这里暂时使用精确匹配   不过一期先做分页（为二期做准备）   二期多条件模糊查询
    public ServerResponse<PageInfo> manageSearchByUsername(String username,int pageNum,int pageSize){

        PageHelper.startPage(pageNum,pageSize);
        User user = userMapper.selectByUsername(username);
        if(user != null){
            List<Order> orderList = orderMapper.selectByUserId(user.getId());
            if(orderList.size()>0){
                PageInfo pageResult = new PageInfo(orderList);//别搞错了是填充得失dao层的 list、
                List<OrderVo> orderVoList = assembleOrderVoList(orderList, user.getId());//组装一下适合前端展示的view object
                pageResult.setList(orderVoList);//新封装的list只是覆盖一下原来的  适合前端展示
                return ServerResponse.createBySuccess(pageResult);
            }
            return ServerResponse.createByErrorMessage("该用户没有订单");
        }
        return ServerResponse.createByErrorMessage("不存在该用户");

    }




    /**
     *      管理员   订单发货
     *
     *      返回String 发货成功  或者失败就好了
     */
    public ServerResponse<String> manageSendGoods(Long orderNo , Logistics logistics){
        Order order = orderMapper.selectByOrderNo(orderNo);//查询到这个订单
        if(order != null){
            if(order.getStatus() == Const.OrderStatusEnum.PAID.getCode()){//订单已经付款


                //我们没有要求前端传 快递公司名称  我们自己设置一下就好了
                //String logisticCompany =  LogisticsCompanyEnum.nameOf(paseInt(logistics.getLogisticsCode())).get;
                String logisticCompanyName =  LogisticsCompanyEnum.typeOf(logistics.getLogisticsCode()).getName();
                logistics.setLogisticsCompany(logisticCompanyName);

                //useGeneratedKeys="true" keyProperty="id"   加上这个在sql配置中
                int rowCount =  logisticsMapper.insert(logistics);//带id 了

                //我们在插入数据以后立即拿到  刚刚自动生成的主键
                //默认的sql 返回的是影响的数据的行数   需要修改一下
                //useGeneratedKeys="true" keyProperty="id"   加上这个在sql配置中
                //这样logistics 里面就会有自增主键的id了
                if(rowCount <= 0 ){
                    //Map result = Maps.newHashMap();//这里没有必要创建一个对象 放数据  直接使用map返回
                    //result.put("shippingId",shipping.getId());
                    //return ServerResponse.createBySuccess("地址新建成功",result);
                    return ServerResponse.createByErrorMessage("新建物流信息失败");
                }


                order.setLogisticsId(logistics.getId());
                order.setStatus(Const.OrderStatusEnum.SHIPPED.getCode());//设置订单已发货状态
                order.setSendTime(new Date());//设置发货时间
                orderMapper.updateByPrimaryKeySelective(order);
                return ServerResponse.createBySuccess("发货成功");
            }
        }
        return ServerResponse.createByErrorMessage("订单不存在");
    }


//    public ServerResponse add(Integer userId,Shipping shipping){
//        //我们没有要求前端传 用户的id  我们自己设置一下就好了
//        shipping.setUserId(userId);
//        int rowCount = shippingMapper.insert(shipping);
//
//        //我们和前端预定好 在插入数据以后立即拿到  刚刚自动生成的主键
//        //默认的sql 返回的是影响的数据的行数   需要修改一下
//        //useGeneratedKeys="true" keyProperty="id"   加上这个在sql配置中
//        //这样shipping 里面就会有自增主键的id了
//        if(rowCount > 0 ){
//            Map result = Maps.newHashMap();//这里没有必要创建一个对象 放数据  直接使用map返回
//            result.put("shippingId",shipping.getId());
//            return ServerResponse.createBySuccess("地址新建成功",result);
//        }
//        return ServerResponse.createByErrorMessage("新建地址失败");
//    }





    //backend  后台管理员的service接口  获取订单列表
    public int getOrderCount(){
        List<Order> orderList = orderMapper.selectAllOrder();
        int orderCount = orderList.size();
        return orderCount;
    }

    public User getUser(String username){
        User user = userMapper.selectByUsername(username);
        if(user == null){
            return null;
        }
        return user;
    }



}