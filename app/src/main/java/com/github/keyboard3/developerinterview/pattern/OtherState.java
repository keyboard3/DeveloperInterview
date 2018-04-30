package com.github.keyboard3.developerinterview.pattern;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.github.keyboard3.developerinterview.fragment.ContentFragment;
import com.github.keyboard3.developerinterview.R;

/**
 * 其他状态
 *
 * @author keyboard3
 * @date 2017/9/7
 */

public class OtherState extends BaseProblemState {
    public static final int ID = 4;
    public static final String NAME = "other";
    public static final int ICON = R.mipmap.ic_menu_other;
    public static final int MENU_ID = R.id.menu_other;

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
