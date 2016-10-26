package com.ibrightech.eplayer.sdk.common.util;

import android.os.Bundle;

/**
 * Created by zhaoxu2014 on 15/7/30.
 */
public class BundleUtil {
    /*
   * 通过 Key从Bundle中获取相对应的String值
   */
    public static String getStringFormBundle(Bundle bundle, String parm) {

        return getStringFormBundle(bundle, parm, "");
    }

    public static String getStringFormBundle(Bundle bundle, String parm,String deafult) {
        String res = deafult;
        try {
            String str = bundle.get(parm).toString();
            if (StringUtils.isEmpty(str)) {
                res = deafult;
            } else {
                res = str;
            }
        } catch (Exception e) {
            res = deafult;
        }
        return res;
    }
    public static int getIntFormBundle(Bundle bundle, String parm) {

        return getIntFormBundle(bundle,parm, -1);
    }
    public static int getIntFormBundle(Bundle bundle, String parm,int deafult) {
        if(null==bundle){
            return deafult;
        }

        return bundle.getInt(parm, deafult);
    }

    public static long getLongFormBundle(Bundle bundle, String parm) {

        return getLongFormBundle(bundle,parm, -1);
    }
    public static long getLongFormBundle(Bundle bundle, String parm,long deafult) {
        if(null==bundle){
            return deafult;
        }

        return bundle.getLong(parm, deafult);
    }

    public static boolean getBooleanFormBundle(Bundle bundle, String parm) {

        return getBooleanFormBundle(bundle,parm,false);
    }
    public static boolean getBooleanFormBundle(Bundle bundle, String parm,boolean deafult) {
        if(null==bundle){
            return deafult;
        }
        return bundle.getBoolean(parm, deafult);
    }

    public static Object getObjectFormBundle(Bundle bundle, String parm) {
        if(null==bundle){
            return null;
        }
        return bundle.get(parm);
    }
}
