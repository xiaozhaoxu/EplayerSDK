package com.ibrightech.eplayer.sdk.student.util;

import android.content.Context;
import android.content.Intent;

import com.ibrightech.eplayer.sdk.common.engin.EplayerEngin;
import com.ibrightech.eplayer.sdk.common.entity.EPlayerData;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EPlayerPlayModelType;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerUserInfoUserType;
import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.student.activity.LiveCourseActivity;
import com.ibrightech.eplayer.sdk.student.activity.PlaybackCourseActivity;
import com.ibrightech.eplayer.sdk.teacher.activity.SDKBaseActivity;
import com.ibrightech.eplayer.sdk.teacher.util.SDKTeacherUtil;

/**
 * Created by zhaoxu2014 on 16/8/30.
 */
public class SDKUtil extends SDKTeacherUtil {
    /*
   * 启动新的Activity
   */
    public static void initPlayer(Context context, EPlayerData playerData) {
        if(!CheckUtil.isEmpty(playerData)){
            playerData.usertype= EplayerUserInfoUserType.UserInfoUserTypeStudent;
        }

        EplayerEngin.initInstance(context,playerData);

        if(playerData.playModel== EPlayerPlayModelType.EPlayerPlayModelTypePlayback){
            //todo
            Intent intent = new Intent(context, PlaybackCourseActivity.class);
            intent.putExtra(SDKBaseActivity.KEY_EPLAY_DATA, playerData);
            context.startActivity(intent);
        }else{
            Intent intent = new Intent(context, LiveCourseActivity.class);
            intent.putExtra(SDKBaseActivity.KEY_EPLAY_DATA, playerData);
            context.startActivity(intent);
        }



    }
}
