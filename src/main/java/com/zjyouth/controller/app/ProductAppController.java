package com.zjyouth.controller.app;

import com.github.pagehelper.PageInfo;
import com.zjyouth.common.ServerResponse;
import com.zjyouth.service.IProductService;
import com.zjyouth.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Created by Administrator on 2018/6/16.
 *
 *
 *    前台用户查看产品的接口
 */
@Controller
@RequestMapping("/app/product/")
public class ProductAppController {

    @Autowired
    private IProductService iProductService;

    /**
     *       获取商品详情    前台用户查看产品  和管理员查看产品详情差不多
     *       不同之处是要判断  产品是不是上架了
     */
    @RequestMapping("detail.do")
    @ResponseBody
//    public ServerResponse<ProductDetailVo> detail(HttpSession session, Integer productId ){
    public ServerResponse<ProductDetailVo> detail(  Integer productId ){
        //所有的后台管理都是要强制登录的
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //if(user == null){
        //    return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        //}
        return iProductService.getProductDetail(productId);
        /*
            if(iUserService.checkAdminRole(user).isSuccess()){
                //填充业务   属性读取 vo的建立
                return iProductService.manageProductDetail(productId);
            }else{
                return ServerResponse.createByErrorMessage("非管理员 ,无权限操作");
            }
        */
    }

    /**
     *     前端用户查询产品列表   也是要分页的   按照关键字查找  不是必须的
     *
     *       获取产品列表   要做分页 参数有
     *       mybatis分页插件  通过该aop分页插件  实现分页
     *       com.github.pagehelper
     *
     *       不传入参数就默认是   第一页    一页10条记录
     *
     *       多条件查询  可以是产品name  可以是productId   默认还是有分页  按照默认id排序
     */
//    @RequestMapping("list.do")
//    @ResponseBody
//    public ServerResponse<PageInfo> list(@RequestParam(value = "keyword",required = false) String keyword,
//                                          @RequestParam(value = "categoryId",required = false) Integer categoryId,
//                                          @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
//                                          @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize,
//                                          @RequestParam(value = "orderBy",defaultValue = "") String orderBy,
//                                          HttpSession session ){
//
//
//        //所有的后台管理都是要强制登录的
//        // User user = (User) session.getAttribute(Const.CURRENT_USER);
//        //if(user == null){
//        //     return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员");
//        //}
//       return iProductService.getProductByKeywordCategory(  keyword,  categoryId,   pageNum,  pageSize,  orderBy);
//    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<Map<String, Object>> list(@RequestParam(value = "keyword",required = false) String keyword,
                                                    @RequestParam(value = "categoryId",required = false) Integer categoryId,
                                                    @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                                    @RequestParam(value = "pageSize",defaultValue = "8") Integer pageSize,
                                                    @RequestParam(value = "orderBy",defaultValue = "") String orderBy,
                                                    HttpSession session ){


        //所有的后台管理都是要强制登录的    categoryId=100001
        // User user = (User) session.getAttribute(Const.CURRENT_USER);
        //if(user == null){
        //     return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员");
        //}
        return iProductService.getProductByKeywordCategoryApp(  keyword,  categoryId,   pageNum,  pageSize,  orderBy);
    }





}