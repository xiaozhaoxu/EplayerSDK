package com.ibrightech.eplayer.sdk.common.util;

import android.util.Log;

import com.jiongbull.jlog.JLog;
import com.jiongbull.jlog.Settings;

/**
 * Created with IntelliJ IDEA.
 * User: SlothMonkey
 * Date: 13-11-14
 * Time: 下午5:45
 * To change this template use File | Settings | File Templates.
 */
public class LogUtil {

    private static final String TEST_TAG = "---Test Log---";

    public static void setwriteToFile(boolean isWrite){
        Settings set= JLog.getSettings();
        set.writeToFile(isWrite);
        JLog.setSettings(set);
    }

    public static void setDebug(boolean DEBUG) {
        Settings set= JLog.getSettings();
        set.setDebug(DEBUG);
        JLog.setSettings(set);
    }

    public static boolean isJLogDebug(){
        Settings set= JLog.getSettings();
        return set.isDebug();
    }
    /**
     * 用于测试日志，随时可以关闭输出
     *
     * @param msg 测试日志
     */
    public static void d(String msg) {
       if(isJLogDebug()){
           JLog.d(TEST_TAG, msg);
       }else{
           Log.d(TEST_TAG, msg);
       }

    }

    public static void e(String msg) {
        if(isJLogDebug()){
            JLog.e(TEST_TAG, msg);
        }else{
            Log.e(TEST_TAG, msg);
        }

    }


    public static void d(String tag, String msg) {
        if(isJLogDebug()){
            JLog.d(tag, msg);
        }else{
            Log.d(tag, msg);
        }
    }

    public static void e(String tag, String msg) {

        if(isJLogDebug()){
            JLog.e(tag, msg);
        }else{
            Log.e(tag, msg);
        }
    }




}
