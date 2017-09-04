package com.github.keyboard3.developerinterview;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.github.keyboard3.developerinterview.entity.Problem;
import com.github.keyboard3.developerinterview.utils.SharePreferencesHelper;

import nl.changer.audiowife.AudioWife;

public class ProblemDetailActivity extends AppCompatActivity {
    public static final String TAG = "ProblemDetailActivity";
    Problem entity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_detail);

        entity = (Problem) getIntent().getSerializableExtra(Config.INTENT_ENTITY);
        initViews();
    }

    private void initViews() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("题目详情");

        TextView tvTitle = findViewById(R.id.tv_title);
        TextView tvContent = findViewById(R.id.tv_content);
        TextView tvSource = findViewById(R.id.tv_source);
        TextView tvAudio = findViewById(R.id.tv_audio);
        WebView wb_answer = findViewById(R.id.wb_answer);

        tvTitle.setText(entity.title);
        tvContent.setText(entity.content);
        wb_answer.getSettings().setDefaultTextEncodingName("UTF-8");
        wb_answer.loadData(entity.answer, "text/html; charset=UTF-8", null);
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
                Log.d(TAG, "source:" + entity.source);
                intent.putExtra(Config.INTENT_KEY, entity.source);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        ViewGroup audioContainer = findViewById(R.id.audio_container);
        SharePreferencesHelper spHelper = new SharePreferencesHelper(this, Config.ProblemJava);
        String path = spHelper.getString(entity.id);
        if (!TextUtils.isEmpty(path)) {
            audioContainer.removeAllViews();
            new AudioWife().init(this, Uri.parse(path))
                    .useDefaultUi(audioContainer, getLayoutInflater());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
