package com.github.keyboard3.developerinterview.view;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.github.keyboard3.developerinterview.base.IProgressDialog;

/**
 * Created by keyboard3 on 2017/9/22.
 */

public class CusWebViewClient extends WebViewClient implements IProgressDialog {
    IProgressDialog dialog;

    public CusWebViewClient(IProgressDialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        showDialog();
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        hideDialog();
        super.onPageFinished(view, url);
    }

    @Override
    public void showDialog() {
        if (dialog != null)
            dialog.showDialog();
    }

    @Override
    public void hideDialog() {
        if (dialog != null) dialog.hideDialog();
    }

    @Override
    public boolean isShowing() {
        return (dialog != null) && dialog.isShowing();
    }
}
