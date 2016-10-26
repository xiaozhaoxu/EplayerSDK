package com.ibrightech.eplayer.sdk.teacher.ui;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import com.ibrightech.eplayer.sdk.teacher.R;
import java.util.ArrayList;
import java.util.List;

public class FaceChatView extends LinearLayout {

    List<String> faceList = new ArrayList<String>();

    List<LinearLayout> tab1liList;

    public FaceChatView(Context context) {
        super(context);
        initView(context);
    }

    public FaceChatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public FaceChatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        View rootview = View.inflate(context, R.layout.chat_tab1_item_phone, this);


        tab1liList=new ArrayList<LinearLayout>();
        tab1liList.add((LinearLayout) rootview.findViewById(R.id.li_tab1_row1_col1));
        tab1liList.add((LinearLayout) rootview.findViewById(R.id.li_tab1_row1_col2));
        tab1liList.add((LinearLayout) rootview.findViewById(R.id.li_tab1_row1_col3));
        tab1liList.add((LinearLayout) rootview.findViewById(R.id.li_tab1_row1_col4));
        tab1liList.add((LinearLayout) rootview.findViewById(R.id.li_tab1_row1_col5));
        tab1liList.add((LinearLayout) rootview.findViewById(R.id.li_tab1_row1_col6));

        tab1liList.add((LinearLayout) rootview.findViewById(R.id.li_tab1_row2_col1));
        tab1liList.add((LinearLayout) rootview.findViewById(R.id.li_tab1_row2_col2));
        tab1liList.add((LinearLayout) rootview.findViewById(R.id.li_tab1_row2_col3));
        tab1liList.add((LinearLayout) rootview.findViewById(R.id.li_tab1_row2_col4));
        tab1liList.add((LinearLayout) rootview.findViewById(R.id.li_tab1_row2_col5));
        tab1liList.add((LinearLayout) rootview.findViewById(R.id.li_tab1_row2_col6));


        tab1liList.add((LinearLayout) rootview.findViewById(R.id.li_tab1_row3_col1));
        tab1liList.add((LinearLayout) rootview.findViewById(R.id.li_tab1_row3_col2));
        tab1liList.add((LinearLayout) rootview.findViewById(R.id.li_tab1_row3_col3));
        tab1liList.add((LinearLayout) rootview.findViewById(R.id.li_tab1_row3_col4));
        tab1liList.add((LinearLayout) rootview.findViewById(R.id.li_tab1_row3_col6));


        initData();

    }

    public void initData() {

        faceList.add("e_ciya");
        faceList.add("e_jiyan");
        faceList.add("e_buding");
        faceList.add("e_shangxin");
        faceList.add("e_daxiao");
        faceList.add("e_jingya");
        faceList.add("e_qinqin");
        faceList.add("e_weixiao");
        faceList.add("e_tushe");
        faceList.add("e_tianshi");
        faceList.add("e_shuaku");
        faceList.add("e_daidai");
        faceList.add("e_daku");
        faceList.add("e_moshu");
        faceList.add("e_yaoguai");
        faceList.add("e_xingxing");
    }

    public List<LinearLayout>getTab1liList(){
        return tab1liList;
    }

    public List<String>getFaceList(){
        return faceList;
    }
}
