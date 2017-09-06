package com.github.keyboard3.developerinterview;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.allenliu.versionchecklib.core.AllenChecker;
import com.github.keyboard3.developerinterview.Http.HttpClient;
import com.github.keyboard3.developerinterview.entity.Problem;
import com.werb.mediautilsdemo.CustomPermissionChecker;

/**
 * 容器页面  包含左侧导航和右侧内容
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static String TAG = "MainActivity";
    private CustomPermissionChecker permissionChecker;
    public static final int P_READ_EXTERNAL_STORAGE = 101;
    ProblemsFragment contentFragment;
    private FloatingActionButton fab;
    private long firstClickTime = 0;

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
                contentFragment.goTop();
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
            setFragmentByType(Problem.typeJava);
        }

        //版本更新检查
        AllenChecker.startVersionCheck(this, HttpClient.getInstance().builder.build());
        //todo 3 自己定制导航栏板块（仅支持选择显示的板块）
        //todo 3 支持gitHub正好登录
        //todo 4 音频文字识别 分享
        //todo 4 支持markdown答案内容显示
        //todo 4 进行混淆和图片压缩模块
        //todo 5 题目录入数据库中,题目手动录入。语音录入
        //todo 6 支持文字搜索 标题。标题、内容、答案。搜索文字标红。语音搜索识别
        //todo 2.5 增加作品显示内容
        //todo 应用图标，无数据 默认图导入。
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case P_READ_EXTERNAL_STORAGE:
                if (permissionChecker.hasAllPermissionsGranted(grantResults)) {
                    setFragmentByType(Problem.typeJava);
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //todo  2 策略模式 要支持动态扩展
        switch (item.getItemId()) {
            case R.id.menu_java:
                setFragmentByType(Problem.typeJava);
                break;
            case R.id.menu_android:
                setFragmentByType(Problem.typeAndroid);
                break;
            case R.id.menu_html:
                setFragmentByType(Problem.typeHtml);
                break;
            case R.id.menu_algorithm:
                setFragmentByType(Problem.typeAlgorithm);
                break;
            case R.id.menu_other:
                setFragmentByType(Problem.typeOther);
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setFragmentByType(int type) {
        //todo 2.策略模式要支持动态扩展
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        switch (type) {
            case Problem.typeJava:
                fab.setVisibility(View.VISIBLE);
                contentFragment = ProblemsFragment.newInstance(Config.ProblemJava);
                fragmentTransaction.replace(R.id.fragment_container, contentFragment, Config.ProblemJava);
                fragmentTransaction.commit();
                break;
            case Problem.typeAndroid:
                fab.setVisibility(View.VISIBLE);
                contentFragment = ProblemsFragment.newInstance(Config.ProblemAndroid);
                fragmentTransaction.replace(R.id.fragment_container, contentFragment, Config.ProblemAndroid);
                fragmentTransaction.commit();
                break;
            case Problem.typeHtml:
                fab.setVisibility(View.VISIBLE);
                contentFragment = ProblemsFragment.newInstance(Config.ProblemHtml);
                fragmentTransaction.replace(R.id.fragment_container, contentFragment, Config.ProblemHtml);
                fragmentTransaction.commit();
                break;
            case Problem.typeAlgorithm:
                fab.setVisibility(View.VISIBLE);
                contentFragment = ProblemsFragment.newInstance(Config.ProblemAlgorithm);
                fragmentTransaction.replace(R.id.fragment_container, contentFragment, Config.ProblemAlgorithm);
                fragmentTransaction.commit();
                break;
            default:
                fab.setVisibility(View.GONE);
                fragmentTransaction.replace(R.id.fragment_container, new ContentFragment(), "typeOther");
                fragmentTransaction.commit();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, UpgradService.class));
    }
}
