package com.github.keyboard3.developerinterview;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.github.keyboard3.developerinterview.utils.SystemUtil;
import com.wang.avi.AVLoadingIndicatorView;

/**
 * 网页页面
 */
public class WebViewActivity extends BaseActivity {

    private WebView webView;
    private AVLoadingIndicatorView avi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("网页详情");

        String url = getIntent().getStringExtra(Config.INTENT_KEY);
        avi = findViewById(R.id.avi);
        webView = findViewById(R.id.wb_content);
        webView.loadUrl(url);
        webView.getSettings().supportZoom();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                avi.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                getSupportActionBar().setTitle(view.getTitle());
                avi.hide();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_copy_url:
                SystemUtil.setClipboard(getApplicationContext(), webView.getTitle(), webView.getUrl());
                Toast.makeText(this, "复制成功", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_open_url:
                SystemUtil.openText(getApplicationContext(), webView.getUrl());
                break;
            case R.id.action_refresh:
                webView.reload();
                break;
            case R.id.action_send:
                SystemUtil.sendText(getApplicationContext(), webView.getUrl());
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
