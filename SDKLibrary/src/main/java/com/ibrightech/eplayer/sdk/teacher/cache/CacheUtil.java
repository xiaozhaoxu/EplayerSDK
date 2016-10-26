package com.ibrightech.eplayer.sdk.teacher.cache;


import com.ibrightech.eplayer.sdk.common.util.StorageUtil;

import java.io.File;

/**
 * Created by zhaoxu2014 on 15/8/30.
 */
public class CacheUtil {
    private static String PUBLICAREA = "publicarea";



    private static ACache getACache() {
        String cacheDir =  getPublicAreaPath();
        return ACache.get(new File(cacheDir));
    }


    private static ACache getPublicAreaACache() {
        return ACache.get(new File(getPublicAreaPath()));
    }


    private static String getPublicAreaPath() {
        return StorageUtil.getDataCacheDierctory() + PUBLICAREA;
    }


    private static String getKeyByUrl(String url) {
        return  url;
    }

    public static void saveString(String url, String value) {
        getACache().put(getKeyByUrl(url), value);
    }

    public static String getStringByKey(String url) {

            return getPublicAreaACache().getAsString(getKeyByUrl(url));


    }



}
