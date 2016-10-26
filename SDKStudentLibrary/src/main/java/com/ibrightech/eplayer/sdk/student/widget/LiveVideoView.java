package com.ibrightech.eplayer.sdk.student.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSONReader;
import com.ibrightech.eplayer.sdk.common.config.EplayerSetting;
import com.ibrightech.eplayer.sdk.common.down.EplayerDownFileUtil;
import com.ibrightech.eplayer.sdk.common.engin.EplayerEngin;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerLiveRoomLiveStatus;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerLiveRoomStreamType;
import com.ibrightech.eplayer.sdk.common.entity.MusicInfo;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.LivaRoomInfo.LiveRoomInfoData;
import com.ibrightech.eplayer.sdk.common.entity.SliderPPT;
import com.ibrightech.eplayer.sdk.common.entity.other.EplayerNetErrorEntity;
import com.ibrightech.eplayer.sdk.common.entity.playback.PlayList;
import com.ibrightech.eplayer.sdk.common.entity.playback.PlaySplice;
import com.ibrightech.eplayer.sdk.common.entity.playback.PlaybackSegment;
import com.ibrightech.eplayer.sdk.common.entity.playback.SpliceInfo;
import com.ibrightech.eplayer.sdk.common.net.http.BaseOkHttpProtocol;
import com.ibrightech.eplayer.sdk.common.net.http.GetMusicInfoProtocol;
import com.ibrightech.eplayer.sdk.common.net.http.GetMusicMoreInfoProtocol;
import com.ibrightech.eplayer.sdk.common.net.ws.event.EplayerInitEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.MusicEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.NextSegmentEvent;
import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.common.util.LogUtil;
import com.ibrightech.eplayer.sdk.common.util.StringUtils;
import com.ibrightech.eplayer.sdk.common.util.TimerDelayScheduler;
import com.ibrightech.eplayer.sdk.common.util.ToastUtil;
import com.ibrightech.eplayer.sdk.student.R;
import com.ibrightech.eplayer.sdk.student.entityenum.PalyTypeState;
import com.ibrightech.eplayer.sdk.student.event.MusiceChangeStateEvent;
import com.ibrightech.eplayer.sdk.student.event.MusiceStateEvent;
import com.ibrightech.eplayer.sdk.student.event.VideoErrorOutTimeEvent;
import com.ibrightech.eplayer.sdk.student.event.VideoLoadEvent;
import com.ibrightech.eplayer.sdk.student.event.VideoLoadOutTimeEvent;
import com.ibrightech.eplayer.sdk.teacher.activity.SDKBaseActivity;
import com.ibrightech.eplayer.sdk.teacher.ui.MyProgressBar;
import com.ibrightech.eplayer.sdk.widget.BaseShowView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by zhaoxu2014 on 16/9/2.
 */
public class LiveVideoView extends BaseShowView implements TimerDelayScheduler.TimeTaskListener {
    public static final int MESSAGE_PARSETHREAD= 1000127;

    EventBus bus;
    Context context;
    MyVideoView my_videoview;
    LinearLayout video_li_progressbar_bg;
    LinearLayout layout_no_push;
    MyProgressBar video_progressbar;
    PalyTypeState palyTypeState = PalyTypeState.STATE_NONE;

    TimerDelayScheduler timerDelayScheduler;
    public LiveVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public LiveVideoView(Context context) {
        super(context);
        initView(context);
    }

    public LiveVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public void showProgress(){
        video_li_progressbar_bg.setVisibility(View.VISIBLE);
    }
    public void hideProgress(){
        video_li_progressbar_bg.setVisibility(View.INVISIBLE);
    }

    private void initView(Context context){
        this.context=context;
        timerDelayScheduler = new TimerDelayScheduler(this);
        bus = EventBus.getDefault();
        bus.register(this);
        View view = View.inflate(context, R.layout.video_view, this);
        my_videoview= (MyVideoView) view.findViewById(R.id.my_videoview);
        layout_no_push= (LinearLayout) view.findViewById(R.id.layout_no_push);
        video_li_progressbar_bg= (LinearLayout) view.findViewById(R.id.video_li_progressbar_bg);;
        video_progressbar= (MyProgressBar) view.findViewById(R.id.video_progressbar);
        video_progressbar.init(MyProgressBar.TYPE_LINEARLAYOUT_LAYOUTPARAMS);
        setListener();
    }

    @Override
    public void handlerTimeTaskFinished(long taskToken,int retryCount) {
        //todo 定时器回调
        if(taskToken==SDKBaseActivity.TASK_TYPE_LOAD_OUT_TIME){
            if(retryCount<=0) {
                bus.post(new VideoLoadOutTimeEvent());
            }
        }else if(taskToken==SDKBaseActivity.TASK_TYPE_ERROR_OUT_TIME){
            if(retryCount<=0) {
                bus.post(new VideoErrorOutTimeEvent());
            }
        }
    }


    private void setListener(){
        my_videoview.setVideoViewListener(new MyVideoView.VideoViewListener() {
            @Override
            public void onCompletion() {
                try {
                    LiveRoomInfoData infoData = EplayerEngin.getInstance().getSessionInfo().infoData;
                    if (infoData.playMusic) {//播放音乐时，当播放完了，再播下一首
                        getMuiscInfo(EplayerEngin.getInstance().getSessionInfo().userInfo.liveClassroomId, infoData.musicType + "");
                    }else{
                        //视频播放结束
                    }

                }catch (Exception e){

                }
            }

            @Override
            public void onError() {
                timerDelayScheduler.scheduledWaitTask(SDKBaseActivity.TASK_TYPE_ERROR_OUT_TIME,10000,TimerDelayScheduler.RETRYCOUNT_MAX);

            }

            @Override
            public void onLoadingStart(IMediaPlayer mp) {
                showProgress();
                bus.post(new VideoLoadEvent(true));

                timerDelayScheduler.scheduledWaitTask(SDKBaseActivity.TASK_TYPE_LOAD_OUT_TIME,10000,TimerDelayScheduler.RETRYCOUNT_MAX);
            }

            @Override
            public void onLoadingEnd(IMediaPlayer mp) {
                hideProgress();
                bus.post(new VideoLoadEvent(false));

                timerDelayScheduler.invalidateWaitTask(SDKBaseActivity.TASK_TYPE_LOAD_OUT_TIME);
                timerDelayScheduler.invalidateWaitTask(SDKBaseActivity.TASK_TYPE_ERROR_OUT_TIME);

            }

            @Override
            public void onFirstRenderingVideo(IMediaPlayer mp) {
                onLoadingEnd(mp);
            }

            @Override
            public void onFirstRenderingAudio(IMediaPlayer mp) {
                onLoadingEnd(mp);
            }


        });


    }

    @Override
    public void initScreenWidthHeight(int width, int height){
        if(width<=0||height<=0){
            return;
        }

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this
                .getLayoutParams();

        params.width=width;
        params.height=height;
        this.setLayoutParams(params);
        my_videoview.setVideoLayout(width, height);
    }

    public boolean isPlayMuiceState() {
        if (palyTypeState == PalyTypeState.STATE_PLAY_MUICE) {
            return true;
        } else {
            return false;
        }

    }

    public void setVideoURI( PalyTypeState palyTypeState,final LiveRoomInfoData data) {
        if(CheckUtil.isEmpty(data)){
            return;
        }
        this.palyTypeState=palyTypeState;
        if(!data.canSplice){
            my_videoview.setVideoURI(data.getPlayUrl());
        }else{
            try {

                final PlaybackSegment currentPlaybackSegment = data.currentPlaybackSegment;
                final PlayList info = currentPlaybackSegment.info;
                if (info.isexists()) {
                    String downUrl = EplayerSetting.getInstance().spliceVideoPlayBaseUrl + info.getSuffix() + "/" + info.getDownSpliceUrl() + "/index.list";

                        LogUtil.d("downUrl:" + downUrl);

                    initPlayBackVideoView(info.type, currentPlaybackSegment.endTime, currentPlaybackSegment.startTime - currentPlaybackSegment.allSegmentStartTime, currentPlaybackSegment.seq, data.getPlayUrl(), info.getDownLocationPath(), info.endfiletime, info.getSuffix());
                } else {
                    String downUrl = EplayerSetting.getInstance().spliceVideoPlayBaseUrl + info.getSuffix() + "/" + info.getDownSpliceUrl() + "/index.list";
                    final String localPath = info.getDownLocationPath();
                    LogUtil.d("downUrl:" + downUrl);
                    EplayerDownFileUtil.getInstance().downLoad(downUrl,info.getDownLocationParentPath(),info.getDownLocationFileName(),new EplayerDownFileUtil.DownLoadCallBack(){

                        @Override
                        public void onFail() {

                        }
                        @Override
                        public void onSucceed(File file) {
                            initPlayBackVideoView(info.type, currentPlaybackSegment.endTime, currentPlaybackSegment.startTime - currentPlaybackSegment.allSegmentStartTime, currentPlaybackSegment.seq, data.getPlayUrl(), localPath, info.endfiletime, info.getSuffix());
                        }
                    });
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /*
    allseq //有可能当段视频是由一整个视频切成多个的，此处记录下当前片段在整个视频的偏移量
     */
    public void initPlayBackVideoView(EplayerLiveRoomStreamType type, long endTime, long allseq, long seq, String palyUrl, String path, String endfiletime, String suffix) {
        new ParseThread(type,endTime, allseq, seq,palyUrl, path, endfiletime, suffix).start();
    }



    public class ParseThread extends Thread{
        EplayerLiveRoomStreamType type;
        long endTime;
        long allseq;
        long seq;
        String palyUrl;
        String path;
        String endfiletime;
        String suffix;

        public ParseThread(EplayerLiveRoomStreamType type,long endTime,long allseq,long seq,String palyUrl,String path,String endfiletime,String suffix){
            this.type=type;
            this.endTime=endTime;
            this.allseq=allseq;
            this.seq=seq;
            this.palyUrl=palyUrl;
            this.path=path;
            this.endfiletime=endfiletime;
            this.suffix=suffix;
        }

        @Override
        public void run() {
            try {

                SpliceInfo bean =new SpliceInfo();

                bean.suffix=suffix;
                bean.endfiletime=endfiletime;

                //将画笔信息按页保存，一页为一个对象，里面包含画笔列表，并冗余第一条和最后一条的时间，方便查询
                JSONReader reader = new JSONReader(new FileReader(path));
                reader.startObject();
                int count =0;
                while (reader.hasNext()) {
                    String key = reader.readString();

                    if ("interval".equals(key)) {
                        int interval = reader.readInteger();
                        bean.interval = interval;
                        count++;
                    } else if ("timestamp".equals(key)) {
                        long timestamp = reader.readLong();
                        bean.timestamp = timestamp;
                        count++;
                    } else if ("length".equals(key)) {
                        long length = reader.readLong();
                        bean.length = length;
                        count++;

                    } else if ("zero4Thirteenth".equals(key)) {
                        String zero4Thirteenth = reader.readString();
                        bean.zero4Thirteenth = "true".equals(zero4Thirteenth);
                        count++;
                    }
                    if(count==4){
                        break;
                    }

                }
//                reader.endObject();
                reader.close();


                LogUtil.e("-------SpliceInfo:interval:" + bean.interval + " timestamp:" + bean.timestamp + " length:" + bean.length + " zero4Thirteenth:" + bean.zero4Thirteenth);


                bean.suffix=suffix;
                bean.endfiletime=endfiletime;
                bean.endTime=endTime;
                bean.seq=allseq+seq;
                bean.type=type;
                bean.palyurl=palyUrl;

                if((bean.endTime-bean.seq)%2000==0)
                    bean.indexSize=(int)((bean.endTime-bean.seq) /2000);
                else
                    bean.indexSize=(int)((bean.endTime-bean.seq) /2000) + 1;

                bean.startIndex = (int)(bean.seq /2000);

                int size =(int)(bean.length /2000);
                LogUtil.e("-------SpliceInfo:startIndex:" +  bean.startIndex + " size:" + size+ " indexSize:" + bean.indexSize+ " seq:" + seq );

                if((bean.startIndex+bean.indexSize)>size){

                    bean.indexSize = size - bean.startIndex;
                }
                LogUtil.e("-------SpliceInfo:startIndex:" +  bean.startIndex + " size:" + size+ " indexSize:" + bean.indexSize+ " seq:" + seq );


//                JSONObject jsonObject = JSONUtils.getJSONFromFile(path);
//                SpliceInfo spliceInfo = SpliceInfo.fromJson(jsonObject,suffix,endfiletime,endTime,allseq+seq, type);
//                spliceInfo.palyurl=palyUrl;
                Message message=Message.obtain();
                message.what= MESSAGE_PARSETHREAD;
                message.obj=bean;
                handler.sendMessage(message);
                System.gc();
            }catch (Exception e){
                e.printStackTrace();
            }

            super.run();
        }
    }

    Handler handler= new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_PARSETHREAD: {
                    try {
                        final SpliceInfo spliceInfo= (SpliceInfo) msg.obj;

                        if(spliceInfo.indexSize<=1){
                            bus.post(new NextSegmentEvent());
                            return;
                        }
                        my_videoview.setVideoURI("ijkfileurlsegment://");
                        my_videoview.setOnControlMessageListener(new IjkMediaPlayer.OnControlMessageListener(){
                            @Override
                            public String onControlResolveSegmentUrl(int segment) {
                                //给出播放地址就行
                                if(segment>=spliceInfo.indexSize){
                                    return null;
                                }

                                PlaySplice ps=spliceInfo.getPlaySplice(segment);
                                if(!ps.needdownload){
                                    return ps.path;
                                }
                                return ps.url;
                            }
                        });


                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                }
            }
            super.handleMessage(msg);
        }
    };



    public boolean isFinished(){
      return   my_videoview.isFinished();
    }
    public int getCurrentPosition() {
        return  my_videoview.getCurrentPosition();

    }

    @Override
    public void setVisibility(int visibility) {
        if(visibility==VISIBLE) {
            LiveRoomInfoData data = EplayerEngin.getInstance().getSessionInfo().infoData;
            if (!CheckUtil.isEmpty(data) && data.isStreamPush) {
                layout_no_push.setVisibility(View.GONE);
            } else {
                layout_no_push.setVisibility(View.VISIBLE);
            }
        }

        super.setVisibility(visibility);
    }

    public void start(){
        my_videoview.start();
    }
    public void pause() {
        my_videoview.pause();
    }


    //EventBus回调函数
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EplayerInitEvent event) {
        switch (event.type){

            case EplayerInitTypeSocketEndJoinRoom:{
                LiveRoomInfoData infoData= EplayerEngin.getInstance().getSessionInfo().infoData;
                if (infoData.playMusic) {
                    palyTypeState = PalyTypeState.STATE_PLAY_MUICE;
                    getMuiscInfo(EplayerEngin.getInstance().getSessionInfo().userInfo.liveClassroomId, infoData.musicType + "");
                    bus.post(new MusiceChangeStateEvent(EplayerLiveRoomLiveStatus.LiveRoomLiveStatusPlay));
                }
                break;
            }
        }
    }

    //播放音乐
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBackgroundThread(MusicEvent event) {
        if(event.getInfoData().playMusic) {
            palyTypeState=PalyTypeState.STATE_PLAY_MUICE;
            int musicType=event.getInfoData().musicType;
            getMuiscInfo( EplayerEngin.getInstance().getSessionInfo().userInfo.liveClassroomId,musicType+"");
            bus.post(new MusiceChangeStateEvent(EplayerLiveRoomLiveStatus.LiveRoomLiveStatusPlay));
        }else{
            if(isPlayMuiceState()) {
                palyTypeState = PalyTypeState.STATE_NONE;
                //todo 停止播放音乐
                my_videoview.videoviewStopPlayback();
            }
            bus.post(new MusiceChangeStateEvent(EplayerLiveRoomLiveStatus.LiveRoomLiveStatusClose));
        }
    }
    //改变音乐播放状态
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBackgroundThread(MusiceStateEvent event){
        //todo 需要先判断下是否是在播放音乐

        EplayerLiveRoomLiveStatus muisceStatus=  event.getStatus();
        switch (muisceStatus){
            case LiveRoomLiveStatusPlay:{
                my_videoview.start();
                break;
            }
            case LiveRoomLiveStatusPause:{
                my_videoview.pause();
                break;
            }
        }
    }


    public void getMuiscInfo(String liveClassRoomId, String musicType){
        GetMusicInfoProtocol protocol = new GetMusicInfoProtocol(liveClassRoomId, musicType);
        protocol.execute(context, new BaseOkHttpProtocol.CallBack() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int errorCode, String msg, Object object) {
                if(errorCode==0) {
                    MusicInfo musicInfo = (MusicInfo) object;
                    try {
                        getMuiscMoreInfo(musicInfo.data);
                    }catch (Exception e){
                        e.printStackTrace();

                    }
                }else{
                    toPlayMuisc("");
                }
            }

            @Override
            public boolean onFailure(Call call, EplayerNetErrorEntity errorEntity) {
                toPlayMuisc("");
                return true;
            }

            @Override
            public void onUpProgress(long currentSize, long totalSize, float progress, long networkSpeed) {

            }
        });
    }
    private void getMuiscMoreInfo(final String dataurl){
        GetMusicMoreInfoProtocol protocol=new GetMusicMoreInfoProtocol(dataurl);
        protocol.execute(context, new BaseOkHttpProtocol.CallBack() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int errorCode, String msg, Object object) {

                Random random = new Random();
                List<SliderPPT> musicList= (List<SliderPPT>) object;
                SliderPPT sliderPPT = musicList.get(random.nextInt(musicList.size()));

                String urlTitle = dataurl.substring(0, dataurl.lastIndexOf("/"));
                urlTitle = urlTitle.substring(0, urlTitle.lastIndexOf("/") );
                String musicPath = urlTitle + sliderPPT.artwork;
                toPlayMuisc(musicPath);

            }

            @Override
            public boolean onFailure(Call call, EplayerNetErrorEntity errorEntity) {
                toPlayMuisc("");
                return true;
            }

            @Override
            public void onUpProgress(long currentSize, long totalSize, float progress, long networkSpeed) {

            }
        });
    }

    public void toPlayMuisc(String musicPath) {
//        musicPath="http://up.haoduoge.com:82/mp3/2016-09-09/1473392252.mp3";
        if (StringUtils.isValid(musicPath)) {
         //todo 播放音乐
            my_videoview.videoviewStopPlayback();
            my_videoview.setVideoURI(musicPath);
            my_videoview.start();
        }else{
            ToastUtil.showStringToast(context,"播放音乐失败");
        }
    }

    public void stopPlayback(){
        try{
           my_videoview.videoviewStopPlayback();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void releaseAll(){
        if(!CheckUtil.isEmpty(bus)&&bus.isRegistered(this)){
            bus.unregister(this);
        }
        my_videoview.releaseAll();
    }


}
