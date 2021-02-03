package com.zhkrb.cloudflare_scrape_webview.webClient;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

import com.zhkrb.cloudflare_scrape_webview.util.LogUtil;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

public class AdvanceWebClient extends WebViewClient {

    private final WeakReference<Context> mContext;
    private final WebView mWebView;
    private Timer mTimer;
    private CancelTask mTimerTask;
    private LoginSuccessListener mListener;
    private String mUrl;
    private String mOriginUrl;
    private static final String APP_CACAHE_DIRNAME = "/webcache";
    private static final int TIME_DELAY = 45000;
    private String ua;
    private CookieManager mCookieManager;

    private boolean isShowWebView = false;
    private boolean isSuccess = false;
    private boolean isFailed = false;
    private boolean canTimeOut = true;
    private int pageVisitCount = 0;
    private static final int MAX_COUNT = 3;

    public AdvanceWebClient(Context context, WebView webView, String userAgent) {
        mContext = new WeakReference<>(context);
        mWebView = webView;
        ua = userAgent;
    }

    public void initWebView(String originUrl, String url) {
        if (mListener == null) {
            throw new RuntimeException("must set listener");
        }
        if (mContext.get() == null) {
            throw new RuntimeException("mContext not find");
        }
        mOriginUrl = originUrl;
        mUrl = url;
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUserAgentString(ua);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        //设置 缓存模式
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        // 开启 DOM storage API 功能
        webSettings.setDomStorageEnabled(true);
        //开启 database storage API 功能
        webSettings.setDatabaseEnabled(true);
        String cacheDirPath = mContext.get().getFilesDir().getAbsolutePath() + APP_CACAHE_DIRNAME;
        //      String cacheDirPath = getCacheDir().getAbsolutePath()+Constant.APP_DB_DIRNAME;
        Log.e("WebView", "cacheDirPath=" + cacheDirPath);
        //设置数据库缓存路径
        webSettings.setDatabasePath(cacheDirPath);
        //设置  Application Caches 缓存目录
        webSettings.setAppCachePath(cacheDirPath);
        //开启 Application Caches 功能
        webSettings.setAppCacheEnabled(true);
        Log.e("WebView", "H5--->" + url);
        mWebView.setWebViewClient(this);

        mCookieManager = CookieManager.getInstance();
        mCookieManager.removeAllCookies(null);

        mWebView.loadUrl(mOriginUrl);
    }

    public CookieManager getCookieManager() {
        return mCookieManager;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public void setOriginUrl(String originUrl) {
        mOriginUrl = originUrl;
    }

    private class CancelTask extends TimerTask {

        @Override
        public void run() {
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    mWebView.stopLoading();
                    mListener.onFail();
                }
            });
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (!TextUtils.isEmpty(mCookieManager.getCookie(mUrl)) && mCookieManager.getCookie(mUrl).contains("cf_clearance")) {
            if (!isSuccess) {
                setSuccess(true);
                mWebView.stopLoading();
                mListener.onSuccess(mCookieManager.getCookie(mUrl));
                return;
            }
        }
        if (url.contains(mUrl) && canTimeOut) {
            mTimer = new Timer();
            mTimerTask = new CancelTask();
            mTimer.schedule(mTimerTask, TIME_DELAY);
        }
    }

    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        Log.e("webView", request.getMethod());
        Log.e("webView", String.valueOf(request.getUrl()));
        if (String.valueOf(request.getUrl()).contains("captcha.com")) {
            setCanTimeOut(false);
            if (mTimer != null) {
                mTimer.cancel();
                mTimerTask.cancel();
            }
            if (!isShowWebView) {
                setShowWebView(true);
                mListener.onCaptchaChallenge();
            }
        } else if (String.valueOf(request.getUrl()).equals(mUrl) ||
                String.valueOf(request.getUrl()).contains(mUrl + "/?__cf_chl_jschl_tk__=")) {
            setPageVisitCount(getPageVisitCount() + 1);
            if (getPageVisitCount() > MAX_COUNT) {
                mWebView.stopLoading();
                mListener.onFail();
            }
        }
        return super.shouldInterceptRequest(view, request);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        Log.e("cookie", "" + mCookieManager.getCookie(mUrl));
        if (mTimer != null) {
            mTimer.cancel();
            mTimerTask.cancel();
        }
        if (!TextUtils.isEmpty(mCookieManager.getCookie(mUrl)) && mCookieManager.getCookie(mUrl).contains("cf_clearance")) {
            if (!isSuccess) {
                setSuccess(true);
                mWebView.stopLoading();
                mListener.onSuccess(mCookieManager.getCookie(mUrl));
                return true;
            }
        }
        setCanTimeOut(true);
        return super.shouldOverrideUrlLoading(view, request);
    }


    public void reset() {
        setSuccess(false);
        setShowWebView(false);
        setPageVisitCount(0);
        setFailed(false);
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        if (request.getUrl().toString().equals(mUrl)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                LogUtil.e("WebError: " + error.getErrorCode());
            }
            mWebView.stopLoading();
            setSuccess(false);
            if (!isFailed) {
                setFailed(true);
                mListener.onFail();
            }
        }
    }

    public void setListener(LoginSuccessListener listener) {
        mListener = listener;
    }

    private synchronized void setShowWebView(boolean showWebView) {
        isShowWebView = showWebView;
    }

    private synchronized void setSuccess(boolean success) {
        isSuccess = success;
    }

    private synchronized void setFailed(boolean failed) {
        isFailed = failed;
    }

    private synchronized int getPageVisitCount() {
        return pageVisitCount;
    }

    private synchronized void setPageVisitCount(int pageVisitCount) {
        this.pageVisitCount = pageVisitCount;
    }

    private synchronized boolean isCanTimeOut() {
        return canTimeOut;
    }

    private synchronized void setCanTimeOut(boolean canTimeOut) {
        this.canTimeOut = canTimeOut;
    }

    public interface LoginSuccessListener {
        void onSuccess(String cookie);

        void onCaptchaChallenge();

        void onFail();
    }
}
