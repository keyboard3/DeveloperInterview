package com.github.keyboard3.developerinterview;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.github.keyboard3.developerinterview.adapter.AudioAdapter;
import com.github.keyboard3.developerinterview.base.BaseActivity;
import com.github.keyboard3.developerinterview.entity.Problem;
import com.github.keyboard3.developerinterview.model.AudioModel;
import com.github.keyboard3.developerinterview.view.RecordVoiceButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 语音列表
 *
 * @author ganchunyu
 */
public class AudioListActivity extends BaseActivity {
    private static final int PERMISSION_READ_EXTERNAL_STORAGE = 101;
    private static final int PERMISSION_RECORD_AUDIO = 102;

    private final List<String> audios = new ArrayList<>();
    private Problem problem;
    private String problemPath;
    private AudioAdapter adapter;

    @BindView(R.id.btn_record) RecordVoiceButton recordVoiceView;
    @BindView(R.id.rl_content) RecyclerView audiosView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_list);
        ButterKnife.bind(this);

        initActionBar();
        initProblemFromIntentAndMkdir();
        initAudiosWithProblem();
        initViewsWithAudios();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.audio_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                if (AudioModel.deleteAudio(getApplicationContext(), adapter)) return true;
                AudioModel.initAudiosFromPathAndNotify(audios, problemPath, adapter);
                adapter.init();
                return true;
            case R.id.action_set:
                if (AudioModel.setAudio(this, adapter, problem)) {
                    return true;
                }
                break;
            case R.id.action_share:
                if (AudioModel.shareAudio(adapter, getApplicationContext())) {
                    return true;
                }
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_READ_EXTERNAL_STORAGE:
                if (hasAllPermissionsGranted(grantResults)) {
                    AudioModel.initAudiosFromPathAndNotify(audios, problemPath, adapter);
                }
                break;
            default:
                hasAllPermissionsGranted(grantResults);
                break;
        }
    }

    private void initActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_audio);
        }
    }

    private void initProblemFromIntentAndMkdir() {
        problem = (Problem) getIntent().getSerializableExtra(ConfigConst.INTENT_ENTITY);
        problemPath = problem.getStorageDir() + "/" + problem.id + "/";
        File file = new File(problemPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    private void initAudiosWithProblem() {
        if (!checkPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_EXTERNAL_STORAGE)) {
            AudioModel.initAudiosFromPathAndNotify(audios, problemPath, adapter);
        }
        checkPermission(new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_RECORD_AUDIO);
    }

    private void initViewsWithAudios() {
        toggleDialogAdvance(true);

        audiosView.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
        adapter = new AudioAdapter(audios, this);
        audiosView.setAdapter(adapter);

        String fileName = problem.getTypeName() + "-" + problem.id;
        recordVoiceView.init(this, problemPath, fileName);
        recordVoiceView.setOnRecordListener(new RecordVoiceButton.OnRecordListener() {
            @Override
            public void onStart() {}

            @Override
            public void onEnd() {
                adapter.init();
                AudioModel.initAudiosFromPathAndNotify(audios, problemPath, adapter);
            }

            @Override
            public void onError(String msg) {}
        });
    }
}
