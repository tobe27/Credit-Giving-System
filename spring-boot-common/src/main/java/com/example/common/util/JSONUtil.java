package com.example.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

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
    public static Map<String, Object> convertObject2Map(Object object) {
        JSONObject jsonObject = (JSONObject) JSON.toJSON(object);
        Set<Map.Entry<String, Object>> entrySet = jsonObject.entrySet();
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<String, Object> entry : entrySet) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }
}
