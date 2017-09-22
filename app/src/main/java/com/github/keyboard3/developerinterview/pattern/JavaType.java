package com.github.keyboard3.developerinterview.pattern;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.github.keyboard3.developerinterview.fragments.ProblemsFragment;
import com.github.keyboard3.developerinterview.R;

/**
 * Created by keyboard3 on 2017/9/7.
 */

public class JavaType extends ProblemType {
    public static final int type = 1;
    public static final String typeStr = "ProblemJava";

    static class Single {
        static JavaType instance = new JavaType();
    }

    public static JavaType getInstance() {
        return Single.instance;
    }

    @Override
    public ProblemType setFragmentByType(FloatingActionButton fab, FragmentManager fragmentManager) {
        fab.setVisibility(View.VISIBLE);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, ProblemsFragment.newInstance(typeStr), typeStr);
        fragmentTransaction.commit();
        return this;
    }

    @Override
    public int getTypeIcon() {
        return R.mipmap.ic_menu_java;
    }

    @Override
    public String getTypeStr() {
        return typeStr;
    }

    @Override
    public int getType() {
        return type;
    }
}
