package com.ibrightech.eplayer.sdk.student.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.ibrightech.eplayer.sdk.common.config.EplayerSetting;
import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.common.util.LogUtil;
import com.ibrightech.eplayer.sdk.event.Enum.ViewState;
import com.ibrightech.eplayer.sdk.event.ViewClickEvent;
import com.ibrightech.eplayer.sdk.student.R;

import org.greenrobot.eventbus.EventBus;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.view.IjkVideoView;

/**
 * Created by zhaoxu2014 on 16/9/9.
 */
public class MyVideoView extends RelativeLayout {
    Context context;
    VideoViewListener videoViewListener;
    IjkVideoView ijkvideoview;


    public void setVideoViewListener(VideoViewListener videoViewListener) {
        this.videoViewListener = videoViewListener;
    }

    public MyVideoView(Context context) {
        super(context);
        initView(context);
    }

    public MyVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MyVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context){
        this.context=context;

        View rootView=View.inflate(context, R.layout.my_videoview,this);
        ijkvideoview = (IjkVideoView) rootView.findViewById(R.id.ijkvideoview);
        //todo 处理点击事件
        rootView.findViewById(R.id.view_click).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                LogUtil.d("----imageView---", "ijkvideoview onClick");
                EventBus.getDefault().post(new ViewClickEvent(ViewState.View_Video));
            }
        });


        ijkvideoview.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                if (!CheckUtil.isEmpty(videoViewListener)) {
                    videoViewListener.onCompletion();
                }
            }
        });
        ijkvideoview.setOnErrorListener(new IMediaPlayer.OnErrorListener(){

            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                if (!CheckUtil.isEmpty(videoViewListener)) {
                    videoViewListener.onError();
                }
                return true;
            }
        });
        ijkvideoview.setOnInfoListener(new IMediaPlayer.OnInfoListener(){

            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int arg1, int i1) {
                switch (arg1){
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_START:{
                        if (!CheckUtil.isEmpty(videoViewListener)) {
                            videoViewListener.onLoadingStart(iMediaPlayer);
                        }
                        break;
                    }
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_END:{
                        if (!CheckUtil.isEmpty(videoViewListener)) {
                            videoViewListener.onLoadingEnd(iMediaPlayer);
                        }
                        break;
                    }
                    case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:{
                        if (!CheckUtil.isEmpty(videoViewListener)) {
                            videoViewListener.onFirstRenderingVideo(iMediaPlayer);
                        }
                        break;
                    }
                    case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:{
                        if (!CheckUtil.isEmpty(videoViewListener)) {
                            videoViewListener.onFirstRenderingAudio(iMediaPlayer);
                        }
                        break;
                    }

                }
                return true;
            }
        });

        setUA();

    }

    public int getCurrentPosition() {
        return  ijkvideoview.getCurrentPosition();

    }


    public boolean isFinished(){
        return ijkvideoview.isFinished();
    }

    public void start(){//只有音乐能这样

            ijkvideoview.start();

    }
    public void pause(){

            ijkvideoview.pause();

    }

    public void setVideoURI( String url){
        try {

            ijkvideoview.setVideoPath(url);
        }catch (Exception e){

        }
    }

    public void setOnControlMessageListener(IjkMediaPlayer.OnControlMessageListener onControlMessageListener){
        ((IjkMediaPlayer)ijkvideoview.getmMediaPlayer()).setOnControlMessageListener(onControlMessageListener);

    }



    public void setVideoLayout(int w,int h){

        ViewGroup.LayoutParams lp= ijkvideoview.getLayoutParams();
        lp.width= (int) w;
        lp.height= (int) h;

    }

    public void setUA(){
        WebView webview;
        webview = new WebView(this.getContext());
        webview.layout(0, 0, 0, 0);
        WebSettings settings = webview.getSettings();
        String ua = settings.getUserAgentString();

        int end = ua.indexOf("AppleWebKit");
        if(end!=-1)
            ua= ua.substring(0,end);

        ua = ua+" EplayerFramework "+ EplayerSetting.getInstance().version;

        Log.i("UA", ua);
        ijkvideoview.setmUserAgent(ua);
    }

    public void videoviewStopPlayback(){

        if (null != ijkvideoview) {
            ijkvideoview.stopPlayback();
        }
    }

    public void releaseAll(){
        try{
            videoviewStopPlayback();
            ijkvideoview.release(true);

        }catch (Exception e){
            e.printStackTrace();
        }
    }




    public interface VideoViewListener{
        void onCompletion();
        void onError();
        public void onLoadingStart(IMediaPlayer mp);
        public void onLoadingEnd(IMediaPlayer mp);
        public void onFirstRenderingVideo(IMediaPlayer mp);

        public void onFirstRenderingAudio(IMediaPlayer mp);
    }
}
