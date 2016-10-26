package com.ibrightech.eplayer.sdk.widget;

import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.ibrightech.eplayer.sdk.common.down.EplayerDownFileUtil;
import com.ibrightech.eplayer.sdk.common.engin.EplayerEngin;
import com.ibrightech.eplayer.sdk.common.entity.Asset;
import com.ibrightech.eplayer.sdk.common.entity.DrawMsgInfo;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerConstant;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerDrawPadType;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.DrawPadInfo;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.DrawPageInfo;
import com.ibrightech.eplayer.sdk.common.entity.other.EplayerNetErrorEntity;
import com.ibrightech.eplayer.sdk.common.entity.session.EplayerSessionInfo;
import com.ibrightech.eplayer.sdk.common.entity.system.DocumentItem;
import com.ibrightech.eplayer.sdk.common.net.http.BaseOkHttpProtocol;
import com.ibrightech.eplayer.sdk.common.net.http.GetAssetListAsycProtocol;
import com.ibrightech.eplayer.sdk.common.net.http.utils.DocumnetUtils;
import com.ibrightech.eplayer.sdk.common.net.ws.event.DrawPadInfoChangeEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.DrawPadInfoInitEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.DrawPadInfoResetOrChangeEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.DrawPadInfoSwitchEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.DrawPadResetEvent;
import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.common.util.EncodeUtil;
import com.ibrightech.eplayer.sdk.common.util.LogUtil;
import com.ibrightech.eplayer.sdk.common.util.StorageUtil;
import com.ibrightech.eplayer.sdk.common.util.TimerDelayScheduler;
import com.ibrightech.eplayer.sdk.common.util.ToastUtil;
import com.ibrightech.eplayer.sdk.common.util.WeakReferenceHandler;
import com.ibrightech.eplayer.sdk.event.DrawPadInfoLoadingEvent;
import com.ibrightech.eplayer.sdk.event.DrawPadPageChangeEvent;
import com.ibrightech.eplayer.sdk.event.PageScrollStateEvent;
import com.ibrightech.eplayer.sdk.teacher.R;
import com.ibrightech.eplayer.sdk.teacher.event.PPTInfoChangeEvent;
import com.ibrightech.eplayer.sdk.teacher.event.PageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;
import java.util.TreeMap;

import okhttp3.Call;

public class DrawImageAndPadView extends RelativeLayout implements TimerDelayScheduler.TimeTaskListener {
    private static final String TAG = "DrawImageAndPadView";

    private  static final int TASK_TYPE_REPESET =10000000;

    private static final int MSG_HIDE_HINT_SPACE=100;
    private static final int MSG_SHOW_HINT_SPACE=101;

    private static final int MSG_CHAGNE_SHOW_HINT_SPACE_STATE=102;
    private static final int MSG_CHAGNE_SHOW_STATE=103;

    public TreeMap<String, Asset> assetMap = new TreeMap<String, Asset>();//保存所有的ppt或wor列表信息


    private float currentImageWidth;
    private float currentImageHeight;


    Context context;

    DrawImageView drawImageView;
    DrawPadView drawPadView;
    RelativeLayout draw_pad_and_image_layout;

    EplayerSessionInfo sessionInfo;

    TimerDelayScheduler timerDelayScheduler;

    private DrawImageAndPadViewSizeListener viewSizeListener;

    public DrawImageAndPadViewSizeListener getViewSizeListener() {
        return viewSizeListener;
    }

    public void setViewSizeListener(DrawImageAndPadViewSizeListener viewSizeListener) {
        this.viewSizeListener = viewSizeListener;
    }

    public void setSessionInfo(EplayerSessionInfo sessionInfo) {
        this.sessionInfo = sessionInfo;
    }


    public interface DrawImageAndPadViewSizeListener{
        int getShowWidth();

        int getShowHeight();

        int getSrollerViewY();
        void srollerViewTo(int y);
    }

    public DrawImageAndPadView(Context context) {
        super(context);
        initView(context);
    }

    public DrawImageAndPadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public DrawImageAndPadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }


    private void initView(Context context) {

        timerDelayScheduler = new TimerDelayScheduler(this);
        this.context = context;

        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }

        View rootview = View.inflate(context, R.layout.draw_pad_and_image, null);

        drawImageView = (DrawImageView) rootview.findViewById(R.id.drawImageView);
        draw_pad_and_image_layout = (RelativeLayout) rootview.findViewById(R.id.draw_pad_and_image_layout);
        drawPadView = (DrawPadView) rootview.findViewById(R.id.drawPadView);

        this.addView(rootview);

    }

    public void releaseAll(){
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
        drawImageView.releaseAll();
        drawPadView.releaseAll();
    }

    private void toGetPPTList(DrawPadInfo drawPadInfo) {
        String liveRoomId = sessionInfo.userInfo.liveClassroomId;
        if (drawPadInfo == null) {
            return;
        }

        final int pptId = drawPadInfo.pptId;
        final int page = drawPadInfo.page;
        final String resType = drawPadInfo.resType;

        GetAssetListAsycProtocol protocol = new GetAssetListAsycProtocol(liveRoomId, "ppt,word");
        protocol.execute(this.context, new BaseOkHttpProtocol.CallBack() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int i, String s, Object o) {
                assetMap = (TreeMap<String, Asset>) o;

                Asset asset = assetMap.get(pptId + "");
                getDocumentItemListByAsset(asset, pptId + "", page,resType);

            }

            @Override
            public boolean onFailure(Call call, EplayerNetErrorEntity errorEntity) {

                ToastUtil.showStringToast("获取ppt/word信息失败");
                return true;
            }

            @Override
            public void onUpProgress(long l, long l1, float v, long l2) {

            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        LogUtil.d("----DrawImageAndPadView.wwwonSizeChanged w:" + w + "--h:" + h + "--oldw:" + oldw + "--oldh:" + oldh);
    }

    public void getDocumentItemListByAsset(final Asset asset , final String pptId,final int page, final String resType){
        if (CheckUtil.isEmpty(asset)) {
            //todo
            EventBus.getDefault().post(new PPTInfoChangeEvent(false));
            return;
        }
        final String fileUrl = asset.fileName;
        LogUtil.d("ListByAsset :fileUrl:", fileUrl);
        EplayerDownFileUtil.getInstance().downLoad(fileUrl, StorageUtil.getImageCacheDierctory(), EncodeUtil.encodeByMD5(fileUrl), new EplayerDownFileUtil.DownLoadCallBack() {
            @Override
            public void onFail() {
                getDocumentItemListByAsset(asset,pptId, page,resType);
                //ToastUtil.showStringToast("获取ppt/word信息失败");
            }


            @Override
            public void onSucceed(File file) {
                List<DocumentItem> documentItemList = DocumnetUtils.getDocumentList(file,pptId,resType, fileUrl);
                Message msg = handler.obtainMessage();
                msg.what = MSG_CHAGNE_SHOW_STATE;
                msg.obj = documentItemList;
                handler.sendMessage(msg);

                //todo
            }
        });
    }


    static class DrawImageAndPadViewHandler extends WeakReferenceHandler<DrawImageAndPadView>{

        public DrawImageAndPadViewHandler(DrawImageAndPadView drawImageView) {
            super(drawImageView);
        }

        @Override
        protected void handleMessage(Message msg, DrawImageAndPadView drawImageView) {
            switch (msg.what){
                case MSG_CHAGNE_SHOW_HINT_SPACE_STATE:{
                    break;
                }
                case MSG_SHOW_HINT_SPACE:{

                    break;
                }
                case MSG_CHAGNE_SHOW_STATE:{
                    List<DocumentItem> documentItemList = (List<DocumentItem>) msg.obj;
                    if (!CheckUtil.isEmpty(documentItemList)) {
                        drawImageView.drawImageView.reloadDocumentItem(documentItemList);
                        EventBus.getDefault().post(new PPTInfoChangeEvent(true));
                    } else {
                        EventBus.getDefault().post(new PPTInfoChangeEvent(false));
                    }
                    break;
                }

                case MSG_HIDE_HINT_SPACE:{
                    break;
                }
            }
        }
    }

    DrawImageAndPadViewHandler handler=new DrawImageAndPadViewHandler(this);

    //EventBus回调函数
    //画板初始化
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEventBackgroundThread(DrawPadInfoInitEvent event) {


//        DrawPadInfo drawPadInfo = playerSessionInfo.drawPadInfo;
        DrawPadInfo _drawPadInfo = event.getPadInfo();
        drawImageView.setCanScroll(_drawPadInfo.padType==EplayerDrawPadType.DrawPadTypeDocument);//画板时不允许左右滑动

        //切换ppt或者word加载

        EventBus.getDefault().post(new DrawPadInfoLoadingEvent(DrawPadInfoLoadingEvent.LoadingState.Loading_Start));
        toGetPPTList(_drawPadInfo);

//        EventBus.getDefault().post(new DrawPadPageChangeEvent(drawPadInfo));

    }

    //画板切换
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEventBackgroundThread(DrawPadInfoSwitchEvent event) {

        //切换两个界面的显示和不显示

        //发送主线程通知，修改外部界面的大小位置和滑动位置

        DrawPadInfo drawPadInfo = sessionInfo.drawPadInfo;
        DrawPadInfo _drawPadInfo = event.getPadInfo();

        if (drawPadInfo.blankColor == _drawPadInfo.blankColor && drawPadInfo.padType == _drawPadInfo.padType) {
            //没有必要切换界面
            return;
        }

        drawPadInfo.blankColor = _drawPadInfo.blankColor;
        drawPadInfo.padType = _drawPadInfo.padType;

        drawImageView.setCanScroll(drawPadInfo.padType == EplayerDrawPadType.DrawPadTypeDocument);//画板时不允许左右滑动

        if(drawPadInfo.validateResetPageChange()){
            EventBus.getDefault().post(new DrawPadResetEvent());
        }

        //切换ppt或者word加载
        EventBus.getDefault().post(new DrawPadInfoLoadingEvent(DrawPadInfoLoadingEvent.LoadingState.Loading_Start));
        toGetPPTList(drawPadInfo);

//        EventBus.getDefault().post(new DrawPadPageChangeEvent(drawPadInfo));


    }

    //画板信息变化
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBackgroundThread(DrawPadInfoChangeEvent event) {

        //判断是否同一界面
        DrawPadInfo _drawPadInfo = sessionInfo.drawPadInfo;

        DrawPadInfo drawPadInfo = event.getPadInfo();
        if (_drawPadInfo.isSamePadInfo(drawPadInfo)) {
            //发现是同一画板，且基本信息相同
            LogUtil.d(TAG, "--isSamePadInfo--");
            System.gc();
            return;
        }


        if(drawPadInfo.padType != _drawPadInfo.padType){

            _drawPadInfo.blankColor = drawPadInfo.blankColor;
            _drawPadInfo.padType = drawPadInfo.padType;
            _drawPadInfo.page=drawPadInfo.page;

            //不同界面或者文档，重新加载界面
            EventBus.getDefault().post(new DrawPadInfoLoadingEvent(DrawPadInfoLoadingEvent.LoadingState.Loading_Start));
            toGetPPTList(drawPadInfo);


            return;
        }

        //同一界面滑动位置
        if (drawPadInfo.pptId == _drawPadInfo.pptId && drawPadInfo.page == _drawPadInfo.page) {
            //同一页面
            if (drawPadInfo.resType.equals(EplayerConstant.KEY_WORD) && drawPadInfo.isWordScroll) {

                sessionInfo.drawPadInfo = drawPadInfo;
                this.onScrollView(drawPadInfo);

                return;
            }
        } else {
            // 切换页面到指定页码

            if (_drawPadInfo.pptId != drawPadInfo.pptId) {
//            imagePaths.clear();
//            imagePaths = null;

                if(!drawPadInfo.userSwitch&&!_drawPadInfo.userSwitch)
                    sessionInfo.drawPageInfo.clearALL();

                drawPadInfo.pageOffset = 0.0f;
                drawPadInfo.isWordScroll = false;

                sessionInfo.drawPadInfo = drawPadInfo;

                EventBus.getDefault().post(new DrawPadInfoLoadingEvent(DrawPadInfoLoadingEvent.LoadingState.Loading_Start));
                toGetPPTList(drawPadInfo);

            } else {

                if (_drawPadInfo.page != drawPadInfo.page) {
                    drawPadInfo.pageOffset = 0.0f;
                    if(!drawPadInfo.userSwitch&&!_drawPadInfo.userSwitch)
                        sessionInfo.drawPageInfo.clearALL();

                    List<DrawMsgInfo> drawMsgInfoList =  DrawMsgInfo.loadByPptIdAndPage(drawPadInfo.pptId,drawPadInfo.page);
                    if(!CheckUtil.isEmpty(drawMsgInfoList)){

                        DrawPageInfo drawPageInfo = sessionInfo.drawPageInfo;
                        drawPageInfo.clearALL();
                        drawPageInfo.addDrawMsgInfoList(drawMsgInfoList);


                    }
                }

                sessionInfo.drawPadInfo = drawPadInfo;


                EventBus.getDefault().post(new DrawPadInfoLoadingEvent(DrawPadInfoLoadingEvent.LoadingState.Loading_Start));
                toGetPPTList(drawPadInfo);

            }

        }
    }

    public void requestResetViewSize(){

       float y =  this.viewSizeListener.getSrollerViewY();
        float orginHeight = this.getHeight() *1.0f;

        resetViewSize();
        //todo  scrollY 不对问题

        if(sessionInfo==null||sessionInfo.drawPadInfo==null)
            return;


        float parentWidth =this.viewSizeListener.getShowWidth() *1.0f;  //上级页面宽度

        float showHeight = parentWidth / currentImageWidth * currentImageHeight;



        int shwoY = (int)( y /  orginHeight  * showHeight);


        if (this.viewSizeListener.getSrollerViewY() != shwoY)
            this.viewSizeListener.srollerViewTo(shwoY);
    }

    private void resetViewSize(){

        if(currentImageWidth==0)
            return;

        float parentWidth =this.viewSizeListener.getShowWidth() *1.0f;  //上级页面宽度
        float parentHeight =this.viewSizeListener.getShowHeight() * 1.0f; //上级页面高度

        float showWidth = parentWidth;
        float showHeight = parentWidth / currentImageWidth * currentImageHeight;

        float lauout_show_height = showHeight;
        if(lauout_show_height<parentHeight)
            lauout_show_height = parentHeight;

        {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int) showWidth, (int) lauout_show_height);

            this.setLayoutParams(layoutParams);
        }
        {
            ViewGroup.LayoutParams layoutParams = draw_pad_and_image_layout.getLayoutParams();
            layoutParams.width = (int) showWidth;
            layoutParams.height = (int) lauout_show_height;

            draw_pad_and_image_layout.setLayoutParams(layoutParams);
        }
        {
            RelativeLayout.LayoutParams layoutParams = (LayoutParams) drawImageView.getLayoutParams();
            layoutParams.addRule(CENTER_HORIZONTAL);
            layoutParams.addRule(CENTER_IN_PARENT);
            layoutParams.addRule(CENTER_VERTICAL);
            layoutParams.width = (int) showWidth;
            layoutParams.height = (int) showHeight;
            drawImageView.setLayoutParams(layoutParams);
        }
        {
            RelativeLayout.LayoutParams layoutParams = (LayoutParams) drawPadView.getLayoutParams();
            layoutParams.addRule(CENTER_HORIZONTAL);
            layoutParams.addRule(CENTER_IN_PARENT);
            layoutParams.addRule(CENTER_VERTICAL);
            layoutParams.width = (int) showWidth;
            layoutParams.height = (int) showHeight;
            drawPadView.setLayoutParams(layoutParams);
        }
        drawPadView.resetViewSizeAndMsg((int) showWidth, (int) showHeight);
    }

    //页面信息变化，包括切换ppt、word、白班切换,每次都重新设置画笔和文字画板大小，另外重新绘制画笔的信息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(DrawPadPageChangeEvent event) {
        currentImageWidth = event.getWidth() * 1.0f;
        currentImageHeight = event.getHeight() * 1.0f;


        this.resetViewSize();


//        drawImageView.resetViewSizeAndMsg((int) showWidth, (int) showHeight);

        if (this.viewSizeListener.getSrollerViewY() != 0)
            this.viewSizeListener.srollerViewTo(0);
    }

    private void onScrollView(DrawPadInfo drawPadInfo){
        //todo 同一页面，只做页面滚动的事情
        float viewWidth =this.getWidth() *1.0f;  //当前页面宽度
        float viewHeight =this.getHeight() *1.0f; //当前页面高度


        float parentWidth =this.viewSizeListener.getShowWidth() *1.0f;  //上级页面宽度
        float parentHeight =this.viewSizeListener.getShowHeight() * 1.0f; //上级页面高度

        int y = 0;
        if (null != drawPadInfo && drawPadInfo.resType.equals(EplayerConstant.KEY_WORD) && drawPadInfo.padType == EplayerDrawPadType.DrawPadTypeDocument) {


            if (drawPadInfo.isWordScroll) {
                y = (int) ((viewHeight - parentHeight) * drawPadInfo.pageOffset);
            }
        }
        if (this.viewSizeListener.getSrollerViewY() != y)
            this.viewSizeListener.srollerViewTo(y);
    }



    //页面信息变化，包括切换ppt、word、白班切换,每次都重新设置画笔和文字画板大小，另外重新绘制画笔的信息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(PageScrollStateEvent event) {
        switch (event.state){
            case 0:{
                if(EplayerEngin.getInstance().isPlayback()){
                    EventBus.getDefault().post(new PageEvent(drawImageView.getCurrentItem() + 1));
                }else {
                    //todo 下面是直播的逻辑
                    EplayerSessionInfo eplayerSessionInfo = EplayerEngin.getInstance().getSessionInfo();
                    DrawPadInfo drawPadInfo = eplayerSessionInfo.drawPadInfo;

                    if (drawPadInfo.validatePageChange(drawImageView.getCurrentItem() + 1)) {
                        drawPadView.cleanAllPaint();
                        DrawPadInfo drawPadInfoCopySelf = drawPadInfo.copySelf();

                        drawPadInfoCopySelf.cachePageChange(drawImageView.getCurrentItem() + 1);

                        timerDelayScheduler.scheduledWaitTask(TASK_TYPE_REPESET, 10000);

                        EventBus.getDefault().post(new DrawPadInfoChangeEvent(drawPadInfoCopySelf));

                    }
                    drawPadView.setVisibility(View.VISIBLE);
                }
                break;
            }
            case 1:{
                if (!EplayerEngin.getInstance().isPlayback()) {


                    //todo 下面是直播的逻辑

                    drawPadView.setVisibility(View.INVISIBLE);
                    timerDelayScheduler.invalidateWaitTask(TASK_TYPE_REPESET);
                }
                break;
            }
            default:{
                if (!EplayerEngin.getInstance().isPlayback())
                    timerDelayScheduler.invalidateWaitTask(TASK_TYPE_REPESET);

            }
        }

    }
    @Override
    public void handlerTimeTaskFinished(long taskToken,int tryCount) {
        this.onEventMainThread(new DrawPadResetEvent());


    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(DrawPadResetEvent event) {
        timerDelayScheduler.invalidateWaitTask(TASK_TYPE_REPESET);

        EplayerSessionInfo eplayerSessionInfo =  EplayerEngin.getInstance().getSessionInfo();
        DrawPadInfo drawPadInfo = eplayerSessionInfo.drawPadInfo;

        if(drawPadInfo.validateResetPageChange()){
            drawPadView.cleanAllPaint();
            DrawPadInfo drawPadInfoCopySelf =  drawPadInfo.copySelf();
            drawPadInfoCopySelf.resetPageChange();
            EventBus.getDefault().post(new DrawPadInfoChangeEvent(drawPadInfoCopySelf));
        }


    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(DrawPadInfoResetOrChangeEvent event) {
        timerDelayScheduler.invalidateWaitTask(TASK_TYPE_REPESET);

        EplayerSessionInfo eplayerSessionInfo =  EplayerEngin.getInstance().getSessionInfo();
        DrawPadInfo drawPadInfo = eplayerSessionInfo.drawPadInfo;

        if(drawPadInfo.validateResetPageChange()){
            drawPadView.cleanAllPaint();
        }
        EventBus.getDefault().post(new DrawPadInfoChangeEvent(event.getPadInfo()));


    }
}
