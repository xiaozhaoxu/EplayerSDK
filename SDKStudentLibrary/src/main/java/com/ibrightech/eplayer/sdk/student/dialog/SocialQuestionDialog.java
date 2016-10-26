package com.ibrightech.eplayer.sdk.student.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ibrightech.eplayer.sdk.common.engin.EplayerEngin;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.VoteMsgInfo;
import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.common.util.NumberUtil;
import com.ibrightech.eplayer.sdk.student.R;

import java.util.ArrayList;
import java.util.List;

/*
 互动问答
 */
public class SocialQuestionDialog extends Dialog {
    LinearLayout li_all;

    Activity context;

    int view_space;
    int width_twobt=0;
    int width_fourbt=0;

    Button bt1,bt2,bt3,bt4;
    View view_space1,view_space2,view_space3;
    VoteMsgInfo msgInfo;
    public SocialQuestionDialog(Activity context) {
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

    private void init(Activity context) {
        this.context = context;
        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int SCREEN_WIDTH = metrics.widthPixels;

        view_space= (int) context.getResources().getDimension(R.dimen.view_space);

        width_twobt=(SCREEN_WIDTH-view_space*3)/2;
        width_fourbt=(SCREEN_WIDTH-view_space*5)/4;


        View rootView = View.inflate(context, R.layout.dialog_social_question, null);
        li_all= (LinearLayout) rootView.findViewById(R.id.li_all);

        bt1= (Button) rootView.findViewById(R.id.bt1);
        bt2= (Button) rootView.findViewById(R.id.bt2);
        bt3= (Button) rootView.findViewById(R.id.bt3);
        bt4= (Button) rootView.findViewById(R.id.bt4);

        view_space1=rootView.findViewById(R.id.view_space1);
        view_space2=rootView.findViewById(R.id.view_space2);
        view_space3=rootView.findViewById(R.id.view_space3);


        setContentView(rootView);

        setListener();

    }
    private void setListener(){
        li_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
        bt1.setOnClickListener(viewClickListener);
        bt2.setOnClickListener(viewClickListener);
        bt3.setOnClickListener(viewClickListener);
        bt4.setOnClickListener(viewClickListener);
    }
    View.OnClickListener viewClickListener=new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            if(!CheckUtil.isEmpty(msgInfo)) {
                int tag = NumberUtil.parseInt((String)v.getTag(),0);
                EplayerEngin.getInstance().voteReq(msgInfo.voteKey, msgInfo.voteType, tag);
                close();
            }
        }
    };

    public void voteReq(VoteMsgInfo msgInfo){
        List<Button>bts=new ArrayList<Button>();
        this.msgInfo=msgInfo;
        if (msgInfo.action) {
            show();
            // 发起问答
            switch (msgInfo.voteType) {
                case VoteType1: {
                    bts.add(bt1);
                    bts.add(bt2);
                    bts.add(bt3);
                    bts.add(bt4);

                    view_space1.setVisibility(View.VISIBLE);
                    view_space2.setVisibility(View.VISIBLE);
                    view_space3.setVisibility(View.VISIBLE);

                    bt1.setText("A");
                    bt2.setText("B");
                    bt3.setText("C");
                    bt4.setText("D");

                    for(Button bt:bts){
                        bt.setVisibility(View.VISIBLE);
                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) bt.getLayoutParams();
                        lp.width=width_fourbt;
                        lp.height=width_fourbt;
                    }
                    break;
                }
                case VoteType2: {
                    bts.add(bt1);
                    bts.add(bt2);
                    bts.add(bt3);
                    bts.add(bt4);
                    view_space1.setVisibility(View.VISIBLE);
                    view_space2.setVisibility(View.VISIBLE);
                    view_space3.setVisibility(View.VISIBLE);
                    bt1.setText("1");
                    bt2.setText("2");
                    bt3.setText("3");
                    bt4.setText("4");
                    for(Button bt:bts){
                        bt.setVisibility(View.VISIBLE);
                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) bt.getLayoutParams();
                        lp.width=width_fourbt;
                        lp.height=width_fourbt;
                    }
                    break;
                }
                case VoteType3: {
                    bt2.setVisibility(View.GONE);
                    bt4.setVisibility(View.GONE);

                    bts.add(bt1);
                    bts.add(bt3);
                    view_space1.setVisibility(View.VISIBLE);
                    view_space2.setVisibility(View.GONE);
                    view_space3.setVisibility(View.GONE);

                    bt1.setText("对");
                    bt3.setText("错");

                    for(Button bt:bts){
                        bt.setVisibility(View.VISIBLE);
                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) bt.getLayoutParams();
                        lp.width=width_twobt;
                        lp.height=width_fourbt;
                    }
                    break;
                }
                case VoteType4: {
                    bt2.setVisibility(View.GONE);
                    bt4.setVisibility(View.GONE);

                    bts.add(bt1);
                    bts.add(bt3);
                    view_space1.setVisibility(View.VISIBLE);
                    view_space2.setVisibility(View.GONE);
                    view_space3.setVisibility(View.GONE);

                    bt1.setText("YES");
                    bt3.setText("NO");
                    for(Button bt:bts){
                        bt.setVisibility(View.VISIBLE);
                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) bt.getLayoutParams();
                        lp.width=width_twobt;
                        lp.height=width_fourbt;
                    }
                    break;
                }
                case VoteType5: {
                    bt2.setVisibility(View.GONE);
                    bt4.setVisibility(View.GONE);
                    bts.add(bt1);
                    bts.add(bt3);
                    view_space1.setVisibility(View.VISIBLE);
                    view_space2.setVisibility(View.GONE);
                    view_space3.setVisibility(View.GONE);

                    bt1.setText("听明白了");
                    bt3.setText("没听明白");
                    for(Button bt:bts){
                        bt.setVisibility(View.VISIBLE);
                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) bt.getLayoutParams();
                        lp.width=width_twobt;
                        lp.height=width_fourbt;
                    }
                    break;
                }
            }
        }else{
            //取消问答
           close();
        }
    }

    @Override
    public void show() {
        if (isShowing()){
            return;
        }
        super.show();
    }

    private void close() {
        if (isShowing()) {
            dismiss();
        }

    }


}
