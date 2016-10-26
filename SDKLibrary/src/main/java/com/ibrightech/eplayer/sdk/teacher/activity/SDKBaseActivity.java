package com.ibrightech.eplayer.sdk.teacher.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.ibrightech.eplayer.sdk.common.entity.EPlayerData;
import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.common.util.StringUtils;
import com.ibrightech.eplayer.sdk.common.util.WeakReferenceHandler;
import com.ibrightech.eplayer.sdk.teacher.R;
import com.ibrightech.eplayer.sdk.teacher.dialog.SDKDialogUtil;
import com.ibrightech.eplayer.sdk.teacher.event.ChangeScreenScuessEvent;
import com.ibrightech.eplayer.sdk.teacher.event.OtherEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by zhaoxu2014 on 15/10/29.
 */
public abstract class SDKBaseActivity extends BaseLibActivity {
    public static final int TASK_TYPE_LOGIN =10000;
    public  static final int TASK_TYPE_LOAD_OUT_TIME =10001;
    public  static final int TASK_TYPE_ERROR_OUT_TIME =10002;
    public final int TIMER_OUTTIME=100000;


    public static final int MESSAGE_CHANGESCREEN_SCUESS = 1000114;
    public static final int TIME_DELAY = 2 * 1000;
    public static final String KEY_EPLAY_DATA = "eplay_data";
    public static final double DRAWPADVIEW_SCALE = 9.0 / 16;
    public static final int CHAT_MESSAGE = 1;
    public static final int MSG_TOKEN_INVALID = 1001;
    public static final int MSG_VIEW = 1002;
    public static final String KEY_PROVICE_BEAN = "key_provice_bean";
    public static final String KEY_CITY_BEAN = "key_city_bean";

    public EPlayerData playerData;
    public int SCREEN_WIDTH;//屏幕宽度
    public int SCREEN_HEIGHT;//屏幕高度
    public EventBus eventBus;
    protected Dialog progressDialog;
    boolean cancelNetwork = false;//返回键取消联网

    boolean isValidChangeScreen = true;
    AlertDialog alertDialog;

    public void showExitDialog(final String title, final String message) {
        if (StringUtils.isEmpty(title) || StringUtils.isEmpty(message)) {
            return;
        }


        if (null != alertDialog && alertDialog.isShowing()) {
            return;
        }
        TextView text = new TextView(SDKBaseActivity.this);
        android.view.ViewGroup.LayoutParams lp = text.getLayoutParams();
        text.setTextSize(18);

        text.setGravity(Gravity.CENTER);
        text.setText(message);

        AlertDialog.Builder edit_builder = new AlertDialog.Builder(context);
        edit_builder.setCancelable(false);
        edit_builder.setTitle(title);
        edit_builder.setView(text);
        edit_builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertDialog = edit_builder.create();
        alertDialog.show();

        requestStop();

    }

    Handler viewhandler =new WeakReferenceHandler(this){

        @Override
        protected void handleMessage(Message msg, Object o) {
            switch (msg.what) {
                case MESSAGE_CHANGESCREEN_SCUESS: {
                    isValidChangeScreen = true;
                    eventBus.post(new ChangeScreenScuessEvent());
                    break;
                }
                case MSG_VIEW:
                    View v = (View) msg.obj;
                    if (!CheckUtil.isEmpty(v)) {
                        hideSoftInput(v.getWindowToken());
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        SCREEN_WIDTH = metrics.widthPixels;
        SCREEN_HEIGHT = metrics.heightPixels;
        eventBus = EventBus.getDefault();
        eventBus.register(this);
        super.onCreate(savedInstanceState);

    }

    public void requestStop(){}


    @Override
    protected void onDestroy() {

        closeProgressBar();
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        if (null != eventBus) {
            eventBus.unregister(this);//反注册EventBus
        }
        super.onDestroy();
    }

    public synchronized void closeProgressBar() {
        if (progressDialog != null && progressDialog.isShowing()) {
            cancelNetwork = false;
            progressDialog.dismiss();
        }

    }

    public synchronized void startProgressBar() {
        startProgressBar(true);

    }
    public synchronized void startProgressBar(boolean canCancel) {
        try {
            if (!CheckUtil.isEmpty(progressDialog) && progressDialog.isShowing()) {
                return;
            }
            cancelNetwork = false;
            progressDialog = SDKDialogUtil.getInstance().showLoadingDialog(context);
            progressDialog.setCancelable(canCancel);
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean isCancelNetwork() {
        return cancelNetwork;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(OtherEvent event) {

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {

            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
           View v = getCurrentFocus();

            if (isShouldHideInput(v, ev)) {
                Message message = new Message();
                message.obj = v;
                message.what = MSG_VIEW;
                viewhandler.sendMessageDelayed(message, 100);
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();

            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 多种隐藏软件盘方法的其中一种
     *
     * @param token
     */
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, 0);
            //   InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    public void startActivityForResultWithAnimation(Intent it, int requestCode) {
        startActivityForResult(it, requestCode);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    public void startActivityWithAnimation(Intent it) {
        startActivity(it);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    public void finishWithAnimation() {
        finish();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }


    public void changedScreen(boolean isPortraitScreen) {
        if (!isValidChangeScreen) {
            return;
        } else {
            isValidChangeScreen = false;
        }

        if (isPortraitScreen) {
            WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attrs);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        viewhandler.sendEmptyMessageDelayed(MESSAGE_CHANGESCREEN_SCUESS, 200);
        super.onConfigurationChanged(newConfig);
    }
}
