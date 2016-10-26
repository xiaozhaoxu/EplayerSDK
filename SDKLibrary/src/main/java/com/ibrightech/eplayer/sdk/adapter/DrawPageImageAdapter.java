package com.ibrightech.eplayer.sdk.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ibrightech.eplayer.sdk.common.engin.EplayerEngin;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerDrawPadType;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.DrawPadInfo;
import com.ibrightech.eplayer.sdk.common.entity.session.EplayerSessionInfo;
import com.ibrightech.eplayer.sdk.common.entity.system.DocumentItem;
import com.ibrightech.eplayer.sdk.event.DrawPadInfoLoadingEvent;
import com.ibrightech.eplayer.sdk.event.DrawPadPageChangeEvent;
import com.ibrightech.eplayer.sdk.event.Enum.ViewState;
import com.ibrightech.eplayer.sdk.event.ViewClickEvent;
import com.ibrightech.eplayer.sdk.teacher.R;
import com.ibrightech.eplayer.sdk.teacher.ui.MyImageView;
import com.ibrightech.eplayer.sdk.teacher.ui.imageconfig.ViewSimpleTarget;
import com.ibrightech.eplayer.sdk.teacher.util.ImageUrlUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class DrawPageImageAdapter extends PagerAdapter {

    private Context context;
    private List<DocumentItem> stringList = new ArrayList<DocumentItem>();

    public DrawPageImageAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {

        return getStringList().size();
    }

    @Override
    public Object instantiateItem(View arg0, int postion) {

        final MyImageView imageView = new MyImageView(context);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(layoutParams);
        imageView.setBackgroundResource(R.color.white);



        final DocumentItem documentItem = getStringList().get(postion);
        //todo 加载

        RequestManager rm= Glide.with(context.getApplicationContext());

        DrawableTypeRequest dr=null;

        EplayerSessionInfo eplayerSessionInfo =  EplayerEngin.getInstance().getSessionInfo();
        DrawPadInfo drawPadInfo = eplayerSessionInfo.drawPadInfo;
        if(drawPadInfo.padType== EplayerDrawPadType.DrawPadTypeDocument){
            dr= rm.load(ImageUrlUtil.getUrl(documentItem.url));
        }else{
            dr= rm.load(R.drawable.blank_bg_green);
        }

        dr.placeholder(R.drawable.default_icon)
                .error(R.drawable.default_icon)
                .listener(new RequestListener<Object, GlideDrawable>() {

                    @Override
                    public boolean onException(Exception e, Object model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, Object model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        int width  = resource.getIntrinsicWidth();
                        int height  = resource.getIntrinsicHeight();

                        //todo 基本的宽高计算方式

                        if("ppt".equals(documentItem.resType)){
                            height =(int)( width /16.0  *9.0);
                        }

                        EventBus.getDefault().post(new DrawPadPageChangeEvent(width, height));

                        //发送图片加载完成事件，控制显示画笔和文字和加载态
                        EventBus.getDefault().post(new DrawPadInfoLoadingEvent(DrawPadInfoLoadingEvent.LoadingState.Loading_End));
                        return false;
                    }
                })
                .into(new ViewSimpleTarget(imageView));



        ((ViewPager) arg0).addView(imageView);

        imageView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
//                LogUtil.d("----imageView---","imageView.setOnClickListener");
                EventBus.getDefault().post(new ViewClickEvent(ViewState.View_PPT));
            }
        });

        return imageView;
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

    public List<DocumentItem> getStringList() {
        return stringList;
    }

    public void setStringList(List<DocumentItem> stringList) {
        this.stringList = stringList;
    }
}
