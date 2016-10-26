package com.ibrightech.eplayer.sdk.widget;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.teacher.R;

/**
 * Created by zhaoxu2014 on 16/9/1.
 */
public class BaseShowView extends RelativeLayout {

    int top_view_width,top_view_height;

    public  boolean canMove =false;
    private GestureDetector mGesture;
    public OnExchangePostionListener exchangeListener;




    public boolean isCanMove() {
        return canMove;
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }



    public void setExchangeListener(OnExchangePostionListener exchangeListener) {
        this.exchangeListener = exchangeListener;
    }



    public BaseShowView(Context context) {
        super(context);
        initView( context);
    }

    public BaseShowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView( context);
    }



    public BaseShowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView( context);
    }
    private void initView(Context context){
        Resources res= context.getResources();
        top_view_width= (int) res.getDimension(R.dimen.top_view_width);
        top_view_height= (int) res.getDimension(R.dimen.top_view_height);
        
        mGesture = new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.d("TAg","onSingleTapUp:");
                if(!CheckUtil.isEmpty(exchangeListener)){
                    exchangeListener.onExchange(BaseShowView.this);
                }
                return super.onSingleTapUp(e);
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
              Log.d("TAg","onScroll:");
//                setTranslationX(getTranslationX()+e2.getX() - e1.getX());
                moveViewByLayout(e2.getX() - e1.getX(),e2.getY() - e1.getY());
                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.d("TAg","onFling:");
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    private void moveViewByLayout( float rawX, float rawY) {
        int pw=((RelativeLayout)(this.getParent())).getWidth();
        int ph=((RelativeLayout)(this.getParent())).getHeight();

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this
                .getLayoutParams();
        params.leftMargin = params.leftMargin+(int) rawX ;
        params.topMargin = params.topMargin+ (int) rawY;
        params.leftMargin= params.leftMargin>0?params.leftMargin:0;
        params.leftMargin= params.leftMargin+this.getWidth()>pw?pw-this.getWidth():params.leftMargin;

        params.topMargin= params.topMargin>0?params.topMargin:0;
        params.topMargin= params.topMargin+this.getHeight()>ph?ph-this.getHeight():params.topMargin;

        this.setLayoutParams(params);

    }

    public void initScreenWidthHeight( int showSpaceWidth, int showSpaceHeight){

    }

    public void clearViewPostion(){
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this
                .getLayoutParams();
        params.setMargins(0,0,0,0);
        this.setLayoutParams(params);
    }
    public void initViewLeftBottomPostion(){
        try {

            int ph = ((RelativeLayout) (this.getParent().getParent())).getLayoutParams().height;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this
                    .getLayoutParams();
            params.width = top_view_width;
            params.height = top_view_height;

            params.leftMargin = 20;
            params.topMargin = ph - top_view_height - 20;


            this.setLayoutParams(params);

            initScreenWidthHeight(params.width,params.height);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void initViewRightTopPostion(){
        try {

            int pw = ((RelativeLayout) (this.getParent().getParent())).getLayoutParams().width;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this
                    .getLayoutParams();
            params.width = top_view_width;
            params.height = top_view_height;
            params.leftMargin = pw-top_view_width-20;
            params.rightMargin = 20;
            params.topMargin = 60;


            this.setLayoutParams(params);

            initScreenWidthHeight(params.width,params.height);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if(canMove){
            return true;
        }else{
            return super.onInterceptTouchEvent(event);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(canMove){
              mGesture.onTouchEvent(event);
            return true;
        }else{
            return super.onTouchEvent(event);
        }

    }

    public interface OnExchangePostionListener {
    
        public void onExchange(View v);
    }
}
