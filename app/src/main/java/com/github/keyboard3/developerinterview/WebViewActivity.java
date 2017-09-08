package com.github.keyboard3.developerinterview;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.github.keyboard3.developerinterview.utils.SystemUtil;
import com.wang.avi.AVLoadingIndicatorView;

import java.lang.reflect.Method;

/**
 * 网页页面
 */
public class WebViewActivity extends BaseActivity {

    private WebView webView;
    private AVLoadingIndicatorView avi;
    private String searchKey;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("网页详情");

        url = getIntent().getStringExtra(Config.INTENT_KEY);
        searchKey = getIntent().getStringExtra(Config.INTENT_SEARCH_KEY);
        avi = findViewById(R.id.avi);
        webView = findViewById(R.id.wb_content);
        webView.loadUrl(url);
        webView.getSettings().supportZoom();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                avi.show();
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                avi.hide();
                super.onPageFinished(view, url);
                getSupportActionBar().setTitle(view.getTitle());
                if (!TextUtils.isEmpty(searchKey)) {
                    searchContent(searchKey);
                }
            }
        });

    }

    public void searchContent(String content) {
        webView.findAllAsync(content);
        try {
            Method m = WebView.class.getMethod("setFindIsUp", Boolean.TYPE);
            m.invoke(webView, true);
        } catch (Throwable ignored) {

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_copy_url:
                SystemUtil.setClipboard(getApplicationContext(), webView.getTitle(), webView.getUrl());
                Toast.makeText(this, "复制成功", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_open_url:
                SystemUtil.openBrowser(WebViewActivity.this, webView.getUrl());
                break;
            case R.id.action_refresh:
                webView.reload();
                break;
            case R.id.action_send:
                SystemUtil.sendText(WebViewActivity.this, webView.getUrl());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.webview, menu);
        return true;
    }
}
