package com.vision.tool.kit.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "aliyun-oss")
public class AliyunOssConfig {

    private String accessKeyId;
    private String accessKeySecret;
    private String internalEndpoint;
    private String externalEndpoint;
    private String ossServerHost;
    private String bucketName;
    private boolean esc;
    
    /**
     * 如果是在阿里云ECS访问OSS。使用阿里云内部域名
     * @return
     */
    public String getEndPoint() {
    	if(esc) {
    		return getInternalEndpoint();
    	}
    	return getExternalEndpoint();
    }
}
