package com.ibrightech.eplayer.sdk.teacher.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ibrightech.eplayer.sdk.common.entity.Asset;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerConstant;
import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.common.util.FileUtils;
import com.ibrightech.eplayer.sdk.common.util.NumberUtil;
import com.ibrightech.eplayer.sdk.common.util.TextViewUtils;
import com.ibrightech.eplayer.sdk.teacher.R;
import com.ibrightech.eplayer.sdk.teacher.ui.refreshlayout.BGASwipeItemLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CourseWareAdapter extends LibraryBaseAdapter {
    int courseWareId;
    int selectedPostion=-1;
    OnItemClickListener listener;
    boolean isUploaPPT=false;

    public int getSelectedPostion() {
        return selectedPostion;
    }

    public void setUploaPPT(boolean uploaPPT) {
        isUploaPPT = uploaPPT;
    }

    private List<BGASwipeItemLayout> mOpenedSil = new ArrayList<BGASwipeItemLayout>();
    public CourseWareAdapter(Context ctx) {
        super(ctx);
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public View getItemView() {
        return View.inflate(ctx, R.layout.item_dialog_select_courseware, null);
    }

    public void setSelectedCourseWareId(int id){
        courseWareId = id;
    }

    @Override
    public void initItemView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder = new ViewHolder(convertView);


        viewHolder.layout_del_course_ware.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CheckUtil.isEmpty(listener)) {
                    listener.deleteAsset(viewHolder.layout_del_course_ware,position);
                }
            }
        });
        viewHolder.layout_item_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPostion=position;
                notifyDataSetChanged();
                if (!CheckUtil.isEmpty(listener)) {
                    listener.onItemClick(position);
                }
            }
        });



        viewHolder.course_ware_item_swipe_root.setDelegate(new BGASwipeItemLayout.BGASwipeItemLayoutDelegate() {
            @Override
            public void onBGASwipeItemLayoutOpened(BGASwipeItemLayout swipeItemLayout) {
                closeOpenedSwipeItemLayoutWithAnim();
                mOpenedSil.add(swipeItemLayout);
            }

            @Override
            public void onBGASwipeItemLayoutClosed(BGASwipeItemLayout swipeItemLayout) {
                mOpenedSil.remove(swipeItemLayout);
            }

            @Override
            public void onBGASwipeItemLayoutStartOpen(BGASwipeItemLayout swipeItemLayout) {
                closeOpenedSwipeItemLayoutWithAnim();
            }
        });

        if (!isUploaPPT) {
            Asset entity = (Asset) libraryAdapterList.get(position);
            TextViewUtils.setText(viewHolder.tv_course_ware_name, entity.name);
            viewHolder.iv_course_ware_icon.setImageResource(entity.type.equalsIgnoreCase(EplayerConstant.KEY_PPT) ? R.drawable.ppt : R.drawable.word);
            viewHolder.iv_course_ware_select.setSelected(NumberUtil.parseInt(entity.id, 0) == courseWareId);
        } else {

            viewHolder.iv_course_ware_select.setSelected(selectedPostion == position);

            File file = (File) libraryAdapterList.get(position);
            TextViewUtils.setText(viewHolder.tv_course_ware_name, file.getName());
            boolean isPPT = FileUtils.fileIsPPT(file.getName());

            viewHolder.iv_course_ware_icon.setImageResource(isPPT ? R.drawable.ppt : R.drawable.word);

        }
    }

    static class ViewHolder {
        RelativeLayout layout_item_class;
        LinearLayout layout_del_course_ware;
        ImageView iv_course_ware_select;
        ImageView iv_course_ware_icon;
        TextView tv_course_ware_name;
        BGASwipeItemLayout course_ware_item_swipe_root;

        ViewHolder(View view) {
            layout_item_class = (RelativeLayout) view.findViewById(R.id.layout_item_class);
            layout_del_course_ware = (LinearLayout) view.findViewById(R.id.layout_del_course_ware);
            iv_course_ware_select = (ImageView) view.findViewById(R.id.iv_course_ware_select);
            iv_course_ware_icon = (ImageView) view.findViewById(R.id.iv_course_ware_icon);
            tv_course_ware_name = (TextView) view.findViewById(R.id.tv_course_ware_name);
            course_ware_item_swipe_root = (BGASwipeItemLayout) view.findViewById(R.id.course_ware_item_swipe_root);

        }
    }

    public void closeOpenedSwipeItemLayoutWithAnim() {
        for (BGASwipeItemLayout sil : mOpenedSil) {
            sil.closeWithAnim();
        }
        mOpenedSil.clear();
    }

    public void closeOpenedSwipeItemLayout() {
        for (BGASwipeItemLayout sil : mOpenedSil) {
            sil.close();
        }
        mOpenedSil.clear();
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
        void deleteAsset(View view,int position);
    }
}
