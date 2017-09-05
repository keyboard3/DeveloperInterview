package com.github.keyboard3.developerinterview;


import android.app.Fragment;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.keyboard3.developerinterview.adapter.ProblemAdapter;
import com.github.keyboard3.developerinterview.entity.Problem;
import com.github.keyboard3.developerinterview.utils.FileUtil;
import com.github.keyboard3.developerinterview.utils.SharePreferencesHelper;
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
import java.util.ArrayList;
import java.util.List;


/**
 * 内容页面 展示不同类型的问题列表
 * A ProblemsFragment {@link Fragment} subclass.
 */
public class ProblemsFragment extends Fragment {
    List<Problem> list = new ArrayList<>();
    private ProblemAdapter adapter;
    private String problemType;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private SharePreferencesHelper spHelper;
    private Gson gson;
    private String dirPath;
    private String problemJsonPath;

    public static ProblemsFragment newInstance(String type) {

        Bundle args = new Bundle();
        args.putString(Config.INTENT_KEY, type);
        ProblemsFragment fragment = new ProblemsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_problems, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        problemType = getArguments().getString(Config.INTENT_KEY);
        gson = new Gson();
        dirPath = Config.StorageDirectory + "/" + problemType + "/";
        problemJsonPath = dirPath + problemType + ".json";
    }

    @Override
    public void onResume() {
        super.onResume();
        spHelper = new SharePreferencesHelper(getActivity(), problemType);

        recyclerView = getView().findViewById(R.id.rl_content);
        linearLayoutManager = new LinearLayoutManager(this.getActivity());

        //下次进来还是显示上次的位置
        final int position = spHelper.getSP().getInt(Config.INTENT_LIST_POSITION, 0);
        int top = spHelper.getSP().getInt(Config.INTENT_LIST_TOP, 0);
        linearLayoutManager.scrollToPositionWithOffset(position, top);
        recyclerView.setLayoutManager(linearLayoutManager);

        initData();

        adapter = new ProblemAdapter(list);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new ProblemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Problem entity = list.get(position);
                Intent intent = new Intent(getActivity(), ProblemDetailActivity.class);
                intent.putExtra(Config.INTENT_ENTITY, entity);
                startActivity(intent);
            }
        });
        /**
         增加题目的删除功能。json->db
         */
        ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder
                    viewHolder) {
                int flag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                return makeMovementFlags(0, flag);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder
                    viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                deleteItem(viewHolder.getAdapterPosition());
            }
        };
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
    }

    public void deleteItem(int position) {
        Problem problem = list.get(position);
        String path = dirPath + problem.id + "/";
        File audio = new File(path);
        list.remove(position);
        String contentStr = gson.toJson(list);
        try {
            OutputStream outputStream = new FileOutputStream(problemJsonPath);
            outputStream.write(contentStr.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "删除失败", Toast.LENGTH_SHORT).show();
        }
        audio.delete();
        adapter.notifyItemRemoved(position);
        Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause() {
        super.onPause();
        //离开保存当前的位置
        int position = linearLayoutManager.findFirstVisibleItemPosition();
        View view = recyclerView.getChildAt(position);
        int top = 0;
        if (view != null) {
            top = view.getTop();
        }
        spHelper.putInt(Config.INTENT_LIST_POSITION, position);
        spHelper.putInt(Config.INTENT_LIST_TOP, top);
    }

    private void initData() {
        //创建文件夹
        File dir = new File(dirPath);
        if (!dir.exists())
            dir.mkdirs();

        File file = null;
        file = new File(problemJsonPath);
        String content = "";
        try {
            if (!file.exists()) {
                //将assets目录的问题文件复制到sdcard
                AssetManager assets = getActivity().getAssets();
                InputStream open = assets.open(problemType + ".json");
                FileUtil.inputstreamtofile(open, file);
            }
            InputStream input = new FileInputStream(file);
            content = CharStreams.toString(new InputStreamReader(input));
            List<Problem> data = gson.fromJson(content, new TypeToken<List<Problem>>() {
            }.getType());
            list.addAll(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goTop() {
        recyclerView.scrollToPosition(0);
    }

    /**
     * 处理listView 的Item的各种触摸事件
     */
}