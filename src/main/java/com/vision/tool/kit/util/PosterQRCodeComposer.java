package com.vision.tool.kit.util;

import jakarta.annotation.Resource;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

@Service
public class PosterQRCodeComposer {
    private static final Logger log = LoggerFactory.getLogger(PosterQRCodeComposer.class);

    static {
        OpenCVLoader.loadOpenCVLibrary();
    }

    @Resource
    private OssUtils ossUtils;

    /**
     * 动态合成二维码到海报图片，并上传至阿里云 OSS（不带 width 和 height）
     * @param posterUrl 海报图片的网络 URL
     * @param qrCodeUrl 二维码图片的网络 URL
     * @param x 正方形区域的 x 坐标
     * @param y 正方形区域的 y 坐标
     * @param scaleFactor 缩放比例（百分比形式，需除以 100）
     * @return 上传后的 OSS 文件 URL
     * @throws IOException 如果图片处理或 OSS 上传失败
     */
    public String composeAndUpload(String posterUrl, String qrCodeUrl, int x, int y, double scaleFactor) throws IOException {
        return composeAndUpload(posterUrl, qrCodeUrl, x, y, -1, -1, scaleFactor);
    }

    /**
     * 动态合成二维码到海报图片，并上传至阿里云 OSS（带 width 和 height）
     * @param posterUrl 海报图片的网络 URL
     * @param qrCodeUrl 二维码图片的网络 URL
     * @param x 正方形区域的 x 坐标
     * @param y 正方形区域的 y 坐标
     * @param width 正方形区域的宽度（可选，-1 表示不指定）
     * @param height 正方形区域的高度（可选，-1 表示不指定）
     * @param scaleFactor 缩放比例（百分比形式，需除以 100）
     * @return 上传后的 OSS 文件 URL
     * @throws IOException 如果图片处理或 OSS 上传失败
     */
    public String composeAndUpload(String posterUrl, String qrCodeUrl, int x, int y, int width, int height, double scaleFactor) throws IOException {
        // 声明所有 Mat 对象
        Mat matPoster = null;
        Mat matQRCode = null;
        Mat resizedQRCode = null;

        try {
            // 1. 下载海报图片
            log.info("下载海报图片：{}", posterUrl);
            BufferedImage posterImage = ImageIO.read(new URL(posterUrl));
            matPoster = bufferedImageToMat(posterImage);
            log.info("海报图片尺寸：width={}, height={}", matPoster.cols(), matPoster.rows());

            // 2. 下载二维码图片
            log.info("下载二维码图片：{}", qrCodeUrl);
            BufferedImage qrCodeImage = ImageIO.read(new URL(qrCodeUrl));
            matQRCode = bufferedImageToMat(qrCodeImage);

            // 3. 调整二维码大小（scaleFactor 缩小 100 倍）
            Size qrCodeSize = matQRCode.size();
            resizedQRCode = new Mat();
            Imgproc.resize(matQRCode, resizedQRCode, new Size(qrCodeSize.width * scaleFactor, qrCodeSize.height * scaleFactor));
            log.info("二维码调整后尺寸：width={}, height={}, scaleFactor={}",
                    resizedQRCode.cols(), resizedQRCode.rows(), scaleFactor);

            // 4. 确定粘贴坐标
            int offsetX = x;
            int offsetY = y;

            // 如果提供了 width 和 height，计算居中坐标
            if (width > 0 && height > 0) {
                // 检查区域尺寸是否合理
                if (width < 50 || height < 50) {
                    log.error("正方形区域过小：width={}, height={}", width, height);
                    throw new IllegalArgumentException("正方形区域过小");
                }

                // 检查区域是否超出图片范围
                if (x + width > matPoster.cols() || y + height > matPoster.rows()) {
                    log.error("正方形区域超出图片范围：x={}, y={}, width={}, height={}, posterWidth={}, posterHeight={}",
                            x, y, width, height, matPoster.cols(), matPoster.rows());
                    throw new IllegalArgumentException("正方形区域超出图片范围");
                }

                // 计算居中偏移
                offsetX = x + (width - resizedQRCode.cols()) / 2;
                offsetY = y + (height - resizedQRCode.rows()) / 2;
                log.info("居中坐标：offsetX={}, offsetY={}", offsetX, offsetY);
            }

            // 5. 边界检查
            if (offsetX < 0 || offsetY < 0 ||
                    offsetX + resizedQRCode.cols() > matPoster.cols() ||
                    offsetY + resizedQRCode.rows() > matPoster.rows()) {
                log.error("ROI 超出图片边界：offsetX={}, offsetY={}, qrWidth={}, qrHeight={}, posterWidth={}, posterHeight={}",
                        offsetX, offsetY, resizedQRCode.cols(), resizedQRCode.rows(), matPoster.cols(), matPoster.rows());
                throw new IllegalArgumentException("ROI 超出图片边界");
            }

            // 6. 粘贴二维码
            Mat roi = matPoster.submat(offsetY, offsetY + resizedQRCode.rows(), offsetX, offsetX + resizedQRCode.cols());
            resizedQRCode.copyTo(roi);

            // 7. 转换为 BufferedImage 并上传到 OSS
            if (DebugUtil.isDebugMode()) {
                // 保存最终的合成结果
                BufferedImage finalImage = matToBufferedImage(matPoster);
                ImageIO.write(finalImage, "jpg", new File("D:\\Desktop\\output_compose_image_with_qrcode.jpg"));
                System.out.println("二维码已成功合成到海报图中，并保存为 output_compose_image_with_qrcode.jpg");
                return "D:\\Desktop\\output_compose_image_with_qrcode.jpg";
            }
            BufferedImage finalImage = matToBufferedImage(matPoster);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(finalImage, "jpg", baos);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

            String objectName = String.valueOf(UUID.randomUUID()) + ".jpg";
            log.info("上传合成图片到 OSS：objectName={}", objectName);
            String ossUrl = ossUtils.uploadFromStream(bais, objectName);
            log.info("成功上传到 OSS：{}", ossUrl);

            return ossUrl;

        } catch (IOException e) {
            log.error("图片处理或 OSS 上传失败：posterUrl={}, qrCodeUrl={}", posterUrl, qrCodeUrl, e);
            throw e;
        } catch (Exception e) {
            log.error("合成图片时发生未知错误", e);
            throw new IOException("合成图片失败", e);
        } finally {
            // 释放 OpenCV 资源
            releaseMats(matPoster, matQRCode, resizedQRCode);
        }
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
