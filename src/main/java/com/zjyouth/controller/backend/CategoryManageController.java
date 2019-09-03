package com.zjyouth.controller.backend;

import com.zjyouth.common.Const;
import com.zjyouth.common.ResponseCode;
import com.zjyouth.common.ServerResponse;
import com.zjyouth.pojo.User;
import com.zjyouth.service.ICatetoryService;
import com.zjyouth.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2018/6/14.
 *
 *     商品分类是在后台的
 */
@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICatetoryService iCatetoryService;

    /**
     *
     * @param session
     * @param categoryName
     * @param parentId        默认是0   如果前端没有传递参数  就写0 标识查找根节点
     * @return
     */
    @RequestMapping("add_category.do")
    @ResponseBody//返回的是json
    public ServerResponse addCategory(HttpSession session,String categoryName,
                 @RequestParam(value = "parentId",defaultValue = "0") int parentId){

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录");
        }
        //校验一下是否是管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //是管理员
            //增加我们处理分类的逻辑
           return iCatetoryService.addCategory(categoryName,parentId);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }



    @RequestMapping("set_category_name.do")
    @ResponseBody//返回的是json
    public ServerResponse setCategoryName(HttpSession session,Integer categoryId,String categoryName){

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录");
        }
        //校验一下是否是管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //是管理员
            //更新categoryName
            return iCatetoryService.updateCategoryName(categoryId,categoryName);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

    /**
     *    根据传入的categroyId  获取下面节点的信息   平级无递归
     */
    @RequestMapping("get_category.do")
    @ResponseBody//返回的是json
    public ServerResponse getChildrenParallelCategory(HttpSession session ,
                                 @RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录");
        }
        //校验一下是否是管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //是管理员
            //查询子节点的category信息，并且不递归，保持平级
            return iCatetoryService.getChildrenParallelCategory(categoryId );
        }else{
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }




    /**
     *     根据传入的categroyId   获取下面节点的信息   并且递归  获取所有的子节点
     */
    @RequestMapping("get_deep_category.do")
    @ResponseBody//返回的是json
    public ServerResponse getCategoryAndDeepChildrenCatetory(HttpSession session ,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录");
        }
        //校验一下是否是管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //是管理员
            //查询子节点的category信息，并且递归获取所有子节点信息
            //查询当前节点的id和递归子节点的id
            return iCatetoryService.selectCatetoryAndChildrenById(categoryId );
        }else{
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

}