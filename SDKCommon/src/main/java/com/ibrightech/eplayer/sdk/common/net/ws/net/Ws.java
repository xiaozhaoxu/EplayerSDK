package com.ibrightech.eplayer.sdk.common.net.ws.net;

import android.util.Log;

import com.ibrightech.eplayer.sdk.common.config.EplayerSetting;
import com.ibrightech.eplayer.sdk.common.net.ws.event.WsConnectedEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.WsConnectingEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.WsDisConnectedEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.WsErrorEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.WsOnBinaryEvent;

import org.greenrobot.eventbus.EventBus;

import java.net.URI;

/**
 * web socket client
 */
public class Ws {

    final static EventBus bus = EventBus.getDefault();

    public interface SendCallback {
        void onSent();

        void onError(Exception e);
    }

    private static SocketClient client;

    private static final String TAG = Ws.class.getSimpleName();

    public static SocketClient getClient() {
        return client;
    }


    public static void init(boolean force) {

        if (!force && client != null) {
            return;
        }
        client = new SocketClient(URI.create(EplayerSetting.getInstance().socket_host), new SocketClient.Listener() {
            @Override
            public void onConnect() {
                Log.i(TAG, "connected");
                bus.post(new WsConnectedEvent());
            }

            @Override
            public void onMessage(byte[] data) {
                Log.i(TAG, "binary message");
                bus.post(new WsOnBinaryEvent(data));
            }

            @Override
            public void onDisconnect(int code, String reason) {
                Log.i(TAG, "disconnected");
                bus.post(new WsDisConnectedEvent(code, reason));
            }

            @Override
            public void onError(Exception error) {
                Log.i(TAG, "err", error);
                bus.post(new WsErrorEvent(error));
            }
        }, "");
    }

    //TODO 外部调用
    public static void connect() {
        Log.i(TAG, "connecting");
        if (client == null) {
            bus.post(new WsConnectingEvent());
            init(false);
            client.connect();
        } else {
            if (!client.isConnected()) {
                bus.post(new WsConnectingEvent());
            }
            client.connect();
        }
    }

    public static void disconnect() {
        Log.i(TAG, "disconnecting");
        if (client != null) {
            client.disconnect();
            //clear client
            client = null;
        }
    }

    public static void send(byte[] b, SendCallback cb) {
        Log.i(TAG, "sending binary");
        if (null != client) {
            client.connect();
            if (!client.isConnected()) {
                Log.i(TAG, "ignore for disconnected ws");
                cb.onError(new Exception("socket disconnected"));
                return;
            }
            client.send(b, cb);
        }else{
            Log.i(TAG, "ignore for client is null");
            cb.onError(new Exception("socket disconnected"));
            return;
        }
    }

}
