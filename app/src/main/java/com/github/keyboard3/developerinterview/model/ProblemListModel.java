package com.github.keyboard3.developerinterview.model;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.github.keyboard3.developerinterview.ConfigConst;
import com.github.keyboard3.developerinterview.R;
import com.github.keyboard3.developerinterview.adapter.ProblemAdapter;
import com.github.keyboard3.developerinterview.entity.Problem;
import com.github.keyboard3.developerinterview.util.SharePreferencesHelper;
import com.google.gson.Gson;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * 问题列表相关逻辑
 *
 * @author keyboard3
 * @date 2017/9/22
 */

public class ProblemListModel {
    public static void saveListPosition(RecyclerView mRecyclerView, LinearLayoutManager mLinearLayoutManager, SharePreferencesHelper mSpHelper) {
        int position = mLinearLayoutManager.findFirstVisibleItemPosition();
        View view = mRecyclerView.getChildAt(position);
        int top = 0;
        if (view != null) {
            top = view.getTop();
        }
        mSpHelper.putInt(ConfigConst.INTENT_POSITIONS, position);
        mSpHelper.putInt(ConfigConst.INTENT_LIST_TOP, top);
    }

    public static void restoreListPosition(LinearLayoutManager mLinearLayoutManager, SharePreferencesHelper mSpHelper) {
        //下次进来还是显示上次的位置
        final int position = mSpHelper.getSP().getInt(ConfigConst.INTENT_POSITIONS, 0);
        int top = mSpHelper.getSP().getInt(ConfigConst.INTENT_LIST_TOP, 0);
        mLinearLayoutManager.scrollToPositionWithOffset(position, top);
    }

    public static void removeProblem(Context applicationContext, ProblemAdapter mAdapter, List<Problem> mList, int position, String mProblemJsonPath) {
        mList.remove(position);
        String contentStr = new Gson().toJson(mList);
        try {
            OutputStream outputStream = new FileOutputStream(mProblemJsonPath);
            outputStream.write(contentStr.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(applicationContext, R.string.com_delete_fail, Toast.LENGTH_SHORT).show();
        }
        mAdapter.notifyItemRemoved(position);
        Toast.makeText(applicationContext, R.string.com_delete_success, Toast.LENGTH_SHORT).show();
    }
}
