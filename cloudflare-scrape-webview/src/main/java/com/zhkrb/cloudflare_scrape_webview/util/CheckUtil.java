package com.zhkrb.cloudflare_scrape_webview.util;

import android.text.TextUtils;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CheckUtil {


    private List<HttpCookie> mCookieList;
    private CookieManager mCookieManager;
    private HttpURLConnection mGetMainConn;
    private CheckListener  mCheckListener;

    private static final int CONN_TIMEOUT = 60000;
    private static final String ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3";

    private boolean canVisit = false;
    private boolean hasNewUrl = false;

    private boolean cancel = false;

    public CheckUtil() {
    }

    public void checkVisit(final String url, final String ua){
        if (mCheckListener == null){
            throw new RuntimeException("must set checkListener");
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                urlThread(url,ua);
            }
        }).start();
    }

    private boolean needCheckResult = true;

    private void urlThread(String url, String ua){
        if (mCookieManager == null){
            mCookieManager = new CookieManager();
            mCookieManager.getCookieStore().removeAll();
            //接受所有cookies
            mCookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            CookieHandler.setDefault(mCookieManager);
        }
        HttpURLConnection.setFollowRedirects(false);
        try {
            getVisitCookie(url,ua);
        } catch (IOException | RuntimeException e) {
            if (mCookieList != null) {
                mCookieList = new ArrayList<>(mCookieList);
                mCookieList.clear();
            }
            e.printStackTrace();
            if (!TextUtils.isEmpty(e.getMessage()) && e.getMessage().contains("Cleartext HTTP traffic")){
                needCheckResult = false;
                LogUtil.e(e.getMessage());
                mCheckListener.onFail(e.getMessage());
            }
        } finally {
            if (needCheckResult){
                checkResult();
            }
        }
    }

    private void getVisitCookie(String url, String ua) throws IOException {
        URL connUrl = new URL(url);
        mGetMainConn = (HttpURLConnection) connUrl.openConnection();
        mGetMainConn.setRequestMethod("GET");
        mGetMainConn.setConnectTimeout(CONN_TIMEOUT);
        mGetMainConn.setReadTimeout(CONN_TIMEOUT);
        mGetMainConn.setRequestProperty("user-agent",ua);
        mGetMainConn.setRequestProperty("accept",ACCEPT);
        mGetMainConn.setRequestProperty("referer", url);
        if (mCookieList!=null&&mCookieList.size()>0){
            mGetMainConn.setRequestProperty("cookie", ConvertUtil.listToString(mCookieList));
        }
        mGetMainConn.setUseCaches(false);
        mGetMainConn.connect();
        switch (mGetMainConn.getResponseCode()){
            case HttpURLConnection.HTTP_OK:
                if (mCookieList == null || mCookieList.size() == 0){
                    mCookieList = mCookieManager.getCookieStore().getCookies();
                    checkCookie(mCookieList);
                }
                canVisit = true;
                break;
            case HttpURLConnection.HTTP_MOVED_PERM:
            case HttpURLConnection.HTTP_MOVED_TEMP:
                hasNewUrl = true;
                break;
            case HttpURLConnection.HTTP_FORBIDDEN:
            case HttpURLConnection.HTTP_UNAVAILABLE:
                break;
            default:
                LogUtil.e("MainUrl","UnCatch Http code: "+mGetMainConn.getResponseCode());
                break;
        }
    }

    private void checkCookie(List<HttpCookie> cookieList) {
        if (cookieList == null || cookieList.size() <= 1){
            return;
        }
        List<HttpCookie> a = new ArrayList<>();
        HttpCookie newestCookie = null;
        for (int i =0;i<cookieList.size();i++){
            if (!cookieList.get(i).getName().equals("_cfduid")){
                continue;
            }
            if (newestCookie == null){
                newestCookie = cookieList.get(i);
                continue;
            }
            a.add(newestCookie);
            newestCookie = cookieList.get(i);
        }
        if (a.size()>0){
            cookieList.removeAll(a);
        }
    }

    private void checkResult(){
        String newUrl = "";
        if (hasNewUrl){
            newUrl = mGetMainConn.getHeaderField("Location");
        }
        closeAllConntion();
        if (canVisit){
            LogUtil.e("MainUrl","visit website success");
            mCheckListener.onSuccess(mCookieList);
        }else {
            if (hasNewUrl){
                LogUtil.e("MainUrl","HTTP 301 :"+newUrl);
                mCheckListener.onChangeNewUrl(newUrl);
            }else {
                LogUtil.e("MainUrl","visit website fail");
                mCheckListener.onFail("");
            }
        }
    }

    private void closeAllConntion(){
        if (mGetMainConn != null){
            mGetMainConn.disconnect();
        }
    }

    public void setCookieList(List<HttpCookie> cookieList) {
        mCookieList = cookieList;
        if (mCookieManager != null){
            mCookieManager.getCookieStore().removeAll();
        }
    }

    public void setCheckListener(CheckListener listener) {
        mCheckListener = listener;
    }

    public void cancel() {
        cancel = true;
        closeAllConntion();
    }

    public boolean isCancel() {
        return cancel;
    }



    public interface CheckListener{
        void onSuccess(List<HttpCookie> cookieList);
        void onFail(String msg);
        void onChangeNewUrl(String url);
    }


}
