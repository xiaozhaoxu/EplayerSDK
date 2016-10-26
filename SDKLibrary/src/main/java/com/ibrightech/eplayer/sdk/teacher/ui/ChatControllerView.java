package com.ibrightech.eplayer.sdk.teacher.ui;

import android.content.Context;
import android.util.AttributeSet;
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
import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.common.util.StringUtils;
import com.ibrightech.eplayer.sdk.common.util.TextViewUtils;
import com.ibrightech.eplayer.sdk.common.util.ToastUtil;
import com.ibrightech.eplayer.sdk.teacher.R;
import com.ibrightech.eplayer.sdk.teacher.activity.LivePushActivity;
import com.ibrightech.eplayer.sdk.teacher.util.VibratorUtil;

import java.util.List;

/**
 * Created by zhaoxu2014 on 16/5/27.
 */
public class ChatControllerView extends LinearLayout {
    LinearLayout li_chat_all;
    ImageView img_lock;
    TextView tv_praise_num;

    ImageView img_bq;
    ImageView img_jp;
    LinearLayout li_bq;
    LinearLayout li_lock;
    TextView tv_send;


    LinearLayout li_praise;
    LinearLayout li_face_area;
    LinearLayout li_group;
    FaceChatView face_chat_view;
    EditText et_bottom_speak;
    InputMethodManager inputMethodManager;


    boolean chatLock = false;
    OnClickListener listener;
    Animation shake, face_enter, face_exit;
    boolean focuse;
    public ChatControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ChatControllerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public ChatControllerView(Context context) {
        super(context);
        initView(context);
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    LivePushActivity context;


    private void initView(Context context) {
        this.context = (LivePushActivity) context;

        inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        View rootview = View.inflate(context, R.layout.chat_controller, this);
        li_chat_all= (LinearLayout) rootview.findViewById(R.id.li_chat_all);
         img_lock= (ImageView) rootview.findViewById(R.id.img_lock);
         tv_praise_num= (TextView) rootview.findViewById(R.id.tv_praise_num);

         img_bq= (ImageView) rootview.findViewById(R.id.img_bq);;
         img_jp= (ImageView) rootview.findViewById(R.id.img_jp);
         li_bq= (LinearLayout) rootview.findViewById(R.id.li_bq);
         li_lock= (LinearLayout) rootview.findViewById(R.id.li_lock);
         tv_send= (TextView) rootview.findViewById(R.id.tv_send);


         li_praise= (LinearLayout) rootview.findViewById(R.id.li_praise);
         li_face_area= (LinearLayout) rootview.findViewById(R.id.li_face_area);
         li_group= (LinearLayout) rootview.findViewById(R.id.li_group);
         face_chat_view= (FaceChatView) rootview.findViewById(R.id.face_chat_view);
         et_bottom_speak= (EditText) rootview.findViewById(R.id.et_bottom_speak);

        shake = AnimationUtils.loadAnimation(context, R.anim.chat_shake);
        face_enter = AnimationUtils.loadAnimation(context, R.anim.chat_face_enter);
        face_exit = AnimationUtils.loadAnimation(context, R.anim.chat_face_exit);
        setPraiseNum(0);
        List<LinearLayout> tab1liList =face_chat_view.getTab1liList();
        for (int i = 0; i < tab1liList.size(); i++) {
            LinearLayout tab1li = tab1liList.get(i);
            if (i != tab1liList.size() - 1) {
                final String contentDescription = tab1li.getContentDescription().toString();
                tab1li.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        et_bottom_speak.append("[" + contentDescription + "]");
                    }
                });
            } else {
                tab1li.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        deleteBtn();
                    }
                });
            }
        }

        setViewClick();
    }

    public void setFocused(boolean focused) {
        if(focuse==focused){
            return;
        }
        focuse = focused;
        changeChatStatus(focused);
    }

    public void changeChatStatus(boolean focused) {
        if (focused) {
            li_lock.setVisibility(View.GONE);
            li_bq.setVisibility(View.VISIBLE);
            li_praise.setVisibility(View.GONE);
            tv_send.setVisibility(View.VISIBLE);
            toJPInput();
            setChatBg(R.color.transparent);
        } else if(li_face_area.getVisibility()==GONE){
            li_lock.setVisibility(View.VISIBLE);
            li_bq.setVisibility(View.GONE);
            li_praise.setVisibility(View.VISIBLE);
            tv_send.setVisibility(View.GONE);
        }
    }

      /*
     表情里的删除和输入法里的删除
     */

    private void deleteBtn() {
        //todo
        String content = StringUtils.getEditTextText(et_bottom_speak);
        if (!StringUtils.isValid(content)) {
            return;
        }

        List<String>faceList = face_chat_view.getFaceList();

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


    public void hideContrller() {
        li_group.setVisibility(View.INVISIBLE);
    }

    public void showContrller() {
        li_group.setVisibility(View.VISIBLE);
    }

    public void setChatLock(boolean sendMessage) {
        if (chatLock) {
            img_lock.setImageResource(R.drawable.chat_locked);
        } else {
            img_lock.setImageResource(R.drawable.chat_unlocked);
        }

        if (!CheckUtil.isEmpty(listener)) {
            listener.chatControl(chatLock, sendMessage);
        }

    }

    public void setChatBg(int resID) {
        li_chat_all.setBackgroundResource(resID);
    }

    public void setPraiseNum(int num) {
        TextViewUtils.setText(tv_praise_num, num + "");
    }

    public void initChatStatus(boolean chatLock){
        this.chatLock = chatLock;
        setChatLock(true);
    }
    public void setViewClick(){
        tv_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String edit_content = StringUtils.getEditTextText(et_bottom_speak);
                if (StringUtils.isValid(edit_content)) {
                    EplayerEngin.getInstance().chatReq(edit_content, EplayerMessageChatType.MessageChatTypeMsg.value());
                    et_bottom_speak.setText("");
                    hideFaceArea();
                    changeChatStatus(false);
                    et_bottom_speak.clearFocus();// 失去焦点
                } else {
                    ToastUtil.showStringToast("内容不能为空");
                    et_bottom_speak.startAnimation(shake);
                    VibratorUtil.Vibrate(context, 1000);
                }
            }
        });
        li_bq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (img_bq.getVisibility() == View.VISIBLE) {
                    inputMethodManager.hideSoftInputFromWindow(et_bottom_speak.getApplicationWindowToken(), 0);
                    img_bq.setVisibility(View.GONE);
                    img_jp.setVisibility(View.VISIBLE);

                    li_face_area.setVisibility(View.VISIBLE);
                    li_face_area.startAnimation(face_enter);
                } else {
                    toJPInput();
                }
            }
        });
        li_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(CheckUtil.isEmpty(EplayerEngin.getInstance().getSessionInfo().infoData)){
                    ToastUtil.showStringToast("正在获取课堂信息,请稍候再试");
                    return;
                }


                ChatControllerView.this.chatLock = !chatLock;
                EplayerEngin.getInstance().getSessionInfo().infoData.canChat = chatLock;
                setChatLock(true);
            }
        });
    }



    /*
     键盘输入
     */
    public void toJPInput() {
        img_bq.setVisibility(View.VISIBLE);
        img_jp.setVisibility(View.GONE);
        inputMethodManager.showSoftInput(et_bottom_speak, InputMethodManager.SHOW_FORCED);

        hideFaceArea();
    }

    private void hideFaceArea() {
        if (li_face_area.getVisibility() == View.VISIBLE) {
            li_face_area.setVisibility(View.GONE);
            li_face_area.startAnimation(face_exit);
        }
    }

    public interface OnClickListener {
        void chatControl(boolean closeChat, boolean sendMessage);//sendMessage 代表是否服务器发送更改聊天控件的指令

    }
}
