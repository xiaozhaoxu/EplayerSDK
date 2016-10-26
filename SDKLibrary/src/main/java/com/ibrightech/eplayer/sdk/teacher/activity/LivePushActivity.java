package com.ibrightech.eplayer.sdk.teacher.activity;

import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ibrightech.eplayer.sdk.common.down.EplayerDownFileUtil;
import com.ibrightech.eplayer.sdk.common.engin.EplayerEngin;
import com.ibrightech.eplayer.sdk.common.entity.Asset;
import com.ibrightech.eplayer.sdk.common.entity.CheckStatusEntity;
import com.ibrightech.eplayer.sdk.common.entity.EPlayerData;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerCheckStatusType;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerConstant;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerLiveRoomLiveStatus;
import com.ibrightech.eplayer.sdk.common.entity.Prainse;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.DrawPadInfo;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.LivaRoomInfo.LiveRoomInfoData;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.LivaRoomInfo.LiveRoomStreamConfig;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.SocketMessage;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.UserSessionInfo;
import com.ibrightech.eplayer.sdk.common.entity.UploadEntity;
import com.ibrightech.eplayer.sdk.common.entity.other.EplayerNetErrorEntity;
import com.ibrightech.eplayer.sdk.common.entity.system.DocumentItem;
import com.ibrightech.eplayer.sdk.common.net.http.BaseOkHttpProtocol;
import com.ibrightech.eplayer.sdk.common.net.http.CheckStatusPptWordProtocol;
import com.ibrightech.eplayer.sdk.common.net.http.GetAssetListAsycProtocol;
import com.ibrightech.eplayer.sdk.common.net.http.SaveAssetProtocol;
import com.ibrightech.eplayer.sdk.common.net.http.UploadPptWordProtocol;
import com.ibrightech.eplayer.sdk.common.net.http.utils.DocumnetUtils;
import com.ibrightech.eplayer.sdk.common.net.ws.event.DrawPadInfoChangeEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.EplayerInitEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.FourceLogoutEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.PraiseNumEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.SocketMessageEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.UserCountEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.VideoAudioStatusEvent;
import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.common.util.ConfigUtil;
import com.ibrightech.eplayer.sdk.common.util.EncodeUtil;
import com.ibrightech.eplayer.sdk.common.util.FileUtils;
import com.ibrightech.eplayer.sdk.common.util.LogUtil;
import com.ibrightech.eplayer.sdk.common.util.NetWorkUtil;
import com.ibrightech.eplayer.sdk.common.util.StorageUtil;
import com.ibrightech.eplayer.sdk.common.util.StringUtils;
import com.ibrightech.eplayer.sdk.common.util.TextViewUtils;
import com.ibrightech.eplayer.sdk.common.util.ToastUtil;
import com.ibrightech.eplayer.sdk.common.util.WeakReferenceHandler;
import com.ibrightech.eplayer.sdk.teacher.R;
import com.ibrightech.eplayer.sdk.teacher.dialog.ExitHintDialog;
import com.ibrightech.eplayer.sdk.teacher.dialog.SDKDialogUtil;
import com.ibrightech.eplayer.sdk.teacher.dialog.SelectCoursewareDialog;
import com.ibrightech.eplayer.sdk.teacher.dialog.UpLoadDialog;
import com.ibrightech.eplayer.sdk.teacher.event.AudioShowHintLiveTitleEvent;
import com.ibrightech.eplayer.sdk.teacher.event.CancelHintLiveTitleEvent;
import com.ibrightech.eplayer.sdk.teacher.event.ShowLiveTitleEvent;
import com.ibrightech.eplayer.sdk.teacher.ui.LivePushAudioControllerView;
import com.ibrightech.eplayer.sdk.teacher.ui.LivePushLiveStatusView;
import com.ibrightech.eplayer.sdk.teacher.ui.LivePushVideoControllerView;
import com.ibrightech.eplayer.sdk.teacher.util.ImageUrlUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import cn.nodemedia.LivePublisher;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.Call;

/**
 * Created by zhaoxu2014 on 16/5/20.
 */
public class LivePushActivity extends SDKBaseActivity implements LivePublisher.LivePublishDelegate {

    public static final String KEY_FILE = "KEY_file";


    public static final int CLOSE = 1;
    public static final int BACK = 2;
    public static final int FORCE_CLOSE = 3;
    public static final int NET_WORK = 4;



    File uploadTempFile;
    boolean isJoinRoom=false;

    //音视频地址
    String video_url = "";
    String audio_url = "";
    boolean isLogout;

    String TAG = LivePushActivity.class.getName();

    int common_toptitle_icon_width;

    TextView tv_teacher_name;
    TextView tv_online_num;
    TextView tv_top_praise_num;
    ImageView img_teacher_icon;
    LivePushLiveStatusView layout_widget_live_status;

    LinearLayout li_back;
    LinearLayout li_teacher_info;
    ImageView img_change_camera;
    ImageView img_pause;
    SurfaceView sv;
    RelativeLayout root_view;

    RelativeLayout li_top_title_all;

    LivePushAudioControllerView livepushaudiocontrollerview;
    LivePushVideoControllerView livepushvideocontrollerview;


    ExitHintDialog dialog;
    UpLoadDialog upLoadDialog;
    private boolean isStarting = false;
    boolean isClosePage = false;

    List<SocketMessage> bufferList = new ArrayList<SocketMessage>();
    HashSet<String> msgKeys = new HashSet<String>();
    private Timer timer = new Timer();

   public TreeMap<String, Asset> assetMap = new TreeMap<String, Asset>();//保存所有的ppt或wor列表信息

    Animation face_top_enter, face_top_exit;


    Handler chatHandler=new WeakReferenceHandler(this){

        @Override
        protected void handleMessage(Message msg, Object o) {
            if(!CheckUtil.isEmpty(o)){
                switch (msg.what) {

                    case CHAT_MESSAGE:
                        if (CheckUtil.isEmpty(bufferList)) {
                            return;
                        }
                        livepushvideocontrollerview.addData(bufferList);
                        livepushaudiocontrollerview.addData(bufferList);
                        bufferList.clear();
                        break;
                }
            }
        }
    };


    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            Message msg = new Message();
            msg.what = CHAT_MESSAGE;
            chatHandler.sendMessage(msg);
        }
    };

    ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {

        @Override
        public void onGlobalLayout() {
            if (layout_widget_live_status.isShowVideo()) {
                livepushvideocontrollerview.showFace(isKeyboardShown());
            } else {
                livepushaudiocontrollerview.showFace(isKeyboardShown());
            }
        }
    };

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_push);
        common_toptitle_icon_width= (int) context.getResources().getDimension(R.dimen.common_toptitle_icon_width);

         tv_teacher_name= (TextView) mContentView.findViewById(R.id.tv_teacher_name);
         tv_online_num= (TextView) mContentView.findViewById(R.id.tv_online_num);
         tv_top_praise_num= (TextView) mContentView.findViewById(R.id.tv_top_praise_num);
         img_teacher_icon= (ImageView) mContentView.findViewById(R.id.img_teacher_icon);
         layout_widget_live_status= (LivePushLiveStatusView) mContentView.findViewById(R.id.layout_widget_live_status);

         li_back= (LinearLayout) mContentView.findViewById(R.id.li_back);
         li_teacher_info= (LinearLayout) mContentView.findViewById(R.id.li_teacher_info);
         img_change_camera= (ImageView) mContentView.findViewById(R.id.img_change_camera);
         img_pause= (ImageView) mContentView.findViewById(R.id.img_pause);
         sv= (SurfaceView) mContentView.findViewById(R.id.cameraView);
         root_view= (RelativeLayout) mContentView.findViewById(R.id.root_view);

         li_top_title_all= (RelativeLayout) mContentView.findViewById(R.id.li_top_title_all);

         livepushaudiocontrollerview= (LivePushAudioControllerView) mContentView.findViewById(R.id.livepushaudiocontrollerview);
         livepushvideocontrollerview= (LivePushVideoControllerView) mContentView.findViewById(R.id.livepushvideocontrollerview);

        face_top_enter = AnimationUtils.loadAnimation(context, R.anim.chat_face_top_enter);
        face_top_exit = AnimationUtils.loadAnimation(context, R.anim.chat_face_top_exit);


        Bundle bd = getIntent().getExtras();
        if(!CheckUtil.isEmpty(bd)) {
            playerData = (EPlayerData) bd.getSerializable(KEY_EPLAY_DATA);
            uploadTempFile= (File) bd.getSerializable(KEY_FILE);
        }

        if (CheckUtil.isEmpty(playerData)) {
            ToastUtil.showStringToast("推流信息不全");
            finishWithAnimation();
            return;
        }

        EplayerEngin.getInstance().startLoading();
        startProgressBar();

        livepushaudiocontrollerview.initScreenWidthHeight(this, SCREEN_WIDTH, SCREEN_HEIGHT, playerData.imgUrl);
        isStarting = false;

        boolean is_only_wifi_state = ConfigUtil.getPreferences(ConfigUtil.KEY_ONLY_WIFI_STATE,true);

        if (is_only_wifi_state&& NetWorkUtil.isMobileNetWorkType(context)) {
            showHintDialog(NET_WORK, 0, R.string.net_work_remand, true);
        }
        LivePublisher.init(this); // 1.初始化
        LivePublisher.setDelegate(this); // 2.设置事件回调

        /**
         * 设置输出音频参数 码率 32kbps 使用HE-AAC ,部分服务端不支持HE-AAC,会导致发布失败
         */
        LivePublisher.setAudioParam(32 * 1000, LivePublisher.AAC_PROFILE_HE);

        /**
         * 设置输出视频参数 宽 640 高 360 fps 15 码率 300kbps 以下建议分辨率及比特率 不用超过1280x720
         * 320X180@15 ~~ 200kbps 480X272@15 ~~ 250kbps 568x320@15 ~~ 300kbps
         * 640X360@15 ~~ 400kbps 720x405@15 ~~ 500kbps 854x480@15 ~~ 600kbps
         * 960x540@15 ~~ 700kbps 1024x576@15 ~~ 800kbps 1280x720@15 ~~ 1000kbps
         * 使用main profile
         */
        LivePublisher.setVideoParam(640, 360, 15, 400 * 1000, LivePublisher.AVC_PROFILE_BASELINE);

        /**
         * 是否开启背景噪音抑制
         */
        LivePublisher.setDenoiseEnable(true);

        /**
         * 开始视频预览， cameraPreview ： 用以回显摄像头预览的SurfaceViewd对象，如果此参数传入null，则只发布音频
         * interfaceOrientation ： 程序界面的方向，也做调整摄像头旋转度数的参数， camId：
         * 摄像头初始id，LivePublisher.CAMERA_BACK 后置，LivePublisher.CAMERA_FRONT 前置
         */
        LivePublisher.startPreview(sv, getWindowManager().getDefaultDisplay().getRotation(), LivePublisher.CAMERA_FRONT);






        timer.scheduleAtFixedRate(task, TIME_DELAY, TIME_DELAY);

        livepushaudiocontrollerview.startEventListener();
        setViewClick(mContentView);
    }




    public String getUserName() {


        String name = "";
        if (!CheckUtil.isEmpty(playerData.name)) {
            name =playerData.name;
        }
        if (CheckUtil.isEmpty(name)) {
            UserSessionInfo userInfo = EplayerEngin.getInstance().getSessionInfo().userInfo;
            if (!CheckUtil.isEmpty(userInfo))
                name = userInfo.name;
        }
        return name;
    }

    public String getUserIconUrl() {


        String iconurl = "";
        if (!CheckUtil.isEmpty(playerData.imgHead)) {
            iconurl = ImageUrlUtil.getUrl(playerData.imgHead);
        } else {
            UserSessionInfo userInfo = EplayerEngin.getInstance().getSessionInfo().userInfo;
            if (!CheckUtil.isEmpty(userInfo))
                iconurl = userInfo.icon;
        }
        return ImageUrlUtil.getUrl(iconurl);
    }

    private void setUserEntityInfo() {

        String iconurl = getUserIconUrl();

        Glide.with(this.getApplicationContext()).load(iconurl)
                .bitmapTransform(new CropCircleTransformation(this.getApplicationContext()))
                .placeholder(R.drawable.teacher_icon_default_sdk)
                .error(R.drawable.teacher_icon_default_sdk)
                .into(img_teacher_icon);

        setOnlineNum(1);
        livepushaudiocontrollerview.setOnlineNum(1);
        setTeacherName();
    }

    private void setLiveState(EplayerLiveRoomLiveStatus liveStatus) {


        livepushaudiocontrollerview.setTeachername(getUserName());
        livepushvideocontrollerview.setTeachername(getUserName());
        livepushaudiocontrollerview.setLiveStatus(liveStatus);
        layout_widget_live_status.setLiveStatusText(liveStatus.desc);
        setTeacherName();
    }

    private void setTeacherName() {
        EplayerLiveRoomLiveStatus liveStatus =EplayerEngin.getInstance().getSessionInfo().infoData.liveStatus;
        String showLiveState = StringUtils.getStringByKey(R.string.name_and_status, getUserName(), liveStatus.desc);
        TextViewUtils.setText(tv_teacher_name, layout_widget_live_status.isShowVideo() ? showLiveState : getUserName());
    }

    private void setOnlineNum(int num) {
        String numsr = StringUtils.getStringByKey(R.string.online_num, num);
        TextViewUtils.setText(tv_online_num, numsr);
    }


    LivePushLiveStatusView.OnClickListener listener = new LivePushLiveStatusView.OnClickListener() {
        @Override
        public void liveMethod(boolean showVideo) {
            eventBus.post(new CancelHintLiveTitleEvent());
            showContrller(true);
            if (showVideo) {
                livepushvideocontrollerview.initChatStatus(EplayerEngin.getInstance().getSessionInfo().infoData.canChat);
            } else {
                livepushaudiocontrollerview.initChatStatus(EplayerEngin.getInstance().getSessionInfo().infoData.canChat);
            }
        }

        @Override
        public void play() {
            String pubUrl = "";
            if (layout_widget_live_status.isShowVideo()) {
                pubUrl = video_url;
            } else {
                pubUrl = audio_url;
            }
            if (CheckUtil.isEmpty(pubUrl)) {
                return;
            }
            LivePublisher.startPublish(pubUrl);


        }
    };

    @Override
    protected void setListener() {
        root_view.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
        layout_widget_live_status.setOnClickListener(listener);
        showContrller(false);

        livepushaudiocontrollerview.setCourseWareChangeListener(new SelectCoursewareDialog.OnItemClickListener() {

            @Override
            public void onItemClick(Asset asset) {

                final DrawPadInfo drawPadInfo = EplayerEngin.getInstance().getSessionInfo().drawPadInfo;
                if (EplayerEngin.getInstance().getSessionInfo().drawPadInfo == null) {
                    return;
                }

                final int pptId = drawPadInfo.pptId;
                if (asset.id.equals(pptId + "")) {
                    return;

                }
                drawPadInfo.pptId = Integer.valueOf(asset.id);
                drawPadInfo.page =1;
                toGetPPTList();
                //todo 去做翻页
                EplayerEngin.getInstance().initPptPageReq(asset.id, asset.name, 1, 0, asset.type);
            }

            @Override
            public void deleteAsset(Asset asset1) {

                assetMap.remove(asset1.id);
                DrawPadInfo drawPadInfo = EplayerEngin.getInstance().getSessionInfo().drawPadInfo;
                drawPadInfo.page = 1;
                String key=CheckUtil.isEmpty(assetMap)?"":assetMap.firstKey();

                if (!CheckUtil.isEmpty(key)) {
                    Asset asset = assetMap.get(key);
                    drawPadInfo.pptId = Integer.valueOf(asset.id);
                    getDocumentItemListByAsset(asset,drawPadInfo.pptId+"",drawPadInfo.page,drawPadInfo.resType );

                    EplayerEngin.getInstance().initPptPageReq(asset.id, asset.name, 1, 0, asset.type);
                } else {
                    drawPadInfo.pptId = 0;
                    EplayerEngin.getInstance().initPptPageReq("", "", 1, 0,"");
                }



            }
        });

    }



    public void hideController(boolean isAnimation) {
        li_top_title_all.setVisibility(View.INVISIBLE);
        if(isAnimation) {
            li_top_title_all.startAnimation(face_top_exit);
        }
    }


    public void showContrller(boolean sendEvent) {
        li_top_title_all.setVisibility(View.VISIBLE);
        li_top_title_all.startAnimation(face_top_enter);

        img_pause.setVisibility(View.VISIBLE);


        if (layout_widget_live_status.isShowVideo()) {
            tv_online_num.setVisibility(View.VISIBLE);
            tv_top_praise_num.setVisibility(View.GONE);
            livepushaudiocontrollerview.setVisibility(View.INVISIBLE);
            livepushvideocontrollerview.setVisibility(View.VISIBLE);
            sv.setVisibility(View.VISIBLE);
            img_change_camera.setVisibility(View.VISIBLE);
        } else {

            tv_online_num.setVisibility(View.GONE);
            tv_top_praise_num.setVisibility(View.VISIBLE);
            livepushaudiocontrollerview.setVisibility(View.VISIBLE);

            if (sendEvent) {
                eventBus.post(new AudioShowHintLiveTitleEvent());
            }

            livepushvideocontrollerview.setVisibility(View.INVISIBLE);
            sv.setVisibility(View.GONE);
            img_change_camera.setVisibility(View.GONE);
        }
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }

    // "654725?wsiphost=ipdbm&wsHost=play.upuday.com&domain=play.upuday.com" 获取?前面的数字串
    public String getKeyInfo(String str) {
        if (CheckUtil.isEmpty(str)) {
            return "";
        }
        String[] strs = str.split("\\?");
        if (!CheckUtil.isEmpty(strs) && strs.length == 2) {
            return strs[0];
        }
        return "";
    }

    //EventBus回调函数
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EplayerInitEvent event) {
        switch (event.type){
            case EplayerTeacherLoginFinished:{
                EplayerEngin.getInstance().startClassEngin();
                showStatusChageView();

                List<LiveRoomStreamConfig> streamConfigList = EplayerEngin.getInstance().getSessionInfo().streamConfigList;
                String config = "";
                if (!CheckUtil.isEmpty(streamConfigList)) {
                    LiveRoomStreamConfig lc = streamConfigList.get(0);
                    config = lc.url;
                }

                LiveRoomInfoData ld = EplayerEngin.getInstance().getSessionInfo().infoData;
                String video1SId = ld.video1.streamId;
                String audioSId = ld.audio1.streamId;

                video_url = config + "/" + getKeyInfo(video1SId);
                audio_url = config + "/" + getKeyInfo(audioSId);

                setUserEntityInfo();
                break;
            }
            case EplayerInitTypeLoginError:{
                closeProgressBar();
                ToastUtil.showStringToast((String) event.obj);
                finishWithAnimation();

                break;
            }
            case EplayerInitTypeSocketEndJoinRoom:{
                isJoinRoom=true;
                EplayerEngin.getInstance().initStatusReq();
                EplayerEngin.getInstance().initPraiseReq();
                livepushvideocontrollerview.initChatStatus(EplayerEngin.getInstance().getSessionInfo().infoData.canChat);
                setUserEntityInfo();
                toGetPPTList();
                toUploadStep1(uploadTempFile);
                break;
            }
            case  EplayerInitTypeInitFinished:{
                closeProgressBar();
                break;
            }
        }
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(DrawPadInfoChangeEvent event) {
        toGetPPTList();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ShowLiveTitleEvent event) {
        if (event.isShow()) {
            showContrller(false);
        } else {
            hideController(event.isAnimation());
        }
    }

    private void hideUpLoadDialog(){
        if(!CheckUtil.isEmpty(upLoadDialog)){
            upLoadDialog.dismiss();
            upLoadDialog=null;
        }
    }
    private void setUpLoadDialogDesc(String desc){
        if(!CheckUtil.isEmpty(upLoadDialog)){
            upLoadDialog.setDesc(desc);
        }
    }

    public void toUploadStep1(File uploadFile){
        if(CheckUtil.isEmpty(uploadFile)){
            return;
        }
        uploadTempFile=uploadFile;

        UploadPptWordProtocol protocol=new UploadPptWordProtocol(uploadFile);
        protocol.execute(context, new BaseOkHttpProtocol.CallBack() {
            @Override
            public void onStart() {
                upLoadDialog=  SDKDialogUtil.getInstance().showUploadProgressDialog(LivePushActivity.this,"上传课件","正在上传文件...",false,true);
            }
            @Override
            public void onUpProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                String prg= (int)(currentSize*100.0/totalSize)+"%";
                upLoadDialog.setDesc("正在上传文件"+prg);
            }

            @Override
            public void onSuccess(int i, String s, Object object) {
                UploadEntity uploadEntity= (UploadEntity) object;
                if(!CheckUtil.isEmpty(uploadEntity)){
                    toUploadStep2(uploadEntity);
                }else{
                    ToastUtil.showStringToast("上传文件失败,请稍候再试!");
                }

            }

            @Override
            public boolean onFailure(Call call, EplayerNetErrorEntity errorEntity) {

                ToastUtil.showStringToast("上传文件失败,请稍候再试!");
                hideUpLoadDialog();
                return true;
            }


        });
    }

    protected void toUploadStep2(final UploadEntity uploadEntity ){
        CheckStatusPptWordProtocol protocol=new CheckStatusPptWordProtocol(uploadEntity.name);
        protocol.execute(context, new BaseOkHttpProtocol.CallBack() {
            @Override
            public void onStart() {
                String desc="正在转换课件";
                upLoadDialog.setDesc(desc);
            }

            @Override
            public void onSuccess(int i, String s, Object object) {
                CheckStatusEntity checkStatusEntity= (CheckStatusEntity) object;
                if(CheckUtil.isEmpty(checkStatusEntity)){
                    ToastUtil.showStringToast("课件转换失败,请稍候再试!");
                    hideUpLoadDialog();
                    return;
                }
                EplayerCheckStatusType type= EplayerCheckStatusType.getCheckStatusTypeByKey( checkStatusEntity.stateStr);
                switch (type){
                    case TYPE_SUCESS:{
                        toUploadStep3(uploadEntity);
                        break;
                    }
                    case TYPE_PROCESSING:{
                        String desc="正在转换课件";
                        if(!checkStatusEntity.currentNum.equalsIgnoreCase("0")){
                            desc +=(" "+checkStatusEntity.currentNum+"/"+checkStatusEntity.count);
                        }
                        upLoadDialog.setDesc(desc);
                        toUploadStep2(uploadEntity);
                        break;
                    }
                    case TYPE_COPYFAIL:
                    case TYPE_FAIL:{

                        ToastUtil.showStringToast(type.value());
                        hideUpLoadDialog();
                        break;
                    }
                }


            }

            @Override
            public boolean onFailure(Call call, EplayerNetErrorEntity errorEntity) {
                toUploadStep2(uploadEntity);
                return true;
            }



            @Override
            public void onUpProgress(long l, long l1, float v, long l2) {

            }
        });
    }
    protected void toUploadStep3(UploadEntity uploadEntity ){
        String filename = uploadTempFile.getName();
        boolean isWord = FileUtils.fileIsWord(filename);
        final String type = isWord ? EplayerConstant.KEY_WORD : EplayerConstant.KEY_PPT;

        SaveAssetProtocol protocol = new SaveAssetProtocol(uploadEntity.xmlPath,
                playerData.liveClassroomId, filename, type);
        protocol.execute(context, new BaseOkHttpProtocol.CallBack() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int i, String s, Object object) {
                Asset asset= (Asset) object;
                if(CheckUtil.isEmpty(asset)){
                    ToastUtil.showStringToast("上传课件失败");
                    return;
                }
                EplayerEngin.getInstance().initPptNotifyReq(asset.data);
                EplayerEngin.getInstance().initPptPageReq(asset.id, asset.name, 1, 0, asset.type);

                final DrawPadInfo drawPadInfo = EplayerEngin.getInstance().getSessionInfo().drawPadInfo;
                if (EplayerEngin.getInstance().getSessionInfo().drawPadInfo == null) {
                    return;
                }

                drawPadInfo.pptId= Integer.parseInt(asset.id);
                drawPadInfo.page = 1;

                assetMap.put(asset.id,asset);
                getDocumentItemListByAsset(asset,drawPadInfo.pptId+"", drawPadInfo.page, drawPadInfo.resType);
                //todo 去做翻页

                hideUpLoadDialog();

                ToastUtil.showStringToast("上传课件成功");

            }

            @Override
            public boolean onFailure(Call call, EplayerNetErrorEntity errorEntity) {

                hideUpLoadDialog();
                return true;
            }

            @Override
            public void onUpProgress(long l, long l1, float v, long l2) {

            }
        });
    }

    private void toGetPPTList() {
        String liveRoomId = EplayerEngin.getInstance().getSessionInfo().userInfo.liveClassroomId;
        final DrawPadInfo drawPadInfo = EplayerEngin.getInstance().getSessionInfo().drawPadInfo;
        if (EplayerEngin.getInstance().getSessionInfo().drawPadInfo == null) {
            return;
        }

        final int pptId = drawPadInfo.pptId;
        final int page = drawPadInfo.page;
        final String resType = drawPadInfo.resType;

        GetAssetListAsycProtocol protocol = new GetAssetListAsycProtocol(liveRoomId, "ppt,word");
        protocol.execute(context, new BaseOkHttpProtocol.CallBack() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int i, String s, Object o) {
                assetMap = (TreeMap<String, Asset>) o;

                 Asset asset = assetMap.get(pptId + "");
                getDocumentItemListByAsset(asset,pptId + "",page,resType);

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

    public void getDocumentItemListByAsset(final Asset asset , final String pptId,final int page, final String resType){
        if (CheckUtil.isEmpty(asset)) {
            livepushaudiocontrollerview.setPPTList(null, 0, null);
            return;
        }
        final String fileUrl = asset.fileName;
        LogUtil.d("ListByAsset :fileUrl:",fileUrl);
        EplayerDownFileUtil.getInstance().downLoad(fileUrl, StorageUtil.getImageCacheDierctory(), EncodeUtil.encodeByMD5(fileUrl), new EplayerDownFileUtil.DownLoadCallBack() {
            @Override
            public void onFail() {
                getDocumentItemListByAsset(asset,pptId,page,resType);
                //ToastUtil.showStringToast("获取ppt/word信息失败");
            }


            @Override
            public void onSucceed(File file) {
                List<DocumentItem> documentItemList = DocumnetUtils.getDocumentList(file,pptId,resType, fileUrl);
                livepushaudiocontrollerview.setPPTList(asset, page, documentItemList);
            }
        });
    }



    //踢人
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(FourceLogoutEvent event) {
        if (isLogout) {
            return;
        }
        isLogout = true;
        showHintDialog(FORCE_CLOSE, 0, R.string.force_logout, false);
    }

    //获取点赞总数
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(PraiseNumEvent event) {
        Prainse prainse = event.getPrainse();
        livepushvideocontrollerview.setPraiseNum(prainse.getCount());
        tv_top_praise_num.setText("" + prainse.getCount());
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(VideoAudioStatusEvent event) {
        LiveRoomInfoData data = EplayerEngin.getInstance().getSessionInfo().infoData;
        if (null == data) {

            return;
        }
        setLiveState(data.liveStatus);

    }

    //获取到聊天
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(SocketMessageEvent event) {
        try {

            synchronized (bufferList) {
                List<SocketMessage> list = (List<SocketMessage>) event.getData();
                for (SocketMessage message : list) {
                    if (message.chatInfoKey != null && !msgKeys.contains(message.chatInfoKey)) {
                        bufferList.add(message);
                        msgKeys.add(message.chatInfoKey);
                    }

                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取在线人数
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(UserCountEvent event) {
        int count = event.getCount();
        setOnlineNum(count);
        livepushaudiocontrollerview.setOnlineNum(count);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // 注意：如果你的业务方案需求只做单一方向的视频直播，可以不处理这段

        // 如果程序UI没有锁定屏幕方向，旋转手机后，请把新的界面方向传入，以调整摄像头预览方向
        LivePublisher.setCameraOrientation(getWindowManager().getDefaultDisplay().getRotation());

        // 还没有开始发布视频的时候，可以跟随界面旋转的方向设置视频与当前界面方向一致，但一经开始发布视频，是不能修改视频发布方向的了
        // 请注意：如果视频发布过程中旋转了界面，停止发布，再开始发布，是不会触发"onConfigurationChanged"进入这个参数设置的
        if (!isStarting) {
            switch (getWindowManager().getDefaultDisplay().getRotation()) {
                case Surface.ROTATION_0:
                    LivePublisher.setVideoOrientation(LivePublisher.VIDEO_ORI_PORTRAIT);
                    break;
                case Surface.ROTATION_90:
                    LivePublisher.setVideoOrientation(LivePublisher.VIDEO_ORI_LANDSCAPE);
                    break;
                case Surface.ROTATION_180:
                    LivePublisher.setVideoOrientation(LivePublisher.VIDEO_ORI_PORTRAIT_REVERSE);
                    break;
                case Surface.ROTATION_270:
                    LivePublisher.setVideoOrientation(LivePublisher.VIDEO_ORI_LANDSCAPE_REVERSE);
                    break;
            }
        }


    }

    @Override
    protected void onStop() {
        finish();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            LivePublisher.stopPreview();
            LivePublisher.stopPublish();
            EplayerEngin.getInstance().cancelLoading();

            livepushaudiocontrollerview.stopEventListener();
            EplayerEngin.getInstance().distory();
            // root_view.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
            if (timer != null) {
                try {
                    timer.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (task != null) {
                try {
                    task.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
           // e.printStackTrace();
        }
    }


    @Override
    public void onEventCallback(int event, String msg) {
        handler.sendEmptyMessage(event);
    }

    private Handler handler = new Handler() {
        // 回调处理
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 2000:
                    LogUtil.d(TAG, "正在发布视频");
                    break;
                case 2001:
                    LogUtil.d(TAG, "视频发布成功");
                    EplayerEngin.getInstance().changeStatusReq(EplayerLiveRoomLiveStatus.LiveRoomLiveStatusPlay);
                    EplayerEngin.getInstance().changeStreamStatus(layout_widget_live_status.isShowVideo(), true);
                    isStarting = true;
                    break;
                case 2002:
                    LogUtil.d(TAG, "视频发布失败");
                    break;
                case 2004:
                    LogUtil.d(TAG, "视频发布结束");
                    if (isClosePage) {
                        return;
                    }
                    EplayerEngin.getInstance().changeStatusReq(EplayerLiveRoomLiveStatus.LiveRoomLiveStatusPause);
                    EplayerEngin.getInstance().changeStreamStatus(layout_widget_live_status.isShowVideo(), false);
                    isStarting = false;
                    break;
                case 2005:
                    LogUtil.d(TAG, "网络异常,发布中断");
                    break;
                case 2100:
                    //发布端网络阻塞，已缓冲了2秒的数据在队列中
                    LogUtil.d(TAG, "网络阻塞，发布卡顿");
                    break;
                case 2101:
                    //发布端网络恢复畅通

                    LogUtil.d(TAG, "网络恢复，发布流畅");
                    break;


                case 3100:
                    // mic off
                    // Toast.makeText(LivePublisherDemoActivity.this, "麦克风静音", Toast.LENGTH_SHORT).show();
                    break;
                case 3101:
                    // mic on
                    // Toast.makeText(LivePublisherDemoActivity.this, "麦克风恢复", Toast.LENGTH_SHORT).show();
                    break;
                case 3102:
                    // camera off
                    // Toast.makeText(LivePublisherDemoActivity.this, "摄像头传输关闭", Toast.LENGTH_SHORT).show();
                    break;
                case 3103:
                    // camera on
                    // Toast.makeText(LivePublisherDemoActivity.this, "摄像头传输打开", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    LogUtil.d(TAG, "----default");
                    break;
            }
        }
    };


    public void setViewClick(View view){
        li_back.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (!livepushaudiocontrollerview.isPortraitScreen()) {
                    livepushaudiocontrollerview.changScreenOrientation();
                    return;
                } else if (liveIsStart()) {
                    showHintDialog(BACK, R.string.live_push_back_dialog_title, R.string.live_push_back_dialog_desc, true);
                } else {
                    finishWithAnimation();
                }
            }
        });

        img_change_camera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                LivePublisher.switchCamera();// 切换前后摄像头
            }
        });
        img_pause.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                LivePublisher.stopPublish();// 停止发布
                if(isJoinRoom){
                    showStatusChageView();
                }

            }
        });
        view.findViewById(R.id.img_close)
        .setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showHintDialog(CLOSE, R.string.live_push_close_dialog_title, R.string.live_push_close_dialog_desc, true);

            }
        });
    }


    public void showStatusChageView() {
        layout_widget_live_status.setVisibility(View.VISIBLE);
    }


    //退出提示
    private void showHintDialog(final int status, int title, int desc, final boolean isShowCancelButton) {
        dialog = SDKDialogUtil.getInstance().showExitHintDialog(this, StringUtils.getStringByKey(title),
                StringUtils.getStringByKey(desc), isShowCancelButton, new ExitHintDialog.ClickListener() {
                    @Override
                    public void confirmClick() {
                        switch (status) {
                            case CLOSE:
                                isClosePage = true;
                                EplayerEngin.getInstance().changeStatusReq(EplayerLiveRoomLiveStatus.LiveRoomLiveStatusClose);
                                finishWithAnimation();
                                break;
                            case BACK:
                                isClosePage = true;
                                EplayerEngin.getInstance().changeStatusReq(EplayerLiveRoomLiveStatus.LiveRoomLiveStatusPause);
                                finishWithAnimation();
                                break;
                            case FORCE_CLOSE:
                                isClosePage = true;
                                EplayerEngin.getInstance().changeStatusReq(EplayerLiveRoomLiveStatus.LiveRoomLiveStatusPause);
                                finishWithAnimation();
                                break;
                            case NET_WORK:
                                break;
                        }
                    }

                    @Override
                    public void cancelClick() {
                        if (status == NET_WORK) {
                            isClosePage = true;
                            EplayerEngin.getInstance().changeStatusReq(EplayerLiveRoomLiveStatus.LiveRoomLiveStatusPause);
                            finishWithAnimation();
                        }
                    }
                });
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {

            if (!livepushaudiocontrollerview.isPortraitScreen()) {
                livepushaudiocontrollerview.changScreenOrientation();
                return true;
            } else if (!CheckUtil.isEmpty(dialog) && dialog.isShowing()) {
                dialog.cancel();
                return true;
            } else {
                if (liveIsStart()) {
                    showHintDialog(BACK, R.string.live_push_back_dialog_title, R.string.live_push_back_dialog_desc, true);
                    return true;
                }
            }

        }
        return super.dispatchKeyEvent(event);
    }

    public boolean liveIsStart() {
        LiveRoomInfoData infoData = EplayerEngin.getInstance().getSessionInfo().infoData;
        return !CheckUtil.isEmpty(infoData) && infoData.liveStatus == EplayerLiveRoomLiveStatus.LiveRoomLiveStatusPlay;
    }

    private boolean isKeyboardShown() {
        Rect r = new Rect();
        //获取当前界面可视部分
        getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        //获取屏幕的高度
        int screenHeight = getWindow().getDecorView().getRootView().getHeight();
        //此处就是用来获取键盘的高度的， 在键盘没有弹出的时候 此高度为0 键盘弹出的时候为一个正数
        return screenHeight - r.bottom != 0;
    }

}
