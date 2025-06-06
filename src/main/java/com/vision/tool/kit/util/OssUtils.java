package com.vision.tool.kit.util;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.vision.tool.kit.config.AliyunOssConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.*;

/**
 * Img工具
 */
@Slf4j
@Service("ossUtils")
public class OssUtils {

    @Autowired
    private AliyunOssConfig ossConfig;

    public String uploadFromStream(InputStream inputStream, String fileName) throws Exception {
        Assert.notNull(inputStream, "文件信息不可为空");
        String key = fileName;
        OSSClient ossClient = null;
        try {
            ossClient = new OSSClient(ossConfig.getEndPoint(), new DefaultCredentialProvider(ossConfig.getAccessKeyId(), ossConfig.getAccessKeySecret()), null);
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentLength(inputStream.available());
            ossClient.putObject(ossConfig.getBucketName(), key, inputStream, meta);
            return ossConfig.getOssServerHost().concat("/").concat(key);
        } catch (Exception e) {
            log.warn("ImgOssUtils upload error = {}", e);
            throw new Exception("文件上传失败");
        } finally {
            try {
                ossClient.shutdown();
            } catch (Exception e) {
                log.warn("shutdown,msg: {}", e);
            }
        }
    }

    /**
     * 上传至OSS
     * @param fullFileName 全路径名称
     * @param fileName 文件名
     * @return
     */
    public String uploadLocalFile2Oss(String fullFileName, String fileName) {
        try {
            FileInputStream fileInputStream = new FileInputStream(fullFileName);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            try {
                while ((len = fileInputStream.read(buffer)) > -1) {
                    byteArrayOutputStream.write(buffer, 0, len);
                }
                byteArrayOutputStream.flush();
            } catch (IOException e) {
                log.info("uploadOss read or write error : ", e);
            } finally {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    log.error("file stream shutdown failed.");
                }
            }

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            return uploadFromStream(byteArrayInputStream, fileName);
        } catch (Exception e) {
            log.info("uploadOss error : ", e);
        }
        return "";
    }

    public void deleteFromOss(String path) {
        if (StringUtils.isEmpty(path)) {
            return;
        }
        try {
            String[] splits = path.split("/");
            if (splits.length != 4) {
                return;
            }
            OSSClient ossClient = new OSSClient(ossConfig.getEndPoint(), new DefaultCredentialProvider(ossConfig.getAccessKeyId(), ossConfig.getAccessKeySecret()), null);
            ossClient.deleteObject(ossConfig.getBucketName(), splits[3]);
            ossClient.shutdown();
        } catch (Exception e) {
            log.warn("阿里云文件删除异常,msg", e);
        }
    }

    // http://zm-wechat-data.oss-cn-hangzhou.aliyuncs.com/friendList_wxid_bn9cm10se4dy12
    public String downloadFriendListFileFromOss(String path) {
        if (StringUtils.isEmpty(path)) {
            return null;
        }
        InputStream in = null;
        OSSClient ossClient = null;
        try {
            String[] splits = path.split("/");
            if (splits.length != 4) {
                return null;
            }
            ossClient = new OSSClient(ossConfig.getEndPoint(), new DefaultCredentialProvider(ossConfig.getAccessKeyId(), ossConfig.getAccessKeySecret()), null);
//            ossClient = new OSSClient(ossConfig.getEndPoint(), new DefaultCredentialProvider(ossConfig.getAccessKeyId(), ossConfig.getSk()),null);
            OSSObject ossObject = ossClient.getObject(ossConfig.getBucketName(), splits[3]);
            in = ossObject.getObjectContent();
            if (in == null) {
                return null;
            }
            byte[] bytes = new byte[1024];
            StringBuffer buffer = new StringBuffer();
            int len;
            while ((len = in.read(bytes)) != -1) {
                buffer.append(new String(bytes, 0, len));
            }
            return buffer.toString();
        } catch (Exception e) {
            log.warn("downloadFriendListFileFromOss Error {}", path, e);
            return null;
        } finally {
            if (ossClient != null) {
                try {
                    ossClient.shutdown();
                } catch (Exception e) {
                    log.warn("downloadFriendListFileFromOss shutdownError {}", path, e);
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.warn("downloadFriendListFileFromOss closeError {}", path, e);
                }
            }
        }
    }
}