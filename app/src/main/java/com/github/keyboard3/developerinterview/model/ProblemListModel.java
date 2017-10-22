package com.github.keyboard3.developerinterview.model;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.github.keyboard3.developerinterview.ConfigConst;
import com.github.keyboard3.developerinterview.R;
import com.github.keyboard3.developerinterview.adapter.ProblemAdapter;
import com.github.keyboard3.developerinterview.entity.Problem;
import com.github.keyboard3.developerinterview.util.FileUtil;
import com.github.keyboard3.developerinterview.util.SharePreferencesHelper;
import com.google.common.io.CharStreams;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
        mSpHelper.putInt(ConfigConst.INTENT_LIST_POSITION, position);
        mSpHelper.putInt(ConfigConst.INTENT_LIST_TOP, top);
    }

    public static void initProblemsFromFile(Context context, List<Problem> mList, File file, String mProblemType) throws IOException {
        String content;
        mList.clear();
        if (!file.exists()) {
            //将assets目录的问题文件复制到sdcard
            AssetManager assets = context.getAssets();
            InputStream open = assets.open(mProblemType + ".json");
            FileUtil.copyFile(open, file);
        }
        InputStream input = new FileInputStream(file);
        content = CharStreams.toString(new InputStreamReader(input));
        List<Problem> data = new Gson().fromJson(content, new TypeToken<List<Problem>>() {
        }.getType());
        mList.addAll(data);
    }

    public static void restoreListPosition(LinearLayoutManager mLinearLayoutManager, SharePreferencesHelper mSpHelper) {
        //下次进来还是显示上次的位置
        final int position = mSpHelper.getSP().getInt(ConfigConst.INTENT_LIST_POSITION, 0);
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
