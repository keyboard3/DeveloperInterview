package com.github.keyboard3.developerinterview;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;

import com.github.keyboard3.developerinterview.base.BaseActivity;
import com.github.keyboard3.developerinterview.base.IProgressDialog;
import com.github.keyboard3.developerinterview.util.SystemUtil;
import com.github.keyboard3.developerinterview.view.ExtProgressWebViewClient;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 网页页面
 *
 * @author keyboard3
 */
public class WebViewActivity extends BaseActivity {
    @BindView(R.id.wb_content) WebView htmlContainer;

    private String searchKey;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        setTitle(R.string.title_web);

        ButterKnife.bind(this);
        initUrlAndSearchKeyFromIntent();
        initWebWithData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_copy_url:
                SystemUtil.setClipboard(getApplicationContext(), htmlContainer.getTitle(), htmlContainer.getUrl());
                Toast.makeText(this, "复制成功", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_open_url:
                SystemUtil.openBrowser(WebViewActivity.this, htmlContainer.getUrl());
                break;
            case R.id.action_refresh:
                htmlContainer.reload();
                break;
            case R.id.action_send:
                SystemUtil.sendText(WebViewActivity.this, htmlContainer.getUrl());
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.webview, menu);
        return true;
    }

    void initUrlAndSearchKeyFromIntent() {
        url = getIntent().getStringExtra(ConfigConst.INTENT_KEY);
        searchKey = getIntent().getStringExtra(ConfigConst.INTENT_SEARCH_KEY);
    }

    void initWebWithData() {
        toggleDialogAdvance(true);
        htmlContainer.loadUrl(url);
        htmlContainer.getSettings().supportZoom();
        htmlContainer.getSettings().setJavaScriptEnabled(true);
        htmlContainer.setWebViewClient(new ExtProgressWebViewClient((IProgressDialog) this) {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                getSupportActionBar().setTitle(view.getTitle());
                if(TextUtils.isEmpty(searchKey)) return;
                try {
                    searchContent(searchKey);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void searchContent(String content) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        htmlContainer.findAllAsync(content);
        Method setFindIsUpMethod = WebView.class.getMethod("setFindIsUp", Boolean.TYPE);
        setFindIsUpMethod.invoke(htmlContainer, true);
    }
}
