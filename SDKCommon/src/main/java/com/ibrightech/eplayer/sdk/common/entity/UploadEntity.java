package com.ibrightech.eplayer.sdk.common.entity;

import com.ibrightech.eplayer.sdk.common.util.CheckUtil;

/**
 * Created by daocren on 14-1-7.
 */
public class UploadEntity {


    public String name;
    public String xmlPath;



    public static UploadEntity fromString(String result) {
        if (CheckUtil.isEmpty(result)) {
            return null;
        }

        try {
            UploadEntity asset = new UploadEntity();
            {
                String[] strs = result.split("<StatusPath>Status/");
                String strtemp = "";
                if (strs.length == 2) {
                    strtemp = strs[1];
                    strtemp = strtemp.substring(0, strtemp.indexOf("</StatusPath>"));
                    asset.name = strtemp;
                }
            }
            {
                String[] strs2 = result.split("<XmlPath>");
                String strtemp2 = "";
                if (strs2.length == 2) {
                    strtemp2 = strs2[1];
                    strtemp2 = strtemp2.substring(0, strtemp2.indexOf("</XmlPath>"));
                    asset.xmlPath = strtemp2;
                }

            }
            return asset;

        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }

    }

}
