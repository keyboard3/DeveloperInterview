package com.github.keyboard3.developerinterview.pattern;

import android.app.FragmentManager;
import android.support.design.widget.FloatingActionButton;

/**
 * Created by keyboard3 on 2017/9/7.
 */

public abstract class BaseProblemState {
    public abstract BaseProblemState setFragmentByType(FloatingActionButton fab, FragmentManager fragmentTransaction);

    public abstract int getTypeIcon();

    public abstract String getTypeStr();

    public abstract int getType();
}