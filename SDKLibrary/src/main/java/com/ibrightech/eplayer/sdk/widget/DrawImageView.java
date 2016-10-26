package com.ibrightech.eplayer.sdk.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import com.ibrightech.eplayer.sdk.adapter.DrawPageImageAdapter;
import com.ibrightech.eplayer.sdk.common.engin.EplayerEngin;
import com.ibrightech.eplayer.sdk.common.entity.session.EplayerSessionInfo;
import com.ibrightech.eplayer.sdk.common.entity.system.DocumentItem;
import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.common.util.LogUtil;
import com.ibrightech.eplayer.sdk.event.PageScrollStateEvent;
import com.ibrightech.eplayer.sdk.teacher.event.ChangePPTPageEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class DrawImageView extends MyViewPager {
    private static final String TAG = "DrawImageView";
    Context context;

    private DrawPageImageAdapter imageAdapter;


    public DrawImageView(Context context) {
        super(context);
        this.context = context;
        initView(context);
    }


    public DrawImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView(context);
    }

    public interface DrawImageViewSizeListener{
        int getDrawImageWidth();
        int getDrawImageHeight();
    }

    private void initView(Context context) {

        imageAdapter =  new DrawPageImageAdapter(context);

        this.setAdapter(imageAdapter);
        this.setOnPageChangeListener(new MyPageChangeListener());


    }
    public void reloadDocumentItem(List<DocumentItem> documentItemList){

        EplayerSessionInfo eplayerSessionInfo =  EplayerEngin.getInstance().getSessionInfo();
        if(CheckUtil.isEmpty(eplayerSessionInfo))return;
        imageAdapter.setStringList(documentItemList);
        imageAdapter.notifyDataSetChanged();
        int page=eplayerSessionInfo.drawPadInfo.page-1>0?eplayerSessionInfo.drawPadInfo.page-1:0;
        setCurrentItem(page);
        EventBus.getDefault().post(new ChangePPTPageEvent(page,imageAdapter.getCount()));
    }

    public void releaseAll(){
    }


    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            LogUtil.d( "DrawImageView position:" + position);
         EventBus.getDefault().post(new ChangePPTPageEvent(position,imageAdapter.getCount()));


        }

        @Override
        public void onPageScrollStateChanged(int state) {
            EventBus.getDefault().post(new PageScrollStateEvent(state));

            LogUtil.d( "DrawImageView state:" + state);

        }
    }



}
