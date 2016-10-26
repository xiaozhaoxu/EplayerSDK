package com.ibrightech.eplayer.sdk.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.ibrightech.eplayer.sdk.common.entity.session.EplayerSessionInfo;
import com.ibrightech.eplayer.sdk.common.util.LogUtil;
import com.ibrightech.eplayer.sdk.common.util.TextViewUtils;
import com.ibrightech.eplayer.sdk.event.DrawPadInfoLoadingEvent;
import com.ibrightech.eplayer.sdk.teacher.R;
import com.ibrightech.eplayer.sdk.teacher.event.ChangePPTPageEvent;
import com.ibrightech.eplayer.sdk.teacher.ui.MyProgressBar;
import com.ibrightech.eplayer.sdk.teacher.ui.ObservableScrollView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class DrawPadControllerView extends BaseShowView implements DrawImageAndPadView.DrawImageAndPadViewSizeListener  {

    Context context;

    MyProgressBar myProgressBar;
    EplayerSessionInfo sessionInfo;
    DrawImageAndPadView drawImageAndPadView;
    ObservableScrollView observableScrollView;
    TextView tv_page;


    private void showPage(int page,int pagecount){

        if(pagecount>0){
            tv_page.setVisibility(View.VISIBLE);
            String pagestr=(page+1)+"/"+pagecount;
            TextViewUtils.setText(tv_page,pagestr);
        }else{
            tv_page.setVisibility(View.INVISIBLE);
        }
    }
    public DrawPadControllerView(Context context) {
        super(context);
        initView(context);
    }

    public DrawPadControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public DrawPadControllerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void setSessionInfo(EplayerSessionInfo sessionInfo) {
        this.sessionInfo = sessionInfo;
        drawImageAndPadView.setSessionInfo(sessionInfo);
    }

    @Override
    public int getShowWidth() {
        return  this.getLayoutParams().width;
    }

    @Override
    public int getShowHeight() {
        return this.getLayoutParams().height;
    }
    public int getSrollerViewY() {
        return observableScrollView.getScrollY();
    }

    @Override
    public void srollerViewTo(int y) {
        observableScrollView.scrollTo(0,y);
    }

    //设置当前的显示的宽高
    @Override
    public void initScreenWidthHeight( int showSpaceWidth, int showSpaceHeight) {

        this.getLayoutParams().width=showSpaceWidth;
        this.getLayoutParams().height=showSpaceHeight;

        drawImageAndPadView.requestResetViewSize();
    }
    public void showHintSpace(){

    }
    private void initView(Context context) {

        this.context = context;

        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }

        View rootview = View.inflate(context, R.layout.draw_pad_controller, null);

        myProgressBar = (MyProgressBar) rootview.findViewById(R.id.myprogressbar);
        myProgressBar.init(MyProgressBar.TYPE_RELATIVELAYOUT_LAYOUTPARAMS);

        drawImageAndPadView = (DrawImageAndPadView) rootview.findViewById(R.id.word_ppt_scrollview_layout);
        observableScrollView = (ObservableScrollView) rootview.findViewById(R.id.scrollview);
        drawImageAndPadView.setViewSizeListener(this);
        tv_page= (TextView) rootview.findViewById(R.id.tv_page);

        this.addView(rootview);


    }

    public void releaseAll(){
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
        drawImageAndPadView.releaseAll();
    }


    private void showProgress(){
        myProgressBar.setVisibility(View.VISIBLE);
    }
    private void hideProgress(){
        myProgressBar.setVisibility(View.INVISIBLE);
    }

    //EventBus回调函数
    //页面的图片加载
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(DrawPadInfoLoadingEvent event) {
        if (event.loadingState == DrawPadInfoLoadingEvent.LoadingState.Loading_Start) {
            showProgress();
        } else if (event.loadingState == DrawPadInfoLoadingEvent.LoadingState.Loading_End) {
            hideProgress();
        }

    }
    //翻页回调函数
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ChangePPTPageEvent event) {
        showPage(event.page,event.pagecount);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        LogUtil.d("----DrawPadControllerView.wwwonSizeChanged w:" + w + "--h:" + h + "--oldw:" + oldw + "--oldh:" + oldh+ "--x:" + this.getX() + "--y:" + this.getY());
    }

}
