package com.zjyouth.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by geely
 */
public class PropertiesAlipayUtil {

    private static Logger logger = LoggerFactory.getLogger(PropertiesAlipayUtil.class);

    private static Properties props;

    /**
     *    util的实现  这个util要在tomcat启动的时候  就要读取到里面的配置
     *    这里要使用静态块  支付宝对接也是一样
     *    静态块优先于普通代码块  普通代码块优先于构造代码块
     */
    static {
        String fileName = "zfbinfo.properties";
        props = new Properties();
        try {//读取文件
            props.load(new InputStreamReader(PropertiesAlipayUtil.class.getClassLoader().getResourceAsStream(fileName),"UTF-8"));
        } catch (IOException e) {
            logger.error("配置文件读取异常",e);
        }
    }

    //静态块优先于普通代码块  普通代码块优先于构造代码块
    //static {}//静态块只执行一次
    //{}//普通代码块
    //public PropertiesUtil() {}//构造代码块


//    public static void main(String[] args) {
//        //Class.forName("com.mysql.jdbc.Drive");//内部也是执行static{}
//        /*
//            public class Driver extends NonRegisteringDriver implements java.sql.Driver {
//                public Driver() throws SQLException {
//                }
//                static {
//                    try {
//                        DriverManager.registerDriver(new Driver());
//                    } catch (SQLException var1) {
//                        throw new RuntimeException("Can\'t register driver!");
//                    }
//                }
//            }
//        */
//    }


    /**   通过key获取配置属性
     *
     * @param key
     * @return
     */
    public static String getProperty(String key){
        String value = props.getProperty(key.trim());
        if(StringUtils.isBlank(value)){
            return null;
        }
        return value.trim();
    }
    /**   通过key获取配置属性  当没有对应的key的value  使用默认value
     *
     * @param key
     * @return
     */
    public static String getProperty(String key,String defaultValue){

        String value = props.getProperty(key.trim());
        if(StringUtils.isBlank(value)){
            value = defaultValue;
        }
        return value.trim();
    }



}
