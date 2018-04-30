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
import com.github.keyboard3.developerinterview.data.ProblemsDrive;
import com.github.keyboard3.developerinterview.entity.Problem;
import com.github.keyboard3.developerinterview.model.ProblemListModel;
import com.github.keyboard3.developerinterview.pattern.BaseProblemState;
import com.github.keyboard3.developerinterview.util.ListUtil;
import com.github.keyboard3.developerinterview.util.SharePreferencesHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 内容页面 展示不同类型的问题列表
 * A ProblemsFragment {@link Fragment} subclass.
 *
 * @author keyboard3
 */
public class ProblemsFragment extends BaseFragment {
    private static String TAG = "ProblemsFragment";

    @BindView(R.id.rl_content) RecyclerView problemsView;
    @BindView(R.id.iv_nodata) View noDataView;
    @BindView(R.id.tv_input) View inputView;

    private List<Problem> problems = new ArrayList<>();
    private ProblemAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private BaseProblemState problemState;
    private SharePreferencesHelper spHelper;
    private ProblemsDrive problemsDrive;

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
        View view= inflater.inflate(R.layout.fragment_problems, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initProblemDataFromIntent();
    }

    @Override
    public void onResume() {
        super.onResume();
        resumeLastPositonOnProblemsView();
        initProblemsFromNet();
        initProblemsViewWithDaa();
    }

    private void initProblemsViewWithDaa() {
        problemsView.setAdapter(adapter);
        adapter.setOnItemClickListener((itemView, position) -> {
            Problem entity = problems.get(position);
            Intent intent = new Intent(getActivity(), ProblemDetailActivity.class);
            intent.putExtra(ConfigConst.INTENT_ENTITY, entity);
            startActivity(intent);
        });
        new ItemTouchHelper(itemTouchCallback).attachToRecyclerView(problemsView);
    }

    @Override
    public void onPause() {
        super.onPause();
        ProblemListModel.saveListPosition(problemsView, linearLayoutManager, spHelper);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initProblemDataFromIntent() {
        problemState = (BaseProblemState) getArguments().getSerializable(ConfigConst.INTENT_KEY);
        problemsDrive = new ProblemsDrive(getActivity().getApplicationContext(), problemState);
    }

    void initProblemsFromNet() {
        toggleDialogAdvance(true);
        showDialog();
        problemsDrive.asyncFetchProblems(new Callback<List<Problem>>() {
            @Override
            public void success(List<Problem> item) {
                try {
                    problems.addAll(item);
                    adapter.notifyDataSetChanged();
                    showEmptyView(ListUtil.isEmpty(problems));
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    hideDialog();
                }
            }

            @Override
            public void fail(Throwable error) {
                hideDialog();
            }
        });
    }

    private void showEmptyView(boolean ishide) {
        if (!ishide) {
            problemsView.setVisibility(View.VISIBLE);
            noDataView.setVisibility(View.GONE);
            inputView.setVisibility(View.GONE);
        } else {
            problemsView.setVisibility(View.GONE);
            noDataView.setVisibility(View.VISIBLE);
            inputView.setVisibility(View.VISIBLE);
        }
    }

    void resumeLastPositonOnProblemsView() {
        spHelper = problemState.createSpHelper(getActivity());
        linearLayoutManager = new LinearLayoutManager(this.getActivity());
        problemsView.setLayoutManager(linearLayoutManager);
        ProblemListModel.restoreListPosition(linearLayoutManager, spHelper);
        adapter = new ProblemAdapter(problems, getActivity());
    }

    @OnClick(R.id.tv_input) void inputViewClick(){
        startActivity(new Intent(getActivity(), SettingActivity.class));
    }

    ItemTouchHelper.Callback itemTouchCallback = new ItemTouchHelper.Callback() {
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
                    .setPositiveButton(R.string.com_ok, (dialogInterface, i) -> {
                        ProblemListModel.removeProblem(getActivity(), adapter, problems, viewHolder.getAdapterPosition(), problemsDrive.problemJsonPath);
                        dialogInterface.dismiss();
                    }).setNegativeButton(R.string.com_cancel, (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        adapter.notifyDataSetChanged();
                    }).create().show();
        }
    };

    public void goTop() {
        if(problemsView == null) return;
        problemsView.scrollToPosition(0);
    }
}