package com.ibrightech.eplayer.sdk.common.util;

import android.content.SharedPreferences;

import com.ibrightech.eplayer.sdk.common.config.EplayerConfigBuilder;

/**
 * Created by zhaoxu2014 on 16/8/27.
 */
public class ConfigUtil {
    public static final String KEY_ONLY_WIFI_STATE="key_only_wifi_state";

    public static <T> void putPreferences(String key, T value){
        SharedPreferences sharedPreferences = EplayerConfigBuilder.getInstance().getPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(value instanceof String){
            editor.putString(key, value.toString());
        }else if(value instanceof Boolean){
            editor.putBoolean(key, ((Boolean) value).booleanValue());
        }else if(value instanceof Integer){
            editor.putInt(key, ((Integer) value).intValue());
        }else if(value instanceof Float){
            editor.putFloat(key, ((Float) value).floatValue());
        }else if(value instanceof Long){
            editor.putLong(key, ((Long) value).longValue());
        }
        editor.commit();
    }

    public static <T> void removePreferences(String key){
        SharedPreferences sharedPreferences = EplayerConfigBuilder.getInstance().getPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.commit();
    }

    public static <T> T getPreferences(String key, T value){
        Object o = null;
        SharedPreferences preferences = EplayerConfigBuilder.getInstance().getPreferences();
        if(value instanceof String){
            o =  preferences.getString(key, value.toString());
        }else if(value instanceof Boolean){
            o = preferences.getBoolean(key, ((Boolean) value).booleanValue());
        }else if(value instanceof Integer){
            o = preferences.getInt(key, ((Integer) value).intValue());
        }else if(value instanceof Float){
            o = preferences.getFloat(key, ((Float) value).floatValue());
        }else if(value instanceof Long){
            o = preferences.getLong(key, ((Long) value).longValue());
        }
        T t = (T) o;
        return t;
    }
}
