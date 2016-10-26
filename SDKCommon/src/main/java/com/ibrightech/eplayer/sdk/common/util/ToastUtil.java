package com.ibrightech.eplayer.sdk.common.util;

import android.content.Context;
import android.widget.Toast;

import com.ibrightech.eplayer.sdk.common.config.EplayerConfigBuilder;


/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-12-4
 * Time: 下午3:11
 * To change this template use File | Settings | File Templates.
 */
public class ToastUtil {
    /*
     * showtime：吐司时长 Stringid：定义 在String里的id object:Stringid里的参数
	 */
    public static void showToast(Context context, int showtime, int Stringid,
                                 Object... object) {
        Toast.makeText(context, StringUtils.getStringByKey( Stringid, object),
                showtime).show();
    }
    public static void showToast( int showtime, int Stringid,
                                  Object... object) {
        Toast.makeText(EplayerConfigBuilder.getInstance().getContext(), StringUtils.getStringByKey( Stringid, object),
                showtime).show();
    }

    public static void showToast(Context context, int Stringid,
                                 Object... object) {
        Toast.makeText(context, StringUtils.getStringByKey( Stringid, object),
                Toast.LENGTH_SHORT).show();
    }

    public static void showToast(int Stringid,
                                 Object... object) {
        String content=StringUtils.getStringByKey( Stringid, object);
        Toast.makeText(EplayerConfigBuilder.getInstance().getContext(),content,
                Toast.LENGTH_SHORT).show();
    }

    public static void showStringToast(Context context, int showtime,
                                       String showstring) {
        Toast.makeText(context, showstring, showtime).show();
    }
    public static void showStringToast( int showtime,
                                        String showstring) {
        Toast.makeText(EplayerConfigBuilder.getInstance().getContext(), showstring, showtime).show();
    }

    public static void showStringToast(Context context, String showstring) {
        Toast.makeText(context, showstring, Toast.LENGTH_SHORT).show();
    }
    public static void showStringToast(String showstring) {
        Toast.makeText(EplayerConfigBuilder.getInstance().getContext(), showstring, Toast.LENGTH_SHORT).show();
    }
}
