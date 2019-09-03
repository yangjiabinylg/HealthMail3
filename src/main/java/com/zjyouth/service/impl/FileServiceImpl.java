package com.zjyouth.service.impl;

import com.google.common.collect.Lists;
import com.zjyouth.service.IFileService;
import com.zjyouth.utils.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by Administrator on 2018/6/15.
 *
 *     处理文件的接口
 */
@Service("iFileService")
public class FileServiceImpl implements IFileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    /**
     *
     * @param file  上传的文件
     * @param path  要保存的位置
     * @return
     */
    public String upload(MultipartFile file,String path){
        String fileName = file.getOriginalFilename();//文件的原始的名字
        //扩展名   abc.jpg     abc.abc.abc.jpg
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);//jpg
        //为什么不能使用上传的名字命名图片  会有同名覆盖的
        //A:abc.jpg
        //B:abc.jag
        String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;//UUID.randomUUID()不会重复的名字
        //日志打印一下
        logger.info("开始上传文件，上传文件的文件名:{},上传的路径:{}，新文件名:{}",fileName,path,uploadFileName);
        File fileDir = new  File(path);
        //文件夹不存在  创建文件夹
        if(!fileDir.exists()){
            //有时候 是没有写的权限的  先确保自己有写的权限
            fileDir.setWritable(true);
            //文件夹不存在  创建文件夹
            //fileDir.mkdir();//只创建当前目录
            fileDir.mkdirs();//创建所有的目录
        }
        //创建文件
        File targetFile = new File(path,uploadFileName);//完整的file包括路径和文件名
        try {
            //springmvc封装的file   图片存放在tomcat目录下面 /webapp/upload/文件夹下面
            file.transferTo(targetFile);//文件已经上传成功   此时图片存放在/webapp/upload/文件夹下面

            //todo  将targetFile上传到我们的FIP服务器上
            FTPUtil.uploadFile(Lists.newArrayList(targetFile)); //上传的ftp服务器上面
            //todo  上传完成后，删除upload下面的文件（tomcat下面 时间长了会很大的）
            targetFile.delete();//tomcat目录下面文件删除   文件夹不删没事

        } catch (IOException e) {
            logger.error("文件上传异常",e);
            return null;
        }
        return targetFile.getName();//返回现在的文件名
    }


//    public static void main(String[] args) {
//        String fileName = "abc.jpg";
//        System.out.println(fileName.substring(fileName.lastIndexOf(".")+1));//jpg
//        String fileName2 = "abc.jpg.abc.jpg";
//        System.out.println(fileName2.substring(fileName2.lastIndexOf(".")));//.jpg
//    }







}