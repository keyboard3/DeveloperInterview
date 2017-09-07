package com.github.keyboard3.developerinterview;


import android.app.Fragment;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.github.keyboard3.developerinterview.utils.ListUtil;
import com.github.keyboard3.developerinterview.utils.SharePreferencesHelper;
import com.google.common.io.CharStreams;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
    private static String TAG = "ProblemsFragment";
    List<Problem> list = new ArrayList<>();
    private ProblemAdapter adapter;
    private String problemType;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private SharePreferencesHelper spHelper;
    private Gson gson;
    private String dirPath;
    private String problemJsonPath;
    private View ivNodata;
    private View tvInput;
    private AVLoadingIndicatorView avi;

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
        EventBus.getDefault().register(this);

        recyclerView = getView().findViewById(R.id.rl_content);
        ivNodata = getView().findViewById(R.id.iv_nodata);
        avi = getView().findViewById(R.id.avi);
        tvInput = getView().findViewById(R.id.tv_input);
        tvInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SettingActivity.class));
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshEvent(SettingActivity.refreshEvent event) {
        initData();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        spHelper = new SharePreferencesHelper(getActivity(), problemType);

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
                int flag = ItemTouchHelper.LEFT;
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
        avi.show();
        //todo 1.使用rxJava 在子线程执行操作
        //todo 3.添加进入leetcode入口
        //创建文件夹
        File dir = new File(dirPath);
        if (!dir.exists())
            dir.mkdirs();

        File file = null;
        file = new File(problemJsonPath);
        String content = "";
        try {
            list.clear();
            if (!file.exists()) {
                //将assets目录的问题文件复制到sdcard
                AssetManager assets = getActivity().getAssets();
                InputStream open = assets.open(problemType + ".json");
                FileUtil.copyFile(open, file);
            }
            InputStream input = new FileInputStream(file);
            content = CharStreams.toString(new InputStreamReader(input));
            List<Problem> data = gson.fromJson(content, new TypeToken<List<Problem>>() {
            }.getType());
            list.addAll(data);

            if (!ListUtil.isEmpty(list)) {
                recyclerView.setVisibility(View.VISIBLE);
                ivNodata.setVisibility(View.GONE);
                tvInput.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                ivNodata.setVisibility(View.VISIBLE);
                tvInput.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            avi.hide();
        }
    }

    public void goTop() {
        recyclerView.scrollToPosition(0);
    }
}