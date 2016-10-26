package com.ibrightech.eplayer.sdk.teacher.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ibrightech.eplayer.sdk.common.engin.EplayerEngin;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.SocketMessage;
import com.ibrightech.eplayer.sdk.teacher.R;
import com.ibrightech.eplayer.sdk.teacher.adapter.ChatVideoAdapter;

import java.util.List;

/**
 * Created by zhaoxu2014 on 16/5/27.
 */
public class LivePushVideoControllerView extends LinearLayout {

    ListView listview;
    ChatControllerView video_chat_view;
    ChatVideoAdapter adapter;

    Context context;


    public void showFace(boolean isFocuse){
        video_chat_view.setFocused(isFocuse);
    }

    public void setTeachername(String teachername) {
        adapter.teachername = teachername;
    }

    public void setPraiseNum(int num){
        video_chat_view.setPraiseNum(num);
    }


    public void initChatStatus(boolean chatLock){
        video_chat_view.initChatStatus(chatLock);
    }

    public LivePushVideoControllerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public LivePushVideoControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }


    public LivePushVideoControllerView(Context context) {
        super(context);
        initView(context);
    }


    private void initView(Context context) {
        View rootview = View.inflate(context, R.layout.livepush_video_controller, null);
        listview= (ListView) rootview.findViewById(R.id.listview);
        video_chat_view= (ChatControllerView) rootview.findViewById(R.id.video_chat_view);


        this.addView(rootview);
        setListener();
        this.context = context;
        adapter = new ChatVideoAdapter(context);
        listview.setAdapter(adapter);
    }

    public void addData(List<SocketMessage> socketMessages) {
        if(adapter.getCount()==0) {
            adapter.addNewDatas(socketMessages);
        }else{
            adapter.addMoreDatas(socketMessages);
        }
        listview.setSelection(ListView.FOCUS_DOWN);
    }

    private void setListener(){
        video_chat_view.setListener(chatControllerListener);
    }


    ChatControllerView.OnClickListener chatControllerListener = new ChatControllerView.OnClickListener() {

        @Override
        public void chatControl(boolean closeChat, boolean sendMessage) {
            if (sendMessage) {
                 EplayerEngin.getInstance().changechatReq(closeChat);
            }
            if (closeChat) {
                listview.setVisibility(GONE);
                video_chat_view.hideContrller();
            } else {
                listview.setVisibility(VISIBLE);
                video_chat_view.showContrller();
            }
        }
    };

}
