package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.product.config.MinioProperties;
import com.atguigu.gmall.product.service.FileUploadService;
import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

/**
 * 文件上传类
 */
@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Autowired
    MinioClient minioClient;
    @Autowired
    MinioProperties minioProperties;


    /**
     * 后台品牌图片上传
     * upload的实现方法
     * @param file
     * @return
     * @throws Exception
     */

    @Override
    public String upload(MultipartFile file) throws Exception{


        // 判断数据桶是否存在
        boolean gmall = minioClient.bucketExists(minioProperties.getBucketName());

        // 如果不存在 则创建桶
        if (!gmall){
            minioClient.makeBucket(minioProperties.getBucketName());
        }

        // 调用方法进行文件上传并返回文件访问地址

        //获取文件名
        String name = file.getOriginalFilename();
        //得到唯一文件名
        String fileName = UUID.randomUUID().toString().replace("-","") + name;

        //获得文件大小
        long size = file.getSize();
        //设置上传参数
        PutObjectOptions options = new PutObjectOptions(size, -1L);

        //获得文件类型
        String type = file.getContentType();
        //设置文件类型
        options.setContentType(type);

        //获得文件流
        InputStream inputStream = file.getInputStream();
        //上传文件
        minioClient.putObject(minioProperties.getBucketName(),fileName,inputStream,options);

        String url = minioProperties.getEndpoint()+"/"+minioProperties.getBucketName()+"/"+fileName;

        return url;
    }
}
