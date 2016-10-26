package com.ibrightech.eplayer.sdk.teacher.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
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
import com.ibrightech.eplayer.sdk.common.entity.SessionData.DrawPadInfo;
import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.common.util.FileUtils;
import com.ibrightech.eplayer.sdk.common.util.StorageUtil;
import com.ibrightech.eplayer.sdk.common.util.StringUtils;
import com.ibrightech.eplayer.sdk.common.util.ToastUtil;
import com.ibrightech.eplayer.sdk.teacher.R;
import com.ibrightech.eplayer.sdk.teacher.activity.LivePushActivity;
import com.ibrightech.eplayer.sdk.teacher.dialog.SDKDialogUtil;
import com.ibrightech.eplayer.sdk.teacher.dialog.SelectCoursewareDialog;
import com.ibrightech.eplayer.sdk.teacher.util.VibratorUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AudioChatControllerView extends LinearLayout {
    Context context;

    Animation shake, face_enter, face_exit;
    LinearLayout li_chat_all;
    ImageView iv_input;
    EditText et_bottom_speak;
    ImageView iv_add;
    TextView tv_send;
    FaceChatView face_chat_view;
    LinearLayout layout_add;
    boolean focused;
    InputMethodManager inputMethodManager;
    TextView tv_audio_whole_gag;
    OnChatClick listener;

    SelectCoursewareDialog.OnItemClickListener courseWareListener;
    boolean chatLock;


    public void initChatStatus(boolean chatLock){
        this.chatLock = chatLock;
        setChatLock(true);
    }

    public AudioChatControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public AudioChatControllerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public AudioChatControllerView(Context context) {
        super(context);
        initView(context);
    }



    public void setCourseWareListener( SelectCoursewareDialog.OnItemClickListener courseWareListener){
        this.courseWareListener = courseWareListener;
    }

    private void initView(Context context) {
        this.context = context;
        inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        View rootView = View.inflate(context, R.layout.audio_chat_controller, this);
         li_chat_all= (LinearLayout) rootView.findViewById(R.id.li_chat_all);
         iv_input= (ImageView) rootView.findViewById(R.id.iv_input);
         et_bottom_speak= (EditText) rootView.findViewById(R.id.et_bottom_speak);
         iv_add= (ImageView) rootView.findViewById(R.id.iv_add);
         tv_send= (TextView) rootView.findViewById(R.id.tv_send);
         face_chat_view= (FaceChatView) rootView.findViewById(R.id.face_chat_view);
         layout_add= (LinearLayout) rootView.findViewById(R.id.layout_add);
         tv_audio_whole_gag= (TextView) rootView.findViewById(R.id.tv_audio_whole_gag);

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
        setViewClick(rootView);
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
        if(this.focused==focused){
            return;
        }
        this.focused = focused;
        if (focused) {
            hideFaceArea();
            layout_add.setVisibility(GONE);
            iv_input.setImageResource(R.drawable.audio_chat_face_icon);
            tv_send.setVisibility(VISIBLE);
            iv_add.setVisibility(GONE);
        } else if (face_chat_view.getVisibility() ==GONE) {
            iv_add.setVisibility(VISIBLE);
            tv_send.setVisibility(GONE);
        }
    }


    public void setViewClick(View rootview){
        tv_send.setOnClickListener(new View.OnClickListener(){

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
        iv_input.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
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
        iv_add.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(layout_add.getVisibility()==View.VISIBLE){
                    layout_add.setVisibility(GONE);
                }else{
                    layout_add.setVisibility(VISIBLE);
                }

            }
        });
        rootview.findViewById(R.id.layout_change_course_ware)
        .setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                showSelectCourseWareDialog();
                layout_add.setVisibility(GONE);
            }
        });

        rootview.findViewById(R.id.layout_whole_gag)
                .setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                chatLock = !chatLock;
                EplayerEngin.getInstance().getSessionInfo().infoData.canChat = chatLock;
                setChatLock(true);
            }
        });

        rootview.findViewById(R.id.layout_upload_course_ware)
                .setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        showUploadPPTDialog();
                        layout_add.setVisibility(GONE);
                    }
                });
    }



    private void setDrawableTop(int res, TextView tv) {

        Drawable drawable = getResources().getDrawable(res);
        // 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        tv.setCompoundDrawables(null, drawable, null, null);
    }

    public void setListener(OnChatClick listener) {
        this.listener = listener;
    }

    private void showSelectCourseWareDialog() {

       LivePushActivity activity= (LivePushActivity) context;
        final DrawPadInfo drawPadInfo = EplayerEngin.getInstance().getSessionInfo().drawPadInfo;
        if (EplayerEngin.getInstance().getSessionInfo().drawPadInfo == null) {
            return;
        }

         int pptId = drawPadInfo.pptId;
        SelectCoursewareDialog selectCoursewareDialog = SDKDialogUtil.getInstance().showSelectCourseWareDialog((Activity) context,pptId, activity.assetMap,
                courseWareListener);
        selectCoursewareDialog.setCanceledOnTouchOutside(false);
    }
    private void showUploadPPTDialog() {
        String parentPath= StorageUtil.getUploadCacheDierctory();
        File file=new File(parentPath);
        File[] tempList = file.listFiles();
        List<File>list=new ArrayList<File>();
        if(!CheckUtil.isEmpty(tempList)) {
            for (int i = 0; i < tempList.length; i++) {
                File tempFile = tempList[i];
                if (FileUtils.fileIsPPT(tempFile.getName()) || FileUtils.fileIsWord(tempFile.getName())) {
                    list.add(tempFile);
                }
            }
        }

            SelectCoursewareDialog selectCoursewareDialog = SDKDialogUtil.getInstance().showUploadDialog((Activity) context,list,fileClickListener);
        selectCoursewareDialog.setCanceledOnTouchOutside(false);
    }

    SelectCoursewareDialog.OnItemFileClickListener fileClickListener=new SelectCoursewareDialog.OnItemFileClickListener(){

        @Override
        public void onItemClick(File asset) {
            LivePushActivity activity = (LivePushActivity) context;
            activity.toUploadStep1(asset);
        }


    };

    public void setChatLock(boolean sendMessage) {
        if (!CheckUtil.isEmpty(listener)) {
            listener.chatControl(chatLock, sendMessage);
        }
        setDrawableTop(chatLock ?R.drawable.audio_whole_gag : R.drawable.audio_chat , tv_audio_whole_gag);
        tv_audio_whole_gag.setText(chatLock ? R.string.whole_gag : R.string.chat );

    }

    /*
   键盘输入
   */
    public void toJPInput() {
        et_bottom_speak.requestFocus();
        inputMethodManager.showSoftInput(et_bottom_speak, InputMethodManager.SHOW_FORCED);
        hideFaceArea();
        layout_add.setVisibility(GONE);
    }

    private void hideFaceArea() {
        face_chat_view.setVisibility(View.GONE);
        face_chat_view.startAnimation(face_exit);
    }

    public interface OnChatClick {
        void chatControl(boolean closeChat, boolean sendMessage);//sendMessage 代表是否服务器发送更改聊天控件的指令
    }

}
