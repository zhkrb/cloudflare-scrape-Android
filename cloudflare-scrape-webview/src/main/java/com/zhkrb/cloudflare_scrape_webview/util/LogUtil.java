package com.zhkrb.cloudflare_scrape_webview.util;

import android.util.Log;

public class LogUtil {

    public static void e(String tag, String content){
        Log.e(tag,content);
    }

    public static void e(String content){
        Log.e("cloudflare",content);
    }

}
