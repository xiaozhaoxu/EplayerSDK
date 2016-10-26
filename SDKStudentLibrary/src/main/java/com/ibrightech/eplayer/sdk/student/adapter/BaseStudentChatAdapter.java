package com.ibrightech.eplayer.sdk.student.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ibrightech.eplayer.sdk.common.engin.EplayerEngin;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerMessageChatType;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.SocketMessage;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.TeacherInfo;
import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.common.util.DeviceUtil;
import com.ibrightech.eplayer.sdk.common.util.TextViewUtils;
import com.ibrightech.eplayer.sdk.student.R;
import com.ibrightech.eplayer.sdk.student.entityenum.ScreenDirEnum;
import com.ibrightech.eplayer.sdk.teacher.adapter.BaseChatAdapter;
import com.ibrightech.eplayer.sdk.teacher.util.ImageUrlUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class BaseStudentChatAdapter extends BaseChatAdapter {
    String  iconurl,teachurl;
    int MAX_ITEM_NUM=Integer.MAX_VALUE;
    ScreenDirEnum screenDirEnum;
    List<SocketMessage> speakAllList=new ArrayList<SocketMessage>();
    List<SocketMessage> speakList=new ArrayList<SocketMessage>();

    public List<String> blackList=new ArrayList<String>();//黒名单
    public BaseStudentChatAdapter(Context ctx) {
        super(ctx);
    }

    public void setTheme(ScreenDirEnum screenDirEnum) {
        this.screenDirEnum = screenDirEnum;
        this.notifyDataSetChanged();
    }

    /**
     *
     * @param bist  列表
     * @param personchatForbid 禁言吗，
     */

    public void updateBlackList(List<String> bist, boolean personchatForbid) {
        if (null == bist ||bist.size()<=0) {
            return;
        }

        if(personchatForbid){
            for(int i=0;i<bist.size();i++){
                blackList.add(bist.get(i));
            }
        }else{
            for(int i=0;i<bist.size();i++){
                String temp=bist.get(i);
                if(blackList.contains(temp)){
                    blackList.remove(temp);
                }

            }
        }
        refresh();
        this.notifyDataSetChanged();
    }

    /**
     * 全体禁言
     *
     * @param allchatForbid
     */
    public void updateBlackList(boolean allchatForbid){
        if(allchatForbid) {
            speakAllList = new ArrayList<SocketMessage>();
            speakList = new ArrayList<SocketMessage>();
            this.notifyDataSetChanged();
        }
    }

    public void refresh(){
        speakList=new ArrayList<SocketMessage>();

        Iterator<SocketMessage> iter = speakAllList.iterator();
        while(iter.hasNext()){
            SocketMessage  socketMessage = iter.next();
            boolean isContains=false;
            for(int k=0;k<blackList.size();k++){
                String userkey=blackList.get(k);
                if(socketMessage.userKey.equals(userkey)){
                    isContains=true;
                    break;
                }
            }
            if(!isContains){
                speakList.add(socketMessage);
            }else{//个人被禁言后，其对应的聊天记录完全删除，本地不保留
                iter.remove();
            }
        }



    }

    public void addSpeakList(List<SocketMessage> list) {
        if(null==list||list.size()<=0){
            return;
        }
        for(int i=0;i<list.size();i++){
            if(speakAllList.size()<MAX_ITEM_NUM){
                speakAllList.add(list.get(i));
            }else{
                speakAllList.remove(0);
                speakAllList.add(list.get(i));
            }
        }
        refresh();


    }

    @Override
    public int getCount() {
        return speakList.size();
    }
    @Override
    public Object getItem(int i) {
        return speakList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getItemView() {
        if(CheckUtil.isEmpty(iconurl)){
            iconurl= EplayerEngin.getInstance().getSessionInfo().userInfo.icon;
            List<TeacherInfo> teacherList=EplayerEngin.getInstance().getSessionInfo().infoData.teacherList;
            if(!CheckUtil.isEmpty(teacherList)){
                teachurl= teacherList.get(0).headImg;
            }
        }
        return View.inflate(ctx, R.layout.adapter_audio_chat, null);
    }

    @Override
    public void initItemView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder(convertView);

        SocketMessage socketMessage = (SocketMessage) getItem(position);
        if (CheckUtil.isEmpty(socketMessage)) {
            return;
        }
        if(screenDirEnum==ScreenDirEnum.HALF_FULL_PORTRAIT_SCREEN){
            holder.rl_item_all.setBackgroundResource(R.color.common_bg);
        }else{
            holder.rl_item_all.setBackgroundResource(R.color.transparent);
        }


        if (socketMessage.userKey.equals(DeviceUtil.getUDID())) {
            //自己发的言，右边显示
            holder.layout_audio_middle.setVisibility(View.GONE);
            holder.layout_audio_left.setVisibility(View.GONE);
            holder.layout_audio_right.setVisibility(View.VISIBLE);
            Glide.with(ctx.getApplicationContext()).load(ImageUrlUtil.getUrl(iconurl))
                    .placeholder(R.drawable.audio_chat_default_icon)
                    .error(R.drawable.audio_chat_default_icon)
                    .into(holder.iv_right_chat_picture);

            holder.tv_right_chat_content.setBackgroundResource(R.drawable.audio_chat_right_bg_2);
            holder.img_right_animation.setBackgroundResource(R.drawable.audio_chat_right_bg_2);
            holder.tv_right_chat_content.setTextColor(ctx.getResources().getColor(R.color.white));
            initViewByContent(socketMessage, holder.tv_right_chat_content,holder.img_right_animation);
        }else if(socketMessage.chatType == EplayerMessageChatType.MessageChatTypeSys){
            //系统的
            holder.layout_audio_middle.setVisibility(View.VISIBLE);
            holder.layout_audio_left.setVisibility(View.GONE);
            holder.layout_audio_right.setVisibility(View.GONE);


            holder.tv_middle_chat_name.setTextColor(ctx.getResources().getColor(R.color.black));
            initViewByContent(socketMessage, holder.tv_middle_chat_content,holder.img_middle_animation);

        }else {
            holder.layout_audio_middle.setVisibility(View.GONE);
            holder.layout_audio_left.setVisibility(View.VISIBLE);
            holder.layout_audio_right.setVisibility(View.GONE);
            Glide.with(ctx.getApplicationContext()).load(ImageUrlUtil.getUrl(teachurl))
                    .placeholder(R.drawable.audio_chat_default_icon)
                    .error(R.drawable.audio_chat_default_icon)
                    .into(holder.iv_left_chat_picture);

             switch (socketMessage.userType){
                 case UserInfoUserTypeTeacher:{
                     holder.tv_left_chat_content.setBackgroundResource(R.drawable.audio_chat_left_bg_2);
                     holder.img_left_animation.setBackgroundResource(R.drawable.audio_chat_left_bg_2);
                     holder.tv_left_chat_content.setTextColor(ctx.getResources().getColor(R.color.white));
                     holder.tv_left_chat_name.setTextColor(ctx.getResources().getColor(R.color.common_teacher_text_color));
                     break;
                 }
                 case UserInfoUserTypeAssist:{
                     holder.tv_left_chat_content.setBackgroundResource(R.drawable.audio_chat_left_bg);
                     holder.img_left_animation.setBackgroundResource(R.drawable.audio_chat_left_bg);
                     holder.tv_left_chat_content.setTextColor(ctx.getResources().getColor(R.color.black));
                     holder.tv_left_chat_name.setTextColor(ctx.getResources().getColor(R.color.common_assist_text_color));
                     break;
                 }
                 default:{
                     holder.tv_left_chat_content.setBackgroundResource(R.drawable.audio_chat_left_bg);
                     holder.img_left_animation.setBackgroundResource(R.drawable.audio_chat_left_bg);
                     holder.tv_left_chat_content.setTextColor(ctx.getResources().getColor(R.color.black));
                     holder.tv_left_chat_name.setTextColor(ctx.getResources().getColor(R.color.black));
                     break;
                 }
             }
            TextViewUtils.setText(holder.tv_left_chat_name,socketMessage.nickname);

            initViewByContent(socketMessage, holder.tv_left_chat_content,holder.img_left_animation);

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
        RelativeLayout rl_item_all;


        RelativeLayout layout_audio_middle;
        TextView tv_middle_chat_name;
        TextView tv_middle_chat_content;
        ImageView img_middle_animation;

        ImageView img_left_animation;
        ImageView img_right_animation;

        ViewHolder(View view) {

            layout_audio_middle= (RelativeLayout) view.findViewById(R.id.layout_audio_middle);
            tv_middle_chat_name= (TextView) view.findViewById(R.id.tv_middle_chat_name);
            tv_middle_chat_content= (TextView) view.findViewById(R.id.tv_middle_chat_content);


            layout_audio_left= (RelativeLayout) view.findViewById(R.id.layout_audio_left);
             iv_left_chat_picture= (ImageView) view.findViewById(R.id.iv_left_chat_picture);
             tv_left_chat_name= (TextView) view.findViewById(R.id.tv_left_chat_name);
             tv_left_chat_content= (TextView) view.findViewById(R.id.tv_left_chat_content);

             iv_right_chat_picture= (ImageView) view.findViewById(R.id.iv_right_chat_picture);
             tv_right_chat_content= (TextView) view.findViewById(R.id.tv_right_chat_content);
             layout_audio_right= (RelativeLayout) view.findViewById(R.id.layout_audio_right);

            img_left_animation= (ImageView) view.findViewById(R.id.img_left_animation);
            img_right_animation= (ImageView) view.findViewById(R.id.img_right_animation);
            img_middle_animation= (ImageView) view.findViewById(R.id.img_middle_animation);
            rl_item_all= (RelativeLayout) view.findViewById(R.id.rl_item_all);
        }
    }

}
