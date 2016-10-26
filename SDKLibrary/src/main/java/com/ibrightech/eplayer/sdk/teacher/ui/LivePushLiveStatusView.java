package com.ibrightech.eplayer.sdk.teacher.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.common.util.TextViewUtils;
import com.ibrightech.eplayer.sdk.teacher.R;

public class LivePushLiveStatusView extends LinearLayout {
    public static final int VIDEO_METHOD = 1;
    public static final int VOICE_METHOD = 2;

    Context context;
    TextView tv_live_status;

    LinearLayout layout_live_method;

    OnClickListener listener;
    View rootView;
    int method;

    public LivePushLiveStatusView(Context context) {
        super(context);
        initView(context);
    }

    public LivePushLiveStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public LivePushLiveStatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        this.context = context;
        rootView = View.inflate(context, R.layout.widget_live_status, this);
        layout_live_method= (LinearLayout) rootView.findViewById(R.id.layout_live_method);
         tv_live_status= (TextView) rootView.findViewById(R.id.tv_live_status);

        rootView.findViewById(R.id.layout_pause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.setVisibility(GONE);
                listener.play();
            }
        });

        layout_live_method.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                method = method==VOICE_METHOD?VIDEO_METHOD:VOICE_METHOD;
                setLiveMethodText();
                listener.liveMethod(isShowVideo());
            }
        });


        setLiveMethod(VIDEO_METHOD);
    }

    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    public void setLiveMethod(int method) {
        this.method=method;
    }

    public void setLiveStatusText(String text) {
        if(!CheckUtil.isEmpty(text)) {
            tv_live_status.setVisibility(View.VISIBLE);
            TextViewUtils.setText(tv_live_status, text);
        }else{
            tv_live_status.setVisibility(View.GONE);
        }
    }
    public boolean isShowVideo(){//当前是否展示的视频
        return method==VIDEO_METHOD;
    }



    private void setLiveMethodText(){
        switch (method){
            case VOICE_METHOD:{
                layout_live_method.setBackgroundResource(R.drawable.widget_live_method_vedio);
                break;
            }
            case VIDEO_METHOD:{
                layout_live_method.setBackgroundResource(R.drawable.widget_live_method_voice);

                break;
            }
        }

    }

    public interface OnClickListener {
        void liveMethod(boolean showVideo);
        void play();
    }
}
