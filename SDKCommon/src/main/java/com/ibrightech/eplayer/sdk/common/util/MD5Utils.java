package com.ibrightech.eplayer.sdk.common.util;

import java.security.MessageDigest;

/**
 * Created by zhaoxu2014 on 16/5/26.
 */
public class MD5Utils {
    private MD5Utils() {}

    public final static String getMessageDigest(byte[] buffer) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(buffer);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    public final static byte[] getMessageDigest(String content) {

        try {
            byte[] buffer =content.getBytes("8859_1");
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(buffer);
            byte[] md = mdTemp.digest();

            return md;
        } catch (Exception e) {
            return null;
        }
    }
}
