package com.ibrightech.eplayer.sdk.teacher.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ibrightech.eplayer.sdk.common.entity.EPlayerData;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EPlayerLoginType;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EPlayerPlayModelType;
import com.ibrightech.eplayer.sdk.common.entity.other.EplayerNetErrorEntity;
import com.ibrightech.eplayer.sdk.common.util.BundleUtil;
import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.common.util.DateUtil;
import com.ibrightech.eplayer.sdk.common.util.FileUtils;
import com.ibrightech.eplayer.sdk.common.util.MD5Utils;
import com.ibrightech.eplayer.sdk.common.util.StorageUtil;
import com.ibrightech.eplayer.sdk.common.util.TextViewUtils;
import com.ibrightech.eplayer.sdk.common.util.ToastUtil;
import com.ibrightech.eplayer.sdk.teacher.Entity.LoginInfEntity;
import com.ibrightech.eplayer.sdk.teacher.Entity.TeacherCourseEntity;
import com.ibrightech.eplayer.sdk.teacher.Entity.TeacherSectionEntity;
import com.ibrightech.eplayer.sdk.teacher.Entity.UserEntity;
import com.ibrightech.eplayer.sdk.teacher.Entity.entityenum.ClassDataTypeEnum;
import com.ibrightech.eplayer.sdk.teacher.adapter.UploadPPTAdapter;
import com.ibrightech.eplayer.sdk.teacher.dialog.SDKDialogUtil;
import com.ibrightech.eplayer.sdk.teacher.event.ZhiBoEvent;
import com.ibrightech.eplayer.sdk.teacher.event.ZhiBoListEvent;
import com.ibrightech.eplayer.sdk.teacher.net.HttpBaseProtocol;
import com.ibrightech.eplayer.sdk.teacher.net.TeacherSectionProtocol;
import com.ibrightech.eplayer.sdk.teacher.ui.pulltorefresh.library.ILoadingLayout;
import com.ibrightech.eplayer.sdk.teacher.ui.pulltorefresh.library.PullToRefreshBase;
import com.ibrightech.eplayer.sdk.teacher.ui.pulltorefresh.library.PullToRefreshListView;
import com.ibrightech.eplayer.sdk.teacher.util.ImageUrlUtil;
import com.ibrightech.eplayer.sdk.teacher.util.SDKTeacherUtil;
import com.ibrightech.eplayer.sdk.teacher.R;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.List;

import Decoder.BASE64Encoder;
import okhttp3.Call;


public class UploadPPTActivity extends SDKBaseActivity {

    public static String KEY_LIVECLASSROOMID = "liveClassroomId=";
    public static String KEY_P = "&p=";
    public static String KEY_CUSTOMER = "customer=";

    public static String KEY_USERINFO = "key_userinfo";
    public static String KEY_FILE_PATH = "KEY_file_path";
    LoginInfEntity loginInfEntity;
    UserEntity userEntity;
    private String filePath="";

    LinearLayout empty_view;

    LinearLayout li_back;
    LinearLayout li_tilte_right;

    TextView tv_common_title;
    TextView tv_ppt_name;
    TextView tv_ppt_size;
    ImageView iv_course_icon;
    PullToRefreshListView pull_refresh_list;
    View tv_next;

    UploadPPTAdapter adapter;
    File newFile;
    int zhibopage=0,DEFALUTSIZE = 20;
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initPPTInfo(intent);
    }
    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_uploadppt);

        empty_view= (LinearLayout) mContentView.findViewById(R.id.empty_view);

        li_back= (LinearLayout) mContentView.findViewById(R.id.li_back);

         li_tilte_right= (LinearLayout) mContentView.findViewById(R.id.li_tilte_right);

         tv_common_title= (TextView) mContentView.findViewById(R.id.tv_common_title);
         tv_ppt_name= (TextView) mContentView.findViewById(R.id.tv_ppt_name);
         tv_ppt_size= (TextView) mContentView.findViewById(R.id.tv_ppt_size);
         iv_course_icon= (ImageView) mContentView.findViewById(R.id.iv_course_icon);
        pull_refresh_list= (PullToRefreshListView) mContentView.findViewById(R.id.pull_refresh_list);
        tv_next=mContentView.findViewById(R.id.tv_next);

        adapter = new UploadPPTAdapter(context);
        pull_refresh_list.setAdapter(adapter);

        initZhoboData(true,0);

        li_back.setVisibility(View.GONE);
        li_tilte_right.setVisibility(View.VISIBLE);
        TextViewUtils.setText(tv_common_title,"上传课件");

        initPPTInfo(getIntent());
    }
    private void initPPTInfo(Intent intent){
        Bundle bd=intent.getExtras();
        if(!CheckUtil.isEmpty(bd)) {
            loginInfEntity= (LoginInfEntity) bd.getSerializable(KEY_USERINFO);
            if(!CheckUtil.isEmpty(loginInfEntity)){
                userEntity=loginInfEntity.getUser();
            }
        }

        filePath=  BundleUtil.getStringFormBundle(bd,KEY_FILE_PATH);
        if(!CheckUtil.isEmpty(filePath)){
            File file=new File(filePath);
            String fileName=file.getName();
            TextViewUtils.setText(tv_ppt_name,fileName);
            if (file.exists() && file.isFile()){
                long fileSize= file.length();
                String fileSizeStr= FileUtils.getFileSizeByLength(fileSize);
                TextViewUtils.setText(tv_ppt_size,fileSizeStr);
            }

            boolean isPDF=FileUtils.fileIsPDF(fileName);
            if(isPDF){
                SDKDialogUtil.getInstance().showUploadProgressDialog(this,"上传课件","暂不支持该文件格式\r\n请上传ppt或word文件",true,false);
                tv_next.setBackgroundResource(R.drawable.adapter_livelist_item_bt_unenable_bg);
                tv_next.setEnabled(false);
                return;
            }




            String prefix=fileName.substring(fileName.lastIndexOf(".")+1);

            String fileNewName= StorageUtil.getUploadCacheDierctory()+fileName.substring(0,fileName.lastIndexOf("."))+"_"+ DateUtil.getNowString()+"."+prefix;
            newFile=new File(fileNewName);
            File parentFile=newFile.getParentFile();
            parentFile.mkdirs();
            try {
                FileUtils.copyFile(file,newFile);
            } catch (IOException e) {
                ToastUtil.showStringToast(e.getMessage());
                return;
            }

            boolean isPPT=FileUtils.fileIsPPT(fileName);

            if(isPPT){
                iv_course_icon.setImageResource(R.drawable.ppt);
            }else{
                iv_course_icon.setImageResource(R.drawable.word);
            }
        }
    }


    private void initZhoboData(boolean isRefresh,int _page){
        zhibopage=_page;
        eventBus.post(new ZhiBoEvent(isRefresh,zhibopage,DEFALUTSIZE));
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ZhiBoListEvent event){
        List<TeacherCourseEntity> dataList=event.list;
        pull_refresh_list.onRefreshComplete();
        if (!CheckUtil.isEmpty(dataList) && dataList.size() >= DEFALUTSIZE) {
            pull_refresh_list.setMode(PullToRefreshBase.Mode.BOTH);
        } else {
            pull_refresh_list.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        }

        if(event.isRefresh){
            adapter.setDatas(dataList);
        }else{
            zhibopage=event.page;
            adapter.addMoreDatas(dataList);
        }
    }




    @Override
    protected void setListener() {

        tv_next.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(!CheckUtil.isEmpty(adapter.getLibraryAdapterList())) {
                    int position = adapter.getSelectPostion();
                    TeacherCourseEntity teacherCourseEntity = (TeacherCourseEntity) adapter.getItem(position);
                    getTeacherSetction(teacherCourseEntity);
                }else {
                    ToastUtil.showStringToast("当前没有可用的课程");

                }
            }
        });

        pull_refresh_list.setMode(PullToRefreshBase.Mode.BOTH);
        pull_refresh_list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase refreshView) {
                initZhoboData(true,0);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase refreshView) {
                initZhoboData(false,++zhibopage);

            }
        });
        initIndicator();



        li_tilte_right.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                finishWithAnimation();

            }
        });
    }
    private void initIndicator() {
        ILoadingLayout startLabels = pull_refresh_list
                .getLoadingLayoutProxy(true, false);

        startLabels.setPullLabel("下拉可以刷新"); // 刚下拉时，显示的提示
        startLabels.setRefreshingLabel("正在刷新数据..."); // 刷新时
        startLabels.setReleaseLabel("松开立即刷新"); // 下来达到一定距离时，显示的提示

        ILoadingLayout endLabels = pull_refresh_list.getLoadingLayoutProxy(
                false, true);
        endLabels.setPullLabel("上拉可以加载更多数据"); // 刚上拉时，显示的提示
        endLabels.setRefreshingLabel("正在刷新数据..."); // 刷新时
        endLabels.setReleaseLabel("松开立即加载更多数据"); // 上来达到一定距离时，显示的提示
    }

    public void getTeacherSetction(final TeacherCourseEntity teacherCourseEntity) {
        if (CheckUtil.isEmpty(teacherCourseEntity)) {
            return;
        }

        TeacherSectionProtocol protocol = new TeacherSectionProtocol(teacherCourseEntity.getId(),loginInfEntity);
        protocol.execute(context, new HttpBaseProtocol.CallBack() {
            @Override
            public void onStart() {
                startProgressBar();
            }

            @Override
            public boolean onUseBufferDataAndCancelNetwork(Object object) {
                return false;
            }

            @Override
            public void onSuccess(boolean isSuccess, String msg, Object object) {
                if (isCancelNetwork())
                    return;

                closeProgressBar();
                if (!isSuccess) {
                    //todo
                    ToastUtil.showStringToast(context, msg);
                    return;
                } else {
                    TeacherSectionEntity teacherSectionEntity = (TeacherSectionEntity) object;
                    String imageUrl= ImageUrlUtil.getUrl(teacherCourseEntity.getUrl());
                    toNextPage(teacherSectionEntity,imageUrl);
                }

            }


            @Override
            public boolean onFailure(Call call, EplayerNetErrorEntity errorEntity) {
                closeProgressBar();
                String msg = EplayerNetErrorEntity.getMsg(errorEntity);
                if (!CheckUtil.isEmpty(msg)) {
                    //如果服务器返回了错误信息就用服务器的
                    ToastUtil.showStringToast(msg);
                    return true;
                }
                ///如果服务器没有返回了错误信息就用提示 "联网失败"
                return false;
            }
        });
    }

    public  void toNextPage(TeacherSectionEntity teacherSectionEntity, String bgImageUrl) {
        if (ClassDataTypeEnum.ZHIBO_Type.get_key().equalsIgnoreCase(teacherSectionEntity.getData_type())) {
            String url = teacherSectionEntity.getUrl();
            String liveClassroomId = getValue(url, KEY_LIVECLASSROOMID);
            String customer = getValue(url, KEY_CUSTOMER);
            String p = getValue(url, KEY_P);
            String token = "";
            if (!CheckUtil.isEmpty(p)) {
                String[] ps = p.split("\\|");
                if (ps.length > 3) {
                    String username = ps[2];
                    long time = DateUtil.getNow().getTime();

                    byte[] md5 = MD5Utils.getMessageDigest((username + "soonerLive" + time));
                    String key = new BASE64Encoder().encode(md5);
                    token = username + "," + key + "," + time;
                }
            }

            EPlayerData playerData=new EPlayerData();
            playerData.liveClassroomId = liveClassroomId;
            playerData.customer = customer;
            playerData.validateStr = token;

            playerData.imgUrl = bgImageUrl;

            if (CheckUtil.isEmpty(userEntity)) {
                playerData.name = userEntity.name;
                playerData.imgHead = userEntity.getIcon();
            }


            //下面是写死的
            playerData.loginType= EPlayerLoginType.EPlayerLoginTypeAuthForward;
            playerData.playModel= EPlayerPlayModelType.EPlayerPlayModelTypelLive;

            SDKTeacherUtil.initPushPlayer(context,playerData,newFile);

        } else {
            ToastUtil.showStringToast("不是直播，无法推流");
        }
    }


    public static String getValue(String dateString, String key) {
        String value;
        try {
            String[] key1 = dateString.split(key);
            String[] key2 = key1[1].split("&");
            value = key2[0];
        } catch (Exception e) {
            e.printStackTrace();
            value = "";
        }
        return value;

    }


    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }



    private void initEmpty() {
        empty_view.setVisibility(adapter.getCount() > 0 ? View.GONE : View.VISIBLE);
    }


}
