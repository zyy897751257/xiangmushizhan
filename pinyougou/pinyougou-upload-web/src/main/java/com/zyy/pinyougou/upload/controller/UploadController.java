package com.zyy.pinyougou.upload.controller;

import com.zyy.pinyougou.common.FastDFSClient;
import com.zyy.pinyougou.entity.Result;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
public class UploadController {

    @RequestMapping("/uploadFile")
    @CrossOrigin(origins = {"http://localhost:9102","http://localhost:9101"},allowCredentials = "true")
    public Result uploadFile(@RequestParam(value = "file") MultipartFile file) {

        try {
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fastdfs_client.conf");
            byte[] bytes = file.getBytes();
            String originalFilename = file.getOriginalFilename();
            String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            String path = fastDFSClient.uploadFile(bytes, extName);// group1/M00/00/05/wKgZhVx_dy-ABPVLAANdC6JX9KA933.jpg
            String realPath="http://192.168.25.133/"+path;
            return new Result(true,realPath);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }
    }
}
