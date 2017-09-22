package com.github.keyboard3.developerinterview.model;

import android.webkit.WebView;

import com.github.keyboard3.developerinterview.interfaces.IProgressDialog;
import com.github.keyboard3.developerinterview.views.CusWebViewClient;

/**
 * Created by keyboard3 on 2017/9/22.
 */

public class WebViewModel {
    public static void initWebView(WebView webview, String content, IProgressDialog progressDialog) {
        if (content.startsWith("http://") || content.startsWith("https://")) {
            webview.loadUrl(content);
        } else {
            webview.getSettings().setDefaultTextEncodingName("UTF-8");
            webview.loadData(content, "text/html; charset=UTF-8", null);
        }
        webview.setWebViewClient(new CusWebViewClient(progressDialog));
    }
}
