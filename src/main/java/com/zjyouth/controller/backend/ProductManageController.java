package com.zjyouth.controller.backend;

import com.google.common.collect.Maps;
import com.zjyouth.common.Const;
import com.zjyouth.common.ResponseCode;
import com.zjyouth.common.ServerResponse;
import com.zjyouth.pojo.Product;
import com.zjyouth.pojo.User;
import com.zjyouth.service.IFileService;
import com.zjyouth.service.IProductService;
import com.zjyouth.service.IUserService;
import com.zjyouth.utils.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Created by Administrator on 2018/6/15.
 */
@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IProductService iProductService;

    @Autowired
    private IFileService iFileService;

    /**
     *    这里要使用分页
     *
     *    这个接口可以更新产品  也可以新增产品  1个接口搞定
     */
    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse productSave(HttpSession session, Product product){
        //所有的后台管理都是要强制登录的
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //填充我们增加产品的业务逻辑
            return iProductService.saveOrUpdateProduct(product);
        }else{
            return ServerResponse.createByErrorMessage("非管理员 ,无权限操作");
        }
    }





    /**
     *      商品上架下架   修改status
     */
    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session,Integer productId ,Integer status){
        //所有的后台管理都是要强制登录的
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode()
                    ,"用户未登录，请登录管理员");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //填充我们增加产品的业务逻辑
            return iProductService.setSaleStatus(productId,status);
        }else{
            return ServerResponse.createByErrorMessage("非管理员 ,无权限操作");
        }
    }



    /**
     *       获取商品详情
     */
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse getDetail(HttpSession session,Integer productId ){
        //所有的后台管理都是要强制登录的
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //填充业务   属性读取 vo的建立
            return iProductService.manageProductDetail(productId);

        }else{
            return ServerResponse.createByErrorMessage("非管理员 ,无权限操作");
        }
    }



    /**
     *       获取产品列表   要做分页 参数有
     *       mybatis分页插件  通过该aop分页插件  实现分页
     *       com.github.pagehelper
     *
     *       不传入参数就默认是   第一页    一页10条记录
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse getList(HttpSession session,
                                  @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                  @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize ){
        //所有的后台管理都是要强制登录的
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage
                    (ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //填充业务   属性读取 vo的建立
            //return iProductService.manageProductDetail(productId);
            return iProductService.getProductList(pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("非管理员 ,无权限操作");
        }
    }




    /**
     *       获取产品列表   要做分页 参数有
     *       mybatis分页插件  通过该aop分页插件  实现分页
     *       com.github.pagehelper
     *
     *       不传入参数就默认是   第一页    一页10条记录
     *
     *       多条件查询  可以是产品name  可以是productId   默认还是有分页  按照默认id排序
     */
    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse productSearch(HttpSession session,String productName,Integer productId,
                                        @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                        @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize ){
        //所有的后台管理都是要强制登录的
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage
                    (ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //填充业务
            //return iProductService.manageProductDetail(productId);
            //return iProductService.getProductList(pageNum,pageSize);
            return iProductService.searchProduct(productName,productId,pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("非管理员 ,无权限操作");
        }
    }


    /**
     *    文件上传  上传产品图片
     *    SpringMVC文件上传工具 MultipartFile
     */
    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse upload(HttpSession session,
               @RequestParam(value = "upload_file" ,required = false) MultipartFile file
               , HttpServletRequest request){

        //所有的后台管理都是要强制登录的
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //填充业务
            //return iProductService.manageProductDetail(productId);
            //return iProductService.getProductList(pageNum,pageSize);
            //return iProductService.searchProduct(productName,productId,pageNum,pageSize);

            //要保存的路径
            String path = request.getSession().getServletContext().getRealPath("upload");//tomcat下面的目录
            path = "c:\\mmmaill\\upload\\";

            //webapp/upload   tomcat文件夹中创建文件  这个文件夹是代码创建  不是手工创建  这个是临时文件夹
            String targetFileName = iFileService.upload(file,path);//上传文件  先放tomcat目录下面  再上传ftp服务器  再删除tomcat下面的文件
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;//ftp服务器上面 图片的url地址
            String urlHttp = PropertiesUtil.getProperty("ftp.server.http.prefix.http")+targetFileName;//http服务器上面 图片的url地址
//            String urlCan="http://127.0.0.1:88/E%3A/mmall_all_file/Ftp/ftpfile/img/611a37fa-8486-4bd1-974c-32edae1fc467.jpg";
            Map fileMap = Maps.newHashMap();
            fileMap.put("uri",targetFileName);
            fileMap.put("url",url);// ftp://mmallftp:ftppassword@127.0.0.1:21/img/bebee25e-8c34-487f-8347-f93017d36859.jpg
            //urlHttp ="http://127.0.0.1:88/E%3A/mmall_all_file/Ftp/ftpfile/img/0004DHA.jpg";
//            urlHttp = urlHttp.toString();
//            System.out.println(urlHttp);
//            urlHttp="http://127.0.0.1:88/E%3A/mmall_all_file/Ftp/ftpfile/img/cdd0db6e-8532-4a42-a97f-4a9914d53e24.jpg";

            System.out.println(urlHttp);
            fileMap.put("urlHttp",urlHttp);//"ftp://mmallftp:ftppassword@127.0.0.1:21/img/bebee25e-8c34-487f-8347-f93017d36859.jpg"
//            fileMap.put("urlCan",urlCan);
            return ServerResponse.createBySuccess(fileMap);
        }else{
            return ServerResponse.createByErrorMessage("非管理员 ,无权限操作");
        }

    }
    /**
     *     Springmvc 关于文件上传的配置
     *
     *     dispatcher-servlet.xml
     *
             文件上传   直接使用SpringMVC提供的multipart这个工具就好了
             <bean id="multipartResolver" class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
                 <property name="maxUploadSize" value="10485760"/>    //  单位字节   上传的最大字节10m
                 <property name="maxInMemorySize" value="4096" />     // 单位字节   最大内存4M  块的大小
                 <property name="defaultEncoding" value="UTF-8"></property>  // 默认编码
             </bean>

            上面这个配置  加上  MultipartFile file   这个完成上传文件的操作


           index.jsp 测试一下我们的接口
     *
     */





    /**
     *    富文本图片上传  和图片文件上传差不多  但是返回的json必须按照指定的格式
     *    因为前端使用的技术  是第三方的架构  必须按照它的json格式传递
     *
     *    百度搜索simditor
     *    http://simditor.tower.im/docs/doc-usage.html
     *
     *        JSON response after uploading complete:   json字符串上传成功的返回
             {
                 "success": true/false,
                 "msg": "error message", # optional（可选的  可以不传递）
                 "file_path": "[real file path]"
             }
     *
     *    文件上传  上传产品图片
     *    SpringMVC文件上传工具 MultipartFile
     */
    @RequestMapping("richtext_img_upload.do")
    @ResponseBody
    //public ServerResponse richtextImgUpload(HttpSession session,@RequestParam(value = "upload_file" ,required = false) MultipartFile file , HttpServletRequest request){
    public Map richtextImgUpload(HttpSession session, @RequestParam(value = "upload_file" ,required = false) MultipartFile file , HttpServletRequest request, HttpServletResponse response){

        //按照simeditor  就使用map了  格式他是死的  simeditor
        Map resultMap = Maps.newHashMap();

        //所有的后台管理都是要强制登录的
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            resultMap.put("success",false);
            resultMap.put("msg","请登录管理员");
            return resultMap;
            //return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员");
        }
        //富文本图片上传中对应返回值有自己的要求，我们使用的是simditor所以按照simditor的要求进行返回
        //JSON response after uploading complete:   json字符串上传成功的返回
        //  {
        //    "success": true/false,
        //    "msg": "error message", # optional（可选的  可以不传递）
        //    "file_path": "[real file path]"
        //  }  我们也没必要  专门封装一个对象直接返回Map就好了
        if(iUserService.checkAdminRole(user).isSuccess()){
            //填充业务
            //return iProductService.manageProductDetail(productId);
            //return iProductService.getProductList(pageNum,pageSize);
            //return iProductService.searchProduct(productName,productId,pageNum,pageSize);

            //要保存的路径
            String path = request.getSession().getServletContext().getRealPath("upload");//tomcat下面的目录
            path = "c:\\mmmaill\\upload\\";



            //webapp/upload   tomcat文件夹中创建文件  这个文件夹是代码创建  不是手工创建  这个是临时文件夹
            String targetFileName = iFileService.upload(file,path);//上传文件  先放tomcat目录下面  再上传ftp服务器  再删除tomcat下面的文件
            if(StringUtils.isBlank(targetFileName)){
                //ftp返回的文件名是空说明  上传失败
                resultMap.put("success",false);
                resultMap.put("msg","上传失败");
                return resultMap;
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;//ftp服务器上面 图片的url地址
            String urlHttp = PropertiesUtil.getProperty("ftp.server.http.prefix.http")+targetFileName;
            resultMap.put("success",true);
            resultMap.put("msg","上传成功");
//            resultMap.put("file_path",url);
            resultMap.put("file_path",urlHttp);
            // 使用第三方的工具 文档要求修改返回头 HttpServletResponse response  只有成功条件下修改
            response.addHeader("Access-Control-Allow-Headers","X-File-Name");//第三方工具要求没办法
            return resultMap;
            //Map fileMap = Maps.newHashMap();
            //fileMap.put("uri",targetFileName);
            //fileMap.put("url",url);
            //return ServerResponse.createBySuccess(fileMap);
        }else{
            resultMap.put("success",false);
            resultMap.put("msg","非管理员 ,无权限操作");
            return resultMap;
            //return ServerResponse.createByErrorMessage("非管理员 ,无权限操作");
        }

    }




}