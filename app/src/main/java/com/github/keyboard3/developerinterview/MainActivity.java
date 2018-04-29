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
import android.widget.Toast;

import com.allenliu.versionchecklib.core.AllenChecker;
import com.github.keyboard3.developerinterview.base.BaseActivity;
import com.github.keyboard3.developerinterview.fragment.ContentFragment;
import com.github.keyboard3.developerinterview.fragment.ProblemsFragment;
import com.github.keyboard3.developerinterview.http.HttpClient;
import com.github.keyboard3.developerinterview.model.ShareModel;
import com.github.keyboard3.developerinterview.pattern.BaseProblemState;
import com.github.keyboard3.developerinterview.pattern.JavaState;
import com.github.keyboard3.developerinterview.pattern.OtherState;
import com.github.keyboard3.developerinterview.pattern.ProblemStateFactory;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 容器页面  包含左侧导航和右侧内容
 *
 * @author keyboard3
 */
public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private static final int PERMISSION_READ_EXTERNAL_STORAGE = 101;

    private BaseProblemState problemType;
    
    @BindView(R.id.fab) FloatingActionButton floatingActionButton;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;



    private long firstClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initDrawerAndNavigation();
        initContentFragmentByProblemType();
        startVersionCheck();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, UpgradService.class));
    }

    @Override
    public void onBackPressed() {
        if (checkWebContentAndBackUrl()) return;
        if (checkAndCloseDrawer()) return;
        doubleClickQuitCheck();
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

        ProblemStateFactory.getProblemTypeByMenu(item.getItemId())
                           .setFragmentByType(floatingActionButton, getFragmentManager());

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_READ_EXTERNAL_STORAGE
                && hasAllPermissionsGranted(grantResults)) {
            problemType.setFragmentByType(floatingActionButton, getFragmentManager());
        }
    }

    @Override
    public boolean hasActionBar() {
        return false;
    }

    @OnClick(R.id.fab) void floatingActionButtonClick (){
        ProblemsFragment fragment = getContentFragment();
        if (fragment != null) {
            fragment.goTop();
        }
    }
    private void initDrawerAndNavigation() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this
                , drawer
                , findToolbar()
                , R.string.navigation_drawer_open
                , R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initContentFragmentByProblemType() {
        problemType = JavaState.getInstance();
        if (!checkPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_EXTERNAL_STORAGE)) {
            problemType.setFragmentByType(floatingActionButton, getFragmentManager());
        }
    }

    private void startVersionCheck() {
        AllenChecker.startVersionCheck(this, HttpClient.getInstance(getApplicationContext()).mHostBuilder.build());
    }

    private void doubleClickQuitCheck() {
        if (System.currentTimeMillis() - firstClickTime < 2000) {
            QuitActivity.exitApplication(this);
        } else {
            Toast.makeText(this, R.string.home_press_quit, Toast.LENGTH_SHORT).show();
        }
        firstClickTime = System.currentTimeMillis();
    }

    public ProblemsFragment getContentFragment() {
        return (ProblemsFragment) getFragmentManager().findFragmentById(R.id.fragment_container);
    }

    private boolean checkWebContentAndBackUrl() {
        Fragment fragment = getFragmentManager().findFragmentByTag(OtherState.typeStr);
        ContentFragment webContentFragment = null;

        if (fragment != null)
            webContentFragment = (ContentFragment) fragment;

        if (webContentFragment !=null && webContentFragment.htmlContainer.canGoBack()) {
            webContentFragment.htmlContainer.goBack();
            return true;
        }
        return false;
    }

    private boolean checkAndCloseDrawer() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        return false;
    }
}
