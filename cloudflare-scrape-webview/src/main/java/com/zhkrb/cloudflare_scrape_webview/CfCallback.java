package com.zhkrb.cloudflare_scrape_webview;

import java.net.HttpCookie;
import java.util.List;

public interface CfCallback {

    /**
     * get cookie success
     * @param cookieList Httpcookie list
     * @param hasNewUrl whether to redirect new url
     * @param newUrl    new url
     */
    void onSuccess(List<HttpCookie> cookieList, boolean hasNewUrl, String newUrl);

    /**
     * get cookie fail
     * @param code fail code
     * @param msg fail mag
     */
    void onFail(int code ,String msg);

}
