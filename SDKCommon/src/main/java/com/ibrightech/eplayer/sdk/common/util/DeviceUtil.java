package com.ibrightech.eplayer.sdk.common.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.ibrightech.eplayer.sdk.common.config.EplayerConfigBuilder;

import org.apache.http.conn.util.InetAddressUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.UUID;

/**
 * Created by zhaoxu2014 on 16/8/26.
 */
public class DeviceUtil {
    private static final String TAG = DeviceUtil.class.getSimpleName();

    public static final int DEFAULT_DISPLAY_WIDTH = 320;
    public static final int DEFAULT_DISPLAY_HEIGHT = 480;

    private static String ua =null;

    public static DisplayMetrics displayMetrics;
    public static final int DEVICE_TYPE_PHONE = 0;
    public static final int DEVICE_TYPE_PAD = 1;


    /**
     * 动态判断当前手机是Phone还是Pad
     * @return int
     */
    public static int getDeviceType(Context context){
        displayMetrics = context.getResources().getDisplayMetrics();
        int densityDPI = displayMetrics.densityDpi; // 屏幕密度（每寸像素：120/160/240/320）
        int height = displayMetrics.heightPixels;   // 屏幕的高
        int width = displayMetrics.widthPixels;     // 屏幕的宽
        // 计算出屏幕的尺寸
        float displaySize = (float) (Math.sqrt(Math.pow(height, 2) + Math.pow(width, 2)) / densityDPI);
//        Log.d(TAG, "------The Device Display Size-------:" + displaySize + "--width:"+width + "--height:"+height);
        if (displaySize < 5.5) {
            // phone
            LogUtil.d("DeviceType phone");
            return DEVICE_TYPE_PHONE;
        } else {
            // pad
            LogUtil.d("DeviceType pad");
            return  DEVICE_TYPE_PAD;
        }
    }



    /**
     * 动态判断当前手机是Phone还是Pad
     * @return int
     */
    public static float getDeviceWidthHeightRatio(Context context){
        displayMetrics = context.getResources().getDisplayMetrics();
        float height = displayMetrics.heightPixels * 1.0f;   // 屏幕的高
        float width = displayMetrics.widthPixels * 1.0f;     // 屏幕的宽

        return width/height;
    }

    public static String getProp(String prop) {
        String output = "";
        try {
            Class<?> sp = Class.forName("android.os.SystemProperties");
            Method get = sp.getMethod("get", String.class);
            output = (String) get.invoke(null, prop);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    public static String getImei() {
        TelephonyManager tm = (TelephonyManager) EplayerConfigBuilder.getInstance().getContext().getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    public static float getDensity() {
        DisplayMetrics displayMetrics = EplayerConfigBuilder.getInstance().getContext().getResources().getDisplayMetrics();
        return displayMetrics.density;
    }

    public static int getDisplayWidth(Context context) {
        DisplayMetrics displayMetrics;
        try {
            displayMetrics = context.getResources().getDisplayMetrics();
            return displayMetrics.widthPixels;
        } catch (Exception e) {
            Log.d(TAG, "Get display width Exception! Use Default value", e);
        }
        return DEFAULT_DISPLAY_WIDTH;
    }

    public static int getDisplayHeight(Context context) {
        DisplayMetrics displayMetrics;
        try {
            displayMetrics = context.getResources().getDisplayMetrics();
            return displayMetrics.heightPixels;
        } catch (Exception e) {
            Log.d(TAG, "Get display height Exception! Use Default value", e);
        }
        return DEFAULT_DISPLAY_HEIGHT;
    }

    public static String getResolution() {
        DisplayMetrics displayMetrics = EplayerConfigBuilder.getInstance().getContext().getResources().getDisplayMetrics();
        return displayMetrics.widthPixels + "*" + displayMetrics.heightPixels;
    }

    public static int getVersionCode() {
        PackageManager manager = EplayerConfigBuilder.getInstance().getContext().getPackageManager();
        ApplicationInfo info = EplayerConfigBuilder.getInstance().getContext().getApplicationInfo();

        try {
            return manager.getPackageInfo(info.packageName, 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }

    public static String getVersionName() {
        PackageManager manager = EplayerConfigBuilder.getInstance().getContext().getPackageManager();
        ApplicationInfo info = EplayerConfigBuilder.getInstance().getContext().getApplicationInfo();

        try {
            return manager.getPackageInfo(info.packageName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "Unknow";
        }
    }
    protected static final String PREFS_FILE = "gank_device_id.xml";
    protected static final String PREFS_DEVICE_ID = "gank_device_id";
    protected static String uuid;
    static public String getUDID()
    {
        if( uuid ==null ) {
            synchronized (EplayerConfigBuilder.getInstance().getContext()) {
                if( uuid == null) {
                    final SharedPreferences prefs = EplayerConfigBuilder.getInstance().getContext().getSharedPreferences( PREFS_FILE, 0);
                    final String id = prefs.getString(PREFS_DEVICE_ID, null );

                    if (id != null) {
                        // Use the ids previously computed and stored in the prefs file
                        uuid = id;
                    } else {

                        final String androidId = Settings.Secure.getString(EplayerConfigBuilder.getInstance().getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

                        // Use the Android ID unless it's broken, in which case fallback on deviceId,
                        // unless it's not available, then fallback on a random number which we store
                        // to a prefs file
                        try {
                            if (!"9774d56d682e549c".equals(androidId)) {
                                uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8")).toString();
                            } else {
                                final String deviceId = ((TelephonyManager) EplayerConfigBuilder.getInstance().getContext().getSystemService( Context.TELEPHONY_SERVICE )).getDeviceId();
                                uuid = deviceId!=null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")).toString() : UUID.randomUUID().toString();
                            }
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }

                        // Write the value out to the prefs file
                        prefs.edit().putString(PREFS_DEVICE_ID, uuid).commit();
                    }
                }
            }
        }
        return uuid;
    }

    /**
     * 获取机顶盒ID，直接获取当前网络的Mac地址（必须处于联网状态）
     *
     * @return String
     */
    public static String getStbId2() {
        String serialNumber = "unknown";
        try {
            //获取当前网络Mac地址；
            serialNumber = getEth0MacAddress();
        } catch (Exception e) {
            Log.e(TAG, "Get Device StbId Error! Set StbId = unknown", e);
        }
        return serialNumber;
    }

    /**
     * 得到当前使用网络对应的MacAddress
     *
     * @return
     */
    public static String getEth0MacAddress() {
        String strMacAddr = null;
        try {
            InetAddress ip = getWifiIp();
            byte[] b = NetworkInterface.getByInetAddress(ip).getHardwareAddress();
            StringBuilder buffer = new StringBuilder();
            for (int i = 0; i < b.length; i++) {
                if (i != 0) {
                    buffer.append(':');
                }
                String str = Integer.toHexString(b[i] & 0xFF);
                buffer.append(str.length() == 1 ? 0 + str : str);
            }
            strMacAddr = buffer.toString().toUpperCase();
        } catch (Exception e) {
            Log.e(TAG, "Get Eth0 MacAddress Exception!", e);
        }
        return strMacAddr;
    }


    /**
     * 获取sdcard的可用空间，单位为kb
     *
     * @return
     */
    public static long getSdcardFreeSpace() {
        long result = -1;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            try {
                String sdcardPath = Environment.getExternalStorageDirectory().getPath();
                StatFs sf = new StatFs(sdcardPath);
                long blockSize = sf.getBlockSize();
                long availCount = sf.getAvailableBlocks();
                result = blockSize * availCount / 1024;

            } catch (Exception e) {
                Log.e(TAG, "Get Sdcard FreeSpace Exception!", e);
            }
        }
        return result;
    }


    /**
     * 返回本地IP，wifi和3G的情况正确返回，其他返回null
     *
     * @return ip(127.0.0.1)
     */
    public static String getLocalIp() {


        if (is3GNet(EplayerConfigBuilder.getInstance().getContext())) {
            return "127.0.0.1";
        }

        InetAddress inetAddress = getWifiIp();
        if (inetAddress == null) {
            return null;
        }
        return inetAddress.getHostAddress();
    }



    public static String getUserAgentString(){
        if(ua==null) {
            WebView webview;
            webview = new WebView(EplayerConfigBuilder.getInstance().getContext());
            webview.layout(0, 0, 0, 0);
            WebSettings settings = webview.getSettings();
            ua = settings.getUserAgentString();
        }
        return ua;
    }


    private static InetAddress getWifiIp() {
        try {
            // 遍历所用的网络接口
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                // 得到每一个网络接口绑定的所有ip
                NetworkInterface networkInterface = en.nextElement();
                // 遍历每一个接口绑定的所有ip
                for (Enumeration<InetAddress> enumIpAddr = networkInterface.getInetAddresses();
                     enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    Log.d(TAG, "Every NetInterface Ip:" + inetAddress.getHostAddress());
                    if (!inetAddress.isLoopbackAddress()
                            && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {
                        return inetAddress;
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(TAG, "Get Current Ip Address Exception!", ex);
        }
        return null;
    }


    /**
     * 判断当前网络是否是3G网络
     *
     * @param context
     * @return boolean
     */
    public static boolean is3GNet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE;
    }


    /**
     * 判断当前网络是否是wifi网络
     * if(activeNetInfo.getType()==ConnectivityManager.TYPE_MOBILE) { //判断3G网
     *
     * @param context
     * @return boolean
     */
    public static boolean isWifiNet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

}
