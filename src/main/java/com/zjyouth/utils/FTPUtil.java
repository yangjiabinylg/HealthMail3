package com.zjyouth.utils;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by geely
 */
public class FTPUtil {

    private static  final Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    private static String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpUser = PropertiesUtil.getProperty("ftp.user");
    private static String ftpPass = PropertiesUtil.getProperty("ftp.pass");

    public FTPUtil(String ip,int port,String user,String pwd){
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pwd = pwd;
    }

    /**
     *    文件批量上传服务器   公有方法
     *
     * @param fileList
     * @return
     * @throws IOException
     */
    public static boolean uploadFile(List<File> fileList) throws IOException {
        //ftpip地址    21是端口 是固定的   用户名  密码
        FTPUtil ftpUtil = new FTPUtil(ftpIp,21,ftpUser,ftpPass);
        logger.info("开始连接ftp服务器");
        boolean result = ftpUtil.uploadFile("img",fileList);//上传到ftp下的img文件夹下面
        logger.info("开始连接ftp服务器,结束上传,上传结果:{}"+result);
        return result;
    }


    //批量上传文件   私有方法
    private boolean uploadFile(String remotePath,List<File> fileList) throws IOException {
        boolean uploaded = true;
        FileInputStream fis = null;
        //连接FTP服务器  还是抽成方法好了
        if(connectServer(this.ip,this.port,this.user,this.pwd)){
            try {
                ftpClient.changeWorkingDirectory(remotePath);//工作目录 需不需要创建文件夹
                ftpClient.setBufferSize(1024);//设置缓冲区
                ftpClient.setControlEncoding("UTF-8");//字符集
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);//文件类型  二进制类型  可以防止乱码
                ftpClient.enterLocalPassiveMode();//打开本地模式
                for(File fileItem : fileList){
                    fis = new FileInputStream(fileItem);//一个一个取出文件流
                    ftpClient.storeFile(fileItem.getName(),fis);//存储文件
                }

            } catch (IOException e) {
                logger.error("上传文件异常",e);
                uploaded = false;//上传失败
                e.printStackTrace();
            } finally {
                fis.close();//关闭流
                ftpClient.disconnect();//释放连接
            }
        }
        return uploaded;//上传成功or失败
    }



    //连接ftp服务器  私有方法
    private boolean connectServer(String ip,int port,String user,String pwd){

        boolean isSuccess = false;
        ftpClient = new FTPClient();//工具类
        try {
            ftpClient.connect(ip);//连接ip
            isSuccess = ftpClient.login(user,pwd);//账户密码
        } catch (IOException e) {
            logger.error("连接FTP服务器异常",e);
        }
        return isSuccess;
    }











    private String ip;
    private int port;
    private String user;
    private String pwd;
    private FTPClient ftpClient;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
