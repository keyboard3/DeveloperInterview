package com.github.keyboard3.developerinterview.pattern;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.github.keyboard3.developerinterview.fragment.ProblemsFragment;
import com.github.keyboard3.developerinterview.R;

/**
 * 算法状态
 *
 * @author keyboard3
 * @date 2017/9/7
 */

public class AlgorithmState extends BaseProblemState {
    public static final int ID = 5;
    public static final String NAME = "ProblemAlgorithm";
    public static final int ICON = R.mipmap.ic_menu_algorithm;
    public static final int MENU_ID = R.id.menu_algorithm;

    static class Single {
        static AlgorithmState instance = new AlgorithmState();
    }

    public static AlgorithmState getInstance() {
        return Single.instance;
    }

    @Override
    public BaseProblemState setFragmentByProblemStateName(FloatingActionButton fab, FragmentManager fragmentManager) {
        fab.setVisibility(View.VISIBLE);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, ProblemsFragment.newInstance(this));
        fragmentTransaction.commit();
        return this;
    }

    @Override
    protected String getProblemStateName() {
        return NAME;
    }

    @Override
    protected int getProblemStateId() {
        return ID;
    }

    @Override
    protected int getProblemIcon() {
        return ICON;
    }
}
