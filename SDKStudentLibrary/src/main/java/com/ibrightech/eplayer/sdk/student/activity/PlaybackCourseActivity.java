package com.ibrightech.eplayer.sdk.student.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ibrightech.eplayer.sdk.common.engin.EplayerEngin;
import com.ibrightech.eplayer.sdk.common.entity.EPlayerData;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerLiveRoomLiveStatus;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.LivaRoomInfo.LiveRoomInfoData;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.TeacherInfo;
import com.ibrightech.eplayer.sdk.common.entity.session.EPlaybackSessionInfo;
import com.ibrightech.eplayer.sdk.common.net.ws.event.EplayerInitEvent;
import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.common.util.TextViewUtils;
import com.ibrightech.eplayer.sdk.common.util.TimerDelayScheduler;
import com.ibrightech.eplayer.sdk.common.util.ToastUtil;
import com.ibrightech.eplayer.sdk.student.R;
import com.ibrightech.eplayer.sdk.student.entityenum.ScreenDirEnum;
import com.ibrightech.eplayer.sdk.student.event.VideoErrorOutTimeEvent;
import com.ibrightech.eplayer.sdk.student.event.VideoLoadOutTimeEvent;
import com.ibrightech.eplayer.sdk.student.widget.PlaybackControllerView;
import com.ibrightech.eplayer.sdk.teacher.activity.SDKBaseActivity;
import com.ibrightech.eplayer.sdk.teacher.util.ImageUrlUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by zhaoxu2014 on 16/9/19.
 */
public class PlaybackCourseActivity extends SDKBaseActivity implements TimerDelayScheduler.TimeTaskListener{
    RelativeLayout root_view;
    LinearLayout li_back;
    RelativeLayout li_top_title_all;
    ImageView img_change_live_method;
    ImageView img_teacher_icon;
    TextView tv_top_class_name;

    TextView tv_teacher_name;
    PlaybackControllerView palyback_controller_view;
    ScreenDirEnum screenDirEnum=ScreenDirEnum.FULL_PORTRAIT_SCREEN;
    Animation face_top_enter, face_top_exit;
    TimerDelayScheduler timerDelayScheduler;
    @Override
    protected void initView(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        timerDelayScheduler = new TimerDelayScheduler(this);
        setContentView(R.layout.activity_playback);
        root_view = (RelativeLayout) findViewById(R.id.root_view);
        li_top_title_all= (RelativeLayout) findViewById(R.id.li_top_title_all);
        li_back = (LinearLayout) findViewById(R.id.li_back);
        img_change_live_method = (ImageView) findViewById(R.id.img_change_live_method);
        tv_teacher_name= (TextView) findViewById(R.id.tv_teacher_name);
        img_teacher_icon= (ImageView) findViewById(R.id.img_teacher_icon);
        img_change_live_method.setVisibility(View.GONE);
       findViewById(R.id.tv_top_praise_num).setVisibility(View.GONE);

        tv_top_class_name = (TextView) findViewById(R.id.tv_top_class_name);
        tv_top_class_name.setVisibility(View.VISIBLE);
        palyback_controller_view= (PlaybackControllerView) findViewById(R.id.palyback_controller_view);

        face_top_enter = AnimationUtils.loadAnimation(context,R.anim.chat_face_top_enter);
        face_top_exit = AnimationUtils.loadAnimation(context, R.anim.chat_face_top_exit);


        Bundle bd = getIntent().getExtras();
        if(!CheckUtil.isEmpty(bd)) {
            playerData = (EPlayerData) bd.getSerializable(KEY_EPLAY_DATA);

        }
        if (CheckUtil.isEmpty(playerData)) {
            ToastUtil.showStringToast("登陆信息不全");
            finishWithAnimation();
            return;
        }

        toLogin();

        timerDelayScheduler.scheduledWaitTask(TASK_TYPE_LOGIN,TIMER_OUTTIME*2,TimerDelayScheduler.RETRYCOUNT_MAX);

        palyback_controller_view.setSessionInfo(EplayerEngin.getInstance().getSessionInfo());
    }

    private void toLogin(){
        EplayerEngin.getInstance().startLoading();
        startProgressBar();
    }


    @Override
    protected void setListener() {
        li_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!palyback_controller_view.isPortraitScreen()) {
                    palyback_controller_view.changScreenOrientation();
                } else {
                    finishWithAnimation();
                }
            }
        });

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }

    private void initRoomInfo(){
        LiveRoomInfoData roomInfoData = EplayerEngin.getInstance().getSessionInfo().infoData;
        if(CheckUtil.isEmpty(roomInfoData))return;

        EplayerLiveRoomLiveStatus liveStatus =roomInfoData.liveStatus;
        List<TeacherInfo> teacherList =roomInfoData.teacherList;
        String name ="";
        String teachHeadImg="";
        if(!CheckUtil.isEmpty(teacherList)){
            name=teacherList.get(0).name;
            teachHeadImg=teacherList.get(0).headImg;
        }

        TextViewUtils.setText(tv_teacher_name,name);
        Glide.with(this.getApplicationContext()).load(ImageUrlUtil.getUrl(teachHeadImg))
                .bitmapTransform(new CropCircleTransformation(this.getApplicationContext()))
                .placeholder(R.drawable.teacher_icon_default_sdk)
                .error(R.drawable.teacher_icon_default_sdk)
                .into(img_teacher_icon);


        TextViewUtils.setText(tv_top_class_name,roomInfoData.subject);


    }

    //EventBus回调函数
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EplayerInitEvent event) {
        switch (event.type){
            case EplayerInitTypeDataError:
            case EplayerInitTypeLoginError:{
                closeProgressBar();
                ToastUtil.showStringToast((String) event.obj);
                finishWithAnimation();
                break;
            }
            case EplayerInitTypeInitError:{
                closeProgressBar();
                ToastUtil.showToast(R.string.playback_info_isempty);
                finishWithAnimation();
                break;
            }

            case  EplayerInitTypeInitFinished:{
                timerDelayScheduler.invalidateWaitTask(TASK_TYPE_LOGIN);
                closeProgressBar();
                //todo 回看处理完了，开始播放
                EPlaybackSessionInfo sessionInfo= (EPlaybackSessionInfo) EplayerEngin.getInstance().getSessionInfo();
                if(CheckUtil.isEmpty(sessionInfo.segmentArrays)){
                    ToastUtil.showToast(R.string.playback_info_isempty);
                    finishWithAnimation();
                    return;
                }
                screenDirEnum= ScreenDirEnum.FULL_PORTRAIT_SCREEN;
                palyback_controller_view.initScreenInfo(this,SCREEN_WIDTH, SCREEN_HEIGHT,screenDirEnum);

                palyback_controller_view.registEnginListener();
                EplayerEngin.getInstance().startClassEngin();

                initRoomInfo();
                break;
            }
        }
    }

    //播放器错误
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBackgroundThread(VideoErrorOutTimeEvent event) {
        //todo
        requestStop();
        showExitDialog("提示","您的网络太差啦，无法稳定的播放音视频，请切换到稳定网络观看");

    }
    //播放器超时
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBackgroundThread(VideoLoadOutTimeEvent event) {
        //todo
        requestStop();
        showExitDialog("提示","您的网络太差啦，无法稳定的播放音视频，请切换到稳定网络观看");

    }


    @Override
    public void requestStop() {
        try {
            palyback_controller_view.releaseALL();
            EplayerEngin.getInstance().cancelLoading();
            EplayerEngin.getInstance().distory();
        }catch (Exception e){

        }
        super.requestStop();
    }

    @Override
    protected void onStop() {
        requestStop();
        finish();
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        requestStop();
        super.onDestroy();
    }

    public void setTopTitleVisibility(int vis,boolean isAnim){

        if(vis==View.VISIBLE&&li_top_title_all.getVisibility()!=View.VISIBLE){
            li_top_title_all.setVisibility(vis);
            if(isAnim) {
                li_top_title_all.startAnimation(face_top_enter);
            }
        }else if(vis!=View.VISIBLE&&li_top_title_all.getVisibility()==View.VISIBLE){
            li_top_title_all.setVisibility(View.INVISIBLE);
            if(isAnim){
                li_top_title_all.startAnimation(face_top_exit);
            }
        }

    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            changePPTHeight(ScreenDirEnum.FULL_LANDSCAPE_SCREEN);
        } else {
            changePPTHeight(ScreenDirEnum.FULL_PORTRAIT_SCREEN);
        }
    }

    private void changePPTHeight(ScreenDirEnum screenDirEnum) {
        palyback_controller_view.changePPTHeight(screenDirEnum);
    }

    @Override
    public void handlerTimeTaskFinished(long taskToken, int retryCount) {
        if (taskToken == TASK_TYPE_LOGIN) {
            if(retryCount<=0) {
                showExitDialog("提示", "您的网络太差啦，请检查您的网络设置");
            }else {
                toLogin();
            }
        }
    }
}
