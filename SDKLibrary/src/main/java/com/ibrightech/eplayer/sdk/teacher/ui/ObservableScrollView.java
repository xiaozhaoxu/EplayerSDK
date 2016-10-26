package com.ibrightech.eplayer.sdk.teacher.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.ibrightech.eplayer.sdk.common.util.LogUtil;
import com.ibrightech.eplayer.sdk.event.Enum.ScrollState;
import com.ibrightech.eplayer.sdk.event.ScrollChangeEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by zhaoxu2014 on 16/7/5.
 */
public class ObservableScrollView extends ScrollView {
    private ScrollViewListener scrollViewListener = null;
    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    public ObservableScrollView(Context context) {
        super(context);
    }

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ObservableScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
//        LogUtil.d("----imageView---","ObservableScrollView onScrollChanged");
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, l, t, oldl, oldt);
        }

        EventBus.getDefault().post(new ScrollChangeEvent(ScrollState.SCROLLING));
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:{
//                LogUtil.d("----imageView---","ObservableScrollView onTouchEvent ACTION_UP");
                EventBus.getDefault().post(new ScrollChangeEvent(ScrollState.SCROLLEND));
                break;
            }

        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        LogUtil.d("----ObservableScrollView.wwwonSizeChanged w:" + w + "--h:" + h + "--oldw:" + oldw + "--oldh:" + oldh+ "--x:" + this.getX() + "--y:" + this.getY());
    }
    public interface ScrollViewListener {

        void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy);

    }
}
