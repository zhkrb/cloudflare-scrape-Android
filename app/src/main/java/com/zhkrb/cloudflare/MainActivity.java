package com.zhkrb.cloudflare;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.zhkrb.cloudflare_scrape.Cloudflare;
import com.zhkrb.cloudflare_scrape_webview.util.ConvertUtil;
import com.zhkrb.cloudflare_scrape_webview.util.LogUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mImageView;
    private RadioGroup mRadioGroup;
    private AutoCompleteTextView mTextView;
    private TextView mResultTextView;

    private static final String ua = "Mozilla/5.0 (Linux; Android 6.0.1; SM-G920V Build/MMB29K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.98 Mobile Safari/537.36";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_start).setOnClickListener(this);
        mImageView = findViewById(R.id.image);
        mRadioGroup = findViewById(R.id.radio);
        mTextView = findViewById(R.id.text);
        mResultTextView = findViewById(R.id.result);
        mResultTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        updateImageState(0x01);
    }

    @Override
    public void onClick(View view) {
        if (TextUtils.isEmpty(mTextView.getText())){
            Toast.makeText(getApplicationContext(),"You need to enter the url",Toast.LENGTH_SHORT).show();
            return;
        }
        String url = mTextView.getText().toString();
        int id = mRadioGroup.getCheckedRadioButtonId();
        switch (id){
            case R.id.btn1:
                updateImageState(0x02);
                Cloudflare cloudflare = new Cloudflare(url);
                cloudflare.setUser_agent(ua);
                cloudflare.getCookies(new Cloudflare.cfCallback() {
                    @Override
                    public void onSuccess(List<HttpCookie> cookieList, boolean hasNewUrl, String newUrl) {
                        updateImageState(0x03);
                        setText(cookieList,hasNewUrl,newUrl);
                    }

                    @Override
                    public void onFail(String msg) {
                        updateImageState(0x04);
                        setText(msg);
                    }
                });
                break;
            case R.id.btn2:
                updateImageState(0x02);
                com.zhkrb.cloudflare_scrape_android.Cloudflare cloudflare1 =
                        new com.zhkrb.cloudflare_scrape_android.Cloudflare(url);
                cloudflare1.setUser_agent(ua);
                cloudflare1.getCookies(new com.zhkrb.cloudflare_scrape_android.Cloudflare.cfCallback() {
                    @Override
                    public void onSuccess(List<HttpCookie> cookieList, boolean hasNewUrl, String newUrl) {
                        updateImageState(0x03);
                        setText(cookieList,hasNewUrl,newUrl);
                    }

                    @Override
                    public void onFail(String msg) {
                        updateImageState(0x04);
                        setText(msg);
                    }
                });
                break;
            case R.id.btn3:
                updateImageState(0x02);
                com.zhkrb.cloudflare_scrape_webview.Cloudflare cloudflare2 =
                        new com.zhkrb.cloudflare_scrape_webview.Cloudflare(this,url);
                cloudflare2.setUser_agent(ua);
                cloudflare2.setCfCallback(new com.zhkrb.cloudflare_scrape_webview.CfCallback() {
                    @Override
                    public void onSuccess(List<HttpCookie> cookieList, boolean hasNewUrl, String newUrl) {
                        updateImageState(0x03);
                        setText(cookieList,hasNewUrl,newUrl);

                        new Thread(() -> {
                            try {
                                Document document = Jsoup.connect(url).userAgent(ua).cookies(ConvertUtil.List2Map(cookieList)).timeout(30000)
                                        .get();
                                LogUtil.e(document.toString());
                                runOnUiThread(() -> Toast.makeText(getApplicationContext(),"jsoup test success",Toast.LENGTH_SHORT).show());
                            } catch (IOException e) {
                                e.printStackTrace();
                                runOnUiThread(() -> Toast.makeText(getApplicationContext(),"jsoup test failed",Toast.LENGTH_SHORT).show());
                            }
                        }).start();

                    }

                    @Override
                    public void onFail(int code,String msg) {
                        updateImageState(0x04);
                        setText(msg);
                    }
                });
                cloudflare2.getCookies();
                break;
            default:
                Toast.makeText(getApplicationContext(),"Need select one",Toast.LENGTH_SHORT).show();
        }
    }

    private void updateImageState(int state){
        switch (state){
            case 0x01:
                mImageView.setImageResource(R.drawable.ic_new_releases_black_24dp);
                break;
            case 0x02:
                mImageView.setImageResource(R.drawable.ic_autorenew_black_24dp);
                break;
            case 0x03:
                mImageView.setImageResource(R.drawable.ic_beenhere_black_24dp);
                break;
            case 0x04:
                mImageView.setImageResource(R.drawable.ic_block_black_24dp);
                break;
        }
    }

    private void setText(List<HttpCookie> cookieList, boolean hasNewUrl, String newUrl){
        String sb = String.format("Cookie:%s\n", cookieList.toString()) +
                String.format("HasNewUrl:%s\n", hasNewUrl) +
                String.format("NewUrl:%s\n", hasNewUrl ? newUrl : "default");
        mResultTextView.setText(sb);
    }

    private void setText(String msg){
        mResultTextView.setText(msg);
    }

}
