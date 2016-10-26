package com.ibrightech.eplayer.sdk.teacher.dialog;

import android.app.Activity;

import com.ibrightech.eplayer.sdk.common.entity.Asset;

import java.io.File;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by zhaoxu2014 on 15/9/1.
 */
public class SDKDialogUtil {

    private static SDKDialogUtil instance1;

    public static SDKDialogUtil getInstance() {
        if (instance1 == null) {
            instance1 = new SDKDialogUtil();
        }
        return instance1;
    }


    public LoadingDialog showLoadingDialog(Activity context){
        LoadingDialog dialog=new LoadingDialog(context);
       return dialog;
    }

    public ExitHintDialog showExitHintDialog(Activity context, String title, String desc, boolean isShowCancelButton, ExitHintDialog.ClickListener clickListener){
        ExitHintDialog dialog= new ExitHintDialog(context,title,desc, isShowCancelButton,clickListener);
        dialog.show();
        return dialog;
    }

    public SelectCoursewareDialog showSelectCourseWareDialog(Activity context, int id, TreeMap<String, Asset> assetTreeMap, SelectCoursewareDialog.OnItemClickListener listener){
        SelectCoursewareDialog dialog= new SelectCoursewareDialog(context,id,assetTreeMap,listener);
        dialog.show();
        return dialog;
    }
    public SelectCoursewareDialog showUploadDialog(Activity context, List<File> filelist, SelectCoursewareDialog.OnItemFileClickListener fileClickListener){
        SelectCoursewareDialog dialog= new SelectCoursewareDialog(context,filelist,fileClickListener);
        dialog.show();
        return dialog;
    }

    public HelpDialog showHelpDialog(Activity context, int withd){
        HelpDialog dialog= new HelpDialog(context,withd);
        dialog.show();
        return dialog;
    }


    public UpLoadDialog showUploadProgressDialog(Activity context,String title,String desc,boolean canCel,boolean showProgress){
        UpLoadDialog dialog= new UpLoadDialog(context,title,desc,canCel,showProgress);
        dialog.show();
        return dialog;
    }
}
