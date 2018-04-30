package com.github.keyboard3.developerinterview.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.github.keyboard3.developerinterview.R;
import com.wang.avi.AVLoadingIndicatorView;

/**
 * 基类的Activity 让子类更加专注的实现自己的业务
 *
 * @author keyboard3
 * @date 2017/9/3
 */
public class BaseActivity extends AppCompatActivity implements IProgressDialog {

    private AVLoadingIndicatorView advanceProgressView;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    private boolean advanceDialogToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (hasActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void setTitle(@StringRes int titleRid) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(titleRid);
    }

    public boolean hasActionBar() {
        return true;
    }

    protected Toolbar findToolbar() {
        if (toolbar == null) {
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
        }
        return toolbar;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (hasActionBar() && item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void toggleDialogAdvance(boolean toggle) {
        advanceDialogToggle = toggle;
    }

    @Override
    public void showDialog() {
        try {
            if (advanceDialogToggle && advanceProgressView == null)
                advanceProgressView = findViewById(R.id.avi);
            else if (!advanceDialogToggle && progressDialog == null) {
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage(getString(R.string.com_loading));
                progressDialog.setIndeterminate(true);
            }

            if (advanceDialogToggle)
                advanceProgressView.show();
            else
                progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void hideDialog() {
        try {
            if (advanceDialogToggle)
                advanceProgressView.hide();
            else
                progressDialog.hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isShowing() {
        return progressDialog.isShowing();
    }
}
