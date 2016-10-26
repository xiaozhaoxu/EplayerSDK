package com.ibrightech.eplayer.sdk.common.entity.system;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by junhai on 14-12-1.
 */
public class DocumentMap {
    private static Map<String,Map<String,DocumentItem>> itemMaps = new HashMap<String, Map<String, DocumentItem>>();

    public static void add(String pptId,Map<String,DocumentItem> map){
        itemMaps.put(pptId,map);
    }
    public static Map<String,DocumentItem> load(String pptId){
        return itemMaps.get(pptId);
    }

    public static boolean validateItem(DocumentItem item,boolean thumb){
        if(item==null)
            return false;

        if(thumb){

        }


        return false;
    }

    public static String getUrl(DocumentItem item,boolean thumb){
        if(item==null)
            return null;

        if(thumb){
            return item.thumbUrl;
        }else{
            return item.url;
        }
    }

    public static String getPath(DocumentItem item,boolean thumb){
        if(item==null)
            return null;

        if(thumb){
            return item.thumbPath;
        }else{
            return item.path;
        }
    }

    public static void setPath(DocumentItem item,boolean thumb,String path){
        if(item!=null){

            if(thumb){
                  item.thumbPath = path;
            }else{
                  item.path = path;
            }

        }
    }

    public static DocumentItem loadItem(String pptId,String page){
        Map<String,DocumentItem> map =itemMaps.get(pptId);
        if (map!=null){
            return map.get(page);
        }
        return null;
    }

    public static void clearALL(){
        itemMaps.clear();
    }
}




