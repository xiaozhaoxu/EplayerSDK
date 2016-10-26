package com.ibrightech.eplayer.sdk.teacher.ui;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ibrightech.eplayer.sdk.common.engin.EplayerEngin;
import com.ibrightech.eplayer.sdk.common.entity.Asset;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerConstant;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.DrawPadInfo;
import com.ibrightech.eplayer.sdk.common.entity.system.DocumentItem;
import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.common.util.LogUtil;
import com.ibrightech.eplayer.sdk.common.util.TextViewUtils;
import com.ibrightech.eplayer.sdk.teacher.R;
import com.ibrightech.eplayer.sdk.teacher.activity.LivePushActivity;
import com.ibrightech.eplayer.sdk.teacher.activity.SDKBaseActivity;
import com.ibrightech.eplayer.sdk.teacher.event.AudioShowHintLiveTitleEvent;
import com.ibrightech.eplayer.sdk.teacher.event.CancelHintLiveTitleEvent;
import com.ibrightech.eplayer.sdk.teacher.event.ChangeScreenScuessEvent;
import com.ibrightech.eplayer.sdk.teacher.event.ShowLiveTitleEvent;
import com.ibrightech.eplayer.sdk.teacher.ui.imageconfig.ViewSimpleTarget;
import com.ibrightech.eplayer.sdk.widget.MyViewPager;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.BitmapCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zhaoxu2014 on 16/5/27.
 */
public class LivePushAudioPPTControllerView extends RelativeLayout {
    private static final int MSG_SCROLL=1000;

    private static final int MSG_HIDE_HINT_SPACE=100;
    private static final int MSG_SHOW_HINT_SPACE=101;

    private static final int MSG_CHAGNE_SHOW_HINT_SPACE_STATE=102;


    private static final int SHOW_HINT_TIME=1000*10;

    Animation  face_enter, face_exit;

    TextView tv_page;

    Button bt_scale;

    LinearLayout li_bottom_space;
    RelativeLayout rl_left_right;

    ImageView image_left;
    ImageView image_right;
    ImageView img_icon;

    MyViewPager vp;


    Context context;
    OnPPTListener onPPTListener;
    int currentItem = 0;


    List<DocumentItem>documentItemList;
    MyAdapter myAdapter;
    Asset asset;
    int SCREEN_WIDTH;//屏幕宽度
    int SCREEN_HEIGHT;//屏幕高度
    String couseImageUrl;
    SDKBaseActivity livePushActivity;
    DrawPadInfo drawPadInfo ;
    EventBus eventBus;

    Animation operatingAnim =null;
    LinearInterpolator lin = null;


    public void initScreenWidthHeight(LivePushActivity livePushActivity, int SCREEN_WIDTH, int SCREEN_HEIGHT,String couseImageUrl) {
        this.livePushActivity = livePushActivity;
        this.SCREEN_WIDTH = SCREEN_WIDTH;
        this.SCREEN_HEIGHT = SCREEN_HEIGHT;
        this.couseImageUrl=couseImageUrl;

        Glide.with(livePushActivity.getApplicationContext()).load(couseImageUrl)
                .placeholder(R.drawable.default_icon)
                .error(R.drawable.default_icon)
                .into(new ViewSimpleTarget(img_icon));


    }

    public void setPPTList(Asset asset, int selectpage, List<DocumentItem> documentItemList){
        drawPadInfo = EplayerEngin.getInstance().getSessionInfo().drawPadInfo;
        this.documentItemList=documentItemList;
        this.asset=asset;
        myAdapter.notifyDataSetChanged();
        changeVPPage(selectpage-1);

        int size=getDocumentItemListSize();
        if(size>0){
            vp.setVisibility(View.VISIBLE);
        }else{
            vp.setVisibility(View.INVISIBLE);
        }
    }

    public int getDocumentItemListSize(){
        if(CheckUtil.isEmpty(documentItemList)){
            return 0;
        }else{
            return documentItemList.size();
        }
    }
    public  void changeVPPage(int selectpage){
        int size= getDocumentItemListSize();

        selectpage=selectpage<0?0:selectpage;
        selectpage=selectpage>=size-1?size-1:selectpage;


        vp.setCurrentItem(selectpage);
        setPageInfo(selectpage);
    }

    double save_imageheight = 0;//图片显示的高度
    double save_showSpaceHeight = 0;
    public void changeImageViewWH(){
        if(CheckUtil.isEmpty(asset)){
            return;
        }
        if (asset.type.equals(EplayerConstant.KEY_PPT)) {
            return;
        }

        double imageheight = 0;//图片显示的高度
        double showSpaceHeight = 0;
        int count = vp.getChildCount();
        for (int i = 0; i < count; i++) {


            View rootview=vp.getChildAt(i);
            ObservableScrollView itemView = (ObservableScrollView)rootview.findViewById(R.id.scrollview);
            if (CheckUtil.isEmpty(itemView)) {
                continue;
            }

            MyImageView image_word = (MyImageView) itemView.findViewById(R.id.image_word);
            image_word.setVisibility(View.GONE);
            Bitmap loadedImage = image_word.getSavebitmap();
            if (CheckUtil.isEmpty(loadedImage)) {
                continue;
            }


            int bitmapW = loadedImage.getWidth();
            int bitmapH = loadedImage.getHeight();
            int requestedOrientation = livePushActivity.getRequestedOrientation();

            int imagewidth = 0;



            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) image_word.getLayoutParams();
           if (asset.type.equals(EplayerConstant.KEY_WORD)) {
                switch (requestedOrientation) {
                    case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT: {
                        imagewidth = SCREEN_WIDTH;

                        break;
                    }
                    default: {
                        imagewidth = SCREEN_HEIGHT;
                        break;
                    }
                }
                imageheight =(imagewidth * bitmapH * 1.0 / bitmapW);
                showSpaceHeight =(imagewidth * LivePushAudioControllerView.DRAWPADVIEW_SCALE);
            }
            LogUtil.d("---imageheight---",imagewidth +":"+imageheight);

            lp.width = imagewidth;
            lp.height = (int) imageheight;

            save_imageheight=imageheight;
            save_showSpaceHeight=showSpaceHeight;

            image_word.setVisibility(View.VISIBLE);
            if (asset.type.equals(EplayerConstant.KEY_WORD)) {

                int scrolltop = (int) (drawPadInfo.pageOffset * (imageheight - showSpaceHeight));
                scrolltop= (int) (scrolltop*image_word.getHeight()/imageheight);
                ((ScrollView) itemView).scrollTo(0, scrolltop);
            }

        }

    }



   public class  MyBitmapCallback extends BitmapCallback {
       ImageView iv;
       ImageView image_rotate;

       public MyBitmapCallback(ImageView iv,ImageView image_rotate) {
           this.iv = iv;
           this.image_rotate=image_rotate;
       }

       @Override
       public void onResponse(boolean b, Bitmap bitmap, Request request, @Nullable Response response) {

           iv.setImageBitmap(bitmap);
           changeImageViewWH();
           image_rotate.clearAnimation();
           image_rotate.setVisibility(View.GONE);
       }
   }



    ObservableScrollView.ScrollViewListener scrollViewListener=new ObservableScrollView.ScrollViewListener(){

        @Override
        public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {

            //这个地方和顶部的偏移量不准(总高度不是里面图片的总高度)
            int yl= (int) (y*1.0*save_imageheight/scrollView.getChildAt(0).getHeight());

           double pageOffset=yl/(save_imageheight-save_showSpaceHeight);
            if(!CheckUtil.isEmpty(asset)) {
                drawPadInfo.pageOffset=pageOffset;
                EplayerEngin.getInstance().initPptPageReq(asset.id, asset.name, currentItem + 1, pageOffset, asset.type);
            }
        }
    };


    private class MyAdapter extends PagerAdapter {

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {

            return getDocumentItemListSize();
        }

        @Override
        public Object instantiateItem(View arg0, int postion) {

            DocumentItem documentItem = documentItemList.get(postion);
            View view = View.inflate(context, R.layout.audio_ppt_item, null);
            ImageView image_ppt= (ImageView) view.findViewById(R.id.image_ppt);
            ObservableScrollView scrollview= (ObservableScrollView) view.findViewById(R.id.scrollview);
            ImageView image_word= (ImageView) view.findViewById(R.id.image_word);
            ImageView image_rotate= (ImageView) view.findViewById(R.id.image_rotate);
            if (operatingAnim != null) {
                image_rotate.startAnimation(operatingAnim);
            }

            ImageView image_down=null;
            if(CheckUtil.isEmpty(asset)){
                return view;
            }
            if (asset.type.equals(EplayerConstant.KEY_WORD)){
                scrollview.setVisibility(View.VISIBLE);
                image_ppt.setVisibility(View.GONE);

                if(save_imageheight>0){
                    image_word.getLayoutParams().height= (int) save_imageheight;
                }
                image_down=image_word;
            }else{
                scrollview.setVisibility(View.GONE);
                image_ppt.setVisibility(View.VISIBLE);
                image_ppt.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {

                    }
                });
                image_down=image_ppt;
            }
            OkHttpUtils.get(documentItem.url).execute(new MyBitmapCallback(image_down,image_rotate));


            ((ViewPager) arg0).addView(view);

            scrollview.setScrollViewListener(scrollViewListener);
            return view;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {

            ((ViewPager) arg0).removeView((View) arg2);

        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {

        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {

        }

        @Override
        public void finishUpdate(View arg0) {

        }
    }

    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {


        /**
         * This method will be invoked when a new page becomes selected.
         * position: Position index of the new selected page.
         */
        public void onPageSelected(int position) {

            if(!CheckUtil.isEmpty(asset)) {
                EplayerEngin.getInstance().initPptPageReq(asset.id, asset.name, position + 1, 0, asset.type);
                if(position!=drawPadInfo.page-1){
                    drawPadInfo.page=position+1;
                    drawPadInfo.pageOffset=0;
                }

            }

            currentItem = position;
            setPageInfo(currentItem);
            changeImageViewWH();
            handler.sendEmptyMessage(MSG_CHAGNE_SHOW_HINT_SPACE_STATE);

        }

        public void onPageScrollStateChanged(int arg0) {

        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
    }


    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_CHAGNE_SHOW_HINT_SPACE_STATE:{
                    if(rl_left_right.getVisibility()==View.VISIBLE){
                        showLeftImage();
                        showRightImage();
                    }
                    break;
                }
                case MSG_SHOW_HINT_SPACE:{
                    rl_left_right.setVisibility(View.VISIBLE);
                    if(getDocumentItemListSize()>0){
                        li_bottom_space.setVisibility(View.VISIBLE);
                        li_bottom_space.startAnimation(face_enter);
                    }
                    showLeftImage();
                    showRightImage();
                    int requestedOrientation = livePushActivity.getRequestedOrientation();
                    if(requestedOrientation==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
                        eventBus.post(new ShowLiveTitleEvent(true,true));
                    }else{
                        eventBus.post(new ShowLiveTitleEvent(false,false));
                    }

                    break;
                }

                case MSG_HIDE_HINT_SPACE:{
                    rl_left_right.setVisibility(View.GONE);
                    li_bottom_space.setVisibility(View.GONE);
                    li_bottom_space.startAnimation(face_exit);
                    image_left.setVisibility(View.GONE);
                    image_right.setVisibility(View.GONE);

                    int requestedOrientation = livePushActivity.getRequestedOrientation();
                    if(requestedOrientation==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
                        eventBus.post(new ShowLiveTitleEvent(false,true));
                    }else{
                        eventBus.post(new ShowLiveTitleEvent(false,false));
                    }
                    break;
                }
            }
            super.handleMessage(msg);
        }
    };

    private void showLeftImage(){

        if(currentItem>0){
            image_left.setVisibility(View.VISIBLE);
        }else{
            image_left.setVisibility(View.GONE);
        }
    }
    private void showRightImage(){
        int count=getDocumentItemListSize();
        if(count>1&&currentItem<count-1){
            image_right.setVisibility(View.VISIBLE);
        }else{
            image_right.setVisibility(View.GONE);
        }
    }

    public void showHintSpace(){
        handler.removeMessages(MSG_HIDE_HINT_SPACE);
        handler.sendEmptyMessage(MSG_SHOW_HINT_SPACE);
        handler.sendEmptyMessageDelayed(MSG_HIDE_HINT_SPACE,SHOW_HINT_TIME);
    }
    public void hideHintSpace(){
        handler.removeMessages(MSG_HIDE_HINT_SPACE);
        handler.removeMessages(MSG_SHOW_HINT_SPACE);
        handler.sendEmptyMessage(MSG_HIDE_HINT_SPACE);
    }


    public void changeHintSpace(){
        if(rl_left_right.getVisibility()==View.VISIBLE){
            hideHintSpace();
        }else{
            showHintSpace();
        }
    }




    public void setScaleBtBg(int resid){
        bt_scale.setBackgroundResource(resid);
    }

    public void setOnPPTListener(OnPPTListener onPPTListener) {
        this.onPPTListener = onPPTListener;
    }

    public LivePushAudioPPTControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public LivePushAudioPPTControllerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public LivePushAudioPPTControllerView(Context context) {
        super(context);
        initView(context);
    }


    public void setPageInfo(int selectpage){
        String content="";
        if(CheckUtil.isEmpty(documentItemList)){
            content="";
        }else{
            content= (selectpage+1)+"/"+documentItemList.size();
        }
        TextViewUtils.setText(tv_page,content);
    }
    MyViewPager.OnViewPagerListener viewPagerListener=new MyViewPager.OnViewPagerListener(){

        @Override
        public void onItemClickListener() {
            changeHintSpace();
        }
    };


    private void initView(Context context) {

        this.context=context;
        operatingAnim = AnimationUtils.loadAnimation(context, R.anim.upload_rotate);
        lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);

        face_enter = AnimationUtils.loadAnimation(context, R.anim.chat_face_enter);
        face_exit = AnimationUtils.loadAnimation(context, R.anim.chat_face_exit);



        View rootview = View.inflate(context, R.layout.livepush_audio_controller_ppt, null);

        rl_left_right= (RelativeLayout) rootview.findViewById(R.id.rl_left_right);
        img_icon= (ImageView) rootview.findViewById(R.id.img_icon);
         tv_page= (TextView) rootview.findViewById(R.id.tv_page);

         bt_scale= (Button) rootview.findViewById(R.id.bt_scale);

         li_bottom_space= (LinearLayout) rootview.findViewById(R.id.li_bottom_space);




         image_left= (ImageView) rootview.findViewById(R.id.image_left);;
         image_right= (ImageView) rootview.findViewById(R.id.image_right);

         vp= (MyViewPager) rootview.findViewById(R.id.vp_calender);

        this.addView(rootview);
        initSetAdapte();
        setViewClick(rootview);
    }



    private void initSetAdapte(){
        myAdapter=new MyAdapter();
        vp.setAdapter(myAdapter);
        vp.setOnPageChangeListener(new MyPageChangeListener());

        vp.setOnViewPagerListener(viewPagerListener);
    }

    public void startEventListener() {
        eventBus=EventBus.getDefault();
        eventBus.register(this);

    }
    public void stopEventListener() {
        eventBus.unregister(this);
    }

    //EventBus回调函数
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ChangeScreenScuessEvent event) {
        save_imageheight=0;
        save_showSpaceHeight=0;
        changeImageViewWH();
        showHintSpace();

    }
    //EventBus回调函数
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(CancelHintLiveTitleEvent event) {
        handler.removeMessages(MSG_HIDE_HINT_SPACE);
        handler.removeMessages(MSG_SHOW_HINT_SPACE);
    }
    //EventBus回调函数
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(AudioShowHintLiveTitleEvent event) {
        showHintSpace();
    }

    @Subscribe
    public void setViewClick(View rootview){
        rootview.findViewById(R.id.image_left).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                changeVPPage(currentItem-1);
            }
        });
        rootview.findViewById(R.id.image_right).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                changeVPPage(currentItem+1);
            }
        });
        img_icon.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                changeHintSpace();
            }
        });
        bt_scale.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(!CheckUtil.isEmpty(onPPTListener)){
                    onPPTListener.onChangeScreenOrientation();
                }
            }
        });
    }

    public interface OnPPTListener {
        void onChangeScreenOrientation();
    }

}
