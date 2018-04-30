package com.github.keyboard3.developerinterview.pattern;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.github.keyboard3.developerinterview.fragment.ProblemsFragment;
import com.github.keyboard3.developerinterview.R;

/**
 * Android状态
 *
 * @author keyboard3
 * @date 2017/9/7
 */

public class AndroidState extends BaseProblemState {
    public static final int ID = 2;
    public static final String NAME = "ProblemAndroid";
    public static final int ICON = R.mipmap.ic_android;
    public static final int MENU_ID = R.id.menu_android;

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
