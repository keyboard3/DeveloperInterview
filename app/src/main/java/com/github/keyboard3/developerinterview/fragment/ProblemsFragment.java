package com.github.keyboard3.developerinterview.fragment;


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

import com.github.keyboard3.developerinterview.ConfigConst;
import com.github.keyboard3.developerinterview.ProblemDetailActivity;
import com.github.keyboard3.developerinterview.R;
import com.github.keyboard3.developerinterview.SettingActivity;
import com.github.keyboard3.developerinterview.adapter.ProblemAdapter;
import com.github.keyboard3.developerinterview.base.BaseFragment;
import com.github.keyboard3.developerinterview.callback.Callback;
import com.github.keyboard3.developerinterview.data.DataFactory;
import com.github.keyboard3.developerinterview.entity.Problem;
import com.github.keyboard3.developerinterview.model.ProblemListModel;
import com.github.keyboard3.developerinterview.pattern.BaseProblemState;
import com.github.keyboard3.developerinterview.util.ListUtil;
import com.github.keyboard3.developerinterview.util.SharePreferencesHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * 内容页面 展示不同类型的问题列表
 * A ProblemsFragment {@link Fragment} subclass.
 *
 * @author keyboard3
 */
public class ProblemsFragment extends BaseFragment {
    private static String TAG = "ProblemsFragment";

    private RecyclerView mRecyclerView;
    private View mIvNoData;
    private View mTvInput;

    private List<Problem> mList = new ArrayList<>();
    private ProblemAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private BaseProblemState mState;
    private SharePreferencesHelper mSpHelper;
    private DataFactory dataFactory;

    public static ProblemsFragment newInstance(BaseProblemState state) {

        Bundle args = new Bundle();
        args.putSerializable(ConfigConst.INTENT_KEY, state);
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
        mState = (BaseProblemState) getArguments().getSerializable(ConfigConst.INTENT_KEY);
        dataFactory = new DataFactory(getActivity().getApplicationContext(), mState);

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
        mSpHelper = new SharePreferencesHelper(getActivity(), mState.getTypeStr());
        mLinearLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        ProblemListModel.restoreListPosition(mLinearLayoutManager, mSpHelper);
        mAdapter = new ProblemAdapter(mList, getActivity());
        initData();

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new ProblemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Problem entity = mList.get(position);
                Intent intent = new Intent(getActivity(), ProblemDetailActivity.class);
                intent.putExtra(ConfigConst.INTENT_ENTITY, entity);
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
                                ProblemListModel.removeProblem(getActivity(), mAdapter, mList, viewHolder.getAdapterPosition(), dataFactory.problemJsonPath);
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
    public void onRefreshEvent(SettingActivity.RefreshEvent event) {
        initData();
        mAdapter.notifyDataSetChanged();
    }

    protected void initData() {
        toggleDialogAdvance(true);
        showDialog();
        dataFactory.init2LocalProblems(new Callback<List<Problem>>() {
            @Override
            public void success(List<Problem> item) {
                mList.addAll(item);
                mAdapter.notifyDataSetChanged();
                try {
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
        });
    }

    public void goTop() {
        mRecyclerView.scrollToPosition(0);
    }
}