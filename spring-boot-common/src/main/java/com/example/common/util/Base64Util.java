package com.example.common.util;

import com.alibaba.druid.util.Base64;
import com.example.common.exception.CommonException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;

/**
 * @author Created by L.C.Y on 2018-9-28
 */
public class Base64Util {

    private static Logger logger = LoggerFactory.getLogger(Base64Util.class);

    /**
     * 对文件进行Base64编码后,再进行URLEncoder处理
     * @param file
     * @return
     */
    public static String encodeBase64(File file) {
        // 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        byte[] data;
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            data = new byte[inputStream.available()];
            inputStream.read(data);
            // 对字节数组Base64编码
            return Base64.byteArrayToBase64(data);
        } catch (Exception e) {
            logger.info("Base64编码异常："+e.getMessage());
            throw new CommonException("Base64编码异常");
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                logger.error("Base64编码关闭IO异常："+e.getMessage());
            }
        }
    }

    /**
     * 对文件进行Base64编码后,再进行URLEncoder处理
     * @param file
     * @return
     */
    public static String encodeBase64AndURLEncoder(File file) {
        // 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        long fileSize = file.length();
        if (fileSize < 50*1024 || fileSize > 10*1024*1024) {
            throw new CommonException("文件太大或太小，请添加50kB-10MB内的文件");
        }
        byte[] data;
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            data = new byte[inputStream.available()];
            inputStream.read(data);
            // 对字节数组Base64及URLEncoder编码
            return URLEncoder.encode(Base64.byteArrayToBase64(data), "UTF-8");
        } catch (Exception e) {
            logger.info("文件编码异常："+e.getMessage());
            throw new CommonException("文件编码异常");
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                logger.error("关闭IO异常："+e.getMessage());
            }
        }
    }



    /**
     * 对文件进行Base64编码处理,并压缩
     * @param image
     * @return
     */
    public static String image2Thumb(File image) {
        // 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        byte[] data;
        try {
            BufferedImage srcImage= ImageIO.read(image);
            int width = srcImage.getWidth();
            int height = srcImage.getHeight();
            int toWidth = (int) (Float.parseFloat(String.valueOf(width)) / 1);
            int toHeight = (int) (Float.parseFloat(String.valueOf(height)) / 1);
            BufferedImage result = new BufferedImage(toWidth, toHeight, BufferedImage.TYPE_INT_RGB);
            result.getGraphics().drawImage(srcImage.getScaledInstance(toWidth, toHeight, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write( result, "jpg", byteArrayOutputStream);
            byteArrayOutputStream.flush();
            byte[] imageInByte = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
            // 对字节数组Base64编码
            return Base64.byteArrayToBase64(imageInByte);
        } catch (Exception e) {
            return null;
        }

    }
}
