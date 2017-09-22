package com.github.keyboard3.developerinterview.pattern;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.github.keyboard3.developerinterview.fragments.ContentFragment;
import com.github.keyboard3.developerinterview.R;

/**
 * Created by keyboard3 on 2017/9/7.
 */

public class OtherType extends ProblemType {
    public static final int type = 4;
    public static final String typeStr = "other";

    static class Single {
        static OtherType instance = new OtherType();
    }

    public static OtherType getInstance() {
        return Single.instance;
    }

    @Override
    public ProblemType setFragmentByType(FloatingActionButton fab, FragmentManager fragmentManager) {
        fab.setVisibility(View.VISIBLE);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new ContentFragment(), typeStr);
        fragmentTransaction.commit();
        return this;
    }

    @Override
    public int getTypeIcon() {
        return R.mipmap.ic_menu_other;
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
