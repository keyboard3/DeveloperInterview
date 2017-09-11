package com.github.keyboard3.developerinterview;

import android.Manifest;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.allenliu.versionchecklib.core.AllenChecker;
import com.github.keyboard3.developerinterview.Http.HttpClient;
import com.github.keyboard3.developerinterview.fragments.ContentFragment;
import com.github.keyboard3.developerinterview.fragments.ProblemsFragment;
import com.github.keyboard3.developerinterview.pattern.JavaType;
import com.github.keyboard3.developerinterview.pattern.ProblemType;
import com.github.keyboard3.developerinterview.pattern.ProblemTypeFactory;
import com.github.keyboard3.developerinterview.utils.SystemUtil;
import com.qihoo360.replugin.RePlugin;
import com.qihoo360.replugin.model.PluginInfo;
import com.werb.mediautilsdemo.CustomPermissionChecker;

/**
 * 容器页面  包含左侧导航和右侧内容
 */
public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static String TAG = "MainActivity";
    private CustomPermissionChecker permissionChecker;
    public static final int P_READ_EXTERNAL_STORAGE = 101;
    private FloatingActionButton fab;
    private long firstClickTime = 0;
    ProblemType problemType = JavaType.getInstance();//初始的是javaType
    private ProgressDialog progressDialog;

    @Override
    public boolean hasActionBar() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProblemsFragment fragment = getContentFragment();
                if (fragment != null)
                    fragment.goTop();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        permissionChecker = new CustomPermissionChecker(this);
        if (permissionChecker.isLackPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE})) {
            permissionChecker.requestPermissions(P_READ_EXTERNAL_STORAGE);
        } else {
            problemType.setFragmentByType(fab, getFragmentManager());
        }

        //版本更新检查
        AllenChecker.startVersionCheck(this, HttpClient.getInstance().hostBuilder.build());
        openComingIntent(getIntent().getData());

        //todo 3 自己定制导航栏板块（仅支持选择显示的板块）
        //todo 3 支持gitHub正好登录
        //todo 4 音频文字识别 分享
        //todo 4 支持markdown答案内容显示
        //todo 5 题目录入数据库中,题目手动录入。语音录入
        //todo 6 支持文字搜索 标题。标题、内容、答案。搜索文字标红。语音搜索识别
        //todo 3 增加作品显示内容
        //todo 应用图标。
        //todo 7 和github登录用户聊天
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case P_READ_EXTERNAL_STORAGE:
                if (permissionChecker.hasAllPermissionsGranted(grantResults)) {
                    problemType.setFragmentByType(fab, getFragmentManager());
                } else {
                    permissionChecker.showDialog();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getFragmentManager().findFragmentByTag("typeOther");
        if (fragment != null) {
            ContentFragment other = (ContentFragment) fragment;
            if (other.webView.canGoBack()) {
                other.webView.goBack();
                return;
            }
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }
        if (System.currentTimeMillis() - firstClickTime < 2000) {
            //执行退出应用
            QuitActivity.exitApplication(this);
        } else {
            Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
        }
        firstClickTime = System.currentTimeMillis();
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
            openInnerUri();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openInnerUri() {
        String uri = SystemUtil.getClipboard(getApplicationContext());
        openComingIntent(Uri.parse(uri));
    }

    private void openComingIntent(Uri data) {
        if (data != null && data.toString().contains("content://" + getPackageName())) {
            String id = data.getQueryParameter(Config.INTENT_ID);
            String type = data.getQueryParameter(Config.INTENT_TYPE);
            String source = data.getQueryParameter(Config.INTENT_SOURCE);
            String title = data.getQueryParameter(Config.INTENT_TITLE);
            if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(type)) {
                Intent intent = new Intent(this, ProblemDetailActivity.class);
                intent.putExtra(Config.INTENT_KEY, data.toString());
                startActivity(intent);
            } else if (!TextUtils.isEmpty(source) && !TextUtils.isEmpty(title)) {
                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra(Config.INTENT_KEY, source);
                intent.putExtra(Config.INTENT_SEARCH_KEY, title);
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        problemType = ProblemTypeFactory.getProblemTypeByMenu(item.getItemId());
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        problemType.setFragmentByType(fab, getFragmentManager());
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideDialog();
    }

    public ProblemsFragment getContentFragment() {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment_container);
        return fragment instanceof ProblemsFragment ? ((ProblemsFragment) fragment) : null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, UpgradService.class));
    }
}
