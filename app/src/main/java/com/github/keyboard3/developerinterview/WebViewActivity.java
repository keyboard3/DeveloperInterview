package com.github.keyboard3.developerinterview;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;

import com.github.keyboard3.developerinterview.utils.SystemUtil;
import com.github.keyboard3.developerinterview.views.CusWebViewClient;

import java.lang.reflect.Method;

/**
 * 网页页面
 */
public class WebViewActivity extends BaseActivity {

    private WebView mWebView;
    private String mSearchKey;
    private String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        setTitle(R.string.title_web);
        initData();
        initWebView();
    }

    private void initData() {
        mUrl = getIntent().getStringExtra(Config.INTENT_KEY);
        mSearchKey = getIntent().getStringExtra(Config.INTENT_SEARCH_KEY);
    }

    private void initWebView() {
        toggleDialogAdvance(true);

        mWebView = findViewById(R.id.wb_content);
        mWebView.loadUrl(mUrl);
        mWebView.getSettings().supportZoom();
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new CusWebViewClient(this) {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                getSupportActionBar().setTitle(view.getTitle());
                if (!TextUtils.isEmpty(mSearchKey)) {
                    searchContent(mSearchKey);
                }
            }
        });
    }

    public void searchContent(String content) {
        mWebView.findAllAsync(content);
        try {
            Method m = WebView.class.getMethod("setFindIsUp", Boolean.TYPE);
            m.invoke(mWebView, true);
        } catch (Throwable ignored) {

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_copy_url:
                SystemUtil.setClipboard(getApplicationContext(), mWebView.getTitle(), mWebView.getUrl());
                Toast.makeText(this, "复制成功", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_open_url:
                SystemUtil.openBrowser(WebViewActivity.this, mWebView.getUrl());
                break;
            case R.id.action_refresh:
                mWebView.reload();
                break;
            case R.id.action_send:
                SystemUtil.sendText(WebViewActivity.this, mWebView.getUrl());
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
