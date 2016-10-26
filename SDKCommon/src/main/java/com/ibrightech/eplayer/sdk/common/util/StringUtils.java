package com.ibrightech.eplayer.sdk.common.util;

import android.os.Bundle;
import android.text.Html;
import android.widget.EditText;
import android.widget.TextView;


import com.ibrightech.eplayer.sdk.common.config.EplayerConfigBuilder;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created with IntelliJ IDEA.
 * User: SlothMonkey
 * Date: 12-8-1
 * Time: 下午2:51
 * To change this template use File | Settings | File Templates.
 */
public class StringUtils {

    /**
     * 解决自动换行排版问题
     *
     * @param input
     * @return
     */
    public static String ToDBC(String input) {
        if(CheckUtil.isEmpty(input)){
            return "";
        }

        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    public static String getDownloadFileName(String audio_url) {
        int lastPosition = audio_url.lastIndexOf(47);
        String audio_name = audio_url.substring(lastPosition, audio_url.length());
        return audio_name;
    }

    public static boolean isValid(String str) {
        return str != null && str.trim().length() > 0;
    }

    /**
     * 判断字符窜是否不为null和空串
     *
     * @param str String
     * @return boolean
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    /*
       * 通过 Key从Bundle中获取相对应的整型值
       */
    public static int getIntResFormBundle(Bundle bundle, String parm) {
        int res = 0;
        try {
            res = new Integer(bundle.get(parm).toString());
        } catch (Exception e) {
            e.printStackTrace();
            res = 0;
        }
        return res;
    }




    /*
      * 根据ID获取相应String内容
      */
    public static String getStringByKey(int keyid,
                                        Object... object) {
        String res = "";
        try {
            res = EplayerConfigBuilder.getInstance().getContext().getResources().getString(keyid, object);
        } catch (Exception e) {
            e.printStackTrace();
            res = "";
        }
        return res;
    }

    /*
     * 根据ID获取相应String内容然后按Html显示 ,
     */
    public static String getHtmlStringByKey( int keyid,
                                            Object... object) {
        String res = "";
        try {
            res = getStringByKey( keyid, object);
            res = Html.fromHtml(res).toString();
        } catch (Exception e) {
            e.printStackTrace();
            res = "";
        }
        return res;
    }

    public static String getEditTextText(EditText et) {
        String res = "";
        if (et != null) {
            res = et.getText().toString().trim();
        }
        return res;
    }

    public static String getTextViewText(TextView et) {
        String res = "";
        if (et != null) {
            res = et.getText().toString().trim();
        }
        return res;
    }

    public static String getStringFromInputStream(InputStream inputstream) {
        String resultData = "";
        try {

            InputStreamReader in = new InputStreamReader(inputstream, "UTF-8");
            BufferedReader buffer = new BufferedReader(in);
            String inputLine = null;
            // 使用循环来读取获得的数据
            while (((inputLine = buffer.readLine()) != null)) {
                // 我们在每一行后面加上一个"\n"来换行
                resultData += inputLine + "\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultData;
    }

    public static InputStream getStringStream(String sInputString) {

        if (sInputString != null && !sInputString.trim().equals("")) {
            try {
                ByteArrayInputStream tInputStringStream = new ByteArrayInputStream(
                        sInputString.getBytes());
                return tInputStringStream;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
}
