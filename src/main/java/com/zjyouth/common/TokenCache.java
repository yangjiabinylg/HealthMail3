package com.zjyouth.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Time;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 *      Created by Administrator on 2018/6/14.
 */
public class TokenCache {

        public static final String TOKEN_PREFIX = "token_";//Token前缀

        //日志
        private static Logger logger = LoggerFactory.getLogger(TokenCache.class);


        //瓜娃里面的本地缓存  初始化容量为1000initialCapacity(1000)
        //        .maximumSize(10000)超过10000 LRU算法移除缓存项目(最少使用算法)
        // .expireAfterAccess(12, TimeUnit.HOURS) 缓存有效期12小时
        private static LoadingCache<String ,String > localCache =
            CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000)
            //.expireAfterAccess(12, TimeUnit.HOURS)
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build(new CacheLoader<String, String>() {
                //默认的数据加载实现，当调用get取值的时候，如果key没有对应的值，
                //就调用这个方法进行加载
                @Override
                public String load(String s) throws Exception {
                    return "null";
                }
            });

        public static void setKey(String key,String value){
            localCache.put(key,value);
        }

        public static String getKey(String key){
            String value = null;
            try {
                value = localCache.get(key);
                if("null".equals(value)){
                    return null;
                }
                return value;
            }catch (Exception e){
                logger.error("localCache get error",e);
            }
            return null;
        }

}