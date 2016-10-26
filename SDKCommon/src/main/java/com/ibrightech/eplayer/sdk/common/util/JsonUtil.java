package com.ibrightech.eplayer.sdk.common.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.List;
import java.util.Map;

public class JsonUtil {


    /**
     * 功能描述：把JSON数据转换成普通字符串列表
     *
     * @param jsonData JSON数据
     * @return
     * @throws Exception
     * @author myclover
     */
    public static List<String> getStringList(String jsonData) {
        try {
            return JSON.parseArray(jsonData, String.class);
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    /**
     * 将一个实体类对象转化成JSON数据格式
     * @param bean 实体类对象
     * @return JSON 数据格式字符串
     */
    public static String bean2Json(Object bean){
        try {
            return JSON.toJSONString(bean);
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }

    }
    /**
     * 功能描述：把JSON数据转换成指定的java对象
     *
     * @param jsonData JSON数据
     * @param clazz    指定的java对象
     * @return
     * @throws Exception
     * @author myclover
     */
    public static <T> T json2Bean(String jsonData, Class<T> clazz) {
        try {
            return JSON.parseObject(jsonData, clazz);
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    /**
     * 将泛型列表集合转换成JSON字符串
     * @param <T>	泛型标识
     * @param list	集合对象
     * @return
     */
    public static <T> String list2Json(List<T> list){
        try {
            return JSON.toJSONString(list);
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 功能描述：把JSON数据转换成指定的java对象列表
     *
     * @param jsonData JSON数据
     * @param clazz    指定的java对象
     * @return
     * @throws Exception
     * @author myclover
     */
    public static <T> List<T> json2List(String jsonData, Class<T> clazz) {
        try {
            return JSON.parseArray(jsonData, clazz);
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    /**
     * 功能描述：把JSON数据转换成较为复杂的java对象列表
     *
     * @param jsonData JSON数据
     * @return
     * @throws Exception
     * @author myclover
     */
    public static List<Map<String, Object>> json2MapList(String jsonData) {
        try {
            return JSON.parseObject(jsonData,
                    new TypeReference<List<Map<String, Object>>>() {
                    });
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

}
