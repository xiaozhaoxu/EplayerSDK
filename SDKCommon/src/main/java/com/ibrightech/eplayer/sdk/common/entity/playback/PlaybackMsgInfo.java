package com.ibrightech.eplayer.sdk.common.entity.playback;


import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerDrawMsgInfoType;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerDrawPadType;
import com.ibrightech.eplayer.sdk.common.entity.DrawMsgInfo;
import com.ibrightech.eplayer.sdk.common.util.NumberUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by junhai on 14-11-20.
 */
public class PlaybackMsgInfo {

    public long startTime;
    public long endTime;

    public long playTime;

    public long page;


    private List<String> whiteClearValues;
    private List<String> docClearValues;

    private List<String> whiteValues;
    private List<String> docValues;

    public PlaybackMsgInfo() {
        super();

        whiteClearValues = new ArrayList<String>();
        docClearValues = new ArrayList<String>();

        whiteValues = new ArrayList<String>();
        docValues = new ArrayList<String>();

    }

    public void addDrawMsgInfo(DrawMsgInfo info) {
        info.loadDrawMsg();

        String key = info.getSeq() + "";

        if (info.getPadType() == EplayerDrawPadType.DrawPadTypeWhiteBoard) {

            whiteValues.add(key);
            if (info.getInfoType() == EplayerDrawMsgInfoType.DrawMsgInfoTypeClear) {
                whiteClearValues.add(key);
            }

        } else {

            docValues.add(key);
            if (info.getInfoType() == EplayerDrawMsgInfoType.DrawMsgInfoTypeClear) {
                docClearValues.add(key);
            }

        }

    }

    public void sortMsgInfo(){

        Collections.sort(whiteClearValues);
        Collections.sort(docClearValues);
        Collections.sort(whiteValues);
        Collections.sort(docValues);

    }

    public void loadTime() {
        this.playTime = this.endTime - this.startTime;
        this.sortMsgInfo();
    }

    public boolean checkOwner(long time) {
        return this.startTime <= time && this.endTime >= time;
    }

    public List<String> findMsg(long time) {
        List<String> datas = new ArrayList<String>();

        //查找离当前时间最近的清屏操作
        long findWhiteClear = 0;
        long findDocClear = 0;

        //查找离时间最近的白板清屏操作
        for (int i = whiteClearValues.size() - 1; i >= 0; i--) {
            String key = whiteClearValues.get(i);
            long _time = NumberUtil.parseLong(key, 0);
            if (_time < time) {
                findWhiteClear = _time;
                break;
            }
        }

        //查找离时间最近的文档清屏操作
        for (int i = (int) docClearValues.size() - 1; i >= 0; i--) {
            String key = docClearValues.get(i);
            long _time = NumberUtil.parseLong(key, 0);

            if (_time < time) {
                findDocClear = _time;
                break;
            }
        }

        //从清屏位置开始查找白板画笔
        for (String key : whiteValues) {
            long _time = NumberUtil.parseLong(key, 0);

            if (_time > time) {
                break;
            }

            if (_time < findWhiteClear) {
                continue;
            }

            datas.add(key);

        }


        //从清屏位置开始查找文档画笔
        for (String key : docValues) {
            long _time = NumberUtil.parseLong(key, 0);

            if (_time > time) {
                break;
            }

            if (_time < findDocClear) {
                continue;
            }

            datas.add(key);

        }

        return datas;

    }

}
