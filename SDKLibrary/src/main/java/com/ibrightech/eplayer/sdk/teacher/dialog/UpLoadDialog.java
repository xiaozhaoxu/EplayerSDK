package com.ibrightech.eplayer.sdk.teacher.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.ibrightech.eplayer.sdk.common.util.TextViewUtils;
import com.ibrightech.eplayer.sdk.teacher.R;


/**
 * Created by zhaoxu2014 on 15/9/1.
 */
public class UpLoadDialog extends Dialog implements DialogInterface {
    private static Context tmpContext;


    TextView tv_desc,tv_title;
    ImageView image_rotate;
    boolean canCel;
    public void setDesc(String desc) {
        TextViewUtils.setText(tv_desc,desc);
    }

    public UpLoadDialog(Context context,String title, String desc, boolean canCel,boolean showProgress) {
        super(context, R.style.sdk_dialog_untran );
        tmpContext = context;
        this.canCel=canCel;
        init(context,title,desc,showProgress);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(params);

    }

    private void init(Context context,String title,String desc,boolean showProgress) {

        View rootView = View.inflate(context, R.layout.dialog_upload, null);
        tv_title = (TextView) rootView.findViewById(R.id.tv_title);
        tv_desc = (TextView) rootView.findViewById(R.id.tv_desc);
        image_rotate= (ImageView) rootView.findViewById(R.id.image_rotate);

        if(showProgress){
            image_rotate.setVisibility(View.VISIBLE);
            Animation operatingAnim = AnimationUtils.loadAnimation(context, R.anim.upload_rotate);
            LinearInterpolator lin = new LinearInterpolator();
            operatingAnim.setInterpolator(lin);
            if (operatingAnim != null) {
                image_rotate.startAnimation(operatingAnim);
            }

        }else{
            image_rotate.setVisibility(View.GONE);

        }

        TextViewUtils.setText(tv_title,title);
        if(!canCel) {
            setOnKeyListener(keylistener);
        }
        setContentView(rootView);
        setDesc(desc);

    }
    OnKeyListener keylistener = new DialogInterface.OnKeyListener(){
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode== KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    } ;



}