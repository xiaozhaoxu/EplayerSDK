package cn.nodemedia.ibrightech.eplayersdkproject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ibrightech.eplayer.sdk.common.entity.EPlayerData;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EPlayerLoginType;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EPlayerPlayModelType;
import com.ibrightech.eplayer.sdk.common.entity.other.EplayerNetErrorEntity;
import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.common.util.ConfigUtil;
import com.ibrightech.eplayer.sdk.common.util.DateUtil;
import com.ibrightech.eplayer.sdk.common.util.MD5Utils;
import com.ibrightech.eplayer.sdk.common.util.ToastUtil;
import com.ibrightech.eplayer.sdk.student.util.SDKUtil;
import com.ibrightech.eplayer.sdk.teacher.Entity.LoginInfEntity;
import com.ibrightech.eplayer.sdk.teacher.Entity.TeacherSectionEntity;
import com.ibrightech.eplayer.sdk.teacher.Entity.entityenum.ClassDataTypeEnum;
import com.ibrightech.eplayer.sdk.teacher.activity.UploadPPTActivity;
import com.ibrightech.eplayer.sdk.teacher.net.HttpBaseProtocol;
import com.ibrightech.eplayer.sdk.teacher.net.TeacherSectionProtocol;
import com.ibrightech.eplayer.sdk.teacher.util.ImageUrlUtil;

import java.util.ArrayList;
import java.util.List;

import Decoder.BASE64Encoder;
import cn.nodemedia.ibrightech.eplayersdkproject.entity.RoomBean;
import cn.nodemedia.ibrightech.eplayersdkproject.widget.spinner.NiceSpinner;
import okhttp3.Call;

public class MainActivity extends Activity implements View.OnClickListener{
    public final String roomid="roomid";

    Button bt1,bt2,bt3;
    Context context;
    NiceSpinner nicespinner;
    String liveClassroomId="57cfd5dc7f52c1b14a000334";

    List<RoomBean> roomList=new ArrayList<RoomBean>();
    List<String> dataset=new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.bt1).setOnClickListener(this);
        findViewById(R.id.bt2).setOnClickListener(this);
        findViewById(R.id.bt3).setOnClickListener(this);

        nicespinner= (NiceSpinner) findViewById(R.id.nicespinner);
        initRoomInfo();

        this.context=this;
    }
    public void initRoomInfo(){
        {
            RoomBean bean=new RoomBean("直播：军海测试1","54bf0f799d565c1a1c0001f0");
            roomList.add(bean);
        }
        {
            RoomBean bean=new RoomBean("直播：赵旭测试1","57cfd5dc7f52c1b14a000334");
            roomList.add(bean);
        }
        {
            RoomBean bean=new RoomBean("直播：大师试讲","576bb57a5081bffb1b000ca3");
            roomList.add(bean);
        }
        {
            RoomBean bean=new RoomBean("回看：法语初级A1（第二季）-18","5630a24c3cac246d2d0017e2");
            roomList.add(bean);
        }
        {
            RoomBean bean=new RoomBean("回看：大师第一课","574d0fb14657c4b6410071e3");
            roomList.add(bean);
        }
        {
            RoomBean bean=new RoomBean("回看：阔知小象学院","57eb1daf5081bffb1b01f1bc");
            roomList.add(bean);
        }
        {
            RoomBean bean=new RoomBean("回看：中小综合素质","57a2b0965081bffb1b00cf67");
            roomList.add(bean);
        }
        {
            RoomBean bean=new RoomBean("回看：赵旭测试1","56deaf48bf1ed01a05005607");
            roomList.add(bean);
        }
        {
            RoomBean bean=new RoomBean("回看：赵旭测试2","57e263d95081bffb1b01d327");
            roomList.add(bean);
        }




        String preRoomid=ConfigUtil.getPreferences(roomid,"");
        int selectPostion=0;
        for(int i=0;i<roomList.size();i++){
            RoomBean bean=roomList.get(i);
            dataset.add(bean.getName());
            if(bean.getRoomid().equalsIgnoreCase(preRoomid)){
                selectPostion=i;
            }
        }
        nicespinner.attachDataSource(dataset);
        nicespinner.setSelectedIndex(selectPostion);

    }


    @Override
    public void onClick(View v) {
        RoomBean bean=roomList.get(nicespinner.getSelectedIndex());
        liveClassroomId=bean.getRoomid();
        ConfigUtil.putPreferences(roomid,bean.getRoomid());

        switch (v.getId()){
            case R.id.bt1:{
                EPlayerData playerData=new EPlayerData();

                playerData.liveClassroomId = liveClassroomId;
                playerData.customer = "soooner";
                playerData.playModel= EPlayerPlayModelType.EPlayerPlayModelTypelLive;
                SDKUtil.initPlayer(context,playerData);
                break;
            }
            case R.id.bt3:{
                EPlayerData playerData=new EPlayerData();

                playerData.liveClassroomId =liveClassroomId;
                playerData.customer = "soooner";
                playerData.playModel= EPlayerPlayModelType.EPlayerPlayModelTypePlayback;
                SDKUtil.initPlayer(context,playerData);
                break;
            }
            case R.id.bt2:{
                getTeacherSetction(5719);
                break;
            }
        }
    }

    public void getTeacherSetction(long section_id) {


        LoginInfEntity loginInfEntity =  new LoginInfEntity();
        loginInfEntity.setToken("MTAwODkxNz1hbmRyb2lkfDEwMDg5MTd8MTQ3NzQ0Nzg3NzcwOA==");
        loginInfEntity.setUser_id(1008917);

        TeacherSectionProtocol protocol = new TeacherSectionProtocol(section_id,loginInfEntity);
        protocol.execute(context, new HttpBaseProtocol.CallBack() {
            @Override
            public void onStart(){
            }

            @Override
            public boolean onUseBufferDataAndCancelNetwork(Object object) {
                return false;
            }

            @Override
            public void onSuccess(boolean isSuccess, String msg, Object object) {

                if (!isSuccess) {
                    //todo
                    ToastUtil.showStringToast(context, msg);
                    return;
                } else {
                    TeacherSectionEntity teacherSectionEntity = (TeacherSectionEntity) object;
                    String imageUrl= ImageUrlUtil.getUrl("http://imagetest.unixue.com/unixue/2336/1465632290334.JPG");
                    toNextPage(teacherSectionEntity,imageUrl);
                }

            }


            @Override
            public boolean onFailure(Call call, EplayerNetErrorEntity errorEntity) {
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

    private void toNextPage(TeacherSectionEntity teacherSectionEntity, String bgImageUrl) {
        if (ClassDataTypeEnum.ZHIBO_Type.get_key().equalsIgnoreCase(teacherSectionEntity.getData_type())) {
            String url = teacherSectionEntity.getUrl();
            String liveClassroomId = UploadPPTActivity.getValue(url,  UploadPPTActivity.KEY_LIVECLASSROOMID);
            String customer =  UploadPPTActivity.getValue(url, UploadPPTActivity. KEY_CUSTOMER);
            String p =  UploadPPTActivity.getValue(url, UploadPPTActivity. KEY_P);
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

            EPlayerData playerData = new EPlayerData();
            playerData.liveClassroomId = liveClassroomId;
            playerData.customer = customer;
            playerData.validateStr = token;
            playerData.imgUrl = bgImageUrl;




            //下面是写死的
            playerData.loginType= EPlayerLoginType.EPlayerLoginTypeAuthForward;
            playerData.playModel= EPlayerPlayModelType.EPlayerPlayModelTypelLive;



            SDKUtil.initPushPlayer(context,playerData);

        } else {
            ToastUtil.showStringToast("不是直播，无法推流");
        }
    }
}
