package com.zhkrb.cloudflare_scrape_webview.util;

import android.util.Log;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConvertUtil {

    /**
     * 转换list为 ; 符号链接的字符串
     * @param list 列表
     * @return string
     */
    public static String listToString(List<HttpCookie> list) {
        char separator = ";".charAt(0);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i).getName()).append("=").append(list.get(i).getValue()).append(separator);
        }
        return sb.toString().substring(0, sb.toString().length() - 1);
    }

    /**
     * 转换为jsoup可用的Hashmap
     * @param list  HttpCookie列表
     * @return Hashmap
     */
    public static Map<String, String> List2Map(List<HttpCookie> list){
        Map<String, String> map = new HashMap<>();
        try {
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    String[] listStr = list.get(i).toString().split("=");
                    map.put(listStr[0], listStr[1].replace("\"",""));
                }
                Log.i("List2Map", map.toString());
            } else {
                return map;
            }

        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return map;
    }

    /**
     * 转换String为List
     * @param cookie httpCookie
     * @return List<HttpCookie>
     */
    public static List<HttpCookie> String2List(String cookie){
        List<HttpCookie> list = new ArrayList<>();
        String[] listStr = cookie.split(";");
        for (String str:listStr){
            String[] cookieStr = str.split("=");
            list.add(new HttpCookie(cookieStr[0],cookieStr[1]));
        }
        return list;
    }

}
