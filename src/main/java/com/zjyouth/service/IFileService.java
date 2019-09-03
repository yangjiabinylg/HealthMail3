package com.zjyouth.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Administrator on 2018/6/15.
 *
 *    处理文件的接口
 */
public interface IFileService {

    String upload(MultipartFile file, String path);
}