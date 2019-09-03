<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<html>
<body>
    <h2>Hello World!</h2>
    springmvc上传文件<br/>
    <form name="form1" action="/manage/product/upload.do" method="post" enctype="multipart/form-data">
        <input type="file" name="upload_file" />
        <input type="submit" value="springMvc上传文件"/>
    </form>
    simditor富文本图片上传<br/>
    <form name="form2" action="/manage/product/richtext_img_upload.do" method="post" enctype="multipart/form-data">
        <input type="file" name="upload_file" />
        <input type="submit" value="simditor富文本图片上传"/>
    </form>

<%--
[16:36:01.356][INFO][com.zjyouth.service.impl.FileServiceImpl][http-nio-8088-exec-21] 开始上传文件，上传文件的文件名:imag04.jpg,上传的路径:E:\web_ssm_ssh_zixue\apache-tomcat-8.5.23-windows-x64\apache-tomcat-8.5.23\webapps\ROOT\upload，新文件名:b5be238c-bd8e-47ba-a275-fa177fec0d05.jpg
[16:36:01.399][INFO][com.zjyouth.utils.FTPUtil][http-nio-8088-exec-21] 开始连接ftp服务器
[16:36:01.449][INFO][com.zjyouth.utils.FTPUtil][http-nio-8088-exec-21] 开始连接ftp服务器,结束上传,上传结果:{}true
--%>
</body>
</html>
