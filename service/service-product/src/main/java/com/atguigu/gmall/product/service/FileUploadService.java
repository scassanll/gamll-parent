package com.atguigu.gmall.product.service;

import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    String upload(MultipartFile file) throws InvalidPortException, InvalidEndpointException, Exception;
}
