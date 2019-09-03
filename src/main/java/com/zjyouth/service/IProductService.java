package com.zjyouth.service;

import com.github.pagehelper.PageInfo;
import com.zjyouth.common.ServerResponse;
import com.zjyouth.pojo.Product;
import com.zjyouth.vo.ProductDetailVo;

import java.util.Map;

/**
 * Created by Administrator on 2018/6/15.
 */
public interface IProductService {


    ServerResponse saveOrUpdateProduct(Product product);

    ServerResponse<String> setSaleStatus(Integer productId ,Integer status);

    ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);

    ServerResponse<PageInfo> getProductList(int pageNum, int pageSize);

    ServerResponse<PageInfo> searchProduct(String productName,Integer productId,int pageNum,int pageSize);

    /**
     *     前台用户和  后台管理员 查看商品详情 差不多就是 多了一个要判断商品是不是在售状态
     */
    ServerResponse<ProductDetailVo> getProductDetail(Integer productId);

    /**
     *    前端用户  模糊条件查询
     */
    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword,Integer categoryId,
                                                                int pageNum,int pageSize,String orderBy);

    /**
     *    App用户  模糊条件查询
     */
    public ServerResponse<Map<String, Object>> getProductByKeywordCategoryApp(String keyword, Integer categoryId,
                                                                              int pageNum, int pageSize, String orderBy);


    int getProductCount();
}