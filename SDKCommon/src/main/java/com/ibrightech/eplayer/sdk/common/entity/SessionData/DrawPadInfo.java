package com.ibrightech.eplayer.sdk.common.entity.SessionData;

import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerDrawPadColorType;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerDrawPadType;

import org.json.JSONObject;

/**
 * Created by junhai on 14-8-13.
 */
public class DrawPadInfo {
    public String fileName;
    public String resType;
    public String createTime;

    public EplayerDrawPadColorType blankColor;
    public EplayerDrawPadType padType;

    public int prePage;
    public int page;


    public double pageOffset;

    public int pptId;
    public long seq;

    public boolean userSwitch;

    public boolean isWordScroll;

    public DrawPadInfo(){
        super();

        this.prePage=-1;
    }

    public boolean validatePageChange(int page){
        return page !=this.page;
    }

    public void cachePageChange(int page){
        if(this.prePage==-1){
            this.prePage = this.page;
        }
        this.page = page;

    }
    public boolean validateResetPageChange(){
        return prePage !=this.page && prePage!=-1;
    }
    public void resetPageChange(){
        this.page = prePage;
        prePage = -1;

    }

   public  DrawPadInfo copySelf(){
       DrawPadInfo  drawPadInfo = new DrawPadInfo();
       drawPadInfo.fileName = this.fileName;
       drawPadInfo.resType = this.resType;
       drawPadInfo.createTime = this.createTime;
       drawPadInfo.blankColor = this.blankColor;
       drawPadInfo.padType = this.padType;
       drawPadInfo.page = this.page;
       drawPadInfo.pageOffset = this.pageOffset;
       drawPadInfo.pptId = this.pptId;
       drawPadInfo.seq = this.seq;
       drawPadInfo.isWordScroll = this.isWordScroll;
       drawPadInfo.prePage=prePage;
       return drawPadInfo;
   }


    public static DrawPadInfo fromJson(JSONObject jsonObject) {
        DrawPadInfo msg = new DrawPadInfo();

        msg.createTime = jsonObject.optString("createTime");
        msg.fileName = jsonObject.optString("fileName");
        msg.resType = jsonObject.optString("resType");

        int blankColor = jsonObject.optInt("blankColor");

        msg.blankColor =EplayerDrawPadColorType.getEnumBykey(blankColor);


        if(jsonObject.optBoolean("isBlank")){
            msg.padType = EplayerDrawPadType.DrawPadTypeWhiteBoard;
        }else{
            msg.padType = EplayerDrawPadType.DrawPadTypeDocument;
        }

        msg.page = jsonObject.optInt("page");

        msg.pageOffset = jsonObject.optDouble("pageOffset");

        msg.seq = jsonObject.optLong("seq");
        msg.pptId = jsonObject.optInt("pptId");

        if (jsonObject.isNull("isWordScroll")) {
            msg.isWordScroll = true;
        } else
            msg.isWordScroll = jsonObject.optBoolean("isWordScroll");

        msg.prePage=-1;
        return msg;
    }

    public String  infoKey(){
        return String.format("%010d-%010d",this.pptId,this.page);
    }

    public boolean isSamePadInfo(DrawPadInfo drawPadInfo) {

        return this.padType == drawPadInfo.padType && this.page == drawPadInfo.page && this.pptId == drawPadInfo.pptId && this.resType.equals(drawPadInfo.resType) && this.pageOffset == drawPadInfo.pageOffset&&this.seq==drawPadInfo.seq;
    }
    public boolean isAllSamePadInfo(DrawPadInfo  drawPadInfo){
        return this.padType == drawPadInfo.padType&&this.page==drawPadInfo.page&&this.pptId==drawPadInfo.pptId&&this.resType.equals(drawPadInfo.resType)&&this.pageOffset==drawPadInfo.pageOffset&&this.isWordScroll==drawPadInfo.isWordScroll&&this.blankColor==drawPadInfo.blankColor;
    }

}
