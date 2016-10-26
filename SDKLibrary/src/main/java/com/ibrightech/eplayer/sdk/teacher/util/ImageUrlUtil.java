package com.ibrightech.eplayer.sdk.teacher.util;

import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.teacher.net.UrlConstant;

/**
 * Created by zhaoxu2014 on 15/8/5.
 */
public class ImageUrlUtil {

    private static String IMAGE_DEAFULT_URL =null;//"drawable://" + R.drawable.img_default_bg;
    public static String getUrl(String suffixUrl) {
        if (CheckUtil.isEmpty(suffixUrl)) {
            return IMAGE_DEAFULT_URL;
        }
        if (CheckUtil.isEmpty(UrlConstant.businessHost)) {
            return IMAGE_DEAFULT_URL;

        }
        if (suffixUrl.startsWith("http://")) {
            return suffixUrl;
        }
        if (suffixUrl.startsWith("//")) {
            return "http:"+suffixUrl;
        }

        return UrlConstant.businessHost + suffixUrl;
    }

}
