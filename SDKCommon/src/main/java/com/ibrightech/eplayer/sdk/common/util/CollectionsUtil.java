package com.ibrightech.eplayer.sdk.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by junhai on 14-11-21.
 */
public class CollectionsUtil {

    public static <T> List<T> coverToList(Collection<? extends T> collection){
        List<T> list = new ArrayList<T>();

        for (T t:collection){
            list.add(t);
        }

        return list;
    }

    public static <T> T lastObject(List<T> list){
        if(list.size()>0){
            return list.get(list.size()-1);
        }

        return null;
    }
    public static <T> T firstObject(List<T> list){
        if(list.size()>0){
            return list.get(0);
        }

        return null;
    }

    public static <T> int keyIndex(List<T> list,T key){
        int currentIndex = -1;
        for (int i = 0; i < list.size(); i++) {
            T _value = list.get(i);
            if (_value.equals(key)) {
                currentIndex = i;
                break;
            }

        }

        return currentIndex;
    }


}
