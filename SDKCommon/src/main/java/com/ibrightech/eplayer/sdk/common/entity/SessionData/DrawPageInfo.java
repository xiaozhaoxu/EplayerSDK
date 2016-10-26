package com.ibrightech.eplayer.sdk.common.entity.SessionData;


import com.ibrightech.eplayer.sdk.common.entity.DrawMsgInfo;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerDrawMsgInfoType;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerDrawPadType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by junhai on 14-8-13.
 */
public class DrawPageInfo {
    public  int pptId;

    private Map<String,List<DrawMsgInfo>> infoDatas =new HashMap<String, List<DrawMsgInfo>>();

    public  DrawPageInfo copySelf(){
        DrawPageInfo  drawPadInfo = new DrawPageInfo();
        drawPadInfo.pptId = this.pptId;
        Map<String,List<DrawMsgInfo>> newinfoDatas=new HashMap<String, List<DrawMsgInfo>>();
        newinfoDatas.putAll(this.infoDatas);
        drawPadInfo.infoDatas = newinfoDatas;
        return drawPadInfo;
    }

    public synchronized void clearALL(){
        infoDatas.clear();
    }
    public synchronized void addDrawMsgInfoList(List<DrawMsgInfo> infoList){
        for (DrawMsgInfo info:infoList){
            addDrawMsgInfo(info);
        }
    }

    public synchronized void addDrawMsgInfo(DrawMsgInfo info){
        if(info.getPadType()==null)
            return;

        String key =  info.getPage() +"﹣"+ info.getPadType().value();

        List<DrawMsgInfo> datas =infoDatas.get(key);
        if(datas==null){
            datas = new ArrayList<DrawMsgInfo>();
            infoDatas.put(key,datas);
        }

        if(info.getInfoType() == EplayerDrawMsgInfoType.DrawMsgInfoTypeClear){
            datas.clear();
        }else{
            datas.add(info);
        }

    }
    public synchronized List<DrawMsgInfo> loadDrawMsgInfos(EplayerDrawPadType padType,int page){
        String key =  page+"﹣"+padType.value();
        List<DrawMsgInfo> datas =infoDatas.get(key);

        List<DrawMsgInfo> temp=new ArrayList<DrawMsgInfo>();
        if(null!=datas)
        temp.addAll(datas);
        return temp;
    }
}
