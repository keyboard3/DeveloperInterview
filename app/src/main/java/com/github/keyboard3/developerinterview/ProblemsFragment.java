package com.github.keyboard3.developerinterview;


import android.app.Fragment;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.keyboard3.developerinterview.adapter.ProblemAdapter;
import com.github.keyboard3.developerinterview.entity.Problem;
import com.github.keyboard3.developerinterview.utils.FileUtil;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * A ProblemsFragment {@link Fragment} subclass.
 */
public class ProblemsFragment extends Fragment {
    List<Problem> list = new ArrayList<>();
    private ProblemAdapter adapter;
    private String problemType;

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
        RecyclerView recyclerView = getView().findViewById(R.id.rl_content);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

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
    }

    private void initData() {
        problemType = getArguments().getString(Config.INTENT_KEY);
        //创建文件夹
        File dir = new File(Config.StorageDirectory + "/" + problemType + "/");
        if (!dir.exists())
            dir.mkdirs();


        File file = null;
        String path = Config.StorageDirectory + "/" + problemType + "/" + "problems" + ".json";
        file = new File(path);
        String content = "";
        try {
            if (!file.exists()) {
                AssetManager assets = getActivity().getAssets();
                InputStream open = assets.open(problemType + ".json");
                FileUtil.inputstreamtofile(open, file);
            }
            InputStream input = new FileInputStream(file);
            content = CharStreams.toString(new InputStreamReader(input));
            Gson gson = new Gson();
            List<Problem> data = gson.fromJson(content, new TypeToken<List<Problem>>() {
            }.getType());
            list.addAll(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}