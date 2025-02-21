package com.vision.tool.kit.util;

import com.vision.tool.kit.ToolKitApplication;
import com.vision.tool.kit.controller.image.dto.PosterCompositionReqDTO;
import com.vision.tool.kit.controller.image.dto.PosterCompositionRespDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class EdgeDetectionBlankSpace {
    static {
        // System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        if (!SystemUtils.IS_OS_WINDOWS) {
            File opencvLibrary = extractLibrary("opencv/windows/opencv_java490.dll"); // 修改为实际路径
            if (opencvLibrary != null && opencvLibrary.exists()) {
                System.load(opencvLibrary.getAbsolutePath());
                log.info("OpenCV library loaded successfully!");
            } else {
                log.error("Failed to load OpenCV library.");
            }
        } else if (!SystemUtils.IS_OS_LINUX) {
            try {
                File opencvLibrary = extractLibrary("opencv/linux/libopencv_java490.so"); // 修改为实际路径
                if (opencvLibrary != null && opencvLibrary.exists()) {
                    System.load(opencvLibrary.getAbsolutePath());
                    log.info("OpenCV library loaded successfully!");
                } else {
                    log.error("Failed to load OpenCV library.");
                }
            } catch (Exception e) {
                log.error("Failed to load OpenCV library. error {} ", e.getMessage(), e);
            }
        }
    }


    private static File extractLibrary(String libraryName) {
        // 获取文件的输入流
        InputStream in = ToolKitApplication.class.getClassLoader().getResourceAsStream(libraryName);
        if (in == null) {
            System.err.println("Library not found in resources!");
            return null;
        }

        // 创建临时文件
        try {
            String[] split = libraryName.split("/");
            String fileName = split[split.length - 1];
            Path tempPath = Files.createTempFile("", fileName);
            Files.copy(in, tempPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("extractLibrary {} will load", tempPath);
            tempPath.toFile().deleteOnExit(); // 确保程序退出时删除临时文件
            return tempPath.toFile();
        } catch (IOException e) {
            log.info("extractLibrary error ", e);
            return null;
        }
    }

    private static File extractSOLibrary(String libraryName) {
        // 获取文件的输入流
        InputStream in = ToolKitApplication.class.getClassLoader().getResourceAsStream(libraryName);
        if (in == null) {
            System.err.println("Library not found in resources!");
            return null;
        }

        log.info("extractSOLibrary {} will load", libraryName);

        String[] split = libraryName.split("/");

        String fileName = split[1];
        log.info("extractSOLibrary {} will load fileName {}", libraryName, fileName);
        // 创建临时文件
        try {
            Path tempPath = Files.createTempFile(fileName, ".so");
            Files.copy(in, tempPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("extractSOLibrary {} will load", tempPath);
            tempPath.toFile().deleteOnExit(); // 确保程序退出时删除临时文件
            return tempPath.toFile();
        } catch (IOException e) {
            log.info("extractSOLibrary error ", e);
            return null;
        }
    }

    // 将 BufferedImage 转换为 Mat，并保持 RGB 颜色
    public static Mat bufferedImageToMat(BufferedImage bi) {
        Mat mat;
        if (bi.getType() == BufferedImage.TYPE_INT_RGB || bi.getType() == BufferedImage.TYPE_INT_ARGB) {
            // 如果是 RGB 格式，保持为 BGR
            mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
            byte[] data = ((java.awt.image.DataBufferByte) bi.getRaster().getDataBuffer()).getData();
            mat.put(0, 0, data);
            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2BGR);  // 保持 OpenCV 使用的 BGR 顺序
        } else if (bi.getType() == BufferedImage.TYPE_3BYTE_BGR) {
            // OpenCV 默认的 BGR 顺序
            mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
            byte[] data = ((java.awt.image.DataBufferByte) bi.getRaster().getDataBuffer()).getData();
            mat.put(0, 0, data);
        } else {
            //throw new IllegalArgumentException("Unsupported image type: " + bi.getType());
            // OpenCV 默认的 BGR 顺序
            mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
            byte[] data = ((java.awt.image.DataBufferByte) bi.getRaster().getDataBuffer()).getData();
            mat.put(0, 0, data);
        }
        return mat;
    }

    // 将 Mat 转换为 BufferedImage 并保持 RGB 颜色
    public static BufferedImage matToBufferedImage(Mat mat) {
        if (mat == null || mat.empty()) {
            return null;
        }

        Mat rgbMat = new Mat();
        Imgproc.cvtColor(mat, rgbMat, Imgproc.COLOR_BGR2RGB);  // 转换为 RGB 顺序

        int type = BufferedImage.TYPE_3BYTE_BGR;
        if (rgbMat.channels() == 1) {
            type = BufferedImage.TYPE_BYTE_GRAY;
        }

        BufferedImage image = new BufferedImage(rgbMat.cols(), rgbMat.rows(), type);
        byte[] data = new byte[rgbMat.rows() * rgbMat.cols() * (int) rgbMat.elemSize()];
        rgbMat.get(0, 0, data);
        image.getRaster().setDataElements(0, 0, rgbMat.cols(), rgbMat.rows(), data);

        return image;
    }

    public static PosterCompositionRespDTO generatePosterCompositionResult(PosterCompositionReqDTO input) {
        PosterCompositionRespDTO respDTO = new PosterCompositionRespDTO();
        try {
            // 1. 从网络下载海报图片
            //URL posterUrl = new URL("https://va-papers.oss-accelerate.aliyuncs.com/oss-platform/1b1a/3f64/100b/369d427c-b001-46f3-a1b1-3bfa7e5bbaaa.jpg");
            URL posterUrl = new URL(input.getPosterUrl());
            BufferedImage posterImage = ImageIO.read(posterUrl);
            Mat matPoster = bufferedImageToMat(posterImage);

            // 2. 从网络下载二维码图片
            //URL qrCodeUrl = new URL("https://va-pics.oss-accelerate.aliyuncs.com/teach/b312e878-73b3-4f91-b29c-e9576e25fbc4.png");
            URL qrCodeUrl = new URL(input.getQrCodeUrl());
            BufferedImage qrCodeImage = ImageIO.read(qrCodeUrl);
            Mat matQRCode = bufferedImageToMat(qrCodeImage);

            // 3. 将海报图片转为灰度
            Mat grayPoster = new Mat();
            Imgproc.cvtColor(matPoster, grayPoster, Imgproc.COLOR_BGR2GRAY);

            // 4. 边缘检测
            Mat edges = new Mat();
            Imgproc.Canny(grayPoster, edges, 100, 200);

            // 5. 轮廓检测
            List<MatOfPoint> contours = new ArrayList<>();
            Mat hierarchy = new Mat();
            Imgproc.findContours(edges, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

            // 初始化最大正方形区域
            Rect maxSquare = null;
            double maxArea = 0;

            // 6. 寻找最大正方形区域
            for (int i = 0; i < contours.size(); i++) {
                MatOfPoint2f contour2f = new MatOfPoint2f(contours.get(i).toArray());
                double arcLen = Imgproc.arcLength(contour2f, true);
                MatOfPoint2f approxCurve = new MatOfPoint2f();
                Imgproc.approxPolyDP(contour2f, approxCurve, 0.02 * arcLen, true);

                // 判断是否为四边形
                if (approxCurve.total() == 4) {
                    Rect rect = Imgproc.boundingRect(new MatOfPoint(approxCurve.toArray()));

                    // 判断是否为正方形（宽度和高度接近）
                    if (Math.abs(rect.width - rect.height) <= 10) {
                        double area = rect.width * rect.height;

                        // 判断是否是目前最大的正方形
                        if (area > maxArea) {
                            maxArea = area;
                            maxSquare = rect;
                        }
                    }
                }
            }

            // 7. 输出正方形区域的坐标并计算二维码的缩放比例
            if (maxSquare != null) {
                int x = maxSquare.x;
                int y = maxSquare.y;
                int width = maxSquare.width;
                int height = maxSquare.height;

                // 计算二维码的缩放比例
                Size qrCodeSize = matQRCode.size();
                double scaleFactor = Math.min((double) width / qrCodeSize.width, (double) height / qrCodeSize.height);

                // 调整二维码大小
                Mat resizedQRCode = new Mat();
                Imgproc.resize(matQRCode, resizedQRCode, new Size(qrCodeSize.width * scaleFactor, qrCodeSize.height * scaleFactor));

                // 计算粘贴二维码的区域
                int offsetX = (int) (width - resizedQRCode.size().width) / 2;
                int offsetY = (int) (height - resizedQRCode.size().height) / 2;
                Mat roi = matPoster.submat(y + offsetY, y + offsetY + resizedQRCode.rows(), x + offsetX, x + offsetX + resizedQRCode.cols());

                // 将二维码粘贴到海报图像的区域中
                resizedQRCode.copyTo(roi);

                // 保存最终的合成结果
                // Imgcodecs.imwrite("C:\\Users\\Administrator\\Desktop\\output_image_with_qrcode.jpg", matPoster);

                BufferedImage finalImage = matToBufferedImage(matPoster);
                ImageIO.write(finalImage, "jpg", new File("D:\\Desktop\\output_image_with_qrcode.jpg"));

                log.info("二维码 x：{} y：{} scaleFactor：{}", x, y, scaleFactor);

                System.out.println("二维码已成功合成到海报图中，并保存为 output_image_with_qrcode.jpg");

                respDTO.setX(x);
                respDTO.setY(y);
                respDTO.setScaleFactor(new BigDecimal(scaleFactor).multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP).intValue());
            } else {
                System.out.println("未找到合适的正方形区域");
            }

        } catch (IOException e) {
            log.error("generatePosterCompositionResult errorMsg {} e -> ", e.getMessage(), e);
        }
        return respDTO;
    }

    public static void main(String[] args) {
        try {
            // 1. 从网络下载海报图片
            URL posterUrl = new URL("https://va-papers.oss-accelerate.aliyuncs.com/oss-platform/1b1a/3f64/100b/369d427c-b001-46f3-a1b1-3bfa7e5bbaaa.jpg");
            BufferedImage posterImage = ImageIO.read(posterUrl);
            Mat matPoster = bufferedImageToMat(posterImage);

            // 2. 从网络下载二维码图片
            URL qrCodeUrl = new URL("https://va-pics.oss-accelerate.aliyuncs.com/teach/b312e878-73b3-4f91-b29c-e9576e25fbc4.png");
            BufferedImage qrCodeImage = ImageIO.read(qrCodeUrl);
            Mat matQRCode = bufferedImageToMat(qrCodeImage);

            // 3. 将海报图片转为灰度
            Mat grayPoster = new Mat();
            Imgproc.cvtColor(matPoster, grayPoster, Imgproc.COLOR_BGR2GRAY);

            // 4. 边缘检测
            Mat edges = new Mat();
            Imgproc.Canny(grayPoster, edges, 100, 200);

            // 5. 轮廓检测
            List<MatOfPoint> contours = new ArrayList<>();
            Mat hierarchy = new Mat();
            Imgproc.findContours(edges, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

            // 初始化最大正方形区域
            Rect maxSquare = null;
            double maxArea = 0;

            // 6. 寻找最大正方形区域
            for (int i = 0; i < contours.size(); i++) {
                MatOfPoint2f contour2f = new MatOfPoint2f(contours.get(i).toArray());
                double arcLen = Imgproc.arcLength(contour2f, true);
                MatOfPoint2f approxCurve = new MatOfPoint2f();
                Imgproc.approxPolyDP(contour2f, approxCurve, 0.02 * arcLen, true);

                // 判断是否为四边形
                if (approxCurve.total() == 4) {
                    Rect rect = Imgproc.boundingRect(new MatOfPoint(approxCurve.toArray()));

                    // 判断是否为正方形（宽度和高度接近）
                    if (Math.abs(rect.width - rect.height) <= 10) {
                        double area = rect.width * rect.height;

                        // 判断是否是目前最大的正方形
                        if (area > maxArea) {
                            maxArea = area;
                            maxSquare = rect;
                        }
                    }
                }
            }

            // 7. 输出正方形区域的坐标并计算二维码的缩放比例
            if (maxSquare != null) {
                int x = maxSquare.x;
                int y = maxSquare.y;
                int width = maxSquare.width;
                int height = maxSquare.height;

                // 计算二维码的缩放比例
                Size qrCodeSize = matQRCode.size();
                double scaleFactor = Math.min((double) width / qrCodeSize.width, (double) height / qrCodeSize.height);

                // 调整二维码大小
                Mat resizedQRCode = new Mat();
                Imgproc.resize(matQRCode, resizedQRCode, new Size(qrCodeSize.width * scaleFactor, qrCodeSize.height * scaleFactor));

                // 计算粘贴二维码的区域
                int offsetX = (int) (width - resizedQRCode.size().width) / 2;
                int offsetY = (int) (height - resizedQRCode.size().height) / 2;
                Mat roi = matPoster.submat(y + offsetY, y + offsetY + resizedQRCode.rows(), x + offsetX, x + offsetX + resizedQRCode.cols());

                // 将二维码粘贴到海报图像的区域中
                resizedQRCode.copyTo(roi);

                // 保存最终的合成结果
                // Imgcodecs.imwrite("C:\\Users\\Administrator\\Desktop\\output_image_with_qrcode.jpg", matPoster);

                BufferedImage finalImage = matToBufferedImage(matPoster);
                ImageIO.write(finalImage, "jpg", new File("D:\\Desktop\\output_image_with_qrcode.jpg"));

                int realScaleFactor = new BigDecimal(scaleFactor).multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP).intValue();
                log.info("二维码 x：{} y：{} scaleFactor：{} realScaleFactor：{}", x, y, scaleFactor, realScaleFactor);
                System.out.println("二维码已成功合成到海报图中，并保存为 output_image_with_qrcode.jpg");
            } else {
                System.out.println("未找到合适的正方形区域");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

