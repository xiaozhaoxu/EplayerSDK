package com.ibrightech.eplayer.sdk.widget;


import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.ibrightech.eplayer.sdk.common.engin.EplayerEngin;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerDrawMsgInfoType;
import com.ibrightech.eplayer.sdk.common.entity.DrawMsgInfo;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.DrawPadInfo;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.DrawPageInfo;
import com.ibrightech.eplayer.sdk.common.entity.session.EplayerSessionInfo;
import com.ibrightech.eplayer.sdk.common.net.ws.event.DrawMsgInfoEvent;
import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.common.util.LogUtil;
import com.ibrightech.eplayer.sdk.common.util.WeakReferenceHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * 画板界面，包含背景图片，画笔面板，已经整个界面的所有事件响应，外部只需要调用打开或者关闭事件监听即可，不需要再做其他任何事情
 */
public class DrawPadView extends RelativeLayout {

    private static final String TAG = DrawPadView.class.getSimpleName();
    public static final int MESSAGE_REFRESH_PEN_DRAW_VIEW = 10000;    //刷新画笔变化


    public static final  double DRAWPADVIEW_SCALE =(double)9/16;
    double DRAWIMAGE_DEAFULT_SCALE=1180.0f / 834.0f;
    double drawImagescale=DRAWIMAGE_DEAFULT_SCALE;

    private DrawTextView drawTextView;
    private DrawPenView drawPenView;

    private int page; //缓存页码，用于画笔重置使用

    //
    public DrawPadView(Context context) {
        super(context);
        initView();
    }

    public DrawPadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public DrawPadView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }


    public void releaseAll() {

        if(EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);

        //TODO 处理界面的内存回收问题

        if (drawTextView != null) {
            drawTextView.clearAll();
            drawTextView.release();
        }

        if (drawPenView != null) {
            drawPenView.clearAll();
            drawPenView.release();
        }
        drawTextView = null;
        drawPenView = null;

    }

    private void initView() {

        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        //绘制文字界面
        drawTextView = new DrawTextView(this.getContext());
        drawTextView.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        this.addView(drawTextView);
        //绘制画笔界面
        drawPenView = new DrawPenView(this.getContext());
        drawPenView.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        this.addView(drawPenView);
    }


    static class DrawPadViewHandler extends WeakReferenceHandler<DrawPadView> {

        public DrawPadViewHandler(DrawPadView drawPadView) {
            super(drawPadView);
        }

        @Override
        protected void handleMessage(Message msg, DrawPadView drawPadView) {
            if (drawPadView.drawTextView == null || drawPadView.drawPenView == null)
                return;

            switch (msg.what) {

                case MESSAGE_REFRESH_PEN_DRAW_VIEW: {
                    LogUtil.d(TAG, "刷新画笔");

                    drawPadView.drawTextView.invalidate();
                    drawPadView.drawPenView.invalidate();

                    break;
                }

            }
        }
    }

    DrawPadViewHandler handler = new DrawPadViewHandler(this);


    //EventBus回调函数

    //画笔信息变化
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEventBackgroundThread(DrawMsgInfoEvent event) {
        //todo 绘制画笔


        DrawMsgInfo msgInfo = event.getMsgInfo();

        if(msgInfo!=null) {
            this.processMsgInfo(msgInfo);
        }


        List<DrawMsgInfo> msgInfos = event.getMsgInfos();
        if(!CheckUtil.isEmpty(msgInfos)) {
            for (DrawMsgInfo _msgInfo : msgInfos) {
                this.processMsgInfo(_msgInfo);
            }
        }
//        drawTextView.invalidate();
//        drawPenView.invalidate();

        //发送主线程通知
        handler.sendEmptyMessage(MESSAGE_REFRESH_PEN_DRAW_VIEW);
        System.gc();

    }
    //画笔信息变化
    public void resetViewSizeAndMsg(int width,int height) {
        //todo 绘制画笔

        drawTextView.clearAll();;
        drawPenView.clearAll();;

//       int width = this.getWidth();
//       int height = this.getHeight();

        drawPenView.setViewSize(width, height);
        drawTextView.setViewSize(width, height);

        drawAllMsgInfo();

        drawTextView.invalidate();
        drawPenView.invalidate();

        System.gc();

    }

    //翻页恢复时显示一开如的画笔
    public void backPrePagedrawAllMsgInfo(){
        drawTextView.clearAll();
        drawPenView.clearAll();
        drawAllMsgInfo();

        drawTextView.invalidate();
        drawPenView.invalidate();

        System.gc();
    }

    public void cleanAllPaint(){
        drawTextView.clearAll();
        drawPenView.clearAll();
        drawTextView.invalidate();
        drawPenView.invalidate();

    }

    private void processMsgInfo(DrawMsgInfo msgInfo){
        EplayerSessionInfo eplayerSessionInfo =  EplayerEngin.getInstance().getSessionInfo();
        eplayerSessionInfo.drawPageInfo.addDrawMsgInfo(msgInfo);

        if( eplayerSessionInfo.drawPadInfo.page==msgInfo.getPage()) {//因为有可能是用户自己翻页了，所以导致页码可能不对，此时不显示画笔信息
            if (msgInfo.getInfoType() == EplayerDrawMsgInfoType.DrawMsgInfoTypeClear) {
                drawTextView.clearAll();
                drawPenView.clearAll();
            } else if (msgInfo.getInfoType() == EplayerDrawMsgInfoType.DrawMsgInfoTypeLine) {
                msgInfo.loadDrawMsg();
                drawPenView.drawLine(msgInfo);
            } else if (msgInfo.getInfoType() == EplayerDrawMsgInfoType.DrawMsgInfoTypeEraser) {
                msgInfo.loadDrawMsg();
                drawPenView.drawCleanLine(msgInfo);
            } else if (msgInfo.getInfoType() == EplayerDrawMsgInfoType.DrawMsgInfoTypeText) {
                msgInfo.loadDrawMsg();
                drawTextView.drawText(msgInfo);
            }
        }

    }

    private void drawAllMsgInfo() {

        EplayerSessionInfo eplayerSessionInfo =  EplayerEngin.getInstance().getSessionInfo();
        DrawPadInfo drawPadInfo = eplayerSessionInfo.drawPadInfo;
        DrawPageInfo drawPageInfo = eplayerSessionInfo.drawPageInfo;

        List<DrawMsgInfo> drawsList = drawPageInfo.loadDrawMsgInfos(drawPadInfo.padType, drawPadInfo.page);

        if (drawsList != null && drawsList.size() > 0) {
            for (DrawMsgInfo msgInfo : drawsList) {
                if (msgInfo.getInfoType() == EplayerDrawMsgInfoType.DrawMsgInfoTypeClear) {
                    drawTextView.clearAll();
                    drawPenView.clearAll();
                } else if (msgInfo.getInfoType() == EplayerDrawMsgInfoType.DrawMsgInfoTypeLine) {
                    msgInfo.loadDrawMsg();
                    drawPenView.drawLine(msgInfo);
                } else if (msgInfo.getInfoType() == EplayerDrawMsgInfoType.DrawMsgInfoTypeEraser) {
                    msgInfo.loadDrawMsg();
                    drawPenView.drawCleanLine(msgInfo);
                } else if (msgInfo.getInfoType() == EplayerDrawMsgInfoType.DrawMsgInfoTypeText) {
                    msgInfo.loadDrawMsg();
                    drawTextView.drawText(msgInfo);
                }
            }
        }
        System.gc();

    }
}

