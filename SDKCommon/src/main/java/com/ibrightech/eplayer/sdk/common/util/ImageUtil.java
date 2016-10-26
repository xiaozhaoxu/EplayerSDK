package com.ibrightech.eplayer.sdk.common.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.ibrightech.eplayer.sdk.common.config.EplayerConfigBuilder;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by zhaoxu2014 on 16/8/27.
 */
public class ImageUtil {
    private static String TAG = ImageUtil.class.getSimpleName();

    public static int[] getImageInfo(String filename){
        int []infos=new int[2];
        InputStream input = null;
        try{
            input = StorageUtil.openInputStream(filename);

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;

            BitmapFactory.decodeStream(input, null, opts);
            infos[0]=opts.outWidth;
            infos[1]=opts.outHeight;
        }catch (Exception e){
            e.printStackTrace();
            infos[0]=0;
            infos[1]=0;
        }finally {
            //TODO 必须关闭流
            IOUtils.closeQuietly(input);
        }
        return infos;
    }
    public static Bitmap extractThumbnail(String filename, int sideLength) throws IOException {
        Bitmap bmp = null;
        InputStream input = null;
        try {
            input = StorageUtil.openInputStream(filename);

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;

            BitmapFactory.decodeStream(input, null, opts);

            int max = Math.max(opts.outWidth, opts.outHeight);

            //无法获取尺寸，直接默认值
            int roundedSize = 2;
            if (sideLength != 0) {
                //递归计算图片压缩比例
                int initialSize = max / sideLength;
                if (initialSize <= 8) {
                    roundedSize = 1;
                    while (roundedSize < initialSize) {
                        roundedSize <<= 1;
                    }
                } else {
                    roundedSize = (initialSize + 7) / 8 * 8;
                }
            }

            IOUtils.closeQuietly(input);
            input = StorageUtil.openInputStream(filename);

            opts.inSampleSize = roundedSize;
            opts.inJustDecodeBounds = false;

            bmp = BitmapFactory.decodeStream(input, null, opts);
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        } finally {
            IOUtils.closeQuietly(input);
        }

        return bmp;
    }
    //pad版本上根据宽度裁剪一个图
    public static Bitmap extractThumbnail(String filename,int width ,int height) throws IOException {
        Bitmap bmp = null;

        try {

            Bitmap bt = readLocalImage(filename);
            int btWidth= bt.getWidth();
            int btheigth =bt.getHeight();
            double widthScale=(double)btWidth/width;


            bmp= Bitmap.createBitmap(bt,0,0,btWidth,(int)(height*widthScale));

        }catch (Exception e){
            e.printStackTrace();
        }
        return bmp;
    }

    public static BitmapDrawable resizeImage(Bitmap bitmap, int newWidth, int newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return new BitmapDrawable(resizedBitmap);
    }

    public static Bitmap extractPicture(String filename) {
        if (filename == null) {
            return null;
        }

        Bitmap bmp = null;
        InputStream input = null;

        DisplayMetrics displayMetrics = EplayerConfigBuilder.getInstance().getContext().getResources().getDisplayMetrics();

        try {
            input = StorageUtil.openInputStream(filename);

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;

            BitmapFactory.decodeStream(input, null, opts);

            if (opts.outWidth > displayMetrics.widthPixels) {
                int dstWidth = displayMetrics.widthPixels;
                int dstHeight = dstWidth * opts.outHeight / opts.outWidth;

                IOUtils.closeQuietly(input);
                input = StorageUtil.openInputStream(filename);

                opts.inJustDecodeBounds = false;

                Bitmap original = BitmapFactory.decodeStream(input, null, opts);

                bmp = Bitmap.createScaledBitmap(original, dstWidth, dstHeight, false);
            } else {
                IOUtils.closeQuietly(input);
                input = StorageUtil.openInputStream(filename);

                opts.inJustDecodeBounds = false;

                bmp = BitmapFactory.decodeStream(input, null, opts);
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        } finally {
            IOUtils.closeQuietly(input);
        }

        return bmp;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //todo：是否下列参数需要传递
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static void bitmapToFile(Bitmap bitmap, String path) throws IOException {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    // 按 图片尺寸 设置图片大小
    public static void bitmapGetWithPhone(View view, float width, float height) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        params.width = (int) (DeviceUtil.getDisplayWidth(EplayerConfigBuilder.getInstance().getContext()) / (640 / width));
        params.height = (int) (params.width / (width / height));
    }

    /**
     * 以内存优化的方式读取本地资源图片，防止OOM问题发生
     *
     * @param filename
     * @return
     */
    public static Bitmap readLocalImage(String filename) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inPurgeable = true;
        options.inInputShareable = true;

        InputStream input = null;
        Bitmap bitmap = null;
        try {
            input = StorageUtil.openInputStream(filename);
            bitmap = BitmapFactory.decodeStream(input, null, options);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (input != null)
                    input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * 以内存优化的方式读取本地资源图片，防止OOM问题发生
     *
     * @param context context
     * @param resId   R.drawable.xxx
     * @return bitmap
     */
    public static Bitmap readLocalBitMap(Context context, int resId) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inPurgeable = true;
        options.inInputShareable = true;
        InputStream inputStream = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(inputStream, null, options);
    }


    public static BitmapFactory.Options getImageInfo(Resources resources, int resId) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        try {
            BitmapFactory.decodeResource(resources, resId, opts);
            LogUtil.d("----Width:" + opts.outWidth + "--Height:" + opts.outHeight);
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        }
        return opts;
    }
}
