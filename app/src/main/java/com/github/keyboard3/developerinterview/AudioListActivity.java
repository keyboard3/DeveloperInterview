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

    private TextView mBtnRecord;
    private TextView mInfo;
    private ImageView micIcon;
    private RelativeLayout mAudioLayout;

    private List<String> mAudioList = new ArrayList<>();
    private Problem mEntity;
    private String mPath;
    private String mDuration;
    private boolean mIsCancel;

    private MediaUtils mediaUtils;
    private Chronometer chronometer;
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
        if (checkPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, P_READ_EXTERNAL_STORAGE)) {
            getAudioDataFromFile();
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
                    getAudioDataFromFile();
                }
                break;
            default:
                hasAllPermissionsGranted(grantResults);
                break;
        }
    }

    private void initView() {
        RecyclerView recyclerView = findViewById(R.id.rl_content);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
        adapter = new AudioAdapter(mAudioList, this);
        recyclerView.setAdapter(adapter);

        mediaUtils = new MediaUtils(this);
        mediaUtils.setRecorderType(MediaUtils.MEDIA_AUDIO);
        mediaUtils.setTargetDir(new File(mPath));

        mBtnRecord = findViewById(R.id.btn_record);
        mInfo = findViewById(R.id.tv_info);
        mBtnRecord.setOnTouchListener(touchListener);
        chronometer = findViewById(R.id.time_display);
        chronometer.setOnChronometerTickListener(tickListener);
        micIcon = findViewById(R.id.mic_icon);
        mAudioLayout = findViewById(R.id.audio_layout);

        toggleDialogAdvance(true);
    }

    private void getAudioDataFromFile() {
        mAudioList.clear();
        //从指定目录下所有文件的名字  组成绝对文件的地址
        File dir = new File(mPath);
        if (dir == null) return;

        File[] files = dir.listFiles();
        if (files == null) return;

        for (File item : files) {
            Log.d(TAG, "path:" + item.getAbsolutePath());
            mAudioList.add(item.getAbsolutePath());
        }
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                if (AudioModel.deleteAudio(getApplicationContext(), adapter)) return true;
                getAudioDataFromFile();
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

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            String name = mEntity.getTypeName() + "-" + mEntity.id + "-" + (System.currentTimeMillis() % 10000);
            mediaUtils.setTargetName(name + ".m4a");

            boolean ret = false;
            float downY = 0;
            int action = event.getAction();
            switch (v.getId()) {
                case R.id.btn_record:
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            startAnim(true);
                            mediaUtils.record();
                            ret = true;
                            break;
                        case MotionEvent.ACTION_UP:
                            stopAnim();
                            if (mIsCancel) {
                                mIsCancel = false;
                                mediaUtils.stopRecordUnSave();
                                Toast.makeText(AudioListActivity.this, R.string.audio_cancel_save, Toast.LENGTH_SHORT).show();
                            } else {
                                int duration = getDuration(chronometer.getText().toString());
                                switch (duration) {
                                    case -1:
                                        break;
                                    case -2:
                                        mediaUtils.stopRecordUnSave();
                                        Toast.makeText(AudioListActivity.this, "时间太短", Toast.LENGTH_SHORT).show();
                                        break;
                                    default:
                                        mediaUtils.stopRecordSave();
                                        //String path = mediaUtils.getTargetFilePath();
                                        Toast.makeText(AudioListActivity.this, "文件以保存成功", Toast.LENGTH_SHORT).show();
                                        adapter.init();
                                        getAudioDataFromFile();
                                        break;
                                }
                            }
                            break;
                        case MotionEvent.ACTION_MOVE:
                            float currentY = event.getY();
                            if (downY - currentY > 10) {
                                moveAnim();
                                mIsCancel = true;
                            } else {
                                mIsCancel = false;
                                startAnim(false);
                            }
                            break;
                    }
                    break;
            }
            return ret;
        }
    };

    Chronometer.OnChronometerTickListener tickListener = new Chronometer.OnChronometerTickListener() {
        @Override
        public void onChronometerTick(Chronometer chronometer) {
            if (SystemClock.elapsedRealtime() - chronometer.getBase() > 60 * 1000) {
                stopAnim();
                mediaUtils.stopRecordSave();
                Toast.makeText(AudioListActivity.this, "录音超时", Toast.LENGTH_SHORT).show();
                String path = mediaUtils.getTargetFilePath();
                Toast.makeText(AudioListActivity.this, "文件以保存至：" + path, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private int getDuration(String str) {
        String a = str.substring(0, 1);
        String b = str.substring(1, 2);
        String c = str.substring(3, 4);
        String d = str.substring(4);
        if (a.equals("0") && b.equals("0")) {
            if (c.equals("0") && Integer.valueOf(d) < 1) {
                return -2;
            } else if (c.equals("0") && Integer.valueOf(d) > 1) {
                mDuration = d;
                return Integer.valueOf(d);
            } else {
                mDuration = c + d;
                return Integer.valueOf(c + d);
            }
        } else {
            mDuration = "60";
            return -1;
        }

    }

    private void startAnim(boolean isStart) {
        mAudioLayout.setVisibility(View.VISIBLE);
        mInfo.setText("上滑取消");
        mBtnRecord.setBackground(getResources().getDrawable(R.color.color_primary));
        micIcon.setBackground(null);
        micIcon.setBackground(getResources().getDrawable(R.drawable.ic_mic_white_24dp));
        if (isStart) {
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.setFormat("%S");
            chronometer.start();
        }
    }

    private void stopAnim() {
        mAudioLayout.setVisibility(View.GONE);
        mBtnRecord.setBackground(getResources().getDrawable(R.color.color_primary));
        chronometer.stop();
    }

    private void moveAnim() {
        mInfo.setText("松手取消");
        micIcon.setBackground(null);
        micIcon.setBackground(getResources().getDrawable(R.drawable.ic_undo_black_24dp));
    }
}
