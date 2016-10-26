package com.ibrightech.eplayer.sdk.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.common.util.LogUtil;

/**
 * Created by zhaoxu2014 on 16/7/4.
 */
public class MyViewPager extends ViewPager {

    OnViewPagerListener onViewPagerListener;

    private boolean isCanScroll = true;

    public void setCanScroll(boolean canScroll) {
        isCanScroll = canScroll;
    }





    public void setOnViewPagerListener(OnViewPagerListener onViewPagerListener) {
        this.onViewPagerListener = onViewPagerListener;
    }

    public MyViewPager(Context context) {
        super(context);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * onInterceptTouchEvent最后坐标
     */
    private float mLastXIntercept;
    private float mLastYIntercept;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!isCanScroll) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:{
                mLastXIntercept=event.getX();
                mLastYIntercept=event.getY();
                break;
            }
            case MotionEvent.ACTION_MOVE:{

                break;
            }

            case MotionEvent.ACTION_UP:{
                float xDs= Math.abs(event.getX()-mLastXIntercept);
                float yDs= Math.abs(event.getY()-mLastYIntercept);
               if(xDs <5&&yDs<5){
                   if(!CheckUtil.isEmpty(onViewPagerListener)){
                       onViewPagerListener.onItemClickListener();
                   }
               }
                mLastXIntercept=0;
                mLastYIntercept=0;

                break;
            }

        }

        return super.onInterceptTouchEvent(event);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        LogUtil.d("----MyViewPager.wwwonSizeChanged w:" + w + "--h:" + h + "--oldw:" + oldw + "--oldh:" + oldh);
    }

    public interface OnViewPagerListener {
        void onItemClickListener();
    }
}
