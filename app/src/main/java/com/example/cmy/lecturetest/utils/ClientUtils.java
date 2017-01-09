package com.example.cmy.lecturetest.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cmy on 2016/11/23.
 */
public final class ClientUtils {
    public static Map stringToMap(String msg){
        String[] parameters = msg.split(",");
        Map<String, String> parameterMap = new HashMap<>();
        for(String parameter:parameters){
            String[] para = parameter.split("=");
            parameterMap.put(para[0], para[1]);
        }
        return parameterMap;
    }

    //String sendMsg = "action=login" + "," + "studNum=" + studNum + "," + "studPsw=" + psw;
    public static String mapToString(Map<String, String> parasMap){
        StringBuilder sendMsg = new StringBuilder("");
        for(String key: parasMap.keySet()){
            sendMsg.append(key).append("=").append(parasMap.get(key)).append(",");
        }
        sendMsg.deleteCharAt(sendMsg.length()-1);
        return sendMsg.toString();
    }
//怎样学好数学|2016年11月13日;怎样学好软件工程|2016年11月21日;机器学习|2016年11月27日;算法设计与分析|2016年11月24日
    public static List<Map<String, String>> stringToListmap(String src){
        List<Map<String, String>> list = new ArrayList<>();
        String[] lectures = src.split(";");
        for(String lecture : lectures){
            Map<String, String> lecMap = new HashMap<>();
            String[] lecInfo = lecture.split("\\|");
            lecMap.put("lecName", lecInfo[0]);
            lecMap.put("lecDate", lecInfo[1]);
            list.add(lecMap);
        }
        return list;
    }
}
