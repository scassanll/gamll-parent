package com.atguigu.gmall.model.to;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Jsons {
    private static ObjectMapper mapper = new ObjectMapper(); 
    /**
     * 对象转为字符串
     * @param object
     * @return
     */
    public static String toStr(Object object) {

        try{
            String s = mapper.writeValueAsString(object);
            return s;
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
