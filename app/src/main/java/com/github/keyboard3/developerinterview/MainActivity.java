package com.github.keyboard3.developerinterview;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.allenliu.versionchecklib.core.AllenChecker;
import com.github.keyboard3.developerinterview.Http.HttpClient;
import com.github.keyboard3.developerinterview.fragments.ContentFragment;
import com.github.keyboard3.developerinterview.fragments.ProblemsFragment;
import com.github.keyboard3.developerinterview.model.ShareModel;
import com.github.keyboard3.developerinterview.pattern.JavaType;
import com.github.keyboard3.developerinterview.pattern.OtherType;
import com.github.keyboard3.developerinterview.pattern.ProblemType;
import com.github.keyboard3.developerinterview.pattern.ProblemTypeFactory;

/**
 * 容器页面  包含左侧导航和右侧内容
 * todo n 自己定制导航栏板块（仅支持选择显示的板块）
 * todo n 支持gitHub正好登录
 * todo n 音频文字识别 分享
 * todo n 支持markdown答案内容显示
 * todo n 题目录入数据库中,题目手动录入。语音录入
 * todo n 支持文字搜索 标题。标题、内容、答案。搜索文字标红。语音搜索识别
 * todo 应用图标。
 */
public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private static final int P_READ_EXTERNAL_STORAGE = 101;//sdcard读写权限

    private FloatingActionButton mFab;

    private ProblemType mProblemType = JavaType.getInstance();//初始的是javaType
    private long mFirstClickTime = 0;

    @Override
    public boolean hasActionBar() {
        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findToolbar();
        initView();
        initData();
        initHandler();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, UpgradService.class));
    }

    @Override
    public void onBackPressed() {
        if (webBackUrl()) return;
        if (closeDrawer()) return;
        doubleClickQuit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingActivity.class));
            return true;
        } else if (id == R.id.action_open_uri) {
            ShareModel.openInnerUri(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mProblemType = ProblemTypeFactory.getProblemTypeByMenu(item.getItemId());
        mProblemType.setFragmentByType(mFab, getFragmentManager());

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case P_READ_EXTERNAL_STORAGE:
                if (hasAllPermissionsGranted(grantResults)) {
                    mProblemType.setFragmentByType(mFab, getFragmentManager());
                }
                break;
        }
    }


    private void initView() {
        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProblemsFragment fragment = getContentFragment();
                if (fragment != null)
                    fragment.goTop();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this
                , drawer
                , findToolbar()
                , R.string.navigation_drawer_open
                , R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initData() {
        if (!checkPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, P_READ_EXTERNAL_STORAGE)) {
            mProblemType.setFragmentByType(mFab, getFragmentManager());
        }
    }

    private void initHandler() {
        //版本更新检查
        AllenChecker.startVersionCheck(this, HttpClient.getInstance().mHostBuilder.build());
    }

    private void doubleClickQuit() {
        if (System.currentTimeMillis() - mFirstClickTime < 2000) {
            //执行退出应用
            QuitActivity.exitApplication(this);
        } else {
            Toast.makeText(this, R.string.home_press_quit, Toast.LENGTH_SHORT).show();
        }
        mFirstClickTime = System.currentTimeMillis();
    }

    public ProblemsFragment getContentFragment() {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment_container);
        return fragment instanceof ProblemsFragment ? ((ProblemsFragment) fragment) : null;
    }

    private boolean webBackUrl() {
        Fragment fragment = getFragmentManager().findFragmentByTag(OtherType.typeStr);
        if (fragment != null) {
            ContentFragment other = (ContentFragment) fragment;
            if (other.mWebView.canGoBack()) {
                other.mWebView.goBack();
                return true;
            }
        }
        return false;
    }

    private boolean closeDrawer() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        return false;
    }
}
