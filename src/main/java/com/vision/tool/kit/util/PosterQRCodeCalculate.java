package com.vision.tool.kit.util;


import com.vision.tool.kit.common.exception.BizException;
import com.vision.tool.kit.controller.image.dto.PosterCalculateReqDTO;
import com.vision.tool.kit.controller.image.dto.PosterCalculateRespDTO;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PosterQRCodeCalculate {
    private static final Logger log = LoggerFactory.getLogger(PosterQRCodeCalculate.class);

    static {
        OpenCVLoader.loadOpenCVLibrary();
    }

    public static PosterCalculateRespDTO generatePosterCompositionResult(PosterCalculateReqDTO input) {
        return PosterQRCodeCalculate.process(input);
    }

    public static void main(String[] args) {
        PosterCalculateReqDTO posterCalculateReqDTO = new PosterCalculateReqDTO();
        posterCalculateReqDTO.setPosterUrl("https://va-papers.oss-accelerate.aliyuncs.com/oss-platform/4a69/1864/f136/f2fd0d01-631f-4681-96a4-1b6cf10fe947.png");
        PosterCalculateRespDTO process = PosterQRCodeCalculate.process(posterCalculateReqDTO);
        log.info(process.toString());
    }

    public static PosterCalculateRespDTO process(PosterCalculateReqDTO input) {
        // 声明所有 Mat 对象
        Mat matPoster = null;
        Mat matQRCode = null;
        Mat grayPoster = null;
        Mat edges = null;
        Mat hierarchy = null;
        Mat resizedQRCode = null;

        PosterCalculateRespDTO resp = new PosterCalculateRespDTO();
        resp.setPosterUrl(input.getPosterUrl());

        try {
            // 1. 从网络下载海报图片
            URL posterUrl = new URL(input.getPosterUrl());
            BufferedImage posterImage = ImageIO.read(posterUrl);
            matPoster = bufferedImageToMat(posterImage);
            log.info("海报图片尺寸：width={}, height={}", matPoster.cols(), matPoster.rows());

            // 2. 从网络下载二维码图片
            URL qrCodeUrl = new URL(input.getQrCodeUrl());
            BufferedImage qrCodeImage = ImageIO.read(qrCodeUrl);
            matQRCode = bufferedImageToMat(qrCodeImage);

            // 3. 将海报图片转为灰度
            grayPoster = new Mat();
            Imgproc.cvtColor(matPoster, grayPoster, Imgproc.COLOR_BGR2GRAY);

            // 4. 边缘检测
            edges = new Mat();
            Imgproc.Canny(grayPoster, edges, 100, 200);

            // 保存边缘图像以调试
            //Imgcodecs.imwrite("D:\\Desktop\\edges.jpg", edges);

            // 5. 轮廓检测
            List<MatOfPoint> contours = new ArrayList<>();
            hierarchy = new Mat();
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

                if (DebugUtil.isDebugMode()) {
                    log.info("轮廓 {} 点数：{}, 近似点数：{}", i, contours.get(i).total(), approxCurve.total());
                }
                // 判断是否为四边形
                if (approxCurve.total() == 4) {
                    Rect rect = Imgproc.boundingRect(new MatOfPoint(approxCurve.toArray()));
                    // 检查坐标是否在图片范围内
                    if (rect.x >= 0 && rect.y >= 0 && rect.x + rect.width <= matPoster.cols() && rect.y + rect.height <= matPoster.rows()) {
                        // 判断是否为正方形（宽度和高度接近）
                        if (Math.abs(rect.width - rect.height) <= 10) {
                            double area = rect.width * rect.height;
                            if (DebugUtil.isDebugMode()) {
                                log.info("候选正方形：x={}, y={}, width={}, height={}, area={}",
                                        rect.x, rect.y, rect.width, rect.height, area);
                            }
                            // 判断是否是目前最大的正方形
                            if (area > maxArea) {
                                maxArea = area;
                                maxSquare = rect;
                            }
                        }
                    } else {
                        log.warn("轮廓坐标超出图片范围：x={}, y={}, width={}, height={}",
                                rect.x, rect.y, rect.width, rect.height);
                    }
                    contour2f.release();
                    approxCurve.release();
                }
                contours.get(i).release();
            }

            // 7. 输出正方形区域的坐标并合成二维码
            if (maxSquare != null) {
                int x = maxSquare.x;
                int y = maxSquare.y;
                int width = maxSquare.width;
                int height = maxSquare.height;

                // 检查区域尺寸是否合理
                if (width < 50 || height < 50) {
                    log.warn("检测到的正方形区域过小：width={}, height={}", width, height);
                    System.out.println("检测到的正方形区域过小");
                    throw new BizException(String.format("检测到的正方形区域过小。width：[%s], height：[%s]", width, height));
                }

                // 计算二维码的缩放比例（缩小 98% 以适应圆角）
                Size qrCodeSize = matQRCode.size();
                double scaleFactor = Math.min((double) width / qrCodeSize.width, (double) height / qrCodeSize.height) * 0.98;

                // 调整二维码大小
                resizedQRCode = new Mat();
                Imgproc.resize(matQRCode, resizedQRCode, new Size(qrCodeSize.width * scaleFactor, qrCodeSize.height * scaleFactor));

                // 计算粘贴二维码的区域（居中）
                int offsetX = x + (width - resizedQRCode.cols()) / 2;
                int offsetY = y + (height - resizedQRCode.rows()) / 2;
                Mat roi = matPoster.submat(offsetY, offsetY + resizedQRCode.rows(), offsetX, offsetX + resizedQRCode.cols());

                // 直接粘贴二维码
                resizedQRCode.copyTo(roi);

                if (DebugUtil.isDebugMode()) {
                    // 保存最终的合成结果
                    BufferedImage finalImage = matToBufferedImage(matPoster);
                    ImageIO.write(finalImage, "jpg", new File("D:\\Desktop\\output_image_with_qrcode.jpg"));
                    System.out.println("二维码已成功合成到海报图中，并保存为 output_image_with_qrcode.jpg");
                }

                int realScaleFactor = new BigDecimal(scaleFactor).multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP).intValue();
                log.info("二维码 x: {}, y: {}, width: {}, height: {}, scaleFactor: {}, realScaleFactor: {}",
                        x, y, width, height, scaleFactor, realScaleFactor);
                resp.setX(x);
                resp.setY(y);
                resp.setWidth(width);
                resp.setHeight(height);
                resp.setScaleFactor(scaleFactor);
            } else {
                log.warn("未找到合适的正方形区域");
                System.out.println("未找到合适的正方形区域");
            }

        } catch (Exception e) {
            log.error("处理图片时发生错误 message：{}, e -> ", e.getMessage(), e);
        } finally {
            // 释放 OpenCV 资源
            releaseMats(matPoster, matQRCode, grayPoster, edges, hierarchy, resizedQRCode);
        }
        return resp;
    }

    // 辅助方法：释放 Mat 对象
    private static void releaseMats(Mat... mats) {
        for (Mat mat : mats) {
            if (mat != null) {
                mat.release();
            }
        }
    }

    // 辅助方法：将 BufferedImage 转换为 Mat
    private static Mat bufferedImageToMat(BufferedImage image) {
        Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
        byte[] data = new byte[image.getWidth() * image.getHeight() * 3];
        int[] rgb = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        for (int i = 0; i < rgb.length; i++) {
            data[i * 3] = (byte) ((rgb[i] >> 16) & 0xFF); // R
            data[i * 3 + 1] = (byte) ((rgb[i] >> 8) & 0xFF); // G
            data[i * 3 + 2] = (byte) (rgb[i] & 0xFF); // B
        }
        mat.put(0, 0, data);
        return mat;
    }

    // 辅助方法：将 Mat 转换为 BufferedImage
    private static BufferedImage matToBufferedImage(Mat mat) {
        int type = BufferedImage.TYPE_3BYTE_BGR;
        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
        byte[] data = new byte[mat.cols() * mat.rows() * (int) mat.elemSize()];
        mat.get(0, 0, data);
        image.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), data);
        return image;
    }
}