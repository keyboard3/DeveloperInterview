package com.github.keyboard3.developerinterview.pattern;

import com.github.keyboard3.developerinterview.R;

/**
 * Created by keyboard3 on 2018/4/30.
 */

public class EmptyProblemState extends BaseProblemState {
    @Override
    protected String getProblemStateName() {
        return "空题目类型";
    }

    @Override
    protected int getProblemStateId() {
        return 0;
    }

    @Override
    protected int getProblemIcon() {
        return R.mipmap.ic_launcher;
    }
}
