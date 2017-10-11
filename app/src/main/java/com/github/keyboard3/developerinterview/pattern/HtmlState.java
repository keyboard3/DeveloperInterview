package com.github.keyboard3.developerinterview.pattern;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.github.keyboard3.developerinterview.fragment.ProblemsFragment;
import com.github.keyboard3.developerinterview.R;

/**
 * Created by keyboard3 on 2017/9/7.
 */

public class HtmlState extends BaseProblemState {
    public static final int type = 3;
    public static final String typeStr = "ProblemHtml";

    static class Single {
        static HtmlState instance = new HtmlState();
    }

    public static HtmlState getInstance() {
        return Single.instance;
    }

    @Override
    public BaseProblemState setFragmentByType(FloatingActionButton fab, FragmentManager fragmentManager) {
        fab.setVisibility(View.VISIBLE);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, ProblemsFragment.newInstance(typeStr), typeStr);
        fragmentTransaction.commit();
        return this;
    }

    @Override
    public int getTypeIcon() {
        return R.mipmap.ic_menu_html;
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
