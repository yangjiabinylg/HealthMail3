package com.zjyouth.service;

import com.zjyouth.common.ServerResponse;
import com.zjyouth.pojo.Category;
import com.zjyouth.vo.CartVo;

import java.util.List;

/**
 * Created by Administrator on 2018/6/14.
 *
 *
 */
public interface ICatetoryService {

    ServerResponse addCategory(String categoryName, Integer parentId);

    ServerResponse updateCategoryName(Integer categroyId,String categoryName);


    /**
     *    按照规范我们是应该先写接口  在写实现类的
     *
     *    但是实际工作中   参数总是要变化   老是要修改接口    还是先写实现  在写接口更实用些
     */
    ServerResponse<List<Category>> getChildrenParallelCategory(Integer catetoryId);
    /**
     *   查询子节点的category信息，并且递归获取所有子节点信息
     *
     *   递归查询本节点的id以及孩子节点的id
     */
    ServerResponse<List<Integer>> selectCatetoryAndChildrenById(Integer categoryId);



}