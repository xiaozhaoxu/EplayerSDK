package com.ibrightech.eplayer.sdk.teacher.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ibrightech.eplayer.sdk.common.util.TextViewUtils;
import com.ibrightech.eplayer.sdk.teacher.Entity.TeacherCourseEntity;
import com.ibrightech.eplayer.sdk.teacher.util.ImageUrlUtil;
import com.ibrightech.eplayer.sdk.teacher.R;

import com.ibrightech.eplayer.sdk.teacher.ui.imageconfig.ViewSimpleTarget;


public class UploadPPTAdapter extends LibraryBaseAdapter {

    int selectPostion=0;

    public UploadPPTAdapter(Context ctx) {
        super(ctx);

    }

    public int getSelectPostion() {
        return selectPostion;
    }

    public void setSelectPostion(int selectPostion) {
            this.selectPostion = selectPostion;
            notifyDataSetChanged();

    }

    @Override
    public View getItemView() {
        return View.inflate(ctx, R.layout.adapter_upload_ppt_item,null);
    }

    @Override
    public void initItemView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder=new ViewHolder(convertView);
        TeacherCourseEntity entity= (TeacherCourseEntity) getItem(position);
        TextViewUtils.setText(viewHolder.tv_name,entity.getTitle());
        TextViewUtils.setText(viewHolder.tv_desc,entity.getSection_title());
        String timeDesc=entity.getSectionTime();
        TextViewUtils.setText(viewHolder.tv_time,timeDesc);


        Glide.with(ctx.getApplicationContext()).load(ImageUrlUtil.getUrl(entity.getUrl()))
                .placeholder(R.drawable.default_icon)
                .error(R.drawable.default_icon)
                .into(new ViewSimpleTarget(viewHolder.image_item_icon));

        viewHolder.iv_course_ware_select.setSelected(selectPostion==position);

        convertView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                setSelectPostion(position);
            }
        });
    }

    static class ViewHolder {
        TextView tv_name;
        TextView tv_desc;
        TextView tv_time;
        ImageView iv_course_ware_select;
        ImageView image_item_icon;


        public ViewHolder(View view) {
             this.tv_name= (TextView) view.findViewById(R.id.tv_name);
             tv_desc= (TextView) view.findViewById(R.id.tv_desc);
             tv_time= (TextView) view.findViewById(R.id.tv_time);
             iv_course_ware_select= (ImageView) view.findViewById(R.id.iv_course_ware_select);
             image_item_icon= (ImageView) view.findViewById(R.id.image_item_icon);
        }
    }


}
