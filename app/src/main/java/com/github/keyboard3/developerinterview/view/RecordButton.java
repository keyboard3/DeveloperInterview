package com.github.keyboard3.developerinterview.view;

import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.keyboard3.developerinterview.R;
import com.github.keyboard3.developerinterview.util.SystemUtil;
import com.werb.mediautilsdemo.MediaUtils;

import java.io.File;

/**
 * Created by keyboard3 on 2017/9/23.
 */

public class RecordButton extends android.support.v7.widget.AppCompatTextView {
    private static final String TAG = "RecordButton";

    private Chronometer mChronometer;
    private TextView mInfo;
    private ImageView micIcon;
    private PopupWindow mPopWindow;

    private MediaUtils mMediaUtils;
    private OnRecordListener mRecordListener;
    private String mFileName;
    private boolean mIsCancel;
    private int maximum = 120;


    public RecordButton(Context context) {
        super(context);
    }

    public RecordButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(Activity activity, String path, String fileName) {
        mMediaUtils = new MediaUtils(activity);
        mMediaUtils.setRecorderType(MediaUtils.MEDIA_AUDIO);
        mMediaUtils.setTargetDir(new File(path));
        mFileName = fileName;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (TextUtils.isEmpty(mFileName)) {
            throw new IllegalArgumentException("fileName must pre have content");
        }
        String fileName = mFileName + "-" + (System.currentTimeMillis() % 10000);
        mMediaUtils.setTargetName(fileName + ".m4a");
        float downY = 0;
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startAnim(true);
                mMediaUtils.record();
                if (mRecordListener != null) mRecordListener.onStart();
                break;
            case MotionEvent.ACTION_UP:
                stopAnim();
                if (mIsCancel) {
                    mIsCancel = false;
                    mMediaUtils.stopRecordUnSave();
                    Toast.makeText(getContext(), R.string.audio_cancel_save, Toast.LENGTH_SHORT).show();
                } else {
                    int duration = getDuration(mChronometer.getText().toString());
                    if (duration < 5) {
                        mMediaUtils.stopRecordUnSave();
                        Toast.makeText(getContext(), R.string.audio_time_short, Toast.LENGTH_SHORT).show();
                        if (mRecordListener != null)
                            mRecordListener.onError(getResources().getString(R.string.audio_time_short));
                    } else {
                        mMediaUtils.stopRecordSave();
                        Toast.makeText(getContext(), R.string.audio_file_save, Toast.LENGTH_SHORT).show();
                        if (mRecordListener != null) mRecordListener.onEnd();
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
        super.dispatchTouchEvent(event);
        return true;
    }

    private int getDuration(String str) {
        String a = str.substring(0, 1);
        String b = str.substring(1, 2);
        String c = str.substring(3, 4);
        String d = str.substring(4);
        String mDuration;
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
            mDuration = maximum + "";
            return -1;
        }
    }

    private void startAnim(boolean isStart) {
        if (mPopWindow == null) {
            View contentView = LayoutInflater.from(getContext()).inflate(R.layout.pop_audio, null);
            mChronometer = contentView.findViewById(R.id.time_display);
            mInfo = contentView.findViewById(R.id.tv_info);
            micIcon = contentView.findViewById(R.id.mic_icon);
            mChronometer.setOnChronometerTickListener(tickListener);
            mPopWindow = new PopupWindow(contentView, SystemUtil.dp2px(getContext(), 100), SystemUtil.dp2px(getContext(), 100));
            mPopWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        }

        mInfo.setText(R.string.audio_up_cancel);
        setBackground(getResources().getDrawable(R.color.color_primary));
        micIcon.setBackground(null);
        micIcon.setBackground(getResources().getDrawable(R.drawable.ic_mic_white_24dp));
        if (isStart) {
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.setFormat("%S");
            mChronometer.start();
        }
        if (!mPopWindow.isShowing()) {
            mPopWindow.showAtLocation(this, Gravity.CENTER, 0, 0);
        }
    }

    private void stopAnim() {
        if (mPopWindow != null) mPopWindow.dismiss();
        setBackground(getResources().getDrawable(R.color.color_primary));
        mChronometer.stop();
    }

    private void moveAnim() {
        if (mPopWindow != null) mPopWindow.dismiss();
        mInfo.setText(R.string.audio_release_cancel);
        micIcon.setBackground(null);
        micIcon.setBackground(getResources().getDrawable(R.drawable.ic_undo_black_24dp));
    }

    Chronometer.OnChronometerTickListener tickListener = new Chronometer.OnChronometerTickListener() {
        @Override
        public void onChronometerTick(Chronometer chronometer) {
            if (SystemClock.elapsedRealtime() - chronometer.getBase() > maximum * 1000) {
                stopAnim();
                mMediaUtils.stopRecordSave();
                Toast.makeText(getContext(), R.string.audio_record_out_time, Toast.LENGTH_SHORT).show();
                String path = mMediaUtils.getTargetFilePath();
                Toast.makeText(getContext(), getContext().getString(R.string.audio_file_save_to) + path, Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void setOnRecordListener(OnRecordListener listener) {
        mRecordListener = listener;
    }

    public interface OnRecordListener {
        void onStart();

        void onEnd();

        void onError(String msg);
    }
}
