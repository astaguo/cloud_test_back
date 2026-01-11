package com.cloud.test.base.utils;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 * 图片压缩工具类
 */
public class ImageCompressUtil {

    /**
     * 压缩图片字节流（支持PNG/JPG）
     * @param originalBytes 原始图片字节数组
     * @param quality 压缩质量（0.0-1.0，1.0为无损，推荐0.7）
     * @return 压缩后的字节数组
     * @throws IOException 图片处理异常
     */
    public static byte[] compressImage(byte[] originalBytes, float quality) throws IOException {
        // 1. 将字节数组转为BufferedImage
        ByteArrayInputStream bais = new ByteArrayInputStream(originalBytes);
        BufferedImage bufferedImage = ImageIO.read(bais);
        if (bufferedImage == null) {
            throw new IOException("无法解析图片字节流，可能不是有效图片格式");
        }

        // 2. 创建字节输出流，用于存储压缩后的数据
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // 3. 获取图片写入器（根据原图片格式选择JPG/PNG）
        String format = getImageFormat(originalBytes);
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(format);
        if (!writers.hasNext()) {
            // 兜底用JPG
            writers = ImageIO.getImageWritersByFormatName("jpg");
        }
        ImageWriter writer = writers.next();

        // 4. 设置压缩参数
        ImageWriteParam writeParam = writer.getDefaultWriteParam();
        if (writeParam.canWriteCompressed()) {
            writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            writeParam.setCompressionQuality(quality); // 核心：设置压缩质量
        }

        // 5. 写入压缩后的图片数据
        writer.setOutput(ImageIO.createImageOutputStream(baos));
        writer.write(null, new IIOImage(bufferedImage, null, null), writeParam);

        // 6. 释放资源
        writer.dispose();
        baos.close();
        bais.close();

        return baos.toByteArray();
    }

    /**
     * 简易判断图片格式（PNG/JPG）
     */
    private static String getImageFormat(byte[] bytes) {
        if (bytes.length < 4) {
            return "jpg";
        }
        // PNG的文件头标识
        if (bytes[0] == (byte) 0x89 && bytes[1] == (byte) 0x50 && bytes[2] == (byte) 0x4E && bytes[3] == (byte) 0x47) {
            return "png";
        }
        // JPG的文件头标识
        if (bytes[0] == (byte) 0xFF && bytes[1] == (byte) 0xD8) {
            return "jpg";
        }
        return "jpg";
    }

    // 重载方法：默认压缩质量0.7（平衡体积和清晰度）
    public static byte[] compressImage(byte[] originalBytes) throws IOException {
        return compressImage(originalBytes, 0.5f);
    }
}