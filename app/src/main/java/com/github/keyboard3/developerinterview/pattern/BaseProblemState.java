package com.github.keyboard3.developerinterview.pattern;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;

import com.github.keyboard3.developerinterview.ConfigConst;
import com.github.keyboard3.developerinterview.R;
import com.github.keyboard3.developerinterview.entity.Problem;
import com.github.keyboard3.developerinterview.fragment.ProblemsFragment;
import com.github.keyboard3.developerinterview.http.HttpClient;
import com.github.keyboard3.developerinterview.util.SharePreferencesHelper;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.Consumer;

/**
 * 问题状态的基类
 *
 * @author keyboard3
 * @date 2017/9/7
 */

public abstract class BaseProblemState implements Serializable{

    public BaseProblemState setFragmentByProblemStateName(FloatingActionButton fab, FragmentManager fragmentManager) {
        fab.setVisibility(View.VISIBLE);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, ProblemsFragment.newInstance(this), getProblemStateName());
        fragmentTransaction.commit();
        return this;
    }

    protected abstract String getProblemStateName();
    protected abstract int getProblemStateId();
    protected abstract int getProblemIcon();

    public void setImageViewIcon(ImageView imageView) {
        imageView.setImageResource(getProblemIcon());
    }

    public SharePreferencesHelper createSpHelper(Activity activity) {
        return new SharePreferencesHelper(activity, getProblemStateName());
    }

    public String getProblemJsonPath() {
        return ConfigConst.STORAGE_DIRECTORY + "/" + getProblemStateName() + "/" + getProblemStateName() + ".json";
    }

    public void getProblemsFromHttp(Context context, Consumer<List<Problem>> success, Consumer<Throwable> fail) {
        HttpClient.getInstance(context).getProblems(getProblemStateName() + ".json",success,fail );
    }

    public SparseArray<Problem> getProblemsMap(Map<Integer, SparseArray<Problem>> typeProblemsMap) {
        return typeProblemsMap.get(getProblemStateId());
    }

    public void putEntryToMap(Map<Integer, SparseArray<Problem>> typeProblemsMap, SparseArray<Problem> problemSparseArray) {
        typeProblemsMap.put(getProblemStateId(), problemSparseArray);
    }
}