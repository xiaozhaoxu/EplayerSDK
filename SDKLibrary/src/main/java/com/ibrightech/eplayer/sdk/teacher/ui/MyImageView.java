package com.ibrightech.eplayer.sdk.teacher.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.ibrightech.eplayer.sdk.common.util.LogUtil;

/**
 * Created by zhaoxu2014 on 16/7/5.
 */
public class MyImageView extends ImageView {
    private Bitmap savebitmap=null;
    public MyImageView(Context context) {
        super(context);
    }

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        this.savebitmap=Bitmap.createBitmap(bm);
        super.setImageBitmap(bm);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        LogUtil.d("----MyImageView.wwwonSizeChanged w:" + w + "--h:" + h + "--oldw:" + oldw + "--oldh:" + oldh);
    }

    public Bitmap getSavebitmap() {
        return savebitmap;
    }
}
