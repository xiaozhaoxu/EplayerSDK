package com.ibrightech.eplayer.sdk.student.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ibrightech.eplayer.sdk.common.engin.EplayerEngin;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerMessageChatType;
import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.common.util.StringUtils;
import com.ibrightech.eplayer.sdk.common.util.TextViewUtils;
import com.ibrightech.eplayer.sdk.student.R;
import com.ibrightech.eplayer.sdk.teacher.ui.RippleView;
import com.ibrightech.eplayer.sdk.teacher.util.VibratorUtil;

public class QuestionDialog extends Dialog {
    LinearLayout li_back;
    TextView tv_common_title;
    RippleView rippleview;
    View li_tilte_right;
    EditText et_course;
    TextView tv_num_restrict;
    Context context;
    private int maxnum = 100;//限制内容的最大字数
    private int thisnum = 0;//当前输入的文字字数

    Animation shake;
    public QuestionDialog(Context context) {
        super(context, R.style.sdk_dialog_untran);
        init(context);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(params);
    }

    private void init(Context context) {
        this.context = context;
        shake = AnimationUtils.loadAnimation(context, R.anim.chat_shake);
        View rootView = View.inflate(context, R.layout.dialog_question, null);
        tv_common_title= (TextView) rootView.findViewById(R.id.tv_common_title);
        li_back= (LinearLayout) rootView.findViewById(R.id.li_back);
        li_tilte_right= rootView.findViewById(R.id.li_tilte_right);
        et_course= (EditText) rootView.findViewById(R.id.et_course);
         tv_num_restrict= (TextView) rootView.findViewById(R.id.tv_num_restrict);
        rippleview= (RippleView) rootView.findViewById(R.id.rippleview);
        setContentView(rootView);
        TextViewUtils.setText(tv_common_title,"文字提问");
        tv_num_restrict.setText(thisnum + "/" + maxnum);
        setListener();

    }
    private void setListener(){
        li_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });

        rippleview.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener(){

            @Override
            public void onComplete(RippleView rippleView) {
                //todo 做一下发送文字的处理
                String etContent= StringUtils.getEditTextText(et_course);
                if(CheckUtil.isEmpty(et_course)){
                    et_course.startAnimation(shake);
                    VibratorUtil.Vibrate(context, 1000);
                    return;
                }
                EplayerEngin.getInstance().chatReq(etContent, EplayerMessageChatType.MessageChatTypeAsk.value());
                close();
            }
        });
        et_course.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                thisnum = editable.length();
                tv_num_restrict.setText(thisnum + "/" + maxnum);
                if (thisnum > maxnum) {
                    et_course.setText(editable.delete(maxnum, thisnum));
                    et_course.setSelection(editable.length()); // 将光标移动最后一个字符后面
                }
            }
        });
    }
    private void close() {
        if (isShowing()) {
            dismiss();
        }

    }

}
