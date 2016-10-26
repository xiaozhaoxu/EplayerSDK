package com.ibrightech.eplayer.sdk.teacher.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.common.util.TextViewUtils;
import com.ibrightech.eplayer.sdk.teacher.R;


/**
 * Created by zhaoxu2014 on 15/9/1.
 */
public class ExitHintDialog extends Dialog implements DialogInterface {
    private static Context tmpContext;

    TextView tv_title;
    TextView tv_desc;

    Button bt_cancel;
    Button bt_ok;

    ClickListener clickListener;

    public ExitHintDialog(Context context,String title,String desc,  boolean isShowCancelButton, ClickListener clickListener) {
        super(context, R.style.sdk_dialog_untran );
        tmpContext = context;
        this.clickListener=clickListener;
        init(context,title,desc,isShowCancelButton);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(params);

    }


    private void init(Context context,String title,String desc, boolean isShowCancelButton) {

        View rootView = View.inflate(context, R.layout.dialog_exithint, null);

        tv_title = (TextView) rootView.findViewById(R.id.tv_title);
        tv_desc = (TextView) rootView.findViewById(R.id.tv_desc);
        bt_cancel = (Button) rootView.findViewById(R.id.bt_cancel);
        bt_ok = (Button) rootView.findViewById(R.id.bt_ok);
        bt_cancel.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                close();

                if(!CheckUtil.isEmpty(clickListener)){
                    clickListener.cancelClick();
                }
            }
        });
        bt_ok.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                close();
                if(!CheckUtil.isEmpty(clickListener)){
                    clickListener.confirmClick();
                }
            }
        });

        setContentView(rootView);
        tv_title.setVisibility(CheckUtil.isEmpty(title)?View.GONE:View.VISIBLE);
        TextViewUtils.setText(tv_title,title);
        TextViewUtils.setText(tv_desc,desc);
        bt_cancel.setVisibility(isShowCancelButton?View.VISIBLE:View.GONE);
    }



    private void close() {
        if (this.isShowing()) {
            this.dismiss();
        }
    }

    public interface ClickListener{
        public void confirmClick();
        void cancelClick();
    }
}