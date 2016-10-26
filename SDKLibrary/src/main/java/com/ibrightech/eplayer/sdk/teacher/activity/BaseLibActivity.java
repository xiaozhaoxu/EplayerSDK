package com.ibrightech.eplayer.sdk.teacher.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by zhaoxu2014 on 15/9/19.
 */
public abstract class BaseLibActivity extends FragmentActivity {


    public Activity context;

    protected View mContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.context=this;
        if (mContentView == null) {
            initView(savedInstanceState);
            setListener();
            processLogic(savedInstanceState);
        }
        super.onCreate(savedInstanceState);
    }


    public void setContentView( int layoutResID) {
        mContentView = LayoutInflater.from(this).inflate(layoutResID, null);
        setContentView(mContentView);
    }

    /**
     * 初始化View控件
     */
    protected abstract void initView(Bundle savedInstanceState);

    /**
     * 给View控件添加事件监听器
     */
    protected abstract void setListener();

    /**
     * 处理业务逻辑，状态恢复等操作
     *
     * @param savedInstanceState
     */
    protected abstract void processLogic(Bundle savedInstanceState);
}
