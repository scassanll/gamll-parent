package com.atguigu.gmall.product.config.minio;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * minio的Bean
 */
@ConfigurationProperties(prefix = "app.minio")
@Component
@Data
public class MinioProperties {
    String endpoint;
    String accessKey;
    String secreKey;
    String bucketName;
}
