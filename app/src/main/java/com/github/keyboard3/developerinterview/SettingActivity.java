package com.github.keyboard3.developerinterview;

import android.os.Bundle;
import android.widget.TextView;

import com.github.keyboard3.developerinterview.base.BaseActivity;
import com.github.keyboard3.developerinterview.util.VersionUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 设置页面
 * 导入、导出、版本检测
 *
 * @author keyboard3
 */
public class SettingActivity extends BaseActivity {
    @BindView(R.id.tv_version) TextView versionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setTitle(R.string.title_setting);
        ButterKnife.bind(this);

        initVersionView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initVersionView() {
        versionView.setText(getString(R.string.setting_version) + VersionUtil.getVersion(this));
    }
}
