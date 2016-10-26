package com.ibrightech.eplayer.sdk.common.entity;

import com.ibrightech.eplayer.sdk.common.db.IbrightechDatabase;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerDrawMsgInfoTextType;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerDrawMsgInfoType;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerDrawPadType;
import com.ibrightech.eplayer.sdk.common.util.LogUtil;
import com.ibrightech.eplayer.sdk.common.util.NumberUtil;
import com.ibrightech.eplayer.sdk.common.util.StringUtils;
import com.jcraft.jzlib.DataHelper;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Where;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Table(database = IbrightechDatabase.class)
public class DrawMsgInfo extends BaseModel implements Serializable {

    @PrimaryKey(autoincrement = true)
    private int dbid;

    @Column
    private long key;


    @Column
    private long seq;

    @Column
    private String createTime;
    @Column
    private String liveClassroomId;
    @Column
    private String resType;
    @Column
    private String drawMsg;
    @Column
    private int infoTypeValue;
    @Column
    private int padTypeValue;
    @Column
    private int page;
    @Column
    private int pptId;
    @Column
    private int pptPageId;


    private EplayerDrawMsgInfoType infoType;

    private EplayerDrawPadType padType;

    private int color;

    private List<String> datas;

    private EplayerDrawMsgInfoTextType textType;

    private float x;       //文字X坐标

    private float y;       //文字Y坐标

    private int s;    //文字大小

    private String d;    //是否删除操作

    private String id;   //文字唯一标识

    private String t;    //文字内容

    private int c;    //文字颜色

    private boolean decoded;


    public static DrawMsgInfo fromJson(JSONObject jsonObject) {
        DrawMsgInfo msg = new DrawMsgInfo();

        msg.setCreateTime(jsonObject.optString("createTime"));
        msg.setLiveClassroomId(jsonObject.optString("liveClassroomId"));
        msg.setResType(jsonObject.optString("resType"));

        msg.setDrawMsg(jsonObject.optString("drawMsg"));

        if(jsonObject.optBoolean("isBlank")){
            msg.setPadType(EplayerDrawPadType.DrawPadTypeWhiteBoard);
        }else{
            msg.setPadType(EplayerDrawPadType.DrawPadTypeDocument);
        }

        msg.setPage(jsonObject.optInt("page"));

        msg.setSeq(jsonObject.optLong("seq"));
        msg.setPptPageId(jsonObject.optInt("pptPageId"));
        msg.setPptId(jsonObject.optInt("pptId"));

        return msg;
    }

    private static final String TYPE_LINE = "1";      //画线

    private static final String TYPE_ERASER = "6";    //橡皮擦

    private static final String TYPE_TEXT = "7";      //文字

    private static final String TYPE_CLEAR = "2";     //清除画布



    private static final String DECODE_KEY_DATAS = "datas";

    private static final String DECODE_KEY_COLOR = "color";


    public void loadInfoType() {
        if (this.getDrawMsg().length()==1&&TYPE_CLEAR.equals(this.getDrawMsg())){
            this.setInfoType(EplayerDrawMsgInfoType.DrawMsgInfoTypeClear);

            return;
        }


        if (this.getDrawMsg().length() > 0) {
            String value = this.getDrawMsg().substring(0, 1);
            if (TYPE_LINE.equals(value)) {
                this.setInfoType(EplayerDrawMsgInfoType.DrawMsgInfoTypeLine);
            }else if (TYPE_ERASER.equals(value)) {
                this.setInfoType(EplayerDrawMsgInfoType.DrawMsgInfoTypeEraser);
            } else if (TYPE_TEXT.equals(value)) {
                this.setInfoType(EplayerDrawMsgInfoType.DrawMsgInfoTypeText);
            }
        }
    }


    public void loadDrawMsg(){
        if(isDecoded())
            return;

        if(this.getInfoType() == EplayerDrawMsgInfoType.DrawMsgInfoTypeLine){
            String value = this.getDrawMsg().substring(2);
            String[] wordsValue = value.split("@");
            for (int i = 0; i < wordsValue.length; i++) {
                String allContent = wordsValue[i];
                String encodeValue = allContent.substring(6);
                String decodeValue = DataHelper.decompress(encodeValue);
                if (allContent.startsWith(DECODE_KEY_COLOR)) {
                    setColor(NumberUtil.parseInt(decodeValue, -1));
                } else if (allContent.startsWith(DECODE_KEY_DATAS)) {
                    if (StringUtils.isValid(decodeValue)) {
                        String[] positions = decodeValue.split(",");
                        this.setDatas(Arrays.asList(positions));
                        if (this.getDatas().size() % 2 == 1 && this.getDatas().size() > 2) {
                            this.setDatas(this.getDatas().subList(0, this.getDatas().size() - 1));
                        }
                    }


                }
            }

        }else if(this.getInfoType() == EplayerDrawMsgInfoType.DrawMsgInfoTypeEraser){
            String value = this.getDrawMsg().substring(8);
            String decodeValue = DataHelper.decompress(value);
            if (StringUtils.isValid(decodeValue)) {
                String[] positions = decodeValue.split(",");
                this.setDatas(Arrays.asList(positions));
            }

        }else if(this.getInfoType() == EplayerDrawMsgInfoType.DrawMsgInfoTypeText){
            String value = this.getDrawMsg().substring(2);
            String[] wordsValue = value.split("@");


            boolean hasColor =false;
            boolean delete =false;
            boolean size =false;

            for (int i = 0; i < wordsValue.length; i++) {
                String content = wordsValue[i];
                if (content.startsWith("x")) {
                    setX(NumberUtil.parseFloat(DataHelper.decompress(content.substring(2)), 0f));
                } else if (content.startsWith("y")) {
                    setY(NumberUtil.parseFloat(DataHelper.decompress(content.substring(2)), 0f));
                } else if (content.startsWith("s")) {
                    size =true;
                    setS(NumberUtil.parseInt(DataHelper.decompress(content.substring(2)), 10));
                } else if (content.startsWith("id")) {
                    setId(DataHelper.decompress(content.substring(3)));
                } else if (content.startsWith("t")) {
                    setT(DataHelper.decompress(content.substring(2)));
                } else if (content.startsWith("c")) {
                    setC(NumberUtil.parseInt(DataHelper.decompress(content.substring(2)), 0));
                    hasColor =true;
                } else if (content.startsWith("d")) {
                    delete =true;
                    setD(DataHelper.decompress(content.substring(2)));
                }
            }
            if(wordsValue.length==6){
                setTextType(EplayerDrawMsgInfoTextType.DrawMsgInfoTextTypeShow);
            }else if (wordsValue.length==3){
                setTextType(EplayerDrawMsgInfoTextType.DrawMsgInfoTextTypePoint);
            }else if (wordsValue.length==2){
                if(size){
                    setTextType(EplayerDrawMsgInfoTextType.DrawMsgInfoTextTypeSize);
                }else if(hasColor){
                    setTextType(EplayerDrawMsgInfoTextType.DrawMsgInfoTextTypeColor);
                }else if(delete){
                    setTextType(EplayerDrawMsgInfoTextType.DrawMsgInfoTextTypeDelete);
                }
            }


        }

        setDecoded(true);
    }

    public String getLineColorHex() {
        String colorHex = Integer.toHexString(getColor());
        while (colorHex.length() < 6) {
            colorHex = "0" + colorHex;
        }
        colorHex = "#" + colorHex;
//        LogUtil.d("Color Hex:" + colorHex);
        return colorHex;
    }

    public String getTextColorHex() {
        String colorHex = Integer.toHexString(getC());
        while (colorHex.length() < 6) {
            colorHex = "0" + colorHex;
        }
        colorHex = "#" + colorHex;
//        LogUtil.d("Color Hex:" + colorHex);
        return colorHex;
    }


    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLiveClassroomId() {
        return liveClassroomId;
    }

    public void setLiveClassroomId(String liveClassroomId) {
        this.liveClassroomId = liveClassroomId;
    }

    public String getResType() {
        return resType;
    }

    public void setResType(String resType) {
        this.resType = resType;
    }

    public EplayerDrawPadType getPadType() {

        if(this.getPadTypeValue() ==0){
            padType = EplayerDrawPadType.DrawPadTypeDocument;
        }else if(this.getPadTypeValue() ==1){
            padType = EplayerDrawPadType.DrawPadTypeWhiteBoard;
        }
        return padType;
    }

    public void setPadType(EplayerDrawPadType padType) {
        this.padType = padType;
        this.setPadTypeValue(padType.value());
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPptId() {
        return pptId;
    }

    public void setPptId(int pptId) {
        this.pptId = pptId;
    }

    public int getPptPageId() {
        return pptPageId;
    }

    public void setPptPageId(int pptPageId) {
        this.pptPageId = pptPageId;
    }

    public long getSeq() {
        return seq;
    }

    public void setSeq(long seq) {
        this.seq = seq;
    }

    public EplayerDrawMsgInfoType getInfoType() {
        if(this.getInfoTypeValue() ==1){
            infoType = EplayerDrawMsgInfoType.DrawMsgInfoTypeLine;
        }else if(this.getInfoTypeValue() ==2){
            infoType = EplayerDrawMsgInfoType.DrawMsgInfoTypeClear;
        }else if(this.getInfoTypeValue() ==6){
            infoType = EplayerDrawMsgInfoType.DrawMsgInfoTypeEraser;
        }else if(this.getInfoTypeValue() ==7){
            infoType = EplayerDrawMsgInfoType.DrawMsgInfoTypeText;
        }
        return infoType;
    }

    public void setInfoType(EplayerDrawMsgInfoType infoType) {
        this.infoType = infoType;
        this.setInfoTypeValue(infoType.value());
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public List<String> getDatas() {
        return datas;
    }

    public void setDatas(List<String> datas) {
        this.datas = datas;
    }

    public String getDrawMsg() {
        return drawMsg;
    }

    public void setDrawMsg(String drawMsg) {
        this.drawMsg = drawMsg;
    }

    public EplayerDrawMsgInfoTextType getTextType() {
        return textType;
    }

    public void setTextType(EplayerDrawMsgInfoTextType textType) {
        this.textType = textType;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getS() {
        return s;
    }

    public void setS(int s) {
        this.s = s;
    }

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public int getC() {
        return c;
    }

    public void setC(int c) {
        this.c = c;
    }

    public int getInfoTypeValue() {
        return infoTypeValue;
    }

    public void setInfoTypeValue(int infoTypeValue) {
        this.infoTypeValue = infoTypeValue;
    }

    public int getPadTypeValue() {
        return padTypeValue;
    }

    public void setPadTypeValue(int padTypeValue) {
        this.padTypeValue = padTypeValue;
    }

    public static List<DrawMsgInfo> loadByPptIdAndPage(int pptId,int page){


        LogUtil.d("----search DrawMsgInfo pptId  page-----", pptId + " "+ page);


//        DrawMsgInfo.find(DrawMsgInfo.class," seq= ?", (String[]) keys.toArray());
//

        Where<DrawMsgInfo>  where  = SQLite.select().from(DrawMsgInfo.class).where(DrawMsgInfo_Table.pptId.eq(pptId)).and(DrawMsgInfo_Table.page.eq(page));


        List<DrawMsgInfo>  list=   where.orderBy(DrawMsgInfo_Table.seq,true).queryList();


        if(list==null){
            list = new ArrayList<DrawMsgInfo>();
        }

        LogUtil.d("----search DrawMsgInfo list-----",list+"");
        return list;
    }

    public static List<DrawMsgInfo> loadByKeys(List<String> keys){


         LogUtil.d("----search DrawMsgInfo keys-----", keys + "");

        if(keys.size()==0){
            return new ArrayList<DrawMsgInfo>();
        }

//        DrawMsgInfo.find(DrawMsgInfo.class," seq= ?", (String[]) keys.toArray());
//

        Where<DrawMsgInfo>  where  = SQLite.select().from(DrawMsgInfo.class).where();

        for (String key:keys){
            where.and(DrawMsgInfo_Table.seq.eq(Long.parseLong(key)));
        }

        List<DrawMsgInfo>  list=   where.orderBy(DrawMsgInfo_Table.seq,true).queryList();


        if(list==null){
            list = new ArrayList<DrawMsgInfo>();
        }

            LogUtil.d("----search DrawMsgInfo list-----",list+"");
        return list;
    }

    public long getKey() {
        return key;
    }

    public void setKey(long key) {
        this.key = key;
    }

    public int getDbid() {
        return dbid;
    }

    public void setDbid(int dbid) {
        this.dbid = dbid;
    }

    public boolean isDecoded() {
        return decoded;
    }

    public void setDecoded(boolean decoded) {
        this.decoded = decoded;
    }
}
