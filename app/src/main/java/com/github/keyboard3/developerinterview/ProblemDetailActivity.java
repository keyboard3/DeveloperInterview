package com.github.keyboard3.developerinterview;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.keyboard3.developerinterview.base.BaseActivity;
import com.github.keyboard3.developerinterview.entity.Problem;
import com.github.keyboard3.developerinterview.model.ShareModel;
import com.github.keyboard3.developerinterview.model.WebViewModel;
import com.github.keyboard3.developerinterview.pattern.JavaState;
import com.github.keyboard3.developerinterview.util.SharePreferencesHelper;
import com.github.keyboard3.developerinterview.util.SystemUtil;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nl.changer.audiowife.AudioWife;

/**
 * 题目详情页
 *
 * @author keyboard3
 */
public class ProblemDetailActivity extends BaseActivity {
    public static final String TAG = "ProblemDetailActivity";
    private Problem problem;
    @BindView(R.id.tv_title) TextView titleView;
    @BindView(R.id.tv_content) TextView contentView;
    @BindView(R.id.tv_source) TextView sourceView;
    @BindView(R.id.tv_audio) TextView audioView;
    @BindView(R.id.wb_answer) WebView answerHtmlView;
    @BindView(R.id.audio_container) ViewGroup audioContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_detail);
        setTitle(R.string.title_problem_detail);
        ButterKnife.bind(this);

        if (initProblemFromIntentAndCheck()) return;
        initViewsWithProblem();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initAudioViewWithProblem();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.problem, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                SystemUtil.sendText(this, ConfigConst.getShareInnerLink(this, problem));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.tv_audio) void audioViewClick(){
        Intent intent = new Intent(ProblemDetailActivity.this, AudioListActivity.class);
        intent.putExtra(ConfigConst.INTENT_ENTITY, problem);
        startActivity(intent);
    }

    @OnClick(R.id.tv_source) void sourceViewClick(){
        Intent intent = new Intent(ProblemDetailActivity.this, WebViewActivity.class);
        intent.putExtra(ConfigConst.INTENT_KEY, problem.source);
        intent.putExtra(ConfigConst.INTENT_SEARCH_KEY, problem.title);
        startActivity(intent);
    }

    private boolean initProblemFromIntentAndCheck() {
        try {
            problem = (Problem) getIntent().getSerializableExtra(ConfigConst.INTENT_ENTITY);
            String uri = "";

            if (problem != null) return false;

            uri = getIntent().getStringExtra(ConfigConst.INTENT_KEY);
            if (TextUtils.isEmpty(uri))
                uri = getIntent().getData().toString();

            problem = ShareModel.problemOpenComingIntent(this, Uri.parse(uri));

            if (problem == null) {
                Toast.makeText(this, R.string.problem_no_exist, Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return problem == null;
    }

    private void initViewsWithProblem() {
        titleView.setText(problem.title);
        contentView.setText(problem.content);

        toggleDialogAdvance(true);
        WebViewModel.initWebView(answerHtmlView, problem.answer, this);
    }

    private void initAudioViewWithProblem() {
        SharePreferencesHelper spHelper = new SharePreferencesHelper(this, JavaState.typeStr);
        String path = spHelper.getSP().getString(problem.id, "");
        if (!TextUtils.isEmpty(path)) {
            audioContainer.removeAllViews();
            new AudioWife().init(this, Uri.parse(path))
                    .useDefaultUi(audioContainer, getLayoutInflater());
        }
    }
}
