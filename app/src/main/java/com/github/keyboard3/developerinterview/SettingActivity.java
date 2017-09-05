package com.github.keyboard3.developerinterview;

import android.os.Bundle;

/**
 * 设置界面
 */
public class SettingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setTitle("设置");
    }
}
