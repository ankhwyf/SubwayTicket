package com.worktogether.subwayticket.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.HashMap;


public class QRCodeUtils {

    /**
     * 生成二维码
     * 参数 context 上下文
     * 参数 content 二维码内容(订单Id+出发站+到达站+总金额+张数+订单生成时间)
     * 返回生成的二维码图片
     * 抛出 WriterException 生成二维码异常
     */
    public static Bitmap createCode(Context context, String content) throws WriterException {

        HashMap<EncodeHintType,String> map = new HashMap<EncodeHintType, String>();

        map.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        MultiFormatWriter writer = new MultiFormatWriter();

        //生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
        BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE,
                DensityUtils.dp2px(context, 350),
                DensityUtils.dp2px(context, 350),map);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        //二维矩阵转为一维像素数组,也就是一直横着排了
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                } else {
                    pixels[y * width + x] = Color.WHITE;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //通过像素数组生成bitmap
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }


    public static class DensityUtils {

        public static int dp2px(Context context, float dp) {
            //获取设备密度
            float density = context.getResources().getDisplayMetrics().density;
            //4.3, 4.9, 加0.5是为了四舍五入
            int px = (int) (dp * density + 0.5f);
            return px;
        }

        public static float px2dp(Context context, int px) {
            //获取设备密度
            float density = context.getResources().getDisplayMetrics().density;
            float dp = px / density;
            return dp;
        }
    }
}
