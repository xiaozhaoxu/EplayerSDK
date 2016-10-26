package com.ibrightech.eplayer.sdk.common.entity;

import com.ibrightech.eplayer.sdk.common.util.CheckUtil;

/**
 * Created by daocren on 14-1-7.
 */
public class CheckStatusEntity {


    public String currentNum;
    public String count;


    public String stateStr;

    public static CheckStatusEntity fromString(String strContent,String stateStr) {

        if (CheckUtil.isEmpty(strContent)) {
            return null;
        }
        CheckStatusEntity asset = new CheckStatusEntity();
        asset.stateStr=stateStr;
        String[] strtemps = strContent.split("/");
        if (strtemps.length == 2) {
            asset.currentNum = strtemps[0];
            asset.count = strtemps[1];
        }

        return asset;

    }


}
