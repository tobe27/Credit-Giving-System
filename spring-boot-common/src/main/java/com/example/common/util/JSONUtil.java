package com.example.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author CREATED BY L.C.Y on 2018/12/13
 */
public class JSONUtil {
    private JSONUtil(){}


    /**
     * 将对象转换成map
     * @param object
     * @return
     */
    public static Map<String, Object> convertObject2Map(Object object) throws IllegalAccessException {
        Map<String, Object> map = new HashMap<>();
        Class<?> clazz = object.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Object value = field.get(object);
            map.put(fieldName, value);
        }
        return map;
    }
}
