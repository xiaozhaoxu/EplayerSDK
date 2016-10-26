package com.ibrightech.eplayer.sdk.common.net.http.utils;

import android.util.Log;

import com.ibrightech.eplayer.sdk.common.entity.MyXMLContentHandler;
import com.ibrightech.eplayer.sdk.common.entity.system.DocumentItem;
import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.common.util.FileUtils;
import com.ibrightech.eplayer.sdk.common.util.IOUtils;

import org.xmlsoft.jaxp.SAXParserFactoryImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;

/**
 * Created by zhaoxu2014 on 16/7/1.
 */
public class DocumnetUtils {
//    String parentPath = StorageUtil.getAppImageDir();
//    String filename = EncodeUtil.encodeByMD5(asset.fileName);

    public static List<DocumentItem> getDocumentList(File file,String pptId,String resType, String fileNetName) {


        List<DocumentItem> list = new ArrayList<DocumentItem>();
        try {
            FileInputStream inputStream = FileUtils.openInputStream(file);
            Map<String, DocumentItem> pptMap = parse(inputStream);

            if (!CheckUtil.isEmpty(pptMap)) {
                for (String key : pptMap.keySet()) {
                    DocumentItem item = pptMap.get(key);
                    String urlTitle = fileNetName.substring(0, fileNetName.lastIndexOf("/") + 1);

                    String url = urlTitle + item.url;
                    String thumbUrl = urlTitle + item.thumbUrl;
                    item.url = url;
                    item.thumbUrl = thumbUrl;
                    item.ppdId = pptId;
                    item.resType = resType;
                    list.add(item);
                }
            }

            IOUtils.closeQuietly(inputStream);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return list;
        }

    }

    private static Map<String, DocumentItem> parse(InputStream is) throws Exception {

        SAXParserFactoryImpl spf = new SAXParserFactoryImpl();
        SAXParser saxParser = spf.newSAXParser();
        MyXMLContentHandler handler = new MyXMLContentHandler();


        Log.e("-----------xxxx------", "xml start");
        saxParser.parse(is, handler);
        Log.e("-----------xxxx------", "xml end");
//        Log.e("-----------xxxx------","xml:"+handler.sliderPPTMap.toString());

        return handler.sliderPPTMap;

    }
}
