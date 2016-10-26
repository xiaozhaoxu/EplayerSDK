package com.ibrightech.eplayer.sdk.student.widget;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.ibrightech.eplayer.sdk.common.engin.EplayerEngin;
import com.ibrightech.eplayer.sdk.common.engin.EplayerPlaybackEngin;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerLiveRoomLiveStatus;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerLiveRoomStreamType;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.DrawPadInfo;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.LivaRoomInfo.LiveRoomInfoData;
import com.ibrightech.eplayer.sdk.common.entity.playback.PlaybackDriver;
import com.ibrightech.eplayer.sdk.common.entity.session.EPlaybackSessionInfo;
import com.ibrightech.eplayer.sdk.common.entity.session.EplayerSessionInfo;
import com.ibrightech.eplayer.sdk.common.net.ws.event.VideoAudioStatusEvent;
import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.common.util.LogUtil;
import com.ibrightech.eplayer.sdk.common.util.WeakReferenceHandler;
import com.ibrightech.eplayer.sdk.event.Enum.ViewState;
import com.ibrightech.eplayer.sdk.event.ViewClickEvent;
import com.ibrightech.eplayer.sdk.student.R;
import com.ibrightech.eplayer.sdk.student.activity.PlaybackCourseActivity;
import com.ibrightech.eplayer.sdk.student.entityenum.PalyTypeState;
import com.ibrightech.eplayer.sdk.student.entityenum.ScreenDirEnum;
import com.ibrightech.eplayer.sdk.student.entityenum.ZFrontEnum;
import com.ibrightech.eplayer.sdk.student.event.VideoLoadEvent;
import com.ibrightech.eplayer.sdk.teacher.activity.SDKBaseActivity;
import com.ibrightech.eplayer.sdk.teacher.event.PPTInfoChangeEvent;
import com.ibrightech.eplayer.sdk.teacher.event.PageEvent;
import com.ibrightech.eplayer.sdk.widget.BaseShowView;
import com.ibrightech.eplayer.sdk.widget.DrawPadControllerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class PlaybackControllerView extends RelativeLayout {
    public String TAG=PlaybackControllerView.this.getClass().getSimpleName();
    Context context;
    DrawPadControllerView ppt_controller_view;
    LiveVideoView livevideoview;
    PlayerControllerView playercontrollerview;
    RelativeLayout layout_ppt;
    EventBus bus;
    PlaybackCourseActivity activity;
    int screenWidth,  screenHeight;
    ScreenDirEnum screenDirEnum;
    ZFrontEnum zFrontEnum=ZFrontEnum.ZFRONT_NONE;//Z轴ppt和video的上下显示顺序
    int requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    Animation face_enter, face_exit;

    public PlaybackControllerView(Context context) {
        super(context);
        initView(context);
    }

    public PlaybackControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public PlaybackControllerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }
    PlayerControllerView.OnPlayerControllerListener onPlayerControllerListener=new PlayerControllerView.OnPlayerControllerListener(){

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            livevideoview.stopPlayback();
            ((EplayerPlaybackEngin)EplayerEngin.getInstance()).pausePlayback();
            System.gc();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

            ((EplayerPlaybackEngin)EplayerEngin.getInstance()).resumePlayback(seekBar.getProgress());
            livevideoview.showProgress();
        }

        @Override
        public void onChangeScreen() {
            changScreenOrientation();
        }

        @Override
        public void pausePlayback() {
            livevideoview.pause();
            ((EplayerPlaybackEngin)EplayerEngin.getInstance()).pausePlayback();
            System.gc();
        }

        @Override
        public void resumePlayback() {
            livevideoview.start();
            ((EplayerPlaybackEngin)EplayerEngin.getInstance()).resumePlayback();
            System.gc();
        }
    };

    public void setSessionInfo(EplayerSessionInfo sessionInfo) {
        ppt_controller_view.setSessionInfo(sessionInfo);
    }

    PlaybackDriver.OnEnginListener onEnginListener=new PlaybackDriver.OnEnginListener() {
        @Override
        public boolean playDoneIfHasMedia() {
            return livevideoview.isFinished();
        }

        @Override
        public long playTimeIfHasMedia() {
            return livevideoview.getCurrentPosition();
        }

        @Override
        public void stopPlaybackEngin() {

        }

        @Override
        public void currentAbsTime(long time) {

        }

        @Override
        public void showProgressTime(long progress, long totalTime, long playbackTime, long playbackBeginTime, long playbackEndTime) {
            playercontrollerview.showProgressTime(progress,totalTime,playbackTime,playbackBeginTime,playbackEndTime);
        }
    };


    public void registEnginListener() {
        ((EplayerPlaybackEngin)EplayerEngin.getInstance()).setListener(onEnginListener);
    }

    public void initScreenInfo(PlaybackCourseActivity liveCourseActivity, int screenWidth, int screenHeight, ScreenDirEnum screenDirEnum) {
        this.screenWidth=screenWidth;
        this.screenHeight=screenHeight;
        this.activity = liveCourseActivity;
        changePPTHeight(screenDirEnum);
    }
    public void changePPTHeight(ScreenDirEnum screenDirEnum){
        this.screenDirEnum=screenDirEnum;

        LayoutParams lp3 = (LayoutParams) layout_ppt.getLayoutParams();

        switch ( screenDirEnum){
            case FULL_PORTRAIT_SCREEN:{
                lp3.width = screenWidth;
                lp3.height = screenHeight;

                break;
            }
            case FULL_LANDSCAPE_SCREEN:{
                lp3.width = screenHeight;
                lp3.height = screenWidth;
                break;
            }
        }

        initViewXPostion();
        initTitleVis(false);
    }
    //改变ppt和video上下显示顺序
    public void initViewXPostion() {

        livevideoview.clearViewPostion();
        ppt_controller_view.clearViewPostion();
        LayoutParams lp3 = (LayoutParams) layout_ppt.getLayoutParams();

        int w = lp3.width;
        int h = lp3.height;
        //todo ppt/word按16：9显示
        if (h * 1.0 / w >= SDKBaseActivity.DRAWPADVIEW_SCALE) {
            h = (int) (w * SDKBaseActivity.DRAWPADVIEW_SCALE);
        } else {
            w = (int) (h / SDKBaseActivity.DRAWPADVIEW_SCALE);
        }
        RelativeLayout.LayoutParams pptlp= (LayoutParams) ppt_controller_view.getLayoutParams();

        switch (zFrontEnum) {
            case ZFRONT_NONE:
            case ZFRONT_ONLY_VIDEO: {
                ppt_controller_view.setVisibility(View.GONE);
                livevideoview.setVisibility(View.VISIBLE);
                livevideoview.setCanMove(false);
                pptlp.addRule(RelativeLayout.CENTER_IN_PARENT,0);
                livevideoview.initScreenWidthHeight(lp3.width, lp3.height);
                break;
            }
            case ZFRONT_ONLY_PPT: {
                ppt_controller_view.setVisibility(View.VISIBLE);
                livevideoview.setVisibility(View.GONE);

                ppt_controller_view.setCanMove(false);
                pptlp.addRule(RelativeLayout.CENTER_IN_PARENT);
                ppt_controller_view.initScreenWidthHeight(w, h);
                break;
            }
            case ZFRONT_VIDEO_PPT: {
                //Video填充父组件，ppt按小图显示
                ppt_controller_view.setVisibility(View.VISIBLE);
                livevideoview.setVisibility(View.VISIBLE);
                pptlp.addRule(RelativeLayout.CENTER_IN_PARENT,0);
                livevideoview.initScreenWidthHeight(lp3.width, lp3.height);
                ppt_controller_view.initViewRightTopPostion();
                ppt_controller_view.bringToFront();

                livevideoview.setCanMove(false);
                ppt_controller_view.setCanMove(true);
                break;
            }
            case ZFRONT_PPT_VIDEO: {
                //Video填充父组件，ppt按小图显示
                ppt_controller_view.setVisibility(View.VISIBLE);
                livevideoview.setVisibility(View.VISIBLE);


                pptlp.addRule(RelativeLayout.CENTER_IN_PARENT);

                ppt_controller_view.initScreenWidthHeight(w, h);
                livevideoview.initViewRightTopPostion();
                livevideoview.bringToFront();

                livevideoview.setCanMove(true);
                ppt_controller_view.setCanMove(false);
                break;
            }
        }

    }

    LiveControllerViewHandler handler=new LiveControllerViewHandler(this);
    static class  LiveControllerViewHandler extends WeakReferenceHandler<PlaybackControllerView> {
        public LiveControllerViewHandler(PlaybackControllerView view) {
            super(view);
        }
        @Override
        protected void handleMessage(Message msg, PlaybackControllerView view) {
            switch (msg.what){
                case LiveControllerView.MSG_HIDE_HINT_SPACE:{
                    view.initTitleVis(true);
                    break;
                }
            }
        }
    }
    private void removeAllMessage(){
        if(!CheckUtil.isEmpty(handler)){
            handler.removeMessages(LiveControllerView.MSG_HIDE_HINT_SPACE);
        }

    }


    /*改变上下标题的显示
    *isChangeVis 是否切换当前状态
    */
    public void initTitleVis(boolean isChangeVis){
        removeAllMessage();
        switch ( screenDirEnum){
            case FULL_PORTRAIT_SCREEN:
            case FULL_LANDSCAPE_SCREEN:{
                if(!isChangeVis) {
                    activity.setTopTitleVisibility(View.VISIBLE, true);
                    this.setBottomSpaceVisibility(View.VISIBLE, true);

                    handler.sendEmptyMessageDelayed(LiveControllerView.MSG_HIDE_HINT_SPACE,LiveControllerView.SHOW_HINT_TIME);
                }else{
                    int vis=playercontrollerview.getVisibility()==View.VISIBLE?View.GONE:View.VISIBLE;
                    activity.setTopTitleVisibility(vis, true);
                    this.setBottomSpaceVisibility(vis, true);

                    if(vis==View.VISIBLE){
                        handler.sendEmptyMessageDelayed(LiveControllerView.MSG_HIDE_HINT_SPACE,LiveControllerView.SHOW_HINT_TIME);
                    }
                }
                break;
            }

        }
    }
    public void setBottomSpaceVisibility(int vis,boolean isAnim){

        if(vis==View.VISIBLE&&playercontrollerview.getVisibility()!=View.VISIBLE){
            playercontrollerview.setVisibility(vis);
            if(isAnim) {
                playercontrollerview.startAnimation(face_enter);
            }
        }else if(vis!=View.VISIBLE&&playercontrollerview.getVisibility()==View.VISIBLE){
            playercontrollerview.setVisibility(View.INVISIBLE);
            if(isAnim){
                playercontrollerview.startAnimation(face_exit);
            }
        }

    }
    private int[] getVideoSize(){
        int []infos=new int[2];
        LayoutParams lp3 = (LayoutParams) layout_ppt.getLayoutParams();
        switch ( screenDirEnum){
            case FULL_PORTRAIT_SCREEN:{
                infos[0]=lp3.width;
                infos[1]=lp3.height;
                break;
            }
            case FULL_LANDSCAPE_SCREEN:{
                infos[0]=lp3.width;
                infos[1]=lp3.height;
                break;
            }
        }


        return infos;
    }


    private void initView(Context context) {

        this.context = context;
        bus=EventBus.getDefault();
        bus.register(this);
        face_enter = AnimationUtils.loadAnimation(context, R.anim.chat_face_enter);
        face_exit = AnimationUtils.loadAnimation(context, R.anim.chat_face_exit);

        View rootView = View.inflate(context, R.layout.playback_controller, this);
        ppt_controller_view= (DrawPadControllerView) rootView.findViewById(R.id.ppt_controller_view);
        livevideoview= (LiveVideoView) rootView.findViewById(R.id.livevideoview);
        playercontrollerview= (PlayerControllerView) rootView.findViewById(R.id.playercontrollerview);
        layout_ppt = (RelativeLayout) rootView.findViewById(R.id.rl_ppt_area);
        setListener();

    }

    private void setListener() {
        ppt_controller_view.setExchangeListener(onExchangePostionListener);
        livevideoview.setExchangeListener(onExchangePostionListener);
        playercontrollerview.setListener(onPlayerControllerListener);
    }
    BaseShowView.OnExchangePostionListener onExchangePostionListener=new BaseShowView.OnExchangePostionListener(){

        @Override
        public void onExchange(View v) {
            if(v.getId()== R.id.ppt_controller_view){
                if(zFrontEnum==ZFrontEnum.ZFRONT_VIDEO_PPT){
                    zFrontEnum=ZFrontEnum.ZFRONT_PPT_VIDEO;
                    initViewXPostion();
                }
            }else if(v.getId()== R.id.livevideoview){
                if(zFrontEnum==ZFrontEnum.ZFRONT_PPT_VIDEO){
                    zFrontEnum=ZFrontEnum.ZFRONT_VIDEO_PPT;
                    initViewXPostion();
                }
            }


        }
    };

    public void changScreenOrientation() {
        changeScreenOrientation();
        playercontrollerview.initViewBgByOrientation(isPortraitScreen());
        activity.changedScreen(isPortraitScreen());
    }

    public void changeScreenOrientation() {
        requestedOrientation = isPortraitScreen() ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    public boolean isPortraitScreen() {
        return requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    public void releaseALL(){
        if(!CheckUtil.isEmpty(bus)&&bus.isRegistered(this)){
            bus.unregister(this);
        }
        livevideoview.releaseAll();
        ppt_controller_view.releaseAll();

    }
    //点击了ppt或视频时的回调函数，注意只有在父组件一样大小时才会收到这些消息，
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ViewClickEvent event) {
        ViewState viewState= event.viewState;
        //todo 处理上下标题栏的显示和隐藏
        initTitleVis(true);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(VideoLoadEvent event) {
        if(event.isLoad){
            playercontrollerview.setEnabled(false);
        }else{
            playercontrollerview.setEnabled(true);
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(PageEvent event) {

        EplayerPlaybackEngin engin= ((EplayerPlaybackEngin)EplayerEngin.getInstance());
        EPlaybackSessionInfo sessionInfo= engin.getSessionInfo();
        if(sessionInfo==null)
            return;
        DrawPadInfo drawPadInfo = sessionInfo.drawPadInfo;
        if(drawPadInfo==null)
            return;
        livevideoview.stopPlayback();
        playercontrollerview.changePlayerState(PlayerControllerView.PlayerState.PLAYERSTATE_PLAY);

        if (engin.isPlayback()){
            engin.pausePlayback();
        }
        zFrontEnum=ZFrontEnum.ZFRONT_ONLY_PPT;
        engin.resetPlaybackPPtId(drawPadInfo.pptId,event.page);
        System.gc();
    }

    private void videoInfoChange(boolean isShow){
        synchronized (zFrontEnum) {
            switch (zFrontEnum) {
                case ZFRONT_NONE: {
                    if(isShow) {
                        zFrontEnum = ZFrontEnum.ZFRONT_ONLY_VIDEO;
                        initViewXPostion();
                    }else{
                        zFrontEnum = ZFrontEnum.ZFRONT_ONLY_PPT;
                        initViewXPostion();
                    }

                    break;
                }
                case ZFRONT_ONLY_PPT: {
                    if (isShow) {
                        zFrontEnum = ZFrontEnum.ZFRONT_PPT_VIDEO;
                        initViewXPostion();
                    }
                    break;
                }
                case ZFRONT_ONLY_VIDEO: {
                    if (!isShow) {
                        zFrontEnum = ZFrontEnum.ZFRONT_NONE;
                        initViewXPostion();
                    }
                    break;
                }

                case ZFRONT_VIDEO_PPT:
                case ZFRONT_PPT_VIDEO: {
                    if (!isShow) {
                        zFrontEnum = ZFrontEnum.ZFRONT_ONLY_PPT;
                        initViewXPostion();
                    }
                    break;
                }
            }

        }
    }

    //翻页回调函数
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(PPTInfoChangeEvent event) {
        synchronized (zFrontEnum) {
            switch (zFrontEnum) {
                case ZFRONT_NONE: {
                    if (event.isContainContent) {
                        zFrontEnum = ZFrontEnum.ZFRONT_ONLY_PPT;
                        initViewXPostion();
                    }
                    break;
                }
                case ZFRONT_ONLY_VIDEO: {
                    if (event.isContainContent) {
                        zFrontEnum = ZFrontEnum.ZFRONT_PPT_VIDEO;
                        initViewXPostion();
                    }
                    break;
                }
                case ZFRONT_ONLY_PPT: {
                    if (!event.isContainContent) {
                        zFrontEnum = ZFrontEnum.ZFRONT_NONE;
                        initViewXPostion();
                    }
                    break;
                }

                case ZFRONT_VIDEO_PPT:
                case ZFRONT_PPT_VIDEO: {
                    if (!event.isContainContent) {
                        zFrontEnum = ZFrontEnum.ZFRONT_ONLY_VIDEO;
                        initViewXPostion();
                    }
                    break;
                }
            }
        }
    }

    //EventBus回调函数
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(VideoAudioStatusEvent event) {

        LiveRoomInfoData data= EplayerEngin.getInstance().getSessionInfo().infoData;
        if(CheckUtil.isEmpty(data))return;


        if(data.isStreamPush&&data.liveStatus==EplayerLiveRoomLiveStatus.LiveRoomLiveStatusPlay){


            livevideoview.stopPlayback();


            PalyTypeState palyTypeState = PalyTypeState.STATE_PLAY_VOIDE;
            if(data.streamType== EplayerLiveRoomStreamType.LiveRoomStreamTypeAudio){
                //todo 播放的是音频
                palyTypeState = PalyTypeState.STATE_PLAY_AUDIO;
                videoInfoChange(false);
            }else{
                //todo  播放的是视频
                palyTypeState = PalyTypeState.STATE_PLAY_VOIDE;
                //todo
                int []infos=getVideoSize();
                livevideoview.initScreenWidthHeight(infos[0],infos[1]);
                videoInfoChange(true);
            }

            LogUtil.d(TAG,"data.getPlayUrl():"+data.getPlayUrl());
            livevideoview.setVideoURI(palyTypeState,data);
            livevideoview.start();

        }else{
            //todo 做停止播放的操作
            livevideoview.stopPlayback();

            videoInfoChange(false);
        }
    }






}
