package com.ibrightech.eplayer.sdk.teacher.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.SocketMessage;
import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.teacher.R;
import com.ibrightech.eplayer.sdk.teacher.activity.LivePushActivity;
import com.ibrightech.eplayer.sdk.teacher.util.ImageUrlUtil;

/**
 * Created by zhaoxu2014 on 16/5/28.
 * 音频的聊天控制器
 */
public class ChatAudioAdapter extends BaseChatAdapter {
    String iconurl;

    public ChatAudioAdapter(Context ctx) {
        super(ctx);

    }

    @Override
    public View getItemView() {
        if(CheckUtil.isEmpty(iconurl)) {
            iconurl = ((LivePushActivity) ctx).getUserIconUrl();
        }
        return View.inflate(ctx, R.layout.adapter_chat_live_push_audio, null);
    }

    @Override
    public void initItemView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder(convertView);

        SocketMessage socketMessage = (SocketMessage) getItem(position);
        if (CheckUtil.isEmpty(socketMessage)) {
            return;
        }
        if (isTeacherType(socketMessage)) {

            Glide.with(ctx.getApplicationContext()).load(ImageUrlUtil.getUrl(iconurl))
                    .placeholder(R.drawable.audio_chat_default_icon)
                    .error(R.drawable.audio_chat_default_icon)
                    .into(holder.iv_right_chat_picture);



            holder.layout_audio_left.setVisibility(View.GONE);
            holder.layout_audio_right.setVisibility(View.VISIBLE);

            initViewByContent(socketMessage, holder.tv_right_chat_content,holder.img_right_animation);
        } else {
            holder.layout_audio_left.setVisibility(View.VISIBLE);
            holder.layout_audio_right.setVisibility(View.GONE);
            holder.iv_left_chat_picture.setImageResource(R.drawable.audio_chat_default_icon);


            holder.tv_left_chat_content.setText(getExpressionString(ctx, socketMessage.content));
            initViewByContent(socketMessage, holder.tv_left_chat_name,holder.img_left_animation);
        }

    }


    static class ViewHolder {
        RelativeLayout layout_audio_left;
        ImageView iv_left_chat_picture;
        TextView tv_left_chat_name;
        TextView tv_left_chat_content;

        ImageView iv_right_chat_picture;
        TextView tv_right_chat_content;
        RelativeLayout layout_audio_right;


        ImageView img_left_animation;
        ImageView img_right_animation;

        ViewHolder(View view) {
            layout_audio_left= (RelativeLayout) view.findViewById(R.id.layout_audio_left);
             iv_left_chat_picture= (ImageView) view.findViewById(R.id.iv_left_chat_picture);
             tv_left_chat_name= (TextView) view.findViewById(R.id.tv_left_chat_name);
             tv_left_chat_content= (TextView) view.findViewById(R.id.tv_left_chat_content);

             iv_right_chat_picture= (ImageView) view.findViewById(R.id.iv_right_chat_picture);
             tv_right_chat_content= (TextView) view.findViewById(R.id.tv_right_chat_content);
             layout_audio_right= (RelativeLayout) view.findViewById(R.id.layout_audio_right);

            img_left_animation= (ImageView) view.findViewById(R.id.img_left_animation);
            img_right_animation= (ImageView) view.findViewById(R.id.img_right_animation);
        }
    }

}
