package com.ibrightech.eplayer.sdk.teacher.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.ibrightech.eplayer.sdk.teacher.R;
import com.ibrightech.eplayer.sdk.teacher.ui.MyProgressBar;


/**
 * Created by zhaoxu2014 on 15/9/1.
 */
public class LoadingDialog extends Dialog implements DialogInterface {
    private static Context tmpContext;

    MyProgressBar myprogressbar;


    public LoadingDialog(Context context) {
        super(context, R.style.sdk_dialog_untran);
        tmpContext = context;

        init(context);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes((WindowManager.LayoutParams) params);

    }


    private void init(Context context) {

        View rootView = View.inflate(context, R.layout.dialog_load, null);
        myprogressbar= (MyProgressBar) rootView.findViewById(R.id.myprogressbar);
        setContentView(rootView);
        myprogressbar.init(MyProgressBar.TYPE_LINEARLAYOUT_LAYOUTPARAMS);

    }


}