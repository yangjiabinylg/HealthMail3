package com.zjyouth.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zjyouth.common.Const;
import com.zjyouth.common.ResponseCode;
import com.zjyouth.common.ServerResponse;
import com.zjyouth.dao.CategoryMapper;
import com.zjyouth.dao.ProductMapper;
import com.zjyouth.pojo.Category;
import com.zjyouth.pojo.Product;
import com.zjyouth.service.ICatetoryService;
import com.zjyouth.service.IProductService;
import com.zjyouth.utils.DateTimeUtil;
import com.zjyouth.utils.PropertiesUtil;
import com.zjyouth.vo.ProductDetailVo;
import com.zjyouth.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/6/15.
 */
@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICatetoryService iCatetoryService;

    //对前端来说保存和更新是2个操作
    //但是对后端来说是1个操作  一个接口就够了  但是需要判断一下 insert和update的判断
    public ServerResponse saveOrUpdateProduct(Product product){
        if(product != null){
            //如果详情图是不为空的 取出第一张图赋值给  主图
            if(StringUtils.isNoneBlank(product.getSubImages())){
                String[] subImageArray = product.getSubImages().split(",");
                if(subImageArray.length > 0){
                    product.setMainImage(subImageArray[0]);
                }
            }
            if(product.getId() != null){//有id说明是更新
                //int rowCount =  productMapper.updateByPrimaryKey(product);//前端一定是全部传递过来的  不用选择性更新
                int rowCount =  productMapper.updateByPrimaryKeySelective(product);//前端一定是全部传递过来的  不用选择性更新
                if(rowCount > 0){
                    return ServerResponse.createBySuccess("更新产品成功");
                }
                return ServerResponse.createBySuccess("更新产品失败");
            }else{//没有id说明是插入数据
                int rowCount =  productMapper.insert(product);
                if(rowCount > 0) {
                    return ServerResponse.createBySuccess("新增产品成功");
                }
                return ServerResponse.createBySuccess("新增产品失败");
            }
        }
        return ServerResponse.createByErrorMessage("新增或更新产品采纳数不正确");
    }



    public ServerResponse<String> setSaleStatus(Integer productId ,Integer status){
        if(productId == null || status == null){//传入的都是空值  返回非法参数  参数错误
            return ServerResponse.createByErrorCodeMessage
                    (ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);

        int rowCount = productMapper.updateByPrimaryKeySelective(product);//更新非空字段
        if(rowCount > 0 ){
            return ServerResponse.createBySuccess("修改产品销售状态成功");
        }
        return ServerResponse.createByErrorMessage("修改产品销售状态失败");
    }



    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId){
        if(productId == null  ){//传入的都是空值  返回非法参数  参数错误
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null){
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        //属性读取 vo的建立  实体类就是vo了   后台接口以manager开头
        //VO对象--value object
        //pojo->bo(business object)->vo(view object)
        //pojo->bo(business object)(二期再分类)->vo(view object)
        //pojo->vo(view object)(一期就只用2层了)

        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    private ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());

        //新增2个字段
        //imageHost  从配置文件中获取 这样就不用硬编码到代码中   配置和代码分离
//        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix.http","http://img.happymmall.com/"));
        //parentCategoryId
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category == null){//没查找到
            productDetailVo.setParentCategoryId(0);//默认根节点
        }else{
            productDetailVo.setParentCategoryId(category.getParentId());
        }

        //修改2个字段
        //createTime   sql拿出来是毫秒数  要转换一下
        //updateTime
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));

        return productDetailVo;
    }


    /**
     *      使用mybatis的分页插件
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ServerResponse<PageInfo> getProductList(int pageNum,int pageSize){
        //1.startPage--start讲一下mybatis分页的使用方法
        //2.填充自己的sql查询逻辑
        //3.pageHelper-收尾

        //1.startPage--start
        PageHelper.startPage(pageNum,pageSize);


        //2.填充自己的sql查询逻辑
        /*
            order by id asc; 不能加 ;
            mybatis的分页插件会自动加上 limit 10 offset 1 这部分是mybatis自动加上的

            Product 这么多信息是不用加载的   重写一个对象要显示什么  传递什么
            对productListItem 对象进行封装
        */
        List<Product> productList = productMapper.selectList();//很多字段列表是不需要显示的
        List<ProductListVo> productListVoArrayList = Lists.newArrayList();//封装成
        for(Product productItem : productList){
            //封装一个适合listItem的对象  减少数据传输的数据量
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoArrayList.add(productListVo);
        }


        //3.pageHelper-收尾   这里myabtis 会自动进行分页处理
        // mybatis的分页插件会自动加上 limit 10 offset 1 这部分是mybatis自动加上的
        PageInfo pageResult = new PageInfo(productList);//自动加limit是在这里加的
        //覆盖原来的list
        pageResult.setList(productListVoArrayList);//封装成适合  列表显示的结果  去除多余的字段

        return ServerResponse.createBySuccess(pageResult);

    }

    /**
     *   封装一个适合listItem的对象  减少数据传输的数据量
     * @param product
     * @return
     */
    private ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCategoryId(product.getCategoryId());
//        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix.http","http://img.happymmall.com/"));
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());

        return  productListVo;
    }





    /**    模糊条件查询
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ServerResponse<PageInfo> searchProduct(String productName,Integer productId,int pageNum,int pageSize){
        //1.startPage--start讲一下mybatis分页的使用方法
        //2.填充自己的sql查询逻辑
        //3.pageHelper-收尾

        //1.startPage--start
        PageHelper.startPage(pageNum,pageSize);


        //模糊查询
        if(StringUtils.isNotBlank(productName)){
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }


        //2.填充自己的sql查询逻辑
        List<Product> productList = productMapper.selectByNameAndProductId(productName,productId);//很多字段列表是不需要显示的
        List<ProductListVo> productListVoArrayList = Lists.newArrayList();//封装成
        for(Product productItem : productList){
            //封装一个适合listItem的对象  减少数据传输的数据量
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoArrayList.add(productListVo);
        }


        //3.pageHelper-收尾   这里myabtis 会自动进行分页处理
        // mybatis的分页插件会自动加上 limit 10 offset 1 这部分是mybatis自动加上的
        PageInfo pageResult = new PageInfo(productList);//自动加limit是在这里加的
        pageResult.setList(productListVoArrayList);//封装成适合  列表显示的结果  去除多余的字段
        return ServerResponse.createBySuccess(pageResult);

    }


    /**
     *     前台用户和  后台管理员 查看商品详情 差不多就是 多了一个要判断商品是不是在售状态
     */
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId){
        if(productId == null  ){//传入的都是空值  返回非法参数  参数错误
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode()
                    ,ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null){
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        //属性读取 vo的建立  实体类就是vo了   后台接口以manager开头
        //VO对象--value object
        //pojo->bo(business object)->vo(view object)
        //pojo->bo(business object)(二期再分类)->vo(view object)
        //pojo->vo(view object)(一期就只用2层了)

        //前台用户和  后台管理员 就差个这个
        if(product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()){
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }

        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }






    /**    前端用户  模糊条件查询
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword,Integer categoryId,
                                                                int pageNum,int pageSize,String orderBy){
        //1.startPage--start讲一下mybatis分页的使用方法
        //2.填充自己的sql查询逻辑
        //3.pageHelper-收尾

        if(StringUtils.isBlank(keyword) && categoryId == null){//传入的都是空值  返回非法参数  参数错误
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode()
                    ,ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        //当传入的是 一个大类的categoryId  我们就要递归出下面所有的分类   sql中直接in 这个集合就行了
        List<Integer> categoryIdList = new ArrayList<Integer>();
        if(categoryId != null){
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if(category == null && StringUtils.isBlank(keyword)){
                //没有该分类，并且还没有关键字,这个时候返回一个空的结果集合  不报错
                PageHelper.startPage(pageNum,pageSize);
                List<ProductListVo> productListVoArrayList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVoArrayList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            //递归出这个大类下面的所有的分类和子分类
            categoryIdList = iCatetoryService.selectCatetoryAndChildrenById(category.getId()).getData();
        }

        //搜索的关键字  不为空的话进行拼接  实现模糊查询  //模糊查询
        if(StringUtils.isNotBlank(keyword)){
            keyword = new   StringBuilder().append("%").append(keyword).append("%").toString();
        }

        //1.startPage--start
        PageHelper.startPage(pageNum,pageSize);

        //排序处理
        if(StringUtils.isNotBlank(orderBy)){
            if(Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String[] orderByArray = orderBy.split("_");
                //PageHelper.orderBy("price desc");
                PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
            }
        }


        //2.填充自己的sql查询逻辑
        List<Product> productList = productMapper.selectByNameAndCategoryIds(
                StringUtils.isBlank(keyword) ? null:  keyword
                ,categoryIdList.size() == 0 ? null : categoryIdList);

        List<ProductListVo> productListVoArrayList = Lists.newArrayList();//封装成适合listItem的对象
        for(Product productItem : productList){
            //封装一个适合listItem的对象  减少数据传输的数据量
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoArrayList.add(productListVo);
        }


        //3.pageHelper-收尾   这里myabtis 会自动进行分页处理
        // mybatis的分页插件会自动加上 limit 10 offset 1 这部分是mybatis自动加上的
        PageInfo pageResult = new PageInfo(productList);//自动加limit是在这里加的
        pageResult.setList(productListVoArrayList);//封装成适合  列表显示的结果  去除多余的字段

        return ServerResponse.createBySuccess(pageResult);

    }


    /**    前端用户  模糊条件查询
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ServerResponse<Map<String, java.lang.Object>> getProductByKeywordCategoryApp(String keyword,
                                                Integer categoryId, int pageNum, int pageSize, String orderBy){
        //1.startPage--start讲一下mybatis分页的使用方法
        //2.填充自己的sql查询逻辑
        //3.pageHelper-收尾

        if(StringUtils.isBlank(keyword) && categoryId == null){//传入的都是空值  返回非法参数  参数错误
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode()
                    ,ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        //当传入的是 一个大类的categoryId  我们就要递归出下面所有的分类   sql中直接in 这个集合就行了
        List<Integer> categoryIdList = new ArrayList<Integer>();
        if(categoryId != null){
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if(category == null && StringUtils.isBlank(keyword)){
                //没有该分类，并且还没有关键字,这个时候返回一个空的结果集合  不报错
                PageHelper.startPage(pageNum,pageSize);
                List<ProductListVo> productListVoArrayList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVoArrayList);
                Map<String , java.lang.Object> resultMap = Maps.newHashMap();
                resultMap.put("pageResult",pageInfo);
                List<String > list = new ArrayList<String>();
                list.add(PropertiesUtil.getProperty("ftp.server.http.prefix.http","http://img.happymmall.com/")+"binner001.png");
                list.add(PropertiesUtil.getProperty("ftp.server.http.prefix.http","http://img.happymmall.com/")+"binner002.png");
                list.add(PropertiesUtil.getProperty("ftp.server.http.prefix.http","http://img.happymmall.com/")+"binner003.png");
                list.add(PropertiesUtil.getProperty("ftp.server.http.prefix.http","http://img.happymmall.com/")+"binner004.png");
                list.add(PropertiesUtil.getProperty("ftp.server.http.prefix.http","http://img.happymmall.com/")+"binner005.png");
                resultMap.put("binner",list);
                return ServerResponse.createBySuccess(resultMap);
                //return ServerResponse.createBySuccess(pageInfo);
            }
            //递归出这个大类下面的所有的分类和子分类
            categoryIdList = iCatetoryService.selectCatetoryAndChildrenById(category.getId()).getData();
        }

        //搜索的关键字  不为空的话进行拼接  实现模糊查询  //模糊查询
        if(StringUtils.isNotBlank(keyword)){
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }

        //1.startPage--start
        PageHelper.startPage(pageNum,pageSize);

        //排序处理
        if(StringUtils.isNotBlank(orderBy)){
            if(Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String[] orderByArray = orderBy.split("_");
                //PageHelper.orderBy("price desc");
                PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
            }
        }


        //2.填充自己的sql查询逻辑
        List<Product> productList = productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword)
                ? null:  keyword ,categoryIdList.size() == 0 ? null : categoryIdList);

        List<ProductListVo> productListVoArrayList = Lists.newArrayList();//封装成适合listItem的对象
        for(Product productItem : productList){
            //封装一个适合listItem的对象  减少数据传输的数据量
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoArrayList.add(productListVo);
        }


        //3.pageHelper-收尾   这里myabtis 会自动进行分页处理
        // mybatis的分页插件会自动加上 limit 10 offset 1 这部分是mybatis自动加上的
        PageInfo pageResult = new PageInfo(productList);//自动加limit是在这里加的
        pageResult.setList(productListVoArrayList);//封装成适合  列表显示的结果  去除多余的字段
        Map<String , java.lang.Object> resultMap = Maps.newHashMap();
        resultMap.put("pageResult",pageResult);
        List<String > list = new ArrayList<String>();
        list.add(PropertiesUtil.getProperty("ftp.server.http.prefix.http","http://img.happymmall.com/")+"binner001.png");
        list.add(PropertiesUtil.getProperty("ftp.server.http.prefix.http","http://img.happymmall.com/")+"binner002.png");
        list.add(PropertiesUtil.getProperty("ftp.server.http.prefix.http","http://img.happymmall.com/")+"binner003.png");
        list.add(PropertiesUtil.getProperty("ftp.server.http.prefix.http","http://img.happymmall.com/")+"binner004.png");
        list.add(PropertiesUtil.getProperty("ftp.server.http.prefix.http","http://img.happymmall.com/")+"binner005.png");
        resultMap.put("binner",list);
        return ServerResponse.createBySuccess(resultMap);

    }





    public int getProductCount(){

        List<Product> productList = productMapper.selectList();
        int productCount = productList.size();
        return productCount;

    }


}