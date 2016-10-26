package com.ibrightech.eplayer.sdk.common.net.ws.pb;

import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ibrightech.eplayer.sdk.common.net.ws.vo.Cmt;
import com.ibrightech.eplayer.sdk.common.net.ws.vo.Msg;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Converter between vo and pb.*
 */
public class ConverterMsg {
    public static final String TAG = ConverterMsg.class.getSimpleName();

    public static Msg msg(Wraper.Msg msg) {
        Msg m = new Msg();
        //TODO

        try {
            binaryToLocal(m, msg);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return m;
    }

    public static Cmt cmt(Wraper.Cmd cmd) {
        Cmt m = new Cmt();
        try {
            cmdToLocal(m, cmd);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return m;
    }

    private static void binaryToLocal(Msg msg, Wraper.Msg msgOrgin) throws UnsupportedEncodingException, InvalidProtocolBufferException {
        switch (msgOrgin.getTp()) {
            case CLIENT_EVENT: {

                Log.i(TAG, "<<<接收到的Msg数据<<<:" + msgOrgin.getEvent());
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(msgOrgin.getEvent());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                msg.setType(Msg.MESSAGE_TYPE_CLIENT_EVENT);
                msg.setEvent(jsonObject);
                break;
            }
            case USER_COUNT: {
                Log.i(TAG, "<<<接收到的人数数据<<<:" + msgOrgin.getEvent());
                msg.setType(Msg.MESSAGE_TYPE_USER_COUNT);
                msg.setEvent(msgOrgin.getEvent());
                msg.setUserCount(msgOrgin.getUserCount());
                break;
            }

        }
    }

    private static void cmdToLocal(Cmt msg, Wraper.Cmd cmdOrgin) throws UnsupportedEncodingException, InvalidProtocolBufferException {
        switch (cmdOrgin.getTp()) {
            case UNKNOWN: {

                break;
            }
            case PING: {
                Log.i(TAG, " ping........ ");

                break;
            }
            case HANDSHAKE: {

                msg.setType(Cmt.CMT_TYPE_HANDSHAKE);
                msg.setTxt(cmdOrgin.getTxt());
                Log.i(TAG, " handshake........ ");
                Log.i(TAG, "<<<接收到的Cmd数据<<<:" + cmdOrgin.getTxt());

                break;
            }

        }
    }


    public static Wraper.Msg msg(Msg msg) {
        Wraper.Msg.Builder builder = Wraper.Msg.newBuilder();

        switch (msg.getType()) {
            case Msg.MESSAGE_TYPE_CLIENT_EVENT: {
                JSONObject jsonObject = (JSONObject) msg.getEvent();
                String event = jsonObject.toString();

                Log.i(TAG, ">>>发送请求的数据>>>:" + event);
                builder.setEvent(event);
                builder.setTp(Wraper.Msg.Type.CLIENT_EVENT);
                break;
            }
            case Msg.MESSAGE_TYPE_USER_COUNT: {
                Log.i(TAG, ">>>发送获取人数请求>>>:" + msg.getEvent());
                builder.setEvent((String) msg.getEvent());
                builder.setTp(Wraper.Msg.Type.USER_COUNT);
                break;
            }

        }


        return builder.build();
    }
}
