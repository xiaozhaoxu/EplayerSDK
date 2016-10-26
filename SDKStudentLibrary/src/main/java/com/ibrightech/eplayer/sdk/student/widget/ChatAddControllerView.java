package com.ibrightech.eplayer.sdk.student.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ibrightech.eplayer.sdk.common.engin.EplayerEngin;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerMessageChatType;
import com.ibrightech.eplayer.sdk.common.util.TextViewUtils;
import com.ibrightech.eplayer.sdk.student.R;
import com.ibrightech.eplayer.sdk.student.dialog.SDKStudentDialogUtil;
import com.ibrightech.eplayer.sdk.student.event.ChatStateEvent;
import com.ibrightech.eplayer.sdk.teacher.adapter.BaseChatAdapter;

import org.greenrobot.eventbus.EventBus;

public class ChatAddControllerView extends LinearLayout {
    Context context;
    RelativeLayout layout_praise,layout_question,layout_whole_gag;
    TextView tv_audio_whole_gag;
    public ChatAddControllerView(Context context) {
        super(context);
        initView(context);
    }

    public ChatAddControllerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public ChatAddControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context){
        this.context=context;
        View rootView = View.inflate(context, R.layout.chat_add_controller, null);
        layout_praise= (RelativeLayout) rootView.findViewById(R.id.layout_praise);
        layout_question= (RelativeLayout) rootView.findViewById(R.id.layout_question);
        layout_whole_gag= (RelativeLayout) rootView.findViewById(R.id.layout_whole_gag);

        tv_audio_whole_gag= (TextView) rootView.findViewById(R.id.tv_audio_whole_gag);
        this.addView(rootView);

        initChatViewInfo(true,false);
        setListener();
    }

    private void initChatViewInfo(boolean isShow,boolean sendEvent){
        layout_whole_gag.setTag(isShow);
        int strid= isShow?R.string.hint_chat:R.string.show_chat;
        int topimageid=isShow?R.drawable.student_hint_chat_icon:R.drawable.student_show_chat_icon;

        TextViewUtils.setText(tv_audio_whole_gag,strid);
        Drawable topDrawable = getResources().getDrawable(topimageid);
        topDrawable.setBounds(0, 0, topDrawable.getMinimumWidth(), topDrawable.getMinimumHeight());

        tv_audio_whole_gag.setCompoundDrawables(null,topDrawable,null,null);
        if(sendEvent) {
            EventBus.getDefault().post(new ChatStateEvent(isShow));
        }
    }

    private void setListener(){
        layout_praise.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
               //todo 点赞的处理
                EplayerEngin.getInstance().praiseReq();
                EplayerEngin.getInstance().chatReq(BaseChatAdapter.ANIMATION_A_ZAN, EplayerMessageChatType.MessageChatTypeReward.value());

                layout_praise.setAlpha((float) 0.5);
                layout_praise.setClickable(false);
                hideSelf();
            }
        });
        layout_question.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SDKStudentDialogUtil.getInstance().showQuestionDialog(context);
                hideSelf();
            }
        });
        layout_whole_gag.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                initChatViewInfo(!(boolean) layout_whole_gag.getTag(),true);
                hideSelf();
            }
        });

    }
    private void hideSelf(){
        if(this.getVisibility()==View.VISIBLE){
            this.setVisibility(View.GONE);
        }
    }
}
