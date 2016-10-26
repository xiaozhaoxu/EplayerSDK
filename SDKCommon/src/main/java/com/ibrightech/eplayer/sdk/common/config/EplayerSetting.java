package com.ibrightech.eplayer.sdk.common.config;

public class EplayerSetting {

    public static String uploadhost="http://42.62.95.20/";
    public String socket_host = "tcp://msg.upuday.com:8114";
    public String host = "http://web.upuday.com:8118/";
    public String playback_url = "http://cache.upuday.com/live/json";

    public String spliceVideoPlayBaseUrl = "";//"http://42.62.95.7:8001/play/";
    public String spliceAudioPlayBaseUrl = "";//"http://42.62.95.7:8001/play/";


    public String play_server = null;
    public String version = "2.1.5.3";

    public String res_dl_url = "";
    public String asset_url = "";

    public String playRtmpUrl = "";
    public String app_identity = "SooonerPlayer";

    public boolean isTestServer = false;
    public boolean isPlayback = false;



    private static EplayerSetting mInstance;                 //单例

    public static EplayerSetting getInstance() {
        if (mInstance == null) {
            synchronized (EplayerSetting.class) {
                if (mInstance == null) {
                    mInstance = new EplayerSetting();
                }
            }
        }
        return mInstance;
    }


    public void connectTestServer() {
        isTestServer = true;
        socket_host = "tcp://42.62.95.28:8114";//"tcp://221.122.71.51:8114";
        host = "http://42.62.95.28:8118/";
        asset_url = "http://42.62.95.28:81";

    }

    public void setSpliceAudioPlayBaseUrl(String spliceAudioPlayBaseUrl) {
        if ("upudays.soooner.com".equals(spliceAudioPlayBaseUrl)) {
            spliceAudioPlayBaseUrl = "upuday.soooner.com";
        }

        this.spliceAudioPlayBaseUrl = "http://" + spliceAudioPlayBaseUrl + "/play/";
    }

    public void setSpliceVideoPlayBaseUrl(String spliceVideoPlayBaseUrl) {
        if ("upudays.soooner.com".equals(spliceVideoPlayBaseUrl)) {
            spliceVideoPlayBaseUrl = "upuday.soooner.com";
        }
        this.spliceVideoPlayBaseUrl = "http://" + spliceVideoPlayBaseUrl + "/play/";
    }
}
