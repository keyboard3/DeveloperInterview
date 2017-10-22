package com.github.keyboard3.developerinterview;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import nl.changer.audiowife.AudioWife;

/**
 * 题目详情页
 *
 * @author keyboard3
 */
public class ProblemDetailActivity extends BaseActivity {
    public static final String TAG = "ProblemDetailActivity";
    private Problem mEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_detail);
        setTitle(R.string.title_problem_detail);

        if (initIntentData()) {
            return;
        }
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
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
                SystemUtil.sendText(this, ConfigConst.getShareInnerLink(this, mEntity));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean initIntentData() {
        mEntity = (Problem) getIntent().getSerializableExtra(ConfigConst.INTENT_ENTITY);
        if (mEntity == null) {
            String uri = getIntent().getStringExtra(ConfigConst.INTENT_KEY);
            if (TextUtils.isEmpty(uri)) {
                uri = getIntent().getData().toString();
            }
            mEntity = ShareModel.problemOpenComingIntent(this, Uri.parse(uri));
            if (mEntity == null) {
                return true;
            }
        }
        if (mEntity == null) {
            Toast.makeText(this, R.string.problem_no_exist, Toast.LENGTH_SHORT).show();
            finish();
            return true;
        }
        return false;
    }

    private void initViews() {
        toggleDialogAdvance(false);

        TextView tvTitle = findViewById(R.id.tv_title);
        TextView tvContent = findViewById(R.id.tv_content);
        TextView tvSource = findViewById(R.id.tv_source);
        TextView tvAudio = findViewById(R.id.tv_audio);
        WebView wb_answer = findViewById(R.id.wb_answer);

        tvTitle.setText(mEntity.title);
        tvContent.setText(mEntity.content);

        toggleDialogAdvance(true);
        WebViewModel.initWebView(wb_answer, mEntity.answer, this);

        tvAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProblemDetailActivity.this, AudioListActivity.class);
                intent.putExtra(ConfigConst.INTENT_ENTITY, mEntity);
                startActivity(intent);
            }
        });
        tvSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProblemDetailActivity.this, WebViewActivity.class);
                intent.putExtra(ConfigConst.INTENT_KEY, mEntity.source);
                intent.putExtra(ConfigConst.INTENT_SEARCH_KEY, mEntity.title);
                startActivity(intent);
            }
        });
    }


    private void initData() {
        ViewGroup audioContainer = findViewById(R.id.audio_container);
        SharePreferencesHelper spHelper = new SharePreferencesHelper(this, JavaState.typeStr);
        String path = spHelper.getSP().getString(mEntity.id, "");
        if (!TextUtils.isEmpty(path)) {
            audioContainer.removeAllViews();
            new AudioWife().init(this, Uri.parse(path))
                    .useDefaultUi(audioContainer, getLayoutInflater());
        }
    }
}
