package com.github.keyboard3.developerinterview.pattern;

import android.app.FragmentManager;
import android.support.design.widget.FloatingActionButton;

/**
 * 问题状态的基类
 *
 * @author keyboard3
 * @date 2017/9/7
 */

public abstract class BaseProblemState {
    /**
     * 根据类型设置Fragment
     *
     * @param fab
     * @param fragmentTransaction
     * @return
     */
    public abstract BaseProblemState setFragmentByType(FloatingActionButton fab, FragmentManager fragmentTransaction);

    /**
     * 题目的icon
     *
     * @return
     */
    public abstract int getTypeIcon();

    /**
     * 题目名称
     *
     * @return
     */
    public abstract String getTypeStr();

    /**
     * 题目类型
     *
     * @return
     */
    public abstract int getType();
}