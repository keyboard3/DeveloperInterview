package com.github.keyboard3.developerinterview;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.keyboard3.developerinterview.entity.Problem;
import com.werb.mediautilsdemo.CustomPermissionChecker;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private CustomPermissionChecker permissionChecker;
    public static final int P_READ_EXTERNAL_STORAGE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
        } else {
            super.onBackPressed();
        }
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
            case R.id.menu_other:
                setFragmentByType(Problem.typeOther);
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setFragmentByType(int type) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        switch (type) {
            case Problem.typeJava:
                fragmentTransaction.replace(R.id.fragment_container, ProblemsFragment.newInstance(Config.ProblemJava), Config.ProblemJava);
                fragmentTransaction.commit();
                break;
            case Problem.typeAndroid:
                fragmentTransaction.replace(R.id.fragment_container, ProblemsFragment.newInstance(Config.ProblemAndroid), Config.ProblemAndroid);
                fragmentTransaction.commit();
                break;
            case Problem.typeHtml:
                fragmentTransaction.replace(R.id.fragment_container, ProblemsFragment.newInstance(Config.ProblemHtml), Config.ProblemHtml);
                fragmentTransaction.commit();
                break;
            default:
                fragmentTransaction.replace(R.id.fragment_container, new ContentFragment(), "typeOther");
                fragmentTransaction.commit();
                break;
        }
    }
}
