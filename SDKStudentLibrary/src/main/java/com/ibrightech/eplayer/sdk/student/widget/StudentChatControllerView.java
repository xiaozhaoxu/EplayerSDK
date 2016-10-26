package com.ibrightech.eplayer.sdk.student.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ibrightech.eplayer.sdk.common.engin.EplayerEngin;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerMessageChatType;
import com.ibrightech.eplayer.sdk.common.util.StringUtils;
import com.ibrightech.eplayer.sdk.common.util.ToastUtil;
import com.ibrightech.eplayer.sdk.student.R;
import com.ibrightech.eplayer.sdk.student.entityenum.ScreenDirEnum;
import com.ibrightech.eplayer.sdk.teacher.ui.FaceChatView;
import com.ibrightech.eplayer.sdk.teacher.util.VibratorUtil;

import java.util.List;


public class StudentChatControllerView extends LinearLayout {
    Context context;

    Animation shake, face_enter, face_exit;
    LinearLayout li_chat_all;
    ImageView iv_input;
    EditText et_bottom_speak;
    ImageView iv_add;
    TextView tv_send;
    FaceChatView face_chat_view;
    ChatAddControllerView layout_add;
    boolean focused;
    InputMethodManager inputMethodManager;
    ScreenDirEnum screenDirEnum;
    boolean isChatForbid=false;

    public StudentChatControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public StudentChatControllerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public StudentChatControllerView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        this.context = context;
        inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        View rootView = View.inflate(context, R.layout.student_chat_controller, this);
        li_chat_all = (LinearLayout) rootView.findViewById(R.id.li_chat_all);
        iv_input = (ImageView) rootView.findViewById(R.id.iv_input);
        et_bottom_speak = (EditText) rootView.findViewById(R.id.et_bottom_speak);
        iv_add = (ImageView) rootView.findViewById(R.id.iv_add);
        tv_send = (TextView) rootView.findViewById(R.id.tv_send);
        face_chat_view = (FaceChatView) rootView.findViewById(R.id.face_chat_view);
        layout_add = (ChatAddControllerView) rootView.findViewById(R.id.layout_add);

        shake = AnimationUtils.loadAnimation(context, R.anim.chat_shake);
        face_enter = AnimationUtils.loadAnimation(context, R.anim.chat_face_enter);
        face_exit = AnimationUtils.loadAnimation(context, R.anim.chat_face_exit);


        List<LinearLayout> tab1liList = face_chat_view.getTab1liList();
        for (int i = 0; i < tab1liList.size(); i++) {
            LinearLayout tab1li = tab1liList.get(i);
            if (i != tab1liList.size() - 1) {
                final String contentDescription = tab1li.getContentDescription().toString();
                tab1li.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        et_bottom_speak.append("[" + contentDescription + "]");
                    }
                });
            } else {
                tab1li.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        deleteBtn();
                    }
                });
            }
        }
        setViewClick();
    }
    //禁言状态下样式的处理
    public void initChatForbid(boolean isChatForbid){
        this.isChatForbid=isChatForbid;
        if(isChatForbid){
            //被禁言
            hideFaceArea();
            et_bottom_speak.setGravity(Gravity.CENTER);
            et_bottom_speak.setEnabled(false);
            et_bottom_speak.setText("老师已关闭聊天");
            setBackground();

        }else{
            //可以聊天了
            et_bottom_speak.setGravity(Gravity.CENTER_VERTICAL);
            et_bottom_speak.setEnabled(true);
            et_bottom_speak.setText("");
        }
    }

    private void setBackground() {
        setBackground(screenDirEnum);
    }

    public void setBackground(ScreenDirEnum screenDirEnum) {
        this.screenDirEnum = screenDirEnum;
        switch (screenDirEnum){
            case HALF_FULL_PORTRAIT_SCREEN:{
                initNoPhoneLive();
                break;
            }
            default:{
                initPhoneLive();
                break;
            }
        }

    }

    private void initPhoneLive() {
        li_chat_all.setBackgroundColor(Color.BLACK);
        iv_add.setImageResource(R.drawable.phone_live_chat_add);
        iv_input.setImageResource(R.drawable.phone_live_chat_face_icon);

        et_bottom_speak.setBackgroundResource(R.drawable.audio_chat_input_bg2);
        if(isChatForbid){
            et_bottom_speak.setTextColor(context.getResources().getColor(R.color.white));
        }else{
            et_bottom_speak.setTextColor(context.getResources().getColor(R.color.common_dark_text_color));

        }



    }

    private void initNoPhoneLive() {
        li_chat_all.setBackgroundResource(R.drawable.common_title_drawable);
        iv_add.setImageResource(R.drawable.audio_chat_add);
        iv_input.setImageResource(R.drawable.audio_chat_face_icon);

        et_bottom_speak.setBackgroundResource(R.drawable.audio_chat_input_bg);

        et_bottom_speak.setTextColor(context.getResources().getColor(R.color.common_dark_text_color));
    }

     /*
     表情里的删除和输入法里的删除
     */

    private void deleteBtn() {
        String content = StringUtils.getEditTextText(et_bottom_speak);
        if (!StringUtils.isValid(content)) {
            return;
        }

        List<String> faceList = face_chat_view.getFaceList();

        for (int i = 0; i < faceList.size(); i++) {
            String face = "[" + faceList.get(i) + "]";
            if (content.endsWith(face)) {
                et_bottom_speak.setText(content.substring(0, content.length() - face.length()));
                et_bottom_speak.setSelection(content.substring(0, content.length() - face.length()).length());
                return;
            }
        }
        et_bottom_speak.setText(content.substring(0, content.length() - 1));
        et_bottom_speak.setSelection(content.substring(0, content.length() - 1).length());
    }

    public void changeChatStatus(boolean focused) {
        if (this.focused == focused) {
            return;
        }
        this.focused = focused;
        if (focused) {
            initNoPhoneLive();
            hideFaceArea();
            layout_add.setVisibility(GONE);
            iv_input.setImageResource(R.drawable.audio_chat_face_icon);
            tv_send.setVisibility(VISIBLE);
            iv_add.setVisibility(GONE);
        } else if (face_chat_view.getVisibility() == GONE) {
            setBackground();
            iv_add.setVisibility(VISIBLE);
            tv_send.setVisibility(GONE);
        }
    }


    public void setViewClick() {
        tv_send.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String edit_content = StringUtils.getEditTextText(et_bottom_speak);
                if (StringUtils.isValid(edit_content)) {
                    EplayerEngin.getInstance().chatReq(edit_content, EplayerMessageChatType.MessageChatTypeMsg.value());
                    et_bottom_speak.setText("");

                    iv_add.setVisibility(VISIBLE);
                    tv_send.setVisibility(GONE);
                    hideFaceArea();
                } else {
                    ToastUtil.showStringToast("内容不能为空");
                    et_bottom_speak.startAnimation(shake);
                    VibratorUtil.Vibrate(context, 1000);
                }
            }
        });
        iv_input.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                tv_send.setVisibility(View.GONE);
                iv_add.setVisibility(View.VISIBLE);
                initNoPhoneLive();
                if (face_chat_view.getVisibility() == View.VISIBLE) {
                    toJPInput();
                } else {
                    tv_send.setVisibility(View.VISIBLE);
                    iv_add.setVisibility(View.GONE);
                    iv_input.setImageResource(R.drawable.audio_chat_jp);
                    face_chat_view.setVisibility(View.VISIBLE);
                    face_chat_view.startAnimation(face_enter);
                    inputMethodManager.hideSoftInputFromWindow(et_bottom_speak.getApplicationWindowToken(), 0);
                    layout_add.setVisibility(GONE);
                }
            }
        });
        iv_add.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (layout_add.getVisibility() == View.VISIBLE) {
                    setBackground();
                    layout_add.setVisibility(GONE);
                } else {
                    initNoPhoneLive();
                    layout_add.setVisibility(VISIBLE);
                }

            }
        });
    }


    /*
   键盘输入
   */
    public void toJPInput() {
        et_bottom_speak.requestFocus();
        inputMethodManager.showSoftInput(et_bottom_speak, InputMethodManager.SHOW_FORCED);
        hideFaceArea();

    }

    public void hideFaceArea() {
        if(face_chat_view.getVisibility()==View.VISIBLE) {
            face_chat_view.setVisibility(View.GONE);
            face_chat_view.startAnimation(face_exit);
        }
        layout_add.setVisibility(GONE);
    }


}
