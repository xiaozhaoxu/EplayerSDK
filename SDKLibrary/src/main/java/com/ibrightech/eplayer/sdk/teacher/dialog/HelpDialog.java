package com.ibrightech.eplayer.sdk.teacher.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ibrightech.eplayer.sdk.common.util.TextViewUtils;
import com.ibrightech.eplayer.sdk.teacher.R;

public class HelpDialog extends Dialog {
    LinearLayout li_back;
    LinearLayout li_tilte_right;
    TextView tv_common_title;
    ImageView img_help;
    int SCREEN_WIDTH;

    public HelpDialog(Context context ,int SCREEN_WIDTH){
        super(context, R.style.sdk_dialog_untran);
        this.SCREEN_WIDTH=SCREEN_WIDTH;
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
        View mContentView = View.inflate(context, R.layout.dialog_uploadhelp, null);
        li_back = (LinearLayout) mContentView.findViewById(R.id.li_back);
        tv_common_title= (TextView) mContentView.findViewById(R.id.tv_common_title);
        li_tilte_right = (LinearLayout) mContentView.findViewById(R.id.li_tilte_right);
        img_help= (ImageView) mContentView.findViewById(R.id.img_help);

        li_back.setVisibility(View.VISIBLE);
        li_tilte_right.setVisibility(View.GONE);
        TextViewUtils.setText(tv_common_title, R.string.help_upload);
        img_help.setMaxWidth(SCREEN_WIDTH);
        img_help.setMaxHeight( (int) (SCREEN_WIDTH*1232/750.0));

        setListener();

        setContentView(mContentView);


    }



    private void setListener() {
        li_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               dismiss();
            }
        });


    }
}
