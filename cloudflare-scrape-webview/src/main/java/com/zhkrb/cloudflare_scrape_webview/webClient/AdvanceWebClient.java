package com.zhkrb.cloudflare_scrape_webview.webClient;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.zhkrb.cloudflare_scrape_webview.util.LogUtil;

import java.lang.ref.WeakReference;
import java.util.List;

public class AdvanceWebClient extends WebViewClient {

    private final WeakReference<Context> mContext;
    private final WebView mWebView;
    private LoginSuccessListener mListener;
    private String mUrl;
    private static final String APP_CACAHE_DIRNAME = "/webcache";
    private String ua;
    private CookieManager mCookieManager;
    private boolean isShowWebView = false;
    private boolean isSuccess = false;
    private boolean isFailed = false;

    public AdvanceWebClient(Context context, WebView webView,String userAgent) {
        mContext = new WeakReference<>(context);
        mWebView = webView;
        ua = userAgent;
    }

    public void initWebView(String url) {
        if (mContext.get() == null){
            return;
        }
        mUrl = url;
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUserAgentString(ua);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);  //设置 缓存模式
        // 开启 DOM storage API 功能
        webSettings.setDomStorageEnabled(true);
        //开启 database storage API 功能
        webSettings.setDatabaseEnabled(true);
        String cacheDirPath = mContext.get().getFilesDir().getAbsolutePath()+APP_CACAHE_DIRNAME;
        //      String cacheDirPath = getCacheDir().getAbsolutePath()+Constant.APP_DB_DIRNAME;
        Log.e("WebView","cacheDirPath="+cacheDirPath);
        //设置数据库缓存路径
        webSettings.setDatabasePath(cacheDirPath);
        //设置  Application Caches 缓存目录
        webSettings.setAppCachePath(cacheDirPath);
        //开启 Application Caches 功能
        webSettings.setAppCacheEnabled(true);
        Log.e("WebView","H5--->" + url);
        mWebView.setWebViewClient(this);

        mCookieManager = CookieManager.getInstance();
        mCookieManager.removeAllCookies(null);

        mWebView.loadUrl(mUrl);
    }

    public CookieManager getCookieManager() {
        return mCookieManager;
    }

    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        Log.e("webView",request.getMethod());
        Log.e("webView", String.valueOf(request.getUrl()));
        if (String.valueOf(request.getUrl()).contains("captcha.com")){
            if (!isShowWebView){
                setShowWebView(true);
                if (mListener != null){
                    mListener.onCaptchaChallenge();
                }
            }
        }
        return super.shouldInterceptRequest(view, request);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        Log.e("cookie",mCookieManager.getCookie(mUrl));
        if (mCookieManager.getCookie(mUrl).contains("cf_clearance")){
            if (!isSuccess){
                setSuccess(true);
                if (mListener != null){
                    mWebView.stopLoading();
                    mListener.onSuccess(mCookieManager.getCookie(mUrl));
                }
            }
        }
        return super.shouldOverrideUrlLoading(view, request);
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        if (request.getUrl().toString().equals(mUrl)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                LogUtil.e("WebError: " + error.getErrorCode());
            }
            setSuccess(false);
            setShowWebView(false);
            if (!isFailed){
                setFailed(true);
                mListener.onFail();
            }
        }
    }

    public void setListener(LoginSuccessListener listener) {
        mListener = listener;
    }

    public synchronized void setShowWebView(boolean showWebView) {
        isShowWebView = showWebView;
    }

    public synchronized void setSuccess(boolean success) {
        isSuccess = success;
    }

    public synchronized void setFailed(boolean failed) {
        isFailed = failed;
    }


    public interface LoginSuccessListener{
        void onSuccess(String cookie);
        void onCaptchaChallenge();
        void onFail();
    }
}
