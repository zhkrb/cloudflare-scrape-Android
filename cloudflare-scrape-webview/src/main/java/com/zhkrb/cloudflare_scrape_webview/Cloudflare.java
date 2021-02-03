package com.zhkrb.cloudflare_scrape_webview;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.zhkrb.cloudflare_scrape_webview.dialogFragment.WebViewDialog;
import java.lang.ref.WeakReference;

public class Cloudflare {

    public static final int ERR_BUNDLE_PARAM = 0x101;
    public static final int ERR_URL = 0x102;
    public static final int ERR_CANCEL = 0x103;
    public static final int ERR_EXCEED_LIMIT = 0x104;
    public static final int ERR_CAUSE = 0x105;

    private WeakReference<Context> mContext;
    private String mUrl;
    private String mUser_agent = "";

    private CfCallback mCfCallback;
    private WebViewDialog viewDialog;

    public Cloudflare(Context context,@NonNull String url) {
        mContext = new WeakReference<>(context);
        mUrl = url;
    }

    public Cloudflare(Context context,@NonNull String url, String user_agent) {
        mContext = new WeakReference<>(context);
        mUrl = url;
        mUser_agent = user_agent;
    }

    public String getUser_agent() {
        return mUser_agent;
    }

    public void setUser_agent(String user_agent) {
        mUser_agent = user_agent;
    }

    public void setCfCallback(@NonNull CfCallback cfCallback) {
        mCfCallback = cfCallback;
    }

    public void getCookies(){
        if (viewDialog != null){
            viewDialog.dismissAllowingStateLoss();
            viewDialog = null;
        }
        viewDialog = getViewDialog();
        Bundle bundle = new Bundle();
        bundle.putString("url",mUrl);
        bundle.putString("ua",getUser_agent());
        viewDialog.setArguments(bundle);
        viewDialog.setListener(mCfCallback);
        viewDialog.show(((AppCompatActivity)mContext.get()).getSupportFragmentManager(),"WebViewDialog");
    }

    protected WebViewDialog getViewDialog(){
        return new WebViewDialog();
    }

    public void cancel(){
        if (viewDialog != null){
            viewDialog.cancelAll();
            viewDialog.dismissAllowingStateLoss();
            viewDialog = null;
        }
    }

}
