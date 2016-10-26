package com.ibrightech.eplayer.sdk.common.util;

import android.os.Environment;

import com.ibrightech.eplayer.sdk.common.config.EplayerSetting;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by zhaoxu2014 on 16/8/26.
 */
public class HeaderUtil {
    public static String getHeaderPath() {
        String headerDir = Environment.getExternalStorageDirectory() + "/" + EplayerSetting.getInstance().app_identity;
        String headerPath = headerDir + "/header.flv";

        FileOutputStream out = null;

        try {
            File file = new File(headerDir);
            if (!file.exists()) {
                file.mkdir();
            }

            File headerFile = new File(headerPath);
            if (!headerFile.exists()) {
                headerFile.createNewFile();
            }else{
                if(headerFile.length()>0)
                    return headerPath;
            }

            out = new FileOutputStream(headerPath);
            byte[] buf = new byte[13];
            buf[0] = 0x46;
            buf[1] = 0x4C;
            buf[2] = 0x56;
            buf[3] = 0x01;
            buf[4] = 0x01;
            buf[5] = 0x00;
            buf[6] = 0x00;
            buf[7] = 0x00;
            buf[8] = 0x09;
            buf[9] = 0x00;
            buf[10] = 0x00;
            buf[11] = 0x00;
            buf[12] = 0x00;

            out.write(buf);
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(out);
        }


        return headerPath;
    }
}
