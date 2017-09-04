package com.github.keyboard3.developerinterview;

import android.Manifest;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.github.keyboard3.developerinterview.utils.SharePreferencesHelper;
import com.werb.mediautilsdemo.CustomPermissionChecker;
import com.werb.mediautilsdemo.MediaUtils;

import org.joda.time.DateTime;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class AudioListActivity extends AppCompatActivity {
    public static final String TAG = "AudioListActivity";
    List<String> audioList = new ArrayList<>();
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    public static final int P_READ_EXTERNAL_STORAGE = 101;
    public static final int P_RECORD_AUDIO = 102;
    private TextView btn_record, info;
    private ImageView micIcon;
    private MediaUtils mediaUtils;
    private boolean isCancel;
    private Chronometer chronometer;
    private RelativeLayout audioLayout;
    private String duration;
    private CustomPermissionChecker permissionChecker;
    private AudioAdapter adapter;
    private Problem entity;
    private String path;
    private SharePreferencesHelper spHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("语音答案");
        initPre();
        initView();
    }

    private void initPre() {
        entity = (Problem) getIntent().getSerializableExtra(Config.INTENT_ENTITY);
        path = entity.getStorageDir() + "/" + entity.id + "/";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        permissionChecker = new CustomPermissionChecker(this);
        if (permissionChecker.isLackPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE})) {
            permissionChecker.requestPermissions(P_READ_EXTERNAL_STORAGE);
        } else {
            initData();
        }
        if (permissionChecker.isLackPermissions(new String[]{Manifest.permission.RECORD_AUDIO})) {
            permissionChecker.requestPermissions(P_RECORD_AUDIO);
        }
        spHelper = new SharePreferencesHelper(this, entity.getTypeName());
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
                if (permissionChecker.hasAllPermissionsGranted(grantResults)) {
                    initData();
                } else {
                    permissionChecker.showDialog();
                }
                break;
            case P_RECORD_AUDIO:
                if (permissionChecker.hasAllPermissionsGranted(grantResults)) {
                } else {
                    permissionChecker.showDialog();
                }
                break;
        }
    }

    private void initView() {
        RecyclerView recyclerView = findViewById(R.id.rl_content);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
        adapter = new AudioAdapter(audioList, this);
        recyclerView.setAdapter(adapter);

        mediaUtils = new MediaUtils(this);
        mediaUtils.setRecorderType(MediaUtils.MEDIA_AUDIO);
        mediaUtils.setTargetDir(new File(path));

        btn_record = findViewById(R.id.btn_record);
        info = findViewById(R.id.tv_info);
        btn_record.setOnTouchListener(touchListener);
        chronometer = findViewById(R.id.time_display);
        chronometer.setOnChronometerTickListener(tickListener);
        micIcon = findViewById(R.id.mic_icon);
        audioLayout = findViewById(R.id.audio_layout);
    }

    private void initData() {
        audioList.clear();
        //从指定目录下所有文件的名字  组成绝对文件的地址
        File dir = new File(path);
        if (dir == null) return;
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File item : files) {
            Log.d(TAG, "path:" + item.getAbsolutePath());
            audioList.add(item.getAbsolutePath());
        }
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_delete:
                List<String> selectedItems = adapter.getSelectedItems();
                for (String path : selectedItems) {
                    File file = new File(path);
                    file.delete();
                }
                initData();
                adapter.init();
                return true;
            case R.id.action_set:
                List<String> signleItems = adapter.getSelectedItems();
                if (signleItems.size() > 1) {
                    Toast.makeText(this, "只能选中一条", Toast.LENGTH_SHORT).show();
                    return true;
                }
                //保存到sharePre
                spHelper.putString(entity.id, signleItems.get(0));
                Toast.makeText(this, "设置成功！", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            DateTime dateTime = new DateTime();
            String time = dateTime.toString("yyyy-MM-dd HH-mm-ss");

            mediaUtils.setTargetName(time + ".m4a");

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
                            if (isCancel) {
                                isCancel = false;
                                mediaUtils.stopRecordUnSave();
                                Toast.makeText(AudioListActivity.this, "取消保存", Toast.LENGTH_SHORT).show();
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
                                        initData();
                                        break;
                                }
                            }
                            break;
                        case MotionEvent.ACTION_MOVE:
                            float currentY = event.getY();
                            if (downY - currentY > 10) {
                                moveAnim();
                                isCancel = true;
                            } else {
                                isCancel = false;
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
                duration = d;
                return Integer.valueOf(d);
            } else {
                duration = c + d;
                return Integer.valueOf(c + d);
            }
        } else {
            duration = "60";
            return -1;
        }

    }

    private void startAnim(boolean isStart) {
        audioLayout.setVisibility(View.VISIBLE);
        info.setText("上滑取消");
        btn_record.setBackground(getResources().getDrawable(R.color.colorPrimary));
        micIcon.setBackground(null);
        micIcon.setBackground(getResources().getDrawable(R.drawable.ic_mic_white_24dp));
        if (isStart) {
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.setFormat("%S");
            chronometer.start();
        }
    }

    private void stopAnim() {
        audioLayout.setVisibility(View.GONE);
        btn_record.setBackground(getResources().getDrawable(R.color.colorPrimary));
        chronometer.stop();
    }

    private void moveAnim() {
        info.setText("松手取消");
        micIcon.setBackground(null);
        micIcon.setBackground(getResources().getDrawable(R.drawable.ic_undo_black_24dp));
    }
}
