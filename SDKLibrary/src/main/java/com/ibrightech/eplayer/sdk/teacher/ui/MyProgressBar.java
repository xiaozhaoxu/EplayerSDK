package com.ibrightech.eplayer.sdk.teacher.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.ibrightech.eplayer.sdk.teacher.R;

/**
 * Created by zhaoxu2014 on 14-6-12.
 */
public class MyProgressBar extends ProgressBar {
    public static final int TYPE_LINEARLAYOUT_LAYOUTPARAMS = 1;
    public static final int TYPE_FRAMELAYOUT_LAYOUTPARAMS = 2;
    public static final int TYPE_RELATIVELAYOUT_LAYOUTPARAMS = 3;



    public MyProgressBar(Context context) {
        super(context);

    }

    public MyProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public MyProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    //设置下大小

    public void init(int type) {
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.progress_loading_01);
        int h = bm.getHeight();
        int w = bm.getWidth();

        switch (type) {
            case TYPE_LINEARLAYOUT_LAYOUTPARAMS: {
                LinearLayout.LayoutParams flp = new LinearLayout.LayoutParams(w, h);
                flp.gravity = Gravity.CENTER;
                this.setLayoutParams(flp);
                break;
            }
            case TYPE_FRAMELAYOUT_LAYOUTPARAMS: {
                FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(w, h);
                flp.gravity = Gravity.CENTER;
                this.setLayoutParams(flp);
                break;
            }
            case TYPE_RELATIVELAYOUT_LAYOUTPARAMS:{
                RelativeLayout.LayoutParams flp = new RelativeLayout.LayoutParams(w, h);
                flp.addRule(RelativeLayout.CENTER_IN_PARENT);
                this.setLayoutParams(flp);
                break;
            }
        }


    }


}
