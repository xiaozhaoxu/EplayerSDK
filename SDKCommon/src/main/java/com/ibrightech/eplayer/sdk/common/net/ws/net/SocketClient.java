package com.ibrightech.eplayer.sdk.common.net.ws.net;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import javax.net.SocketFactory;
import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SocketClient {
    private static final String TAG = SocketClient.class.getSimpleName();

    private static final int HEADER_LEN = 4;

    private URI mURI;
    protected Listener mListener;
    protected Socket mSocket;
    private Thread mThread;
    private HandlerThread mHandlerThread;
    protected Handler mHandler;
    private String cookieStr;
    private boolean mConnected;

    protected final Object mSendLock = new Object();


    public SocketClient(URI uri, Listener listener, String cookieStr) {
        mURI = uri;
        mListener = listener;
        this.cookieStr = cookieStr;
        mConnected = false;

        mHandlerThread = new HandlerThread("tcp-tok-thread");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
    }

    public Listener getListener() {
        return mListener;
    }

    public void connect() {
        if (mThread != null && mThread.isAlive()) {
            return;
        }

        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    mSocket = SocketFactory.getDefault().createSocket(mURI.getHost(), mURI.getPort());
                    mSocket.setTcpNoDelay(true);
                    Log.i(TAG, "tcp no delay: " + mSocket.getTcpNoDelay());

                    OutputStream os = mSocket.getOutputStream();

                    byte[] b = cookieStr.getBytes("UTF-8");

                    os.write(headerData(b));
                    os.write(b);
                    os.flush();

                    mListener.onConnect();
                    mConnected = true;


                    HappyDataInputStream stream = new HappyDataInputStream(mSocket.getInputStream());


                    while (true) {
                        if (stream.available() == -1) break;

                        b = stream.readBytes(HEADER_LEN);
                        int len = ByteBuffer.wrap(b).order(ByteOrder.BIG_ENDIAN).getInt();

                        if (len <= 0) break;

                        b = stream.readBytes(len);
                        mListener.onMessage(b);
                    }
                    mListener.onDisconnect(0, "EOF");

                } catch (EOFException ex) {
                    Log.d(TAG, "WebSocket EOF!", ex);
                    mListener.onDisconnect(0, "EOF");
                    mConnected = false;

                } catch (Exception ex) {
                    mListener.onError(ex);
                }
            }
        });
        mThread.start();
    }

    public void disconnect() {
        if (mSocket != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mSocket != null) {
                        try {
                            mSocket.close();
                        } catch (IOException ex) {
                            Log.d(TAG, "Error while disconnecting", ex);
                            mListener.onError(ex);
                        }
                        mSocket = null;
                    }
                    mConnected = false;
                }
            });
        }
    }

    public byte[] headerData(byte[] data) {
        ByteBuffer bb = ByteBuffer.allocate(HEADER_LEN).order(ByteOrder.BIG_ENDIAN);
        bb.putInt(data.length);
        return bb.array();
    }

    public void send(byte[] data, Ws.SendCallback cb) {
        sendFrame(data, cb);
    }

    public boolean isConnected() {
        return mConnected;
    }


    void sendFrame(final byte[] frame, final Ws.SendCallback cb) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (mSendLock) {
                        OutputStream outputStream = mSocket.getOutputStream();
                        outputStream.write(headerData(frame));
                        outputStream.write(frame);
                        outputStream.flush();
                        cb.onSent();
                    }
                } catch (IOException e) {
                    cb.onError(e);
                    mListener.onError(e);
                }
            }
        });
    }

    public interface Listener {
        public void onConnect();

        public void onMessage(byte[] data);

        public void onDisconnect(int code, String reason);

        public void onError(Exception error);
    }

    public static class HappyDataInputStream extends DataInputStream {
        public HappyDataInputStream(InputStream in) {
            super(in);
        }

        public byte[] readBytes(int length) throws IOException {
            byte[] buffer = new byte[length];

            int total = 0;

            while (total < length) {
                int count = read(buffer, total, length - total);
                if (count == -1) {
                    break;
                }
                total += count;
            }

            if (total != length) {
                throw new IOException(String.format("Read wrong number of bytes. Got: %s, Expected: %s.", total, length));
            }

            return buffer;
        }
    }

}