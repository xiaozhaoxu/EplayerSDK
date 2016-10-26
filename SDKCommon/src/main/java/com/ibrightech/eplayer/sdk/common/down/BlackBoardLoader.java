package com.ibrightech.eplayer.sdk.common.down;

import android.util.Log;

import com.ibrightech.eplayer.sdk.common.entity.Asset;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.DrawPadInfo;
import com.ibrightech.eplayer.sdk.common.entity.session.EPlaybackSessionInfo;
import com.ibrightech.eplayer.sdk.common.entity.session.EplayerSessionInfo;
import com.ibrightech.eplayer.sdk.common.entity.system.DocumentItem;
import com.ibrightech.eplayer.sdk.common.entity.system.DocumentMap;
import com.ibrightech.eplayer.sdk.common.net.http.GetAssetListProtocol;
import com.ibrightech.eplayer.sdk.common.util.EncodeUtil;
import com.ibrightech.eplayer.sdk.common.util.FileUtils;
import com.ibrightech.eplayer.sdk.common.util.IOUtils;
import com.ibrightech.eplayer.sdk.common.util.LogUtil;
import com.ibrightech.eplayer.sdk.common.util.StorageUtil;
import com.ibrightech.eplayer.sdk.common.util.StringUtils;
import com.lzy.okhttputils.OkHttpUtils;

import org.xmlsoft.jaxp.SAXParserFactoryImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.parsers.SAXParser;

import okhttp3.Response;

/**
 * Created by zhaoxu2014 on 16/8/26.
 */
public class BlackBoardLoader {
    private static final String TAG = BlackBoardLoader.class.getSimpleName();

    private static final Stack<Loader.Holder> queue = new Stack<Loader.Holder>();

    private static Loader loader = new Loader();

    static {
        loader.setPriority(Thread.NORM_PRIORITY - 1);
    }


    public static void load(DrawPadInfo drawPadInfo,EPlaybackSessionInfo eplayerSessionInfo, CallBack callBack) {

        synchronized (queue) {
            Loader.Holder holder = loader.new Holder();
            holder.drawPadInfo = drawPadInfo;
            holder.callBack = callBack;
            holder.eplayerSessionInfo = eplayerSessionInfo;

            if (queue.size() > 0) {
                queue.clear();
            }

            queue.push(holder);
            queue.notifyAll();
        }

        if (loader.getState() == Thread.State.NEW) {
            loader.start();
        }
    }

    public static void loadList(DrawPadInfo drawPadInfo,EPlaybackSessionInfo eplayerSessionInfo, CallBack callBack) {

        synchronized (queue) {
            Loader.Holder holder = loader.new Holder();
            holder.drawPadInfo = drawPadInfo;
            holder.callBack = callBack;
            holder.dataList = true;
            holder.eplayerSessionInfo = eplayerSessionInfo;

            if (queue.size() > 0) {
                queue.clear();
            }

            queue.push(holder);
            queue.notifyAll();
        }

        if (loader.getState() == Thread.State.NEW) {
            loader.start();
        }
    }
    public static void load(DrawPadInfo drawPadInfo,EPlaybackSessionInfo eplayerSessionInfo, CallBack callBack,boolean thumb) {

        synchronized (queue) {
            Loader.Holder holder = loader.new Holder();
            holder.drawPadInfo = drawPadInfo;
            holder.callBack = callBack;
            holder.thumb = thumb;
            holder.eplayerSessionInfo = eplayerSessionInfo;

            if (queue.size() > 0) {
                queue.clear();
            }

            queue.push(holder);
            queue.notifyAll();
        }

        if (loader.getState() == Thread.State.NEW) {
            loader.start();
        }
    }

    private static class Loader extends Thread {

        public void run() {
            try {
                while (true) {
                    //Wait if no task in queue
                    if (queue.size() == 0) {
                        synchronized (queue) {
                            queue.wait();
                        }
                    }
                    //Start work on task
                    else {
                        Holder holder;

                        synchronized (queue) {
                            holder = queue.pop();
                        }

                        action(holder);
                        System.gc();
                    }
                }
            } catch (Exception e) {
                Log.e("TAG", "Something wrong with ImageLoader thread. " + e.getMessage());
            }
        }

        private class Holder {
            public DrawPadInfo drawPadInfo;
            public EPlaybackSessionInfo eplayerSessionInfo;
            public CallBack callBack;
            public boolean thumb;
            public boolean dataList;
            public boolean isSuccess=false;
        }

    }


    public static String getLocalPicPath(String url) {
        return StorageUtil.getImageCacheDierctory() + EncodeUtil.encodeByMD5(url);
    }

    private static void action(final Loader.Holder holder) {


        DrawPadInfo drawPadInfo = holder.drawPadInfo;
        CallBack callback = holder.callBack;
        int pptId = drawPadInfo.pptId;
        int page = drawPadInfo.page;


            String pptLocalPath = null;

//            final String cachePath = pptId + "_" + page;

            try {

                //TODO 是否是需要列表
                if(!holder.dataList){
                    DocumentItem item  = DocumentMap.loadItem(pptId + "", page + "");

                    //TODO 对应资源数据存在
                    if(item!=null){
                        pptLocalPath = DocumentMap.getPath(item,holder.thumb);
                        //TODO 对应缓存存在
                        if(StringUtils.isValid(pptLocalPath)){
                            if (StringUtils.isValid(pptLocalPath)) {
                                holder.isSuccess=true;
                                callback.onComplete(pptLocalPath,pptId,page);
                                return;
                            }
                        }else{
                            String pptURL = DocumentMap.getUrl(item, holder.thumb);
                            String filePath= getLocalPicPath(pptURL);
                            File locatFile= new File(filePath);
                            //TODO 对应地址存在
                            if (StringUtils.isValid(pptURL)) {
                                Response response = OkHttpUtils.get(pptURL).execute();
                                InputStream in = response.body().byteStream();
                                FileUtils.copyInputStreamToFile(in, locatFile);

                                if (!locatFile.exists() || locatFile.length() <= 0) {
                                    //todo 下载图片有误
                                    holder.isSuccess = false;
                                } else {
                                    //缓存ppt图片本地路径
                                    DocumentMap.setPath(item, holder.thumb, filePath);
                                    holder.isSuccess = true;
                                    callback.onComplete(filePath, pptId, page);
                                    return;
                                }
                            }
                        }
                    }

                }else{
                    Map<String, DocumentItem> maps = DocumentMap.load(pptId + "");
                    if(maps!=null&&maps.size()>0) {

                        List<DocumentItem> list = new ArrayList<DocumentItem>();

                        for (String key : maps.keySet()) {
                            DocumentItem item = maps.get(key);
                            list.add(item);
                        }

                        holder.isSuccess = true;
                        callback.onCompleteList(list, pptId);
                        return;
                    }
                }


                String liveRoomId = holder.eplayerSessionInfo.userInfo.liveClassroomId;

                //TODO 本地没有PPT图片启动下载流程
                GetAssetListProtocol protocol = new GetAssetListProtocol(liveRoomId, "ppt,word");
                protocol.execute(null, null);

                Map<String, Asset> assetMap = protocol.getAssetMap();
                Asset asset = assetMap.get(pptId + "");
                if (asset != null) {
                    Response response = OkHttpUtils.get(asset.fileName).execute();
                    InputStream is = response.body().byteStream();

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                    IOUtils.copy(is, outputStream);
                    IOUtils.closeQuietly(is);
                    LogUtil.d("lenght" + outputStream.size());


                    InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());//得到网络返回的输入流

                    LogUtil.d("lenght" + inputStream.available());
                    Map<String, DocumentItem> pptMap = parse(inputStream);
                    IOUtils.closeQuietly(outputStream);


                    List<DocumentItem> list = new ArrayList<DocumentItem>();

                    if(pptMap.size()==0){
                        holder.isSuccess=true;
                        callback.onAssetIsNull(pptId,page);
                    }else{
                        boolean foundAsset = false;
                        DocumentItem foundItem = null;
                        for(String key:pptMap.keySet()){
                            DocumentItem item = pptMap.get(key);
                            String urlTitle = asset.fileName.substring(0, asset.fileName.lastIndexOf("/") + 1);

                            String url = urlTitle + item.url;
                            String thumbUrl = urlTitle + item.thumbUrl;

                            item.url = url;
                            item.thumbUrl = thumbUrl;

                            if(holder.dataList){
                                list.add(item);
                                continue;
                            }

                            if(item.page.equals(page+"")){
                                foundAsset =true;
                                foundItem= item;
                            }

                        }
                        DocumentMap.add(pptId+"",pptMap);

                        if(holder.dataList){
                            holder.isSuccess = true;
                            callback.onCompleteList(list,pptId);
                        }else {

                            if (foundAsset) {

                                String pptURL = DocumentMap.getUrl(foundItem, holder.thumb);
                                String filePath = getLocalPicPath(pptURL);
                                File locatFile = new File(filePath);
                                if (StringUtils.isValid(pptURL)) {

                                    Response response2 = OkHttpUtils.get(pptURL).execute();
                                    InputStream in = response2.body().byteStream();
                                    FileUtils.copyInputStreamToFile(in, locatFile);

                                    if (!locatFile.exists() || locatFile.length() <= 0) {
                                        //todo 下载图片有误
                                        holder.isSuccess = false;
                                    } else {
                                        //缓存ppt图片本地路径
                                        DocumentMap.setPath(foundItem, holder.thumb, filePath);
                                        holder.isSuccess = true;
                                        callback.onComplete(filePath, pptId, page);
                                        return;
                                    }

                                }

                            } else {
                                holder.isSuccess = true;
                                callback.onAssetIsNull(pptId, page);

                            }
                        }

                    }

                    IOUtils.closeQuietly(inputStream);

//                conn.disconnect();
                }else{
                    holder.isSuccess=true;
                    callback.onAssetIsNull(pptId,page);
                    return;

                }

            } catch (Exception e) {
                LogUtil.e("Get Asset List Request Exception!", e.getMessage());
                holder.isSuccess = false;
            }



        if(!holder.isSuccess){
            callback.onLoadingFailed(pptId,page);
        }
    }

    private static Map<String, DocumentItem> parse(InputStream is) throws Exception {


        SAXParserFactoryImpl spf = new SAXParserFactoryImpl();


        SAXParser saxParser = spf.newSAXParser();
        XMLContentHandler handler = new XMLContentHandler();


        Log.e("-----------xxxx------","xml start");
        saxParser.parse(is, handler);
        Log.e("-----------xxxx------","xml end");
        return handler.sliderPPTMap;

    }


    public interface CallBack {
        public void onComplete(String pictureLocalUrl,int pptId,int page);
        public void onCompleteList(List<DocumentItem> list,int pptId);
        public void onLoadingFailed(int pptId,int page);
        public void onAssetIsNull(int pptId,int page);

    }

}
