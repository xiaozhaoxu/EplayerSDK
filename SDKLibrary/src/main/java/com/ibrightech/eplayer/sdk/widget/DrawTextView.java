package com.ibrightech.eplayer.sdk.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerDrawMsgInfoTextType;
import com.ibrightech.eplayer.sdk.common.entity.DrawMsgInfo;
import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.common.util.LogUtil;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by junhai on 14-8-14.
 */
public class DrawTextView extends View {


    private List<String> lineKeys =  new ArrayList<String>();

    private Hashtable<String,TextLine>  lineMaps =new Hashtable<String, TextLine>();


    private Object lock = new Object();

//    private List<TextLine> lines = new ArrayList<TextLine>();


    private Paint canvasPaint;
    private int canvasWidth;
    private float scale;

    public DrawTextView(Context context) {
        super(context);
        this.initView();
    }


    public DrawTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initView();
    }

    public DrawTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.initView();
    }

    private void initView() {
        canvasPaint = new Paint();
        canvasPaint.setStyle(Paint.Style.FILL);
    }

    public void setViewSize(int width, int height) {
        canvasWidth = width;
        scale = canvasWidth / 834f;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);


        if(w>0)
            canvasWidth = w;
        scale = canvasWidth / 834f;
        LogUtil.d("----onSizeChanged w:" + w + "--h:" + h + "--oldw:" + oldw + "--oldh:" + oldh+"--scale:"+scale);
    }


    private void addKey(String key){
        synchronized (lock){
            lineKeys.add(key);
        }
    }

    private List<String> getKeys(){

        List<String> keys =  new ArrayList<String>();

        synchronized (lock){
            for (String key : lineKeys) {
                keys.add(key);
            }

        }
        return keys;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float _scale = scale;

        if (lineKeys.size() > 0) {
            List<String> keys = getKeys();
            for (String key : keys) {
                TextLine line =lineMaps.get(key);
                if(line==null)
                    continue;

                canvasPaint.reset();
                canvasPaint.setStyle(Paint.Style.FILL);
                canvasPaint.setColor(line.color);
                canvasPaint.setTextSize(line.size*_scale);

                for (int i = 0; i < line.points.size(); i++) {
                    TextPoint point = line.points.get(i);
                    if(!CheckUtil.isEmpty(point)&&!CheckUtil.isEmpty(point.text)) {
                        try {
                            canvas.drawText(point.text, (line.x + point.x) * _scale, (line.y + point.y) * _scale, canvasPaint);
                        }catch (Exception e){

                        }
                    }
                }
            }
        }
    }




    //添加文字信息
    public void drawText(DrawMsgInfo drawMsgInfo) {

        if (drawMsgInfo == null) {
            return;
        }

        String key = drawMsgInfo.getId();

        if(drawMsgInfo.getTextType() == EplayerDrawMsgInfoTextType.DrawMsgInfoTextTypeShow){
            TextLine line = null;
            if(lineKeys.contains(key)){
                line =  lineMaps.get(key);
                line.points.clear();
            }else{
                line = new TextLine();
                lineMaps.put(key,line);
                addKey(key);
            }

            line.color = Color.parseColor(drawMsgInfo.getTextColorHex());
            line.size = drawMsgInfo.getS();
            line.x = drawMsgInfo.getX();
            line.y = drawMsgInfo.getY();
            if(!CheckUtil.isEmpty(drawMsgInfo.getT())){


                String[] split = drawMsgInfo.getT().split("\\r");
                int index = 0;
                for (String s : split) {
                    TextPoint point = new TextPoint();
                    point.text = s;
                    point.x = 0;
                    point.y = drawMsgInfo.getS() + (index * drawMsgInfo.getS());
                    line.points.add(point);
                    index++;
                }
            }


        }else if(drawMsgInfo.getTextType() ==EplayerDrawMsgInfoTextType.DrawMsgInfoTextTypeDelete){

           int index =-1;
            for (int i=0;i<lineKeys.size();i++){
                String _key = lineKeys.get(i);
                if(_key.equals(key)){
                    index=i;
                    break;
                }
            }

            if(index>=0)
                lineKeys.remove(index);
            lineMaps.remove(key);

        }else if(drawMsgInfo.getTextType() ==EplayerDrawMsgInfoTextType.DrawMsgInfoTextTypePoint){
            TextLine line =  lineMaps.get(key);
            line.x = drawMsgInfo.getX();
            line.y = drawMsgInfo.getY();

        }else if(drawMsgInfo.getTextType() ==EplayerDrawMsgInfoTextType.DrawMsgInfoTextTypeSize){
            TextLine line =  lineMaps.get(key);
            line.size = drawMsgInfo.getS();

        }else if(drawMsgInfo.getTextType() ==EplayerDrawMsgInfoTextType.DrawMsgInfoTextTypeColor){

            TextLine line =  lineMaps.get(key);
            line.color = Color.parseColor(drawMsgInfo.getTextColorHex());
        }


//
//        TextLine line = new TextLine();
//        line.color = Color.parseColor(drawMsgInfo.getTextColorHex());
//        line.size = drawMsgInfo.s;
//
//        String[] split = drawMsgInfo.t.split("\\r");
//        int index = 0;
//        for (String s : split) {
//            TextPoint point = new TextPoint();
//            point.text = s;
//            point.x = drawMsgInfo.x;
//            point.y = drawMsgInfo.y + drawMsgInfo.s + (index * drawMsgInfo.s);
//            line.points.add(point);
//            index++;
//        }
//
//        lines.add(line);


    }

    //画板清屏
    public void clearAll() {
        synchronized (lock){
            lineMaps.clear();
            lineKeys.clear();
        }
    }


    public void release() {
        LogUtil.d("----DrawTextView Finished Recycled");
    }

    class TextLine {


        public int color;
        public int size;

        public float x;
        public float y;

        public List<TextPoint> points = new ArrayList<TextPoint>();


    }

    public class TextPoint {

        public String text;
        public float x;
        public float y;

    }
}
