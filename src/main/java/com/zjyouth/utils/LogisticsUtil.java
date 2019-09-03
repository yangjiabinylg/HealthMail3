package com.zjyouth.utils;



import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

/**
 * Created by Administrator on 2018/8/7.
 */
public class LogisticsUtil {
    public static final String QUERYURL = "http://www.kuaidi100.com/query?";

    public static String setUrl(String logisticsCode, String logisticsNo) {
        String temp = String.valueOf(Math.random());
        StringBuilder sb = new StringBuilder(QUERYURL);
        sb.append("tpye=").append(logisticsCode).append("&");
        sb.append("postid=").append(logisticsNo).append("&");
        sb.append("temp=").append(temp);
        return sb.toString();
    }

    public static String queryData(String logisticsCode, String logisticsNo) {
        String line = "";
        String temp = String.valueOf(Math.random());
        String url = "http://www.kuaidi100.com/query?type=" + logisticsCode + "&postid=" + logisticsNo + "&temp=" + temp ;
        return getLogisticDetail(url);

    }


    public static String getLogisticDetail(String url) {
        CloseableHttpClient client;
        client = HttpClients.createDefault();

        HttpGet get = new HttpGet(url);
        HttpResponse response;
        try {
            response = client.execute(get);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instreams = entity.getContent();
                String str =  convertStreamToString(instreams);
                get.abort();
                return str;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String convertStreamToString(InputStream is) {
        StringBuilder sb1 = new StringBuilder();
        byte[] bytes = new byte[4096];
        int size;

        try {
            while ((size = is.read(bytes)) > 0) {
                String str = new String(bytes, 0, size, "UTF-8");
                sb1.append(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb1.toString();
    }





    public static String getLogisticsDetail(String logisticsCode, String logisticsNo) throws Exception {
        String logisticsDetail = queryData(logisticsCode, logisticsNo);
        return   logisticsDetail ;
    }


//    public static void main(String[] args) throws HttpException, IOException {
////        System.out.println(queryData("yunda", "3910202477100"));
////        {"message":"快递公司参数异常：单号不存在或者已经过期","nu":"","ischeck":"0","condition":"","com":"","status":"201","state":"0","data":[]}
////{"message":"ok","nu":"220192092873","ischeck":"0","condition":"00","com":"zhongtong","status":"200","state":"0","data":[{"time":"2018-08-07 14:57:08","ftime":"2018-08-07 14:57:08","context":"【嘉兴市】 快件离开 【杭州中转部】 发往 【滨江东部】","location":"杭州中转部"},{"time":"2018-08-07 14:51:10","ftime":"2018-08-07 14:51:10","context":"【嘉兴市】 快件到达 【杭州中转部】","location":"杭州中转部"},{"time":"2018-08-05 19:18:37","ftime":"2018-08-05 19:18:37","context":"【哈尔滨市】 快件离开 【哈尔滨中转】 发往 【杭州中转部】","location":"哈尔滨中转"},{"time":"2018-08-05 19:14:31","ftime":"2018-08-05 19:14:31","context":"【哈尔滨市】 快件到达 【哈尔滨中转】","location":"哈尔滨中转"},{"time":"2018-08-05 18:02:06","ftime":"2018-08-05 18:02:06","context":"【哈尔滨市】 快件离开 【哈尔滨透笼】 发往 【杭州中转部】","location":"哈尔滨透笼"},{"time":"2018-08-05 17:24:24","ftime":"2018-08-05 17:24:24","context":"【哈尔滨市】 【哈尔滨透笼】（0451-84674244） 的 于涛 （18745127530） 已揽收","location":"哈尔滨透笼"}]}
////        String uuu = queryData("zhongtong", "220192092873");
////        System.out.println(uuu);
////        System.out.println(queryData("zhongtong", "220192092873"));
//        System.out.println(getLogisticsDetail("zhongtong", "220192092873"));
//    }

}

/*
http://www.kuaidi100.com/query?type=yunda&postid=220192092873&temp= ;
{"message":"快递公司参数异常：单号不存在或者已经过期","nu":"","ischeck":"0","condition":"","com":"","status":"201","state":"0","data":[]}


http://www.kuaidi100.com/query?type=zhongtong&postid=220192092873&temp= ;
{"message":"ok","nu":"220192092873","ischeck":"0","condition":"00","com":"zhongtong","status":"200","state":"0","data":[{"time":"2018-08-08 08:00:54","ftime":"2018-08-08 08:00:54","context":"【杭州市】 【滨江东部】 的侯鹏13705819219（13705819219） 正在第1次派件, 请保持电话畅通,并耐心等待","location":""},{"time":"2018-08-08 05:36:52","ftime":"2018-08-08 05:36:52","context":"【杭州市】 快件到达 【滨江东部】","location":""},{"time":"2018-08-07 14:57:08","ftime":"2018-08-07 14:57:08","context":"【嘉兴市】 快件离开 【杭州中转部】 发往 【滨江东部】","location":""},{"time":"2018-08-07 14:51:10","ftime":"2018-08-07 14:51:10","context":"【嘉兴市】 快件到达 【杭州中转部】","location":""},{"time":"2018-08-05 19:18:37","ftime":"2018-08-05 19:18:37","context":"【哈尔滨市】 快件离开 【哈尔滨中转】 发往 【杭州中转部】","location":""},{"time":"2018-08-05 19:14:31","ftime":"2018-08-05 19:14:31","context":"【哈尔滨市】 快件到达 【哈尔滨中转】","location":""},{"time":"2018-08-05 18:02:06","ftime":"2018-08-05 18:02:06","context":"【哈尔滨市】 快件离开 【哈尔滨透笼】 发往 【杭州中转部】","location":""},{"time":"2018-08-05 17:24:24","ftime":"2018-08-05 17:24:24","context":"【哈尔滨市】 【哈尔滨透笼】（0451-84674244） 的 于涛 （18745127530） 已揽收","location":""}]}




  */