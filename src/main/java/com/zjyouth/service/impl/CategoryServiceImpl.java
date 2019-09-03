package com.zjyouth.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zjyouth.common.ServerResponse;
import com.zjyouth.dao.CategoryMapper;
import com.zjyouth.pojo.Category;
import com.zjyouth.service.ICatetoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2018/6/14.
 */
@Service("iCatetoryService")
public class CategoryServiceImpl implements ICatetoryService {

    //打印日志
    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);


    @Autowired
    private CategoryMapper categoryMapper;

    public ServerResponse addCategory(String categoryName,Integer parentId){
        if(parentId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("添加品类参数错误");
        }

        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);//这个分类是可用的

        int rowCount = categoryMapper.insert(category);
        if(rowCount > 0){
            return ServerResponse.createBySuccess("添加品类成功");
            //return ServerResponse.createBySuccessMessage("添加品类成功");
        }
        return ServerResponse.createByErrorMessage("添加品类失败");
    }


    public ServerResponse updateCategoryName(Integer categroyId,String categoryName){
        if(categroyId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("更新品类参数错误");
        }

        Category category = new Category();
        category.setId(categroyId);
        category.setName(categoryName);

        //要选择有选择性的更新不是全部跟新
        //int rowCount = categoryMapper.updateByPrimaryKey(category);//全部字段更新
        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);//非空字段更新
        if(rowCount > 0 ){
            return ServerResponse.createBySuccess("更新品类名字成功");
        }
        return ServerResponse.createByErrorMessage("更新品类名字失败");
    }



    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer catetoryId){
        List<Category> categoryList = categoryMapper.selectCateroryChildrenByParentId(catetoryId);
        if(CollectionUtils.isEmpty(categoryList)){
            //coll == null || coll.isEmpty();  判null和判空集合
            //空集合也是对的   也可以返回给前端
            logger.info("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(categoryList);
    }


    /**
     *   查询子节点的category信息，并且递归获取所有子节点信息
     *
     *   递归查询本节点的id以及孩子节点的id
     */
    public ServerResponse<List<Integer>> selectCatetoryAndChildrenById(Integer categoryId){

        // Sets.newHashSet();这个是瓜娃提供的方法  还是很实用的
        Set<Category> categorySet = Sets.newHashSet();
        findChildCategory(categorySet,categoryId);

        // Lists.newArrayList();这个是瓜娃提供的方法  还是很实用的
        List<Integer> categoryIdList = Lists.newArrayList();
        if(categoryId != null){
            for(Category categoryItem : categorySet){
                categoryIdList.add(categoryItem.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }



    /**
     *   递归函数  Category 不像基本类类型String，Interger已经重写hashcode和equals方法
     *   要自己重写一下
     *
     *    Set<Category> 去重复
     *    set集合categroy（重写hashcode和equals方法）
     *
     *    equals 和hashcode方法的关系是这样的 如果两个对象相同
     *    就用equals返回true 并且这两个对象的hashcode值必须相同
     *
     *     但是如果只是hashcode相同这两个对象不一定相同
     *
     *     所以set集合的对象要去重复一定要重写equals和hashcode两个方法
     *
     *     所谓的递归算法就是自己调用自己  递归算法，算出子节点
     */
    private Set<Category> findChildCategory(Set<Category> categorySet,Integer categoryId){

        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category != null){
            categorySet.add(category);
        }
        //查找子节点，递归算法一定要有一个退出的条件
        List<Category> categories = categoryMapper.selectCateroryChildrenByParentId(categoryId);
        //categories   我们使用的是mabatis所以我们拿到一定不会是null  不需要判断了
        for(Category categoryItem : categories ){//循环递归
            //结束条件是 categories  为空
              findChildCategory(categorySet,categoryItem.getId());//自己调用自己
        }
        return categorySet;//递归结束返回结果
    }



}