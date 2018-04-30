package com.github.keyboard3.developerinterview.pattern;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.github.keyboard3.developerinterview.fragment.ProblemsFragment;
import com.github.keyboard3.developerinterview.R;

/**
 * Html状态
 *
 * @author keyboard3
 * @date 2017/9/7
 */

public class HtmlState extends BaseProblemState {
    public static final int ID = 3;
    public static final String NAME = "ProblemHtml";
    public static final int ICON = R.mipmap.ic_menu_html;
    public static final int MENU_ID = R.id.menu_html;


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
