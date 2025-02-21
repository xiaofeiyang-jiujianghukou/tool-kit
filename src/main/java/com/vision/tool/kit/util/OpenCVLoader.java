package com.vision.tool.kit.util;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class OpenCVLoader {
    /*private static final String OPENCV_DIR = "/tmp/opencv"; // Linux 临时目录

    public static void loadOpenCV() {
        File opencvDir = new File(OPENCV_DIR);
        if (!opencvDir.exists()) {
            opencvDir.mkdirs(); // 创建临时目录
        }

        try {
            // 1️⃣ 扫描所有 `resources/opencv/` 目录下的 `.so` 文件
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:/opencv/*.so");

            for (Resource resource : resources) {
                String fileName = resource.getFilename();
                if (fileName != null) {
                    File targetFile = new File(opencvDir, fileName);
                    if (!targetFile.exists()) { // 只解压一次
                        extractResource(resource, targetFile);
                    }
                }
            }

            // 2️⃣ 设置 `java.library.path`
            System.setProperty("java.library.path", OPENCV_DIR);

            // 3️⃣ 手动加载所有 `.so` 文件
            File[] soFiles = opencvDir.listFiles((dir, name) -> name.endsWith(".so"));
            if (soFiles != null) {
                for (File soFile : soFiles) {
                    System.load(soFile.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load OpenCV libraries", e);
        }
    }

    private static void extractResource(Resource resource, File targetFile) throws IOException {
        try (InputStream inputStream = resource.getInputStream();
             OutputStream outputStream = Files.newOutputStream(targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING)) {
            FileCopyUtils.copy(inputStream, outputStream);
        }
        targetFile.setExecutable(true);
    }*/
}