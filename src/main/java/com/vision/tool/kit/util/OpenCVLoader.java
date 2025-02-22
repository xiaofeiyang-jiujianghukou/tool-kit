package com.vision.tool.kit.util;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Slf4j
public class OpenCVLoader {
    public static void loadOpenCVLibrary() {
        // 获取操作系统信息
        String os = System.getProperty("os.name").toLowerCase();
        String libPath = null;

        // 根据操作系统选择不同的本地库目录
        if (os.contains("win")) {
            libPath = "opencv/windows";
        } else {
            libPath = "opencv/linux";
        }

        log.info("Loading OpenCV library: " + libPath);

        // 解压所有本地库文件到临时目录
        try {
            String tempDir = System.getProperty("java.io.tmpdir") + "/opencv";
            File tempLibDir = new File(tempDir);
            if (!tempLibDir.exists()) {
                tempLibDir.mkdirs();
            }

            // 解压所有 .so 文件到临时目录
            extractLibrariesToTemp(libPath, tempLibDir);

            // 加载所有解压后的 .so 文件
            loadLibraries(tempLibDir);
        } catch (IOException | URISyntaxException e) {
            log.info("Failed to load opencv library {} ", e.getMessage(), e);
        }
    }

    public static void extractLibrariesToTemp(String libPath, File tempLibDir) throws IOException, URISyntaxException {

        // 获取目标目录的 URL
        URL url = OpenCVLoader.class.getClassLoader().getResource(libPath);
        if (url == null) {
            log.error("Resource path not found: " + libPath);
            return;
        }

        // 如果资源路径是 JAR 文件中的路径
        if (url.getProtocol().equals("jar")) {
            // 获取 JAR 文件的路径
            String jarFilePath = url.getFile();

            log.info("Extracting jar url Path: " + jarFilePath);

            if (jarFilePath.startsWith("nested:/")) {
                // 如果是 nested:/，则去掉该前缀并正确获取资源文件
                jarFilePath = jarFilePath.substring(8); // 去掉 "nested:/" 部分
            }
            log.info("Extracting jar 2 url Path: " + jarFilePath);
            jarFilePath = jarFilePath.substring(0, jarFilePath.indexOf("!")); // 获取 JAR 文件路径
            log.info("Extracting jar 3 url Path: " + jarFilePath);
            jarFilePath = java.net.URLDecoder.decode(jarFilePath, "UTF-8"); // 解码路径
            log.info("JAR File Path: " + jarFilePath);
            if (SystemUtils.IS_OS_LINUX) {
                jarFilePath = "/" + jarFilePath;
            }
            log.info("JAR File Path: " + jarFilePath);
            try (JarFile jarFile = new JarFile(jarFilePath)) {
                // 获取 JAR 中的资源并解压
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String entryName = entry.getName();

                    // 检查文件是否位于指定的目录下
                    if (entryName.contains(libPath) && !entryName.endsWith("/")) {

                        log.info("Extracting jar entry {}, libPath {}", entryName, libPath);

                        InputStream in = jarFile.getInputStream(entry);
                        String fileName = entryName.substring(entryName.indexOf(libPath) + libPath.length() + 1); // 去掉路径的前缀

                        // 解压文件到临时目录
                        File tempFile = new File(tempLibDir, fileName);
                        tempFile.getParentFile().mkdirs();
                        Files.copy(in, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        in.close();
                        log.info("Extracted " + fileName + " to " + tempFile.getAbsolutePath());

                        /*// 加载库
                        System.load(tempFile.getAbsolutePath());
                        log.info("Loaded library: " + fileName);*/
                    }
                }
            }
        } else {
            // 如果路径不是 JAR 中的资源，直接处理文件系统路径
            Path path = Paths.get(url.toURI());
            File dir = path.toFile();

            if (dir.exists() && dir.isDirectory()) {
                // 遍历该目录下的所有文件
                for (File file : dir.listFiles()) {
                    String fileName = file.getName();
                    try (InputStream in = new FileInputStream(file)) {
                        // 创建临时文件
                        File tempFile = new File(tempLibDir, fileName);
                        tempFile.getParentFile().mkdirs(); // 创建目录结构
                        tempFile.deleteOnExit();
                        Files.copy(in, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        log.info("Extracted " + fileName + " to " + tempFile.getAbsolutePath());

                        /*// 加载本地库
                        System.load(tempFile.getAbsolutePath());
                        log.info("Loaded library: " + fileName);*/
                    } catch (IOException e) {
                        log.error("Failed to extract or load " + file.getName(), e);
                    }
                }
            } else {
                log.error("The directory does not exist or is not a directory: " + dir.getAbsolutePath());
            }
        }
    }

    private static void loadLibraries(File tempLibDir) {
        log.info("Loading libraries from " + tempLibDir.getAbsolutePath());
        // 获取临时目录下所有的 .so 文件并加载
        File[] files = tempLibDir.listFiles();
        if (files != null) {
            List<File> failedFiles = new ArrayList<>();
            for (File file : files) {
                try {
                    log.info("Loading file " + file.getAbsolutePath());
                    // 加载 .so 文件
                    System.load(file.getAbsolutePath());
                    log.info("Loaded: {} success", file.getName());
                } catch (UnsatisfiedLinkError e) {
                    failedFiles.add(file);
                }
            }

            loadFailed(failedFiles);
        }
    }

    private static void loadFailed(List<File> failedFiles) {
        List<File> result = new ArrayList<>();
        for (File failedFile : failedFiles) {
            try {
                log.info("Loading file " + failedFile.getAbsolutePath());
                // 加载 .so 文件
                System.load(failedFile.getAbsolutePath());
                log.info("Loaded: {} success", failedFile.getName());
            } catch (UnsatisfiedLinkError e) {
                result.add(failedFile);
            }
        }
        if (CollectionUtil.isNotEmpty(result)) {
            loadFailed(result);
        }
    }
}
