package com.ibrightech.eplayer.sdk.student.widget;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ibrightech.eplayer.sdk.common.engin.EplayerEngin;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerLiveRoomLiveStatus;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerLiveRoomStreamType;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.ForbidMessage;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.LivaRoomInfo.LiveRoomInfoData;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.SocketMessage;
import com.ibrightech.eplayer.sdk.common.entity.session.EplayerSessionInfo;
import com.ibrightech.eplayer.sdk.common.net.ws.event.ChatControlEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.ForbinChatEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.VideoAudioStatusEvent;
import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.common.util.LogUtil;
import com.ibrightech.eplayer.sdk.common.util.StringUtils;
import com.ibrightech.eplayer.sdk.common.util.TextViewUtils;
import com.ibrightech.eplayer.sdk.common.util.WeakReferenceHandler;
import com.ibrightech.eplayer.sdk.event.Enum.ViewState;
import com.ibrightech.eplayer.sdk.event.ViewClickEvent;
import com.ibrightech.eplayer.sdk.student.R;
import com.ibrightech.eplayer.sdk.student.activity.LiveCourseActivity;
import com.ibrightech.eplayer.sdk.student.adapter.BaseStudentChatAdapter;
import com.ibrightech.eplayer.sdk.student.entityenum.PalyTypeState;
import com.ibrightech.eplayer.sdk.student.entityenum.ScreenDirEnum;
import com.ibrightech.eplayer.sdk.student.entityenum.ZFrontEnum;
import com.ibrightech.eplayer.sdk.student.event.ChatStateEvent;
import com.ibrightech.eplayer.sdk.student.event.MusiceChangeStateEvent;
import com.ibrightech.eplayer.sdk.student.event.MusiceStateEvent;
import com.ibrightech.eplayer.sdk.student.util.ViewUtil;
import com.ibrightech.eplayer.sdk.teacher.event.PPTInfoChangeEvent;
import com.ibrightech.eplayer.sdk.widget.BaseShowView;
import com.ibrightech.eplayer.sdk.widget.DrawPadControllerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


public class LiveControllerView extends RelativeLayout {
    public static final int MSG_HIDE_HINT_SPACE=100;
    public static final int SHOW_HINT_TIME=1000*5;


    public String TAG=LiveControllerView.this.getClass().getSimpleName();
    DrawPadControllerView ppt_controller_view;
    StudentChatControllerView chat_controller_view;
    RelativeLayout layout_ppt;
    RelativeLayout root_view;
    ListView list_view;
    ImageView bt_scale;
    LinearLayout  li_bottom_space;
    LinearLayout li_scale;
    LinearLayout layout_live_status;
    ImageView iv_music;
    LinearLayout layout_music,li_cat;
    TextView tv_music_name;
    LiveVideoView livevideoview;

    Context context;
    LiveCourseActivity liveCourseActivity;
    TextView tv_audio_online_num;

    BaseStudentChatAdapter chatAdapter;
    int requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    Animation music_rotate;

    EventBus bus;
    EplayerLiveRoomLiveStatus muisceStatus;//这个是保存音乐播放状态的，和房间状态区分开
    Animation musicEnter,musicExit;

    ScreenDirEnum screenDirEnum;

    ZFrontEnum zFrontEnum=ZFrontEnum.ZFRONT_NONE;//Z轴ppt和video的上下显示顺序
    public boolean personChatForbid=false;//个人禁言
    public boolean allChatForbid=false;//全体禁言
    Animation  face_enter, face_exit;

    LiveControllerViewHandler handler=new LiveControllerViewHandler(this);
    static class  LiveControllerViewHandler extends WeakReferenceHandler<LiveControllerView>{
        public LiveControllerViewHandler(LiveControllerView view) {
            super(view);
        }
        @Override
        protected void handleMessage(Message msg, LiveControllerView view) {
             switch (msg.what){
                 case MSG_HIDE_HINT_SPACE:{
                     view.initTitleVis(true);
                     break;
                 }
             }
        }
    }


    public LiveControllerView(Context context) {
        super(context);
        initView(context);
    }

    public LiveControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public LiveControllerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        bus = EventBus.getDefault();
        bus.register(this);
        this.context = context;
        face_enter = AnimationUtils.loadAnimation(context, R.anim.chat_face_enter);
        face_exit = AnimationUtils.loadAnimation(context, R.anim.chat_face_exit);


        View rootView = View.inflate(context, R.layout.live_controller, this);
        ppt_controller_view = (DrawPadControllerView) rootView.findViewById(R.id.ppt_controller_view);
        chat_controller_view = (StudentChatControllerView) rootView.findViewById(R.id.chat_controller_view);
        layout_ppt = (RelativeLayout) rootView.findViewById(R.id.rl_ppt_area);
        root_view = (RelativeLayout)rootView.findViewById(R.id.root_view);
        list_view = (ListView) rootView.findViewById(R.id.list_view);

        bt_scale = (ImageView) rootView.findViewById(R.id.bt_scale);
        layout_live_status = (LinearLayout) rootView.findViewById(R.id.layout_live_status);
        iv_music = (ImageView) rootView.findViewById(R.id.iv_music);
        layout_music = (LinearLayout) rootView.findViewById(R.id.layout_music);
        tv_music_name= (TextView) rootView.findViewById(R.id.tv_music_name);
        li_cat= (LinearLayout) rootView.findViewById(R.id.li_cat);
        tv_audio_online_num= (TextView) rootView.findViewById(R.id.tv_audio_online_num);
        li_bottom_space= (LinearLayout) rootView.findViewById(R.id.li_bottom_space);
        livevideoview= (LiveVideoView) rootView.findViewById(R.id.livevideoview);
        li_scale= (LinearLayout) rootView.findViewById(R.id.li_scale);


        chatAdapter = new BaseStudentChatAdapter(context);
        list_view.setAdapter(chatAdapter);
        music_rotate = AnimationUtils.loadAnimation(context, R.anim.music_roate);
        LinearInterpolator lir = new LinearInterpolator();
        music_rotate.setInterpolator(lir);

        setListener();
    }

    public void setSessionInfo(EplayerSessionInfo sessionInfo) {
        ppt_controller_view.setSessionInfo(sessionInfo);
    }
    public void setOnlineNum(int num) {
        TextViewUtils.setText(tv_audio_online_num, StringUtils.getStringByKey(R.string.online_num, num));
    }
    public void addData(List<SocketMessage> socketMessages) {
        chatAdapter.addSpeakList(socketMessages);

        list_view.setSelection(ListView.FOCUS_DOWN);
    }

    private void setListener() {
        li_scale.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                changScreenOrientation();
            }
        });
        layout_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (muisceStatus == EplayerLiveRoomLiveStatus.LiveRoomLiveStatusPlay) {
                    muisceStatus=EplayerLiveRoomLiveStatus.LiveRoomLiveStatusPause;
                    chageMuisceViewVisibility(true, false, R.string.music_pause);
                    bus.post(new MusiceStateEvent(muisceStatus));
                } else {
                    muisceStatus=EplayerLiveRoomLiveStatus.LiveRoomLiveStatusPlay;
                    chageMuisceViewVisibility(true, false, R.string.music_play);
                    bus.post(new MusiceStateEvent(muisceStatus));
                }

            }
        });
        ppt_controller_view.setExchangeListener(onExchangePostionListener);
        livevideoview.setExchangeListener(onExchangePostionListener);

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

    public void changePPTHeight(ScreenDirEnum screenDirEnum){
        this.screenDirEnum=screenDirEnum;
        chatAdapter.setTheme(screenDirEnum);

        LayoutParams lp3 = (LayoutParams) layout_ppt.getLayoutParams();

        switch ( screenDirEnum){
            case FULL_PORTRAIT_SCREEN:{
                lp3.width = screenWidth;
                lp3.height = screenHeight-statusBarHeight;
                chat_controller_view.setBackgroundColor(Color.TRANSPARENT);

                break;
            }
            case HALF_FULL_PORTRAIT_SCREEN:{
                lp3.width = screenWidth;
                lp3.height = minScreenHeight;
                chat_controller_view.setBackgroundColor(getResources().getColor(R.color.common_bg));

                break;
            }
            case FULL_LANDSCAPE_SCREEN:{
                lp3.width = screenHeight;
                lp3.height = screenWidth;
                break;
            }
        }
        ViewUtil.setViewMargins(li_cat, 0, minScreenHeight + ViewUtil.dipToPx(context, 39), 0, 0);
        chat_controller_view.setBackground(screenDirEnum);

        initViewXPostion();
        initTitleVis(false);
    }

    //改变ppt和video上下显示顺序
    public void initViewXPostion() {

        livevideoview.clearViewPostion();
        ppt_controller_view.clearViewPostion();
        LayoutParams lp3 = (LayoutParams) layout_ppt.getLayoutParams();
        switch (screenDirEnum) {
            case FULL_PORTRAIT_SCREEN: {
                ppt_controller_view.setVisibility(View.GONE);
                livevideoview.setVisibility(View.VISIBLE);
                livevideoview.initScreenWidthHeight(lp3.width, lp3.height);
                livevideoview.bringToFront();
                break;
            }
            case HALF_FULL_PORTRAIT_SCREEN:
            case FULL_LANDSCAPE_SCREEN: {

                switch (zFrontEnum){
                    case ZFRONT_NONE:
                    case ZFRONT_ONLY_VIDEO:{
                        ppt_controller_view.setVisibility(View.GONE);
                        livevideoview.setVisibility(View.VISIBLE);
                        livevideoview.setCanMove(false);

                        livevideoview.initScreenWidthHeight(lp3.width,lp3.height);
                        break;
                    }
                    case ZFRONT_ONLY_PPT:{
                        ppt_controller_view.setVisibility(View.VISIBLE);
                        livevideoview.setVisibility(View.GONE);

                        ppt_controller_view.setCanMove(false);
                        ppt_controller_view.initScreenWidthHeight( lp3.width,  lp3.height);
                        break;
                    }
                    case ZFRONT_VIDEO_PPT:{
                        //Video填充父组件，ppt按小图显示
                        ppt_controller_view.setVisibility(View.VISIBLE);
                        livevideoview.setVisibility(View.VISIBLE);

                        livevideoview.initScreenWidthHeight(lp3.width,lp3.height);
                        ppt_controller_view.initViewLeftBottomPostion();
                        ppt_controller_view.bringToFront();

                        livevideoview.setCanMove(false);
                        ppt_controller_view.setCanMove(true);
                        break;
                    }
                    case ZFRONT_PPT_VIDEO:{
                        //Video填充父组件，ppt按小图显示
                        ppt_controller_view.setVisibility(View.VISIBLE);
                        livevideoview.setVisibility(View.VISIBLE);

                        ppt_controller_view.initScreenWidthHeight( lp3.width,  lp3.height);
                        livevideoview.initViewLeftBottomPostion();
                        livevideoview.bringToFront();

                        livevideoview.setCanMove(true);
                        ppt_controller_view.setCanMove(false);
                        break;
                    }
                }
                break;
            }
        }
    }

    private void removeAllMessage(){
        if(!CheckUtil.isEmpty(handler)){
            handler.removeMessages(MSG_HIDE_HINT_SPACE);
        }

    }

    /*改变上下标题的显示
     *isChangeVis 是否切换当前状态
     */
    public void initTitleVis(boolean isChangeVis){
        removeAllMessage();
        switch ( screenDirEnum){
            case FULL_PORTRAIT_SCREEN:{
                liveCourseActivity.setTopTitleVisibility(View.VISIBLE,true);
                this.setBottomSpaceVisibility(View.GONE,false);
                break;
            }
            case HALF_FULL_PORTRAIT_SCREEN:
            case FULL_LANDSCAPE_SCREEN:{
                if(!isChangeVis) {
                    liveCourseActivity.setTopTitleVisibility(View.VISIBLE, true);
                    this.setBottomSpaceVisibility(View.VISIBLE, true);

                    handler.sendEmptyMessageDelayed(MSG_HIDE_HINT_SPACE,SHOW_HINT_TIME);
                }else{
                    int vis=li_bottom_space.getVisibility()==View.VISIBLE?View.GONE:View.VISIBLE;
                    liveCourseActivity.setTopTitleVisibility(vis, true);
                    this.setBottomSpaceVisibility(vis, true);

                    if(vis==View.VISIBLE){
                        handler.sendEmptyMessageDelayed(MSG_HIDE_HINT_SPACE,SHOW_HINT_TIME);
                    }
                }
                break;
            }

        }
    }

    public void setBottomSpaceVisibility(int vis,boolean isAnim){

        if(vis==View.VISIBLE&&li_bottom_space.getVisibility()!=View.VISIBLE){
            li_bottom_space.setVisibility(vis);
            if(isAnim) {
                li_bottom_space.startAnimation(face_enter);
            }
        }else if(vis!=View.VISIBLE&&li_bottom_space.getVisibility()==View.VISIBLE){
            li_bottom_space.setVisibility(View.INVISIBLE);
            if(isAnim){
                li_bottom_space.startAnimation(face_exit);
            }
        }

    }

    int screenWidth,  screenHeight, minScreenHeight,statusBarHeight;
    public void initScreenInfo(LiveCourseActivity liveCourseActivity, int screenWidth, int screenHeight, int minScreenHeight, int statusBarHeight, ScreenDirEnum screenDirEnum) {
        this.screenWidth=screenWidth;
        this.screenHeight=screenHeight;
        this.minScreenHeight=minScreenHeight;
        this.statusBarHeight=statusBarHeight;
        this.liveCourseActivity = liveCourseActivity;
        changePPTHeight(screenDirEnum);
    }

    public void changeChatStatus(boolean focused) {
        chat_controller_view.changeChatStatus(focused);
    }

    public void changScreenOrientation() {
        changeScreenOrientation();
        if(isPortraitScreen()){
            chat_controller_view.setVisibility(VISIBLE);
            list_view.setVisibility(VISIBLE);
            layout_live_status.setVisibility(VISIBLE);

            if(livevideoview.isPlayMuiceState()){
                chageMuisceViewVisibility(true,false);
            }
        }else{
            chat_controller_view.setVisibility(GONE);
            list_view.setVisibility(GONE);
            layout_live_status.setVisibility(GONE);
            chageMuisceViewVisibility(false,false);
        }
        bt_scale.setImageResource(isPortraitScreen() ? R.drawable.narrow_btn : R.drawable.narrow_btn2);
        ppt_controller_view.showHintSpace();
        liveCourseActivity.changedScreen(isPortraitScreen());
    }

    public void hideAddLayout(){
        chat_controller_view.hideFaceArea();
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
        removeAllMessage();

    }

    //刚进入课堂时的聊天控制
    public void updateBlackList(List<String> bist,boolean personchatForbid){
        initChatView();
        chatAdapter.updateBlackList(bist,personchatForbid);

    }
    public void updateBlackList(boolean allchatForbid){
        this.allChatForbid=allchatForbid;
        initChatView();
        chatAdapter.updateBlackList(allchatForbid);

    }

    public void initChatView(){
       chat_controller_view.initChatForbid(isChatForbid());

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
            case HALF_FULL_PORTRAIT_SCREEN:{

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


    //点击了ppt或视频时的回调函数，注意只有在父组件一样大小时才会收到这些消息，
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ViewClickEvent event) {
        ViewState viewState= event.viewState;
        //todo 处理上下标题栏的显示和隐藏
        initTitleVis(true);
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
    private void videoInfoChange(boolean isShow){
        synchronized (zFrontEnum) {
            switch (zFrontEnum) {
                case ZFRONT_NONE: {
                    if (isShow) {
                        zFrontEnum = ZFrontEnum.ZFRONT_ONLY_VIDEO;
                        initViewXPostion();
                    }else{
                        zFrontEnum = ZFrontEnum.ZFRONT_ONLY_PPT;
                        changePPTHeight(ScreenDirEnum.HALF_FULL_PORTRAIT_SCREEN);
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



    //EventBus回调函数
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(VideoAudioStatusEvent event) {

        LiveRoomInfoData data= EplayerEngin.getInstance().getSessionInfo().infoData;
        if(CheckUtil.isEmpty(data))return;
        if(data.liveStatus==EplayerLiveRoomLiveStatus.LiveRoomLiveStatusPause||data.liveStatus==EplayerLiveRoomLiveStatus.LiveRoomLiveStatusClose){
            if(!isPortraitScreen()){
                changScreenOrientation();
            }
        }

        if(data.isStreamPush&&data.liveStatus==EplayerLiveRoomLiveStatus.LiveRoomLiveStatusPlay){


            livevideoview.stopPlayback();


            PalyTypeState palyTypeState = PalyTypeState.STATE_PLAY_VOIDE;
            if(data.streamType== EplayerLiveRoomStreamType.LiveRoomStreamTypeAudio){
                //todo 播放的是音频
                palyTypeState = PalyTypeState.STATE_PLAY_AUDIO;

            }else{
                //todo  播放的是视频
                palyTypeState = PalyTypeState.STATE_PLAY_VOIDE;
                int []infos=getVideoSize();
                livevideoview.initScreenWidthHeight(infos[0],infos[1]);

            }

            LogUtil.d(TAG,"data.getPlayUrl():"+data.getPlayUrl());
            livevideoview.setVideoURI(palyTypeState,data);
            livevideoview.start();
            videoInfoChange(true);
        }else{
            //todo 做停止播放的操作
            livevideoview.stopPlayback();

            videoInfoChange(false);
        }
    }


    //当前用户是否是禁言状态
    public boolean isChatForbid(){
        return personChatForbid||allChatForbid;
    }


    //进入课堂后的聊天控制
    //禁言和取消禁言(某个人的)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBackgroundThread(ChatStateEvent event) {
       int vis= event.isShow()?View.VISIBLE:View.GONE;
        list_view.setVisibility(vis);
    }

    //进入课堂后的聊天控制
    //禁言和取消禁言(某个人的)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBackgroundThread(ForbinChatEvent event) {
        ForbidMessage forbidMessage=event.getForbidMessage();
        if(null==forbidMessage){
            return;
        }
        List<String> li = new ArrayList<String>();
        li.add(forbidMessage.userKey);
        this.personChatForbid=forbidMessage.chatForbid;
        initChatView();
        chatAdapter.updateBlackList(li, forbidMessage.chatForbid);
    }

    //聊生控制(禁言和取消禁言全部人的)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBackgroundThread(ChatControlEvent event) {
        LiveRoomInfoData infoData= event.getInfoData();
        this.allChatForbid=infoData.canChat;
        initChatView();
        chatAdapter.updateBlackList(infoData.canChat);

    }

    //根据音乐状态来做显示
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMethod(MusiceChangeStateEvent event){
        muisceStatus=  event.getStatus();
        switch (muisceStatus){
            case  LiveRoomLiveStatusPlay:{
                //todo 显示

                chageMuisceViewVisibility(true,true,R.string.music_play);

                break;
            }
            case LiveRoomLiveStatusClose:{
                //todo 隐藏
                chageMuisceViewVisibility(false,true);
                break;
            }
        }
    }


    private void chageMuisceViewVisibility(boolean isShow,boolean isAnim){
        chageMuisceViewVisibility(isShow,isAnim,0);
    }
    private void chageMuisceViewVisibility(boolean isShow,boolean isAnim,int showTitle){
        if(isShow){
            if(showTitle>0){
                TextViewUtils.setText(tv_music_name,showTitle);
                if(showTitle==R.string.music_play){
                    iv_music.startAnimation(music_rotate);
                }else if(showTitle==R.string.music_pause){
                    iv_music.clearAnimation();
                }
            }
            if(layout_music.getVisibility()!=View.VISIBLE) {
                if (isAnim) {

                    if (CheckUtil.isEmpty(musicEnter)) {
                        musicEnter = AnimationUtils.loadAnimation(context, R.anim.music_enter);
                    }
                    layout_music.startAnimation(musicEnter);
                }
                layout_music.setVisibility(View.VISIBLE);
            }

        }else{
            if(layout_music.getVisibility()==View.VISIBLE) {
                if (isAnim) {
                    if (CheckUtil.isEmpty(musicExit)) {
                        musicExit = AnimationUtils.loadAnimation(context, R.anim.music_exit);
                    }
                    layout_music.startAnimation(musicExit);
                }
                layout_music.setVisibility(View.GONE);
            }
        }
    }



}
