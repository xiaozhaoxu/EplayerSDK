package com.ibrightech.eplayer.sdk.teacher.ui;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ibrightech.eplayer.sdk.common.engin.EplayerEngin;
import com.ibrightech.eplayer.sdk.common.entity.Asset;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerLiveRoomLiveStatus;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.SocketMessage;
import com.ibrightech.eplayer.sdk.common.entity.system.DocumentItem;
import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.common.util.StringUtils;
import com.ibrightech.eplayer.sdk.common.util.TextViewUtils;
import com.ibrightech.eplayer.sdk.teacher.R;
import com.ibrightech.eplayer.sdk.teacher.activity.LivePushActivity;
import com.ibrightech.eplayer.sdk.teacher.activity.SDKBaseActivity;
import com.ibrightech.eplayer.sdk.teacher.adapter.ChatAudioAdapter;
import com.ibrightech.eplayer.sdk.teacher.dialog.SelectCoursewareDialog;

import java.util.List;

/**
 * Created by zhaoxu2014 on 16/5/27.
 */
public class LivePushAudioControllerView extends LinearLayout {


    public static final double DRAWPADVIEW_SCALE = (double) 9.0 / 16;
    int SCREEN_WIDTH;//屏幕宽度
    int SCREEN_HEIGHT;//屏幕高度
    SDKBaseActivity livePushActivity;
    ListView listview;
    RelativeLayout rl_audio_all;
    AudioChatControllerView audio_chat_view;
    LivePushAudioPPTControllerView pptcontrollerview;
    RelativeLayout rl_audio_ppt;
    ImageView iv_audio;
    TextView tv_audio_online_num;



    int requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

    ChatAudioAdapter adapter;
    EplayerLiveRoomLiveStatus liveStatus;

    public void setTeachername(String teachername) {
        adapter.teachername = teachername;
    }

    public void setOnlineNum(int num) {
        TextViewUtils.setText(tv_audio_online_num, StringUtils.getStringByKey(R.string.online_num, num));
    }

    public void setLiveStatus(EplayerLiveRoomLiveStatus liveStatus){
        this.liveStatus = liveStatus;
        if(EplayerLiveRoomLiveStatus.LiveRoomLiveStatusPlay==liveStatus){
            ((AnimationDrawable) iv_audio.getDrawable()).start();
        }else{
            ((AnimationDrawable) iv_audio.getDrawable()).stop();
        }
    }

    public void showFace(boolean isFocused){
        audio_chat_view.changeChatStatus(isFocused);
    }

    public void setCourseWareChangeListener(SelectCoursewareDialog.OnItemClickListener courseWareChangeListener){
        if(!CheckUtil.isEmpty(courseWareChangeListener)) {
            audio_chat_view.setCourseWareListener(courseWareChangeListener);
        }
    }



    public void initScreenOrientation(int orientation) {
        this.requestedOrientation = orientation;
    }

    public void initChatStatus(boolean chatLock){
        audio_chat_view.initChatStatus(chatLock);
    }




    public void setPPTList(Asset drawPadInfo, int selectpage, List<DocumentItem> documentItemList){
        pptcontrollerview.setPPTList(drawPadInfo,selectpage,documentItemList);

    }


    public int changeScreenOrientation() {
        if (isPortraitScreen()) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }
        return requestedOrientation;
    }

    public boolean isPortraitScreen() {
        return requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    public LivePushAudioControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public LivePushAudioControllerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public LivePushAudioControllerView(Context context) {
        super(context);
        initView(context);
    }

    public void startEventListener() {
        pptcontrollerview.startEventListener();

    }
    public void stopEventListener() {
        pptcontrollerview.stopEventListener();
    }

    public void initScreenWidthHeight(LivePushActivity livePushActivity, int SCREEN_WIDTH, int SCREEN_HEIGHT,String couseImageUrl) {
        this.livePushActivity = livePushActivity;
        this.SCREEN_WIDTH = SCREEN_WIDTH;
        this.SCREEN_HEIGHT = SCREEN_HEIGHT;
        initScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        LinearLayout.LayoutParams lp3 = (LinearLayout.LayoutParams) rl_audio_ppt.getLayoutParams();
        lp3.width = SCREEN_WIDTH;
        lp3.height = (int) (SCREEN_WIDTH * DRAWPADVIEW_SCALE);

        pptcontrollerview.initScreenWidthHeight(livePushActivity,SCREEN_WIDTH,SCREEN_HEIGHT,couseImageUrl);
    }

    LivePushAudioPPTControllerView.OnPPTListener onPPTListener = new LivePushAudioPPTControllerView.OnPPTListener() {

        @Override
        public void onChangeScreenOrientation() {
            changScreenOrientation();
        }
    };

    public void changScreenOrientation() {
        changeScreenOrientation();
        int bgid = 0;
        if (isPortraitScreen()) {
            bgid = R.drawable.narrow_btn;

            rl_audio_all.removeView(pptcontrollerview);
            rl_audio_ppt.addView(pptcontrollerview);

        } else {
            bgid = R.drawable.narrow_btn2;
            rl_audio_ppt.removeView(pptcontrollerview);
            rl_audio_all.addView(pptcontrollerview);
        }
        pptcontrollerview.setScaleBtBg(bgid);
        pptcontrollerview.showHintSpace();
        livePushActivity.changedScreen(isPortraitScreen());
    }


    private void initView(Context context) {
        View rootview = View.inflate(context, R.layout.livepush_audio_controller, null);


         listview= (ListView) rootview.findViewById(R.id.listview);
         rl_audio_all= (RelativeLayout) rootview.findViewById(R.id.rl_audio_all);
         audio_chat_view= (AudioChatControllerView) rootview.findViewById(R.id.audio_chat_view);
         pptcontrollerview= (LivePushAudioPPTControllerView) rootview.findViewById(R.id.pptcontrollerview);
         rl_audio_ppt= (RelativeLayout) rootview.findViewById(R.id.rl_audio_ppt);
         iv_audio= (ImageView) rootview.findViewById(R.id.iv_audio);
         tv_audio_online_num= (TextView) rootview.findViewById(R.id.tv_audio_online_num);


        this.addView(rootview);
        listview.addFooterView(new View(context));
        adapter = new ChatAudioAdapter(context);
        listview.setAdapter(adapter);

        pptcontrollerview.setOnPPTListener(onPPTListener);

        setListener();

    }





    public void addData(List<SocketMessage> socketMessages) {
        if (adapter.getCount() == 0) {
            adapter.addNewDatas(socketMessages);
        } else {
            adapter.addMoreDatas(socketMessages);
        }
        listview.setSelection(ListView.FOCUS_DOWN);
    }

    private void setListener(){
        audio_chat_view.setListener(audioChatControllerListener);
    }


    AudioChatControllerView.OnChatClick audioChatControllerListener = new AudioChatControllerView.OnChatClick() {

        @Override
        public void chatControl(boolean closeChat, boolean sendMessage) {
            if (sendMessage) {
                EplayerEngin.getInstance().changechatReq(closeChat);
            }
        }
    };
}
