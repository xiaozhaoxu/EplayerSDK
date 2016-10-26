package com.ibrightech.eplayer.sdk.student.dialog;

import android.app.Activity;
import android.content.Context;

import com.ibrightech.eplayer.sdk.common.entity.SessionData.VoteMsgInfo;
import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.teacher.dialog.SDKDialogUtil;

/**
 * Created by zhaoxu2014 on 16/9/5.
 */
public class SDKStudentDialogUtil extends SDKDialogUtil {
    private static SDKStudentDialogUtil instance1;

    SocialQuestionDialog socialQuestionDialog;
    public static SDKStudentDialogUtil getInstance() {
        if (instance1 == null) {
            instance1 = new SDKStudentDialogUtil();
        }
        return instance1;
    }

    public QuestionDialog showQuestionDialog(Context context){
        QuestionDialog dialog=new QuestionDialog(context);
        dialog.show();
        return dialog;
    }

    public void showSocialQuestionDialog(Activity context,VoteMsgInfo msgInfo){
        if(CheckUtil.isEmpty(socialQuestionDialog)){
            socialQuestionDialog=new SocialQuestionDialog(context);
        }
        socialQuestionDialog.voteReq(msgInfo);

    }
}
