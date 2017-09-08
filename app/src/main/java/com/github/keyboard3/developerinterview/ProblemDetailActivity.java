package com.github.keyboard3.developerinterview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.github.keyboard3.developerinterview.data.DataFactory;
import com.github.keyboard3.developerinterview.entity.Problem;
import com.github.keyboard3.developerinterview.pattern.JavaType;
import com.github.keyboard3.developerinterview.pattern.ProblemType;
import com.github.keyboard3.developerinterview.pattern.ProblemTypeFactory;
import com.github.keyboard3.developerinterview.utils.SharePreferencesHelper;
import com.github.keyboard3.developerinterview.utils.SystemUtil;
import com.wang.avi.AVLoadingIndicatorView;

import nl.changer.audiowife.AudioWife;

/**
 * 题目详情
 */
public class ProblemDetailActivity extends BaseActivity {
    public static final String TAG = "ProblemDetailActivity";
    Problem entity;
    private AVLoadingIndicatorView avi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_detail);
        //从应用进来
        entity = (Problem) getIntent().getSerializableExtra(Config.INTENT_ENTITY);
        //从外面进来
        if (entity == null) {
            String uri = getIntent().getStringExtra(Config.INTENT_KEY);
            if (openComingIntent(Uri.parse(uri))) {
                return;
            }
        }
        if (entity == null) {
            Toast.makeText(this, "不存在该题目", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        initViews();
    }

    private boolean openComingIntent(Uri data) {
        String id = data.getQueryParameter(Config.INTENT_ID);
        String type = data.getQueryParameter(Config.INTENT_TYPE);
        String source = data.getQueryParameter(Config.INTENT_SOURCE);
        String title = data.getQueryParameter(Config.INTENT_TITLE);
        if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(type)) {
            ProblemType problemType = ProblemTypeFactory.getProblemType(Integer.parseInt(type));
            entity = new DataFactory(getApplicationContext(), problemType).queryProblem(id);
            if (entity == null) {
                if (!TextUtils.isEmpty(source) && !TextUtils.isEmpty(title)) {
                    Intent intent = new Intent(this, WebViewActivity.class);
                    intent.putExtra(Config.INTENT_KEY, source);
                    intent.putExtra(Config.INTENT_SEARCH_KEY, title);
                    startActivity(intent);
                    finish();
                    return true;
                }
            }
        }
        return false;
    }

    private void initViews() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("题目详情");

        TextView tvTitle = findViewById(R.id.tv_title);
        TextView tvContent = findViewById(R.id.tv_content);
        TextView tvSource = findViewById(R.id.tv_source);
        TextView tvAudio = findViewById(R.id.tv_audio);
        WebView wb_answer = findViewById(R.id.wb_answer);
        avi = findViewById(R.id.avi);

        tvTitle.setText(entity.title);
        tvContent.setText(entity.content);
        wb_answer.getSettings().setDefaultTextEncodingName("UTF-8");
        wb_answer.loadData(entity.answer, "text/html; charset=UTF-8", null);
        wb_answer.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                avi.show();
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                avi.hide();
                super.onPageFinished(view, url);
            }
        });
        tvAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProblemDetailActivity.this, AudioListActivity.class);
                intent.putExtra(Config.INTENT_ENTITY, entity);
                startActivity(intent);
            }
        });
        tvSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProblemDetailActivity.this, WebViewActivity.class);
                intent.putExtra(Config.INTENT_KEY, entity.source);
                intent.putExtra(Config.INTENT_SEARCH_KEY, entity.title);
                startActivity(intent);
            }
        });
        //todo 1 修改答案
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        ViewGroup audioContainer = findViewById(R.id.audio_container);
        SharePreferencesHelper spHelper = new SharePreferencesHelper(this, JavaType.typeStr);
        String path = spHelper.getSP().getString(entity.id, "");
        if (!TextUtils.isEmpty(path)) {
            audioContainer.removeAllViews();
            new AudioWife().init(this, Uri.parse(path))
                    .useDefaultUi(audioContainer, getLayoutInflater());
        }
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
                SystemUtil.sendText(this, Config.getShareInnerLink(this, entity));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
