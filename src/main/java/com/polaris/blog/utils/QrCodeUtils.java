package com.polaris.blog.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class QrCodeUtils {

    public static int QRCODE_SIZE = 300;
    public static final String format = "png";
    public static final String RESPONSE_CONTENT_TYPE = "image/png";

    public static byte[] encodeQRCode(String text) {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            //容错率越高,可存储的信息越少；但是对二维码清晰对要求越小
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            //还可以设置logo之类的
            // 生成二维码
            BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, hints);
            bitMatrix = deleteWhite(bitMatrix);
            BufferedImage bufferedImage = toBufferedImage(bitMatrix);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, format, out);
            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static final int BORDER_WIDTH = 4;

    public static BitMatrix deleteWhite(BitMatrix matrix) {
        int[] rec = matrix.getEnclosingRectangle();

        int resWidth = rec[2] + BORDER_WIDTH;
        int resHeight = rec[3] + BORDER_WIDTH;
        BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);
        resMatrix.clear();
        for (int i = BORDER_WIDTH; i < resWidth; i++) {
            for (int j = BORDER_WIDTH; j < resHeight; j++) {
                if (matrix.get(i + rec[0], j + rec[1])) resMatrix.set(i, j);
            }
        }
        return resMatrix;
    }

    public static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int onColor = 0xFF000000;
        int offColor = 0xFFFFFFFF;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? onColor : offColor);
            }
        }
        return image;
    }
}