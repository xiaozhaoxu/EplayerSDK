package com.ibrightech.eplayer.sdk.student.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ibrightech.eplayer.sdk.common.engin.EplayerEngin;
import com.ibrightech.eplayer.sdk.common.entity.Asset;
import com.ibrightech.eplayer.sdk.common.entity.EPlayerData;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerLiveRoomLiveStatus;
import com.ibrightech.eplayer.sdk.common.entity.Prainse;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.LivaRoomInfo.LiveRoomInfoData;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.SocketMessage;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.TeacherInfo;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.VoteMsgInfo;
import com.ibrightech.eplayer.sdk.common.net.ws.event.EplayerInitEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.FourceLogoutEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.PraiseNumEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.SocketMessageEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.VideoAudioStatusEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.VoteMsgInfoEvent;
import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.common.util.StringUtils;
import com.ibrightech.eplayer.sdk.common.util.TextViewUtils;
import com.ibrightech.eplayer.sdk.common.util.TimerDelayScheduler;
import com.ibrightech.eplayer.sdk.common.util.ToastUtil;
import com.ibrightech.eplayer.sdk.common.util.WeakReferenceHandler;
import com.ibrightech.eplayer.sdk.student.R;
import com.ibrightech.eplayer.sdk.student.dialog.SDKStudentDialogUtil;
import com.ibrightech.eplayer.sdk.student.entityenum.ScreenDirEnum;
import com.ibrightech.eplayer.sdk.student.event.VideoErrorOutTimeEvent;
import com.ibrightech.eplayer.sdk.student.event.VideoLoadOutTimeEvent;
import com.ibrightech.eplayer.sdk.student.widget.LiveControllerView;
import com.ibrightech.eplayer.sdk.teacher.activity.SDKBaseActivity;
import com.ibrightech.eplayer.sdk.teacher.util.ImageUrlUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class LiveCourseActivity extends SDKBaseActivity implements TimerDelayScheduler.TimeTaskListener {


    RelativeLayout root_view;
    LiveControllerView live_controller_view;
    LinearLayout li_back;
    RelativeLayout li_top_title_all;
    ImageView img_change_live_method;
    ImageView img_teacher_icon;
    TextView tv_top_praise_num;
    TextView tv_teacher_name;
    List<SocketMessage> bufferList = new ArrayList<SocketMessage>();
    HashSet<String> msgKeys = new HashSet<String>();
    ScreenDirEnum screenDirEnum=ScreenDirEnum.FULL_PORTRAIT_SCREEN;
    public TreeMap<String, Asset> assetMap = new TreeMap<String, Asset>();//保存所有的ppt或wor列表信息
    Animation face_top_enter, face_top_exit;
    TimerDelayScheduler timerDelayScheduler;
    private Timer timer = new Timer();

    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            Message msg = new Message();
            msg.what = CHAT_MESSAGE;
            chatHandler.sendMessage(msg);
        }
    };
    Handler chatHandler=new WeakReferenceHandler(this){

    public static final double DRAWPADVIEW_SCALE = 9.0 / 16;

        @Override
        protected void handleMessage(Message msg, Object o) {
            if(!CheckUtil.isEmpty(o)){
                switch (msg.what) {

                    case CHAT_MESSAGE:
                        if (CheckUtil.isEmpty(bufferList)) {
                            return;
                        }
                        live_controller_view.addData(bufferList);
                        bufferList.clear();
                        break;
                }
            }
        }
    };

    ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {

        @Override
        public void onGlobalLayout() {
            live_controller_view.changeChatStatus(isKeyboardShown());
        }
    };
    /**
     * 获得状态栏的高度
     *
     * @param context
     * @return
     */
    public static int getStatusHeight(Context context) {

        int statusHeight = -1;
        try {
            Class clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
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
    protected void initView(Bundle savedInstanceState) {
        int statusBarHeight= getStatusHeight(context);
        setContentView(R.layout.activity_live);
        timerDelayScheduler = new TimerDelayScheduler(this);

        root_view = (RelativeLayout) findViewById(R.id.root_view);
        live_controller_view = (LiveControllerView) findViewById(R.id.live_controller_view);
        li_top_title_all= (RelativeLayout) findViewById(R.id.li_top_title_all);
        li_back = (LinearLayout) findViewById(R.id.li_back);
        img_change_live_method = (ImageView) findViewById(R.id.img_change_live_method);
        screenDirEnum=ScreenDirEnum.FULL_PORTRAIT_SCREEN;
        live_controller_view.initScreenInfo(this,SCREEN_WIDTH, SCREEN_HEIGHT , (int) (SCREEN_WIDTH * DRAWPADVIEW_SCALE),statusBarHeight,screenDirEnum);
        tv_teacher_name= (TextView) findViewById(R.id.tv_teacher_name);
        img_teacher_icon= (ImageView) findViewById(R.id.img_teacher_icon);
        tv_top_praise_num= (TextView) findViewById(R.id.tv_top_praise_num);


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
        timerDelayScheduler.scheduledWaitTask(TASK_TYPE_LOGIN,TIMER_OUTTIME,TimerDelayScheduler.RETRYCOUNT_MAX);

        live_controller_view.setSessionInfo(EplayerEngin.getInstance().getSessionInfo());


        timer.scheduleAtFixedRate(task, TIME_DELAY, TIME_DELAY);
    }

    private void toLogin(){
        EplayerEngin.getInstance().startLoading();
        startProgressBar();
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {

            if (!live_controller_view.isPortraitScreen()) {
                live_controller_view.changScreenOrientation();
                return true;
            }

        }
        return super.dispatchKeyEvent(event);
    }



    @Override
    protected void setListener() {

        root_view.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
        li_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!live_controller_view.isPortraitScreen()) {
                    live_controller_view.changScreenOrientation();
                } else {
                    finishWithAnimation();
                }
            }
        });

        img_change_live_method.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (screenDirEnum){
                    case FULL_PORTRAIT_SCREEN:{
                        screenDirEnum=ScreenDirEnum.HALF_FULL_PORTRAIT_SCREEN;
                        break;
                    }
                    case HALF_FULL_PORTRAIT_SCREEN:{
                        screenDirEnum=ScreenDirEnum.FULL_PORTRAIT_SCREEN;
                        break;
                    }
                }
                live_controller_view.hideAddLayout();
                changePPTHeight(screenDirEnum);
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }
    //获取到聊天
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(SocketMessageEvent event) {
        try {

            synchronized (bufferList) {
                List<SocketMessage> list = (List<SocketMessage>) event.getData();
                for (SocketMessage message : list) {
                    if (message.chatInfoKey != null && !msgKeys.contains(message.chatInfoKey)) {
                        bufferList.add(message);
                        msgKeys.add(message.chatInfoKey);
                    }

                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(VideoAudioStatusEvent event) {
        LiveRoomInfoData data = EplayerEngin.getInstance().getSessionInfo().infoData;
        if (null == data) {

            return;
        }
        setTeacherInfo();

    }
    //获取点赞总数
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(PraiseNumEvent event) {
        Prainse prainse = event.getPrainse();
        TextViewUtils.setText(tv_top_praise_num,"" + prainse.getCount());
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
            case EplayerInitTypeSocketEndJoinRoom:{
                timerDelayScheduler.invalidateWaitTask(TASK_TYPE_LOGIN);
                EplayerEngin.getInstance().initStatusReq();
                setUserEntityInfo();
                LiveRoomInfoData infoData= EplayerEngin.getInstance().getSessionInfo().infoData;
                live_controller_view.updateBlackList(infoData.blackList,true);
                live_controller_view.updateBlackList(infoData.canChat);

                break;
            }
            case  EplayerInitTypeInitFinished:{
                closeProgressBar();
                break;
            }
        }
    }

    //老师投票请求
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBackgroundThread(VoteMsgInfoEvent event) {
        //todo
        VoteMsgInfo msgInfo= event.getInfoData();
        SDKStudentDialogUtil.getInstance().showSocialQuestionDialog(this,msgInfo);

    }

    //踢人
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBackgroundThread(FourceLogoutEvent event) {
        //todo
        showExitDialog("下线提醒","\n帐号在其它位置登陆,\n您已经被迫下线!\n");

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

    private void setUserEntityInfo(){

        live_controller_view.setOnlineNum(1);
        setTeacherInfo();

    }
    private void setTeacherInfo(){
        LiveRoomInfoData roomInfoData =EplayerEngin.getInstance().getSessionInfo().infoData;
        EplayerLiveRoomLiveStatus liveStatus =roomInfoData.liveStatus;
        List<TeacherInfo> teacherList =roomInfoData.teacherList;
        String name ="";
        String teachHeadImg="";
        if(!CheckUtil.isEmpty(teacherList)){
            name=teacherList.get(0).name;
            teachHeadImg=teacherList.get(0).headImg;
        }
        String showLiveState = StringUtils.getStringByKey(R.string.name_and_status, name, liveStatus.desc);
        TextViewUtils.setText(tv_teacher_name,showLiveState);
        Glide.with(this.getApplicationContext()).load(ImageUrlUtil.getUrl(teachHeadImg))
                .bitmapTransform(new CropCircleTransformation(this.getApplicationContext()))
                .placeholder(R.drawable.teacher_icon_default_sdk)
                .error(R.drawable.teacher_icon_default_sdk)
                .into(img_teacher_icon);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        try {
            live_controller_view.releaseALL();
        } catch (Exception e) {

        }
        finish();
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        requestStop();
        super.onDestroy();
    }

    @Override
    public void requestStop() {
        try {
            live_controller_view.releaseALL();
            EplayerEngin.getInstance().cancelLoading();
            EplayerEngin.getInstance().distory();
            if (timer != null) {
                try {
                    timer.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (task != null) {
                try {
                    task.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }catch (Exception e){

        }
        super.requestStop();
    }

    private boolean isKeyboardShown() {
        Rect r = new Rect();
        //获取当前界面可视部分
        getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        //获取屏幕的高度
        int screenHeight = getWindow().getDecorView().getRootView().getHeight();
        //此处就是用来获取键盘的高度的， 在键盘没有弹出的时候 此高度为0 键盘弹出的时候为一个正数
        return screenHeight - r.bottom != 0;
    }



    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            changePPTHeight(ScreenDirEnum.FULL_LANDSCAPE_SCREEN);
            img_change_live_method.setVisibility(View.GONE);
        } else {
            changePPTHeight(ScreenDirEnum.HALF_FULL_PORTRAIT_SCREEN);
            img_change_live_method.setVisibility(View.VISIBLE);
        }
    }

    private void changePPTHeight(ScreenDirEnum screenDirEnum) {
        live_controller_view.changePPTHeight(screenDirEnum);
    }



    @Override
    public void handlerTimeTaskFinished(long taskToken,int retryCount) {

        if (taskToken == TASK_TYPE_LOGIN) {
            if(retryCount<=0) {
                showExitDialog("提示", "您的网络太差啦，请检查您的网络设置");
            }else {
                toLogin();
            }
        }

    }
}
