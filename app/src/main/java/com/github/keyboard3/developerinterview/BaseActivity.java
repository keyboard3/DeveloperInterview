package com.github.keyboard3.developerinterview;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.github.keyboard3.developerinterview.interfaces.IProgressDialog;

public class BaseActivity extends AppCompatActivity implements IProgressDialog {

    private ProgressDialog progressDialog;

    public boolean hasActionBar() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (hasActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (hasActionBar()) {
            if (item.getItemId() == android.R.id.home) {//返回按钮
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("加载中...");
            progressDialog.setIndeterminate(true);
        }
        progressDialog.show();
    }

    @Override
    public void hideDialog() {
        if (progressDialog != null) progressDialog.hide();
    }
}
