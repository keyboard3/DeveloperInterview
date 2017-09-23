package com.github.keyboard3.developerinterview;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.keyboard3.developerinterview.adapter.AudioAdapter;
import com.github.keyboard3.developerinterview.entity.Problem;
import com.github.keyboard3.developerinterview.model.AudioModel;
import com.github.keyboard3.developerinterview.utils.FileUtil;
import com.github.keyboard3.developerinterview.utils.ListUtil;
import com.github.keyboard3.developerinterview.utils.SharePreferencesHelper;
import com.github.keyboard3.developerinterview.views.RecordButton;
import com.werb.mediautilsdemo.MediaUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 语音列表
 */
public class AudioListActivity extends BaseActivity {
    public static final String TAG = "AudioListActivity";
    public static final int P_READ_EXTERNAL_STORAGE = 101;
    public static final int P_RECORD_AUDIO = 102;

    private RecordButton mBtnRecord;

    private List<String> mAudioList = new ArrayList<>();
    private Problem mEntity;
    private String mPath;

    private AudioAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_list);

        initActionBar();
        initData();
        initHandle();
        initView();
    }

    private void initActionBar() {
        getSupportActionBar().setTitle(R.string.title_audio);
    }

    private void initData() {
        mEntity = (Problem) getIntent().getSerializableExtra(Config.INTENT_ENTITY);
        mPath = mEntity.getStorageDir() + "/" + mEntity.id + "/";
        File file = new File(mPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    private void initHandle() {
        if (!checkPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, P_READ_EXTERNAL_STORAGE)) {
            AudioModel.getAudioDataFromFile(mAudioList, mPath, adapter);
        }
        checkPermission(new String[]{Manifest.permission.RECORD_AUDIO}, P_RECORD_AUDIO);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.audio_list, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case P_READ_EXTERNAL_STORAGE:
                if (hasAllPermissionsGranted(grantResults)) {
                    AudioModel.getAudioDataFromFile(mAudioList, mPath, adapter);
                }
                break;
            default:
                hasAllPermissionsGranted(grantResults);
                break;
        }
    }

    private void initView() {
        toggleDialogAdvance(true);

        RecyclerView recyclerView = findViewById(R.id.rl_content);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
        adapter = new AudioAdapter(mAudioList, this);
        recyclerView.setAdapter(adapter);

        mBtnRecord = findViewById(R.id.btn_record);
        mBtnRecord.init(this, mPath, mEntity.getTypeName() + "-" + mEntity.id);
        mBtnRecord.setOnRecordListener(new RecordButton.OnRecordListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onEnd() {
                adapter.init();
                AudioModel.getAudioDataFromFile(mAudioList, mPath, adapter);
            }

            @Override
            public void onError(String msg) {
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                if (AudioModel.deleteAudio(getApplicationContext(), adapter)) return true;
                AudioModel.getAudioDataFromFile(mAudioList, mPath, adapter);
                adapter.init();
                return true;
            case R.id.action_set:
                if (AudioModel.setAudio(this, adapter, mEntity)) return true;
                break;
            case R.id.action_share:
                if (AudioModel.shareAudio(adapter, getApplicationContext())) return true;
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
