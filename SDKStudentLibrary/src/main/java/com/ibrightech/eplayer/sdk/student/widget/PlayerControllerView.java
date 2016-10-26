package com.ibrightech.eplayer.sdk.student.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ibrightech.eplayer.sdk.common.engin.EplayerEngin;
import com.ibrightech.eplayer.sdk.common.engin.EplayerPlaybackEngin;
import com.ibrightech.eplayer.sdk.common.entity.session.EPlaybackSessionInfo;
import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.common.util.DateUtil;
import com.ibrightech.eplayer.sdk.common.util.TextViewUtils;
import com.ibrightech.eplayer.sdk.student.R;

/**
 * Created by zhaoxu2014 on 16/9/26.
 */
public class PlayerControllerView extends LinearLayout {
    public static final  int MSG_INIT_PROGRESS_TIME=100;
    View view_chagescreen;
    View view_play;
    TextView tv_current_time,tv_all_time;
    MySeekbar seekbar;

    OnPlayerControllerListener listener;

    public enum PlayerState {
        PLAYERSTATE_PLAY, PLAYERSTATE_PAUSE
    }

    PlayerState playerState = PlayerState.PLAYERSTATE_PLAY;
    public void setListener(OnPlayerControllerListener listener) {
        this.listener = listener;
    }

    public PlayerControllerView(Context context) {
        super(context);
        initView(context);
    }

    public PlayerControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public PlayerControllerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    Context context;
    private void initView(Context context){
        View rooview = View.inflate(context, R.layout.playercontroller_phone, this);
        view_chagescreen = rooview.findViewById(R.id.view_chagescreen);
        view_play = rooview.findViewById(R.id.view_play);
        tv_current_time = (TextView) rooview.findViewById(R.id.tv_current_time);
        tv_all_time = (TextView) rooview.findViewById(R.id.tv_all_time);
        seekbar= (MySeekbar) rooview.findViewById(R.id.seekbar);
        setListener();
        changePlayerState(PlayerState.PLAYERSTATE_PLAY);
    }

    public void initViewBgByOrientation(boolean isPortraitScreen){
        if(isPortraitScreen){
            view_chagescreen.setBackgroundResource(R.drawable.playback_method_hor);
        }else{
            view_chagescreen.setBackgroundResource(R.drawable.playback_method_ver);

        }
    }


    private void setListener() {
        view_chagescreen.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onChangeScreen();
                }
            }
        });
        view_play.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

            }
        });

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override//fromUser如果是用户触发的改变则返回True
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {

                if(fromUser) {
                    EplayerPlaybackEngin engin= ((EplayerPlaybackEngin)EplayerEngin.getInstance());
                    if(CheckUtil.isEmpty(engin))return;
                    String time =engin.getSessionInfo().getShowTimeWithProgress(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (null != listener) {
                    listener.onStartTrackingTouch(seekBar);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                if (null != listener) {
                    listener.onStopTrackingTouch(seekBar);
                }
            }
        });

        view_play.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {

                switch (playerState) {
                    case PLAYERSTATE_PLAY: {
                        if (!CheckUtil.isEmpty(listener)) {
                            listener.pausePlayback();
                        }

                        changePlayerState(PlayerState.PLAYERSTATE_PAUSE);
                        break;
                    }
                    case PLAYERSTATE_PAUSE: {
                        if (!CheckUtil.isEmpty(listener)) {
                            listener.resumePlayback();
                        }
                        changePlayerState( PlayerState.PLAYERSTATE_PLAY);
                        break;
                    }
                }


            }
        });
        seekbar.setListener(new MySeekbar.SizeChangedListener(){
            @Override
            public void sizeChanged(int w) {
//                    changeProgressTime(current_hms,w);
                }

        });
    }


    public void changePlayerState( PlayerState ps){
        playerState=ps;
        switch (playerState) {
            case PLAYERSTATE_PAUSE: {
                view_play.setBackgroundResource(R.drawable.bt_play);
                break;

            }
            case PLAYERSTATE_PLAY : {
                view_play.setBackgroundResource(R.drawable.bt_pause);
                break;
            }
        }

    }

    long totalTime;
    long progress;
    long playbackEndTime;

    public static class PlayBackTime{
        long progress;
        long totalTime;
        long playbackTime;
        long playbackBeginTime;
        long playbackEndTime;
    }
    public void showProgressTime(long progress, long totalTime, long playbackTime, long playbackBeginTime, long playbackEndTime){
        PlayBackTime playBackTime = new PlayBackTime();
        playBackTime.progress = progress;
        playBackTime.totalTime = totalTime;
        playBackTime.playbackTime = playbackTime;
        playBackTime.playbackBeginTime = playbackBeginTime;
        playBackTime.playbackEndTime = playbackEndTime;
        Message message = Message.obtain();
        message.what = MSG_INIT_PROGRESS_TIME;
        message.obj = playBackTime;
        handler.sendMessage(message);
    }

    public void changeProgressTime( String hms){
        TextViewUtils.setText(tv_current_time,hms);
    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_INIT_PROGRESS_TIME:{
                    PlayBackTime playBackTime= (PlayBackTime) msg.obj;
                    if (playBackTime.totalTime <= 0) {
                        return;
                    }
                    playBackTime.progress =  playBackTime.progress >  playBackTime.totalTime ?  playBackTime.totalTime :  playBackTime.progress;
                    playBackTime.playbackBeginTime =  playBackTime.playbackBeginTime >  playBackTime.playbackEndTime ?  playBackTime.playbackEndTime :  playBackTime.playbackBeginTime;
                    playBackTime.playbackTime =  playBackTime.playbackTime >  playBackTime.playbackEndTime ?  playBackTime.playbackEndTime :  playBackTime.playbackTime;
                    playBackTime.playbackTime =  playBackTime.playbackTime <  playBackTime.playbackBeginTime ?  playBackTime.playbackBeginTime :  playBackTime.playbackTime;



                    if (PlayerControllerView.this.totalTime != playBackTime.totalTime) {
                        PlayerControllerView.this.totalTime = playBackTime.totalTime;
                        seekbar.setMax((int) totalTime);
                    }
                    PlayerControllerView.this.progress = playBackTime.progress;
                    seekbar.setProgress((int) progress);

                    EPlaybackSessionInfo es= (EPlaybackSessionInfo) EplayerEngin.getInstance().getSessionInfo();
                    if(CheckUtil.isEmpty(es))return;
                    String hms = es.getShowTimeWithProgress(progress);
                    changeProgressTime(hms);


                    PlayerControllerView.this.playbackEndTime = playBackTime.playbackEndTime;
                    String endTime = DateUtil.getHmsFromMilliSecond(playbackEndTime-playBackTime.playbackBeginTime);
                    TextViewUtils.setText(tv_all_time,endTime);
                }
            }
        }
    };


    public interface OnPlayerControllerListener extends SeekBar.OnSeekBarChangeListener{

        public void onChangeScreen();
        public void pausePlayback();
        public void resumePlayback();
    }

}
