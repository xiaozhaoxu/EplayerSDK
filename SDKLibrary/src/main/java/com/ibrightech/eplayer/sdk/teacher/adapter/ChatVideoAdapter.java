package com.ibrightech.eplayer.sdk.teacher.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.SocketMessage;
import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.common.util.TextViewUtils;
import com.ibrightech.eplayer.sdk.teacher.R;
import com.ibrightech.eplayer.sdk.teacher.activity.LivePushActivity;
import com.ibrightech.eplayer.sdk.teacher.util.ImageUrlUtil;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by zhaoxu2014 on 16/5/28.
 * 视频的聊天控制器
 */
public class ChatVideoAdapter extends BaseChatAdapter {
    String iconurl;
    public ChatVideoAdapter(Context ctx) {
        super(ctx);
    }

    @Override
    public View getItemView() {
        if(CheckUtil.isEmpty(iconurl)) {
            iconurl = ((LivePushActivity) ctx).getUserIconUrl();
        }
        return View.inflate(ctx, R.layout.adapter_chat_live_push_video, null);
    }

    @Override
    public void initItemView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder(convertView);

        SocketMessage socketMessage = (SocketMessage) getItem(position);
        if (CheckUtil.isEmpty(socketMessage)) {
            return;
        }
        if (isTeacherType(socketMessage)){

            Glide.with(ctx.getApplicationContext()).load(ImageUrlUtil.getUrl(iconurl))
                    .bitmapTransform(new CropCircleTransformation(ctx.getApplicationContext()))
                    .placeholder(R.drawable.chat_video_default_icon2)
                    .error(R.drawable.chat_video_default_icon2)
                    .into(holder.iv_chat_picture);
        }else {
            holder.iv_chat_picture.setImageResource(R.drawable.chat_video_default_icon2);
        }

        holder.tv_chat_name.setTextColor(getNickNameColor(socketMessage));
        TextViewUtils.setText(holder.tv_chat_name, String.format(ctx.getString(R.string.chat_video_name), getChatName(socketMessage)));

        initViewByContent(socketMessage, holder.tv_chat_content,holder.img_animation);

    }

    static class ViewHolder {
        ImageView iv_chat_picture;
        ImageView img_animation;

        TextView tv_chat_content;
        TextView tv_chat_name;

        ViewHolder(View view) {
            iv_chat_picture = (ImageView) view.findViewById(R.id.iv_chat_picture);
            img_animation = (ImageView) view.findViewById(R.id.img_animation);

            tv_chat_content = (TextView) view.findViewById(R.id.tv_chat_content);
            tv_chat_name = (TextView) view.findViewById(R.id.tv_chat_name);

        }
    }
}
