package com.ibrightech.eplayer.sdk.common.util;

import android.widget.TextView;

/**
 * Created by zhaoxu2014 on 16/8/27.
 */
public class TextViewUtils {
    public  static  void setText(TextView tv, String content){
        if(CheckUtil.isEmpty(tv)){
            return;
        }
        String ct="";
        if(CheckUtil.isEmpty(content)){
            ct="";
        }else{
            ct=content;
        }
        tv.setText(ct);
    }
    public  static  void setText(TextView tv,int stringid){
        if(CheckUtil.isEmpty(tv)){
            return;
        }

        tv.setText(stringid);
    }
}
