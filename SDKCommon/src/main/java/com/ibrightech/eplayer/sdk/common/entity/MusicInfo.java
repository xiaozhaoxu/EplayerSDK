package com.ibrightech.eplayer.sdk.common.entity;

import com.ibrightech.eplayer.sdk.common.util.NumberUtil;

import org.json.JSONObject;

/**
 * Created by daocren on 14-1-9.
 */
public class MusicInfo {

    //直播状态：0=自定义,1=轻音乐,2=英语歌,3=流行歌
    public static final int MUSIC_TYPE_OPTIONAL = 0;
    public static final int MUSIC_TYPE_LIGHT = 1;
    public static final int MUSIC_TYPE_ENGLISH = 2;
    public static final int MUSIC_TYPE_POP = 3;

    public String code;
    public String musicType;
    public String data;
    public String msg;

    public static MusicInfo fromJson(JSONObject jsonObject) {
        MusicInfo musicInfo = new MusicInfo();
        musicInfo.code = jsonObject.optString("code");
        musicInfo.musicType = jsonObject.optString("musicType");
        musicInfo.data = jsonObject.optString("data");
        musicInfo.msg = jsonObject.optString("msg");
        return musicInfo;
    }

    public boolean isOptionalMusic() {
        if (MUSIC_TYPE_OPTIONAL == NumberUtil.parseInt(musicType, -1)) {
            return true;
        }
        return false;
    }


}
