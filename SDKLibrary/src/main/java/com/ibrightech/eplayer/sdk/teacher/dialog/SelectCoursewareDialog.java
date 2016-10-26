package com.ibrightech.eplayer.sdk.teacher.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ibrightech.eplayer.sdk.common.engin.EplayerEngin;
import com.ibrightech.eplayer.sdk.common.entity.Asset;
import com.ibrightech.eplayer.sdk.common.entity.other.EplayerNetErrorEntity;
import com.ibrightech.eplayer.sdk.common.net.http.BaseOkHttpProtocol;
import com.ibrightech.eplayer.sdk.common.net.http.DeleteCourseWareProtocol;
import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.common.util.FileUtils;
import com.ibrightech.eplayer.sdk.common.util.TextViewUtils;
import com.ibrightech.eplayer.sdk.common.util.ToastUtil;
import com.ibrightech.eplayer.sdk.teacher.R;
import com.ibrightech.eplayer.sdk.teacher.activity.SDKBaseActivity;
import com.ibrightech.eplayer.sdk.teacher.adapter.CourseWareAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import okhttp3.Call;

public class SelectCoursewareDialog extends Dialog {
    LinearLayout li_back;
    LinearLayout li_upload;
    ListView lv_course_ware;
    TextView tv_common_title;
    TextView tv_next;
    TextView tv_right;
    View li_tilte_right;

    CourseWareAdapter adapter;
    OnItemClickListener listener;
    OnItemFileClickListener fileClickListener;
    TreeMap<String, Asset>assetTreeMap;
    Context context;


    int id;
    DeleteCourseWareProtocol deleteCourseWareProtocol;
    List<File>filelist=null;

    public SelectCoursewareDialog(Context context,int id,TreeMap<String, Asset>assetTreeMap,OnItemClickListener listener) {
        super(context, R.style.sdk_dialog_untran);
        this.listener = listener;
        this.id = id;
        this.assetTreeMap = assetTreeMap;
        init(context);
    }

    public SelectCoursewareDialog(Context context, List<File>filelist, OnItemFileClickListener fileClickListener) {
        super(context, R.style.sdk_dialog_untran);
        this.filelist = filelist;
        this.fileClickListener=fileClickListener;
        init(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(params);
    }

    private void init(Context context) {
        this.context = context;
        View rootView = View.inflate(context, R.layout.dialog_select_courseware, null);
        tv_common_title= (TextView) rootView.findViewById(R.id.tv_common_title);
        li_back= (LinearLayout) rootView.findViewById(R.id.li_back);
        lv_course_ware= (ListView) rootView.findViewById(R.id.lv_course_ware);
        li_upload= (LinearLayout) rootView.findViewById(R.id.li_upload);
        tv_next= (TextView) rootView.findViewById(R.id.tv_next);
        li_tilte_right= rootView.findViewById(R.id.li_tilte_right);
        tv_right= (TextView) rootView.findViewById(R.id.tv_right);
        setContentView(rootView);
        adapter = new CourseWareAdapter(context);
        lv_course_ware.setAdapter(adapter);
        setListener();
        if(null==filelist){
            li_upload.setVisibility(View.GONE);
            TextViewUtils.setText(tv_common_title,R.string.select_course_ware);
            if(!CheckUtil.isEmpty(assetTreeMap)) {
                adapter.setDatas(new ArrayList<Asset>(assetTreeMap.values()));
            }

            adapter.setSelectedCourseWareId(id);
        }else{//上传ppt
            if (!CheckUtil.isEmpty(filelist)) {
                li_upload.setVisibility(View.VISIBLE);
            } else {
                li_upload.setVisibility(View.GONE);
            }
            TextViewUtils.setText(tv_common_title,R.string.upload_course_ware);
            adapter.setUploaPPT(true);
            adapter.setDatas( filelist);

            li_tilte_right.setVisibility(View.VISIBLE);
            TextViewUtils.setText(tv_right,R.string.help_upload_title);

        }


    }

    private void close() {
        if (isShowing()) {
            dismiss();
        }
        if(!CheckUtil.isEmpty(deleteCourseWareProtocol)){
            deleteCourseWareProtocol.cancel();
        }
    }

    private void setListener() {
        li_tilte_right.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SDKBaseActivity act= (SDKBaseActivity)context;
                SDKDialogUtil.getInstance().showHelpDialog(act,act.SCREEN_WIDTH);

            }
        });

        tv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CheckUtil.isEmpty(fileClickListener)) {
                    int selectedPostion =adapter.getSelectedPostion();
                    if(selectedPostion<0){
                        ToastUtil.showStringToast("您还没有选择要上传的课件");
                        return;
                    }
                    fileClickListener.onItemClick((File) adapter.getItem(selectedPostion));
                    close();
                }
            }
        });

        adapter.setListener(new CourseWareAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                if(null==filelist){
                    close();
                    if (!CheckUtil.isEmpty(listener)) {
                        listener.onItemClick((Asset) adapter.getItem(position));
                    }
                }else{

                }

            }

            @Override
            public void deleteAsset(View childView ,int position) {
                if(null==filelist) {
                    deleteCourseWare(childView, position, (Asset) adapter.getItem(position));
                }else{
                    if(position==0){
                        position=-1;
                    }
                    File file= (File) adapter.getItem(position);
                    FileUtils.deleteQuietly(file);
                    adapter.removeItem(position);
                    adapter.closeOpenedSwipeItemLayout();
                }
            }
        });


        li_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
    }

    OnKeyListener keylistener = new DialogInterface.OnKeyListener(){
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode== KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    } ;

    private void deleteCourseWare(final View childView, final int position, final Asset asset){
         deleteCourseWareProtocol = new DeleteCourseWareProtocol(EplayerEngin.getInstance().getSessionInfo().userInfo.liveClassroomId, id);
        deleteCourseWareProtocol.execute(context, new BaseOkHttpProtocol.CallBack() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int errorCode, String msg, Object o) {
                if (errorCode == 0){
                    adapter.removeItem(position);
                    adapter.closeOpenedSwipeItemLayout();
                    if (!CheckUtil.isEmpty(listener)){
                        listener.deleteAsset(asset);
                    }
                }else {
                    ToastUtil.showStringToast(msg);
                }
            }

            @Override
            public boolean onFailure(Call call, EplayerNetErrorEntity errorEntity) {

                childView.setClickable(true);
                adapter.closeOpenedSwipeItemLayout();
                return true;
            }

            @Override
            public void onUpProgress(long l, long l1, float v, long l2) {

            }
        });


    }


    public interface OnItemClickListener{
        void onItemClick(Asset asset);
        void deleteAsset(Asset asset);
    }
    public interface OnItemFileClickListener{
        void onItemClick(File asset);
    }
}
