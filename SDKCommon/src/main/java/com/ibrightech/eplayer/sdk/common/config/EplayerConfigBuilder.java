package com.ibrightech.eplayer.sdk.common.config;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.ibrightech.eplayer.sdk.common.util.BundleUtil;
import com.ibrightech.eplayer.sdk.common.util.DeviceUtil;
import com.jiongbull.jlog.JLog;
import com.jiongbull.jlog.constant.LogLevel;
import com.jiongbull.jlog.constant.ZoneOffset;
import com.lzy.okhttputils.OkHttpUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

public class EplayerConfigBuilder {

    private String app_identity = "eplayer_sdk";
    private static EplayerConfigBuilder mInstance;                 //单例
    private Context context;
    private SharedPreferences preferences;
    private EplayerSetting eplayerSetting;

    public EplayerSetting getEplayerSetting() {
        return eplayerSetting;
    }

    public static EplayerConfigBuilder getInstance() {
        if (mInstance == null) {
            synchronized (EplayerConfigBuilder.class) {
                if (mInstance == null) {
                    mInstance = new EplayerConfigBuilder();
                }
            }
        }
        return mInstance;
    }


    public Context getContext() {
        return context;
    }

    public void init(@NonNull Application application) {
        this.init(application, false);
    }

    public void init(@NonNull Application application, boolean debug) {
        this.context = application;
        Locale.setDefault(Locale.US);
        eplayerSetting=EplayerSetting.getInstance();
        DeviceUtil.getUserAgentString();
        preferences = PreferenceManager.getDefaultSharedPreferences(application);
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = info.metaData;
            app_identity = BundleUtil.getStringFormBundle(bundle, "app_identify",app_identity);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        initDB(application);

        OkHttpUtils.init(application);
        OkHttpUtils.getInstance()//
                .setConnectTimeout(OkHttpUtils.DEFAULT_MILLISECONDS)               //全局的连接超时时间
                .setReadTimeOut(OkHttpUtils.DEFAULT_MILLISECONDS)                  //全局的读取超时时间
                .setWriteTimeOut(OkHttpUtils.DEFAULT_MILLISECONDS);

        List<LogLevel> logLevels = new ArrayList<LogLevel>();
        logLevels.add(LogLevel.VERBOSE);
        logLevels.add(LogLevel.DEBUG);
        logLevels.add(LogLevel.INFO);
        logLevels.add(LogLevel.WARN);
        logLevels.add(LogLevel.WTF);
        logLevels.add(LogLevel.ERROR);
        logLevels.add(LogLevel.JSON);


        JLog.init(application)
                .setDebug(debug)
                .writeToFile(true)
                .setLogLevelsForFile(logLevels)
                .setLogDir(app_identity + File.separator + "log")
                .setZoneOffset(ZoneOffset.P0800);
    }

    //关闭数据库
    public void onTerminate(){

    }

    public SharedPreferences getPreferences() {
        return preferences;
    }

    private void initDB(Context context){

        FlowConfig.Builder builder = new FlowConfig.Builder(context);
        FlowManager.init(builder.build());

    }

    public String getApp_identity() {
        return app_identity;
    }


}
