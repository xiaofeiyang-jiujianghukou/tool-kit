package com.vision.tool.kit.util;

import cn.hutool.core.lang.UUID;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

@Slf4j
@Service
public class ImageCompressor {

    @Resource
    private OssUtils ossUtils;

    public String compressImage(String imagePath, float quality) throws Exception {
        BufferedImage inputImage;
        File inputFile = null;
        long fileSize = 0;

        // 判断输入路径是URL还是本地文件路径
        if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
            URL url = new URL(imagePath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.connect();
            fileSize = connection.getContentLengthLong();
            inputImage = ImageIO.read(url);
        } else {
            inputFile = new File(imagePath);
            if (!inputFile.exists()) {
                throw new IOException("File not found: " + imagePath);
            }
            inputImage = ImageIO.read(inputFile);
        }

        /*// 检查文件大小是否超过 2MB
        if (fileSize > 0 && fileSize <= 2 * 1024 * 1024) {
            System.out.println("图片小于 2MB，无需压缩。");
            return imagePath;
        }*/

        log.info("图片压缩前，图片大小: {}", fileSize);

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) {
            throw new IllegalStateException("No writers found for format: jpg");
        }
        ImageWriter writer = writers.next();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageOutputStream ios = new MemoryCacheImageOutputStream(outputStream);
        writer.setOutput(ios);

        ImageWriteParam param = writer.getDefaultWriteParam();
        if (param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);
        }

        log.info("inputImage colorModel {}", inputImage.getColorModel());

        writer.write(null, new IIOImage(inputImage, null, null), param);
        ios.close();
        writer.dispose();

        String fileUrl = ossUtils.uploadFromStream(new ByteArrayInputStream(outputStream.toByteArray()), UUID.randomUUID().toString() + ".jpg");

        log.info("图片压缩后，图片大小: {}", outputStream.size());
        return fileUrl;
    }

    public String compressImage2(String imagePath, float quality) throws Exception {
        // 读取输入图像（支持本地路径或 URL）
        BufferedImage inputImage;
        InputStream inputStream;
        if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
            inputStream = new URL(imagePath).openStream();
        } else {
            inputStream = new FileInputStream(imagePath);
        }

        try (inputStream) {
            inputImage = ImageIO.read(inputStream);
            if (inputImage == null) {
                throw new IOException("无法读取图像: " + imagePath);
            }
        }

        // === 转换为 RGB（去除 alpha / ICC / CMYK）===
        BufferedImage rgbImage = new BufferedImage(
                inputImage.getWidth(),
                inputImage.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );
        Graphics2D g = rgbImage.createGraphics();
        g.setColor(Color.WHITE); // 用白色填充透明背景
        g.fillRect(0, 0, rgbImage.getWidth(), rgbImage.getHeight());
        g.drawImage(inputImage, 0, 0, null);
        g.dispose();

        // 获取 JPEG Writer
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) {
            throw new IllegalStateException("找不到 JPEG 写入器");
        }
        ImageWriter writer = writers.next();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageOutputStream ios = new MemoryCacheImageOutputStream(outputStream);
        writer.setOutput(ios);

        ImageWriteParam param = writer.getDefaultWriteParam();
        if (param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);
        }

        log.info("inputImage colorModel: {}", inputImage.getColorModel());

        // ✅ 写入转换后的 rgbImage，而不是原始 inputImage！
        writer.write(null, new IIOImage(rgbImage, null, null), param);
        ios.close();
        writer.dispose();

        // 上传 OSS
        String fileUrl = ossUtils.uploadFromStream(
                new ByteArrayInputStream(outputStream.toByteArray()),
                UUID.randomUUID().toString() + ".jpg"
        );

        log.info("图片压缩后，图片大小: {} bytes", outputStream.size());
        return fileUrl;
    }

    public String compressImage3(String imagePath, float quality) throws Exception {
        // 读取输入图像（支持本地路径或 URL）
        BufferedImage inputImage;
        InputStream inputStream;
        if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
            inputStream = new URL(imagePath).openStream();
        } else {
            inputStream = new FileInputStream(imagePath);
        }

        try (inputStream) {
            inputImage = ImageIO.read(inputStream);
            if (inputImage == null) {
                throw new IOException("无法读取图像: " + imagePath);
            }
        }

        // 转换为 RGB（去除 alpha / ICC / CMYK）
        BufferedImage rgbImage = new BufferedImage(
                inputImage.getWidth(),
                inputImage.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );
        Graphics2D g = rgbImage.createGraphics();
        g.setColor(Color.WHITE); // 用白色填充透明背景
        g.fillRect(0, 0, rgbImage.getWidth(), rgbImage.getHeight());
        g.drawImage(inputImage, 0, 0, null);
        g.dispose();

        // 获取 JPEG Writer
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) {
            throw new IllegalStateException("找不到 JPEG 写入器");
        }
        ImageWriter writer = writers.next();

        // 本地输出文件路径
        File outputFile = new File("D:\\Desktop\\result.jpg");
        if (!outputFile.getParentFile().exists()) {
            outputFile.getParentFile().mkdirs();
        }

        try (ImageOutputStream ios = ImageIO.createImageOutputStream(outputFile)) {
            writer.setOutput(ios);

            ImageWriteParam param = writer.getDefaultWriteParam();
            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(quality); // 质量 0~1，值越小压缩越狠
            }

            log.info("inputImage colorModel: {}", inputImage.getColorModel());
            writer.write(null, new IIOImage(rgbImage, null, null), param);
        } finally {
            writer.dispose();
        }

        log.info("✅ 图片压缩完成，已保存到: {}", outputFile.getAbsolutePath());
        return outputFile.getAbsolutePath();
    }
}

