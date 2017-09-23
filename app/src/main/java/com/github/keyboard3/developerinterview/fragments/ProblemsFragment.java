package com.github.keyboard3.developerinterview.fragments;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.keyboard3.developerinterview.Config;
import com.github.keyboard3.developerinterview.ProblemDetailActivity;
import com.github.keyboard3.developerinterview.R;
import com.github.keyboard3.developerinterview.SettingActivity;
import com.github.keyboard3.developerinterview.adapter.ProblemAdapter;
import com.github.keyboard3.developerinterview.entity.Problem;
import com.github.keyboard3.developerinterview.model.ProblemListModel;
import com.github.keyboard3.developerinterview.utils.ListUtil;
import com.github.keyboard3.developerinterview.utils.SharePreferencesHelper;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * 内容页面 展示不同类型的问题列表
 * A ProblemsFragment {@link Fragment} subclass.
 */
public class ProblemsFragment extends BaseFragment {
    private static String TAG = "ProblemsFragment";

    private RecyclerView mRecyclerView;
    private View mIvNoData;
    private View mTvInput;

    private List<Problem> mList = new ArrayList<>();
    private ProblemAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private String mProblemType;
    private String mDirPath;
    private String mProblemJsonPath;
    private SharePreferencesHelper mSpHelper;

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
        mProblemType = getArguments().getString(Config.INTENT_KEY);
        mDirPath = Config.STORAGE_DIRECTORY + "/" + mProblemType + "/";
        mProblemJsonPath = mDirPath + mProblemType + ".json";
        EventBus.getDefault().register(this);

        mRecyclerView = getView().findViewById(R.id.rl_content);
        mIvNoData = getView().findViewById(R.id.iv_nodata);
        mTvInput = getView().findViewById(R.id.tv_input);
        mTvInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SettingActivity.class));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mSpHelper = new SharePreferencesHelper(getActivity(), mProblemType);
        mLinearLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        ProblemListModel.restoreListPosition(mLinearLayoutManager, mSpHelper);

        initData();

        mAdapter = new ProblemAdapter(mList, getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new ProblemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Problem entity = mList.get(position);
                Intent intent = new Intent(getActivity(), ProblemDetailActivity.class);
                intent.putExtra(Config.INTENT_ENTITY, entity);
                startActivity(intent);
            }
        });
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
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                new AlertDialog.Builder(getActivity()).setTitle(R.string.com_tint)
                        .setMessage(R.string.home_delete_problem)
                        .setPositiveButton(R.string.com_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ProblemListModel.deleteItem(getActivity(), mAdapter, mList, mProblemJsonPath, mDirPath, viewHolder.getAdapterPosition());
                                dialogInterface.dismiss();
                            }
                        }).setNegativeButton(R.string.com_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        mAdapter.notifyDataSetChanged();
                    }
                }).create().show();
            }
        };
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    public void onPause() {
        super.onPause();
        //离开保存当前的位置
        ProblemListModel.saveListPosition(mRecyclerView, mLinearLayoutManager, mSpHelper);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshEvent(SettingActivity.refreshEvent event) {
        initData();
        mAdapter.notifyDataSetChanged();
    }

    //todo 1.使用rxJava 在子线程执行操作
    //todo 3.添加进入leetCode 账号
    protected void initData() {
        toggleDialogAdvance(true);
        showDialog();
        //创建文件夹
        File dir = new File(mDirPath);
        if (!dir.exists())
            dir.mkdirs();

        File file = new File(mProblemJsonPath);
        try {
            ProblemListModel.initProblemsFromFile(getActivity(), mList, file, mProblemType);

            if (!ListUtil.isEmpty(mList)) {
                mRecyclerView.setVisibility(View.VISIBLE);
                mIvNoData.setVisibility(View.GONE);
                mTvInput.setVisibility(View.GONE);
            } else {
                mRecyclerView.setVisibility(View.GONE);
                mIvNoData.setVisibility(View.VISIBLE);
                mTvInput.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            hideDialog();
        }
    }

    public void goTop() {
        mRecyclerView.scrollToPosition(0);
    }
}