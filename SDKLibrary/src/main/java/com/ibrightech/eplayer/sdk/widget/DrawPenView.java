package com.ibrightech.eplayer.sdk.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerDrawPadType;
import com.ibrightech.eplayer.sdk.common.entity.DrawMsgInfo;
import com.ibrightech.eplayer.sdk.common.util.LogUtil;
import com.ibrightech.eplayer.sdk.common.util.NumberUtil;

/**
 * Created by junhai on 14-8-14.
 */
public class DrawPenView extends View {

    private Paint clearPaint;
    public static float baseScale = 0.8f;

    private static float CLEAR_PAINT_STROKE_WIDTH=60f* baseScale;
    private static float PAINT_STROKE_WIDTH=4f* baseScale;

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;

    private Paint mPaint;
    private Paint mBitmapPaint;


    private EplayerDrawPadType type;
    private float scale;
    private float canvasWidth;


    public static   float sourceWidth =834.0f * baseScale;

    private static float sourceWhiteHeight =470.0f* baseScale;

    private   float sourceDocHeight   = 2000.0f* baseScale;

    public static float getSourceWidth() {
        return sourceWidth;
    }

    public DrawPenView(Context context) {
        super(context);
        this.initPadView();
    }

    public DrawPenView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initPadView();
    }

    public DrawPenView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.initPadView();
    }


    private void initPadView() {

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFFFF0000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(PAINT_STROKE_WIDTH);

        clearPaint = new Paint();
        clearPaint.setColor(Color.parseColor("#6e0000FF"));
        clearPaint.setAntiAlias(true);
        clearPaint.setAlpha(0);
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        clearPaint.setStyle(Paint.Style.STROKE);

        clearPaint.setDither(true);
        clearPaint.setStrokeJoin(Paint.Join.ROUND);
        clearPaint.setStrokeCap(Paint.Cap.ROUND);

        clearPaint.setStrokeWidth(CLEAR_PAINT_STROKE_WIDTH);

        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

    }

    public void setViewSize(int imageWidth, int imageHeight){

        canvasWidth = imageWidth *1.0f;

        //计算出缩放比例
        scale = canvasWidth  / sourceWidth;

        //计算新的显示高度
        sourceDocHeight =  scale * imageHeight;


        if(mBitmap!=null&&!mBitmap.isRecycled()){
            mBitmap.recycle();
            mBitmap = null;
        }

        if(type==EplayerDrawPadType.DrawPadTypeDocument){
            mBitmap = Bitmap.createBitmap((int)sourceWidth, (int)sourceDocHeight, Bitmap.Config.ARGB_4444);
        }else{
            mBitmap = Bitmap.createBitmap((int)sourceWidth, (int)sourceWhiteHeight, Bitmap.Config.ARGB_4444);
        }
        mCanvas = null;
        mCanvas = new Canvas(mBitmap);
    }

    public  void switchType(EplayerDrawPadType _type){
        if(_type==type)
            return;

        type =_type;
        if(mBitmap!=null&&!mBitmap.isRecycled()){
            mBitmap.recycle();
            mBitmap = null;
        }

        if(type==EplayerDrawPadType.DrawPadTypeDocument){
            mBitmap = Bitmap.createBitmap((int)sourceWidth, (int)sourceDocHeight, Bitmap.Config.ARGB_4444);
        }else{
            mBitmap = Bitmap.createBitmap((int)sourceWidth, (int)sourceWhiteHeight, Bitmap.Config.ARGB_4444);
        }
        mCanvas = null;
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        LogUtil.d("----onSizeChanged w:" + w + "--h:" + h + "--oldw:" + oldw + "--oldh:" + oldh);

        if(w>0)
            canvasWidth = w*1.0f;

        scale = canvasWidth / sourceWidth;



    }

    @Override
    protected void onDraw(Canvas canvas) {

        if(mBitmap!=null&&mBitmapPaint!=null) {

            LogUtil.d("----onDraw mBitmap:" + mBitmap + "--mBitmapPaint:" + mBitmapPaint + "--canvas:" + canvas);
            Matrix m = new Matrix();

            m.postScale(scale,scale);
            canvas.drawBitmap(mBitmap, m, mBitmapPaint);

        }




    }

    //绘制普通线条
    public void drawLine(DrawMsgInfo drawMsgInfo) {


        mPaint.setColor(Color.parseColor(drawMsgInfo.getLineColorHex()));


        float startX = NumberUtil.parseFloat(drawMsgInfo.getDatas().get(0), 0f) * baseScale;
        float startY = NumberUtil.parseFloat(drawMsgInfo.getDatas().get(1), 0f)* baseScale;

        mPath.reset();
        mPath.moveTo(startX, startY);

        for (int i = 2; i < drawMsgInfo.getDatas().size(); i = i + 2) {
            int xIndex = i;
            int yIndex = i + 1;
            float x = NumberUtil.parseFloat(drawMsgInfo.getDatas().get(xIndex), -1f)* baseScale;
            float y = NumberUtil.parseFloat(drawMsgInfo.getDatas().get(yIndex), -1f)* baseScale;

            if (x == -1 || y == -1) {
                continue;
            }

            mPath.lineTo((int) x, (int) y);
        }

        mCanvas.drawPath(mPath, mPaint);
        mPath.reset();

//        invalidate();

    }


    //绘制橡皮擦线条
    public void drawCleanLine(DrawMsgInfo drawMsgInfo) {



        float startX = NumberUtil.parseFloat(drawMsgInfo.getDatas().get(0), 0f)* baseScale;
        float startY = NumberUtil.parseFloat(drawMsgInfo.getDatas().get(1), 0f)* baseScale;

        mPath.reset();
        mPath.moveTo(startX, startY);

        for (int i = 2; i < drawMsgInfo.getDatas().size(); i = i + 2) {
            int xIndex = i;
            int yIndex = i + 1;
            float x = NumberUtil.parseFloat(drawMsgInfo.getDatas().get(xIndex), -1f)* baseScale;
            float y = NumberUtil.parseFloat(drawMsgInfo.getDatas().get(yIndex), -1f)* baseScale;

            if (x == -1 || y == -1) {
                continue;
            }
            //橡皮擦最后一个坐标X位置需要向左偏移橡皮擦半径大小
//            if (i == drawMsgInfo.datas.size() - 2) {
//                x -= CLEAR_PAINT_STROKE_WIDTH/2.0f;
//            }

            mPath.lineTo((int) x, (int) y);
        }


        mCanvas.drawPath(mPath, clearPaint);
        mPath.reset();

    }

    //画板清屏
    public void clearAll() {

        if(mCanvas!=null) {
            Paint paint = new Paint();
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            mCanvas.drawPaint(paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        }
    }

    public void release() {
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
            LogUtil.d("----DrawPenView Finished Recycled");
        }
    }

}
