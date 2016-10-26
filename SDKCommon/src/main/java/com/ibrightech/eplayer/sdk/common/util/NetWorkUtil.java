package com.ibrightech.eplayer.sdk.common.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by zhaoxu2014 on 16/8/27.
 */
public class NetWorkUtil {
    /**
     * 判断网络类型 wifi  3G
     *
     * @param context
     * @return
     */
    public static boolean isMobileNetWorkType(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();

        if (info != null && info.isAvailable()) {
            if (info.getType()==ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        }
        return false;
    }
}
