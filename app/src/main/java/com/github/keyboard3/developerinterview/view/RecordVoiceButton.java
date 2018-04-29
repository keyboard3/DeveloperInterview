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
 * 语音记录按钮
 *
 * @author keyboard3
 * @date 2017/9/23
 */

public class RecordVoiceButton extends android.support.v7.widget.AppCompatTextView {
    private static final String TAG = "RecordVoiceButton";

    private Chronometer chronometer;
    private TextView infoView;
    private ImageView iconView;
    private PopupWindow tintFloatView;

    private MediaUtils mediaUtil;
    private OnRecordListener recordListener;
    private String fileName;
    private boolean isCancel;
    private int maxRecordTimeSecond = 120;


    public RecordVoiceButton(Context context) {
        super(context);
    }

    public RecordVoiceButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordVoiceButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(Activity activity, String path, String voiceFileName) {
        mediaUtil = new MediaUtils(activity);
        mediaUtil.setRecorderType(MediaUtils.MEDIA_AUDIO);
        mediaUtil.setTargetDir(new File(path));
        fileName = voiceFileName;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (TextUtils.isEmpty(fileName)) throw new IllegalArgumentException("fileName must pre have content");

        String fileName = this.fileName + "-" + (System.currentTimeMillis() % 10000);
        mediaUtil.setTargetName(fileName + ".m4a");
        float downY = 0;
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startAnim(true);
                mediaUtil.record();
                if (recordListener != null) recordListener.onStart();
                break;
            case MotionEvent.ACTION_UP:
                stopAnim();
                if (isCancel) {
                    isCancel = false;
                    mediaUtil.stopRecordUnSave();
                    Toast.makeText(getContext(), R.string.audio_cancel_save, Toast.LENGTH_SHORT).show();
                } else {
                    int duration = getDuration(chronometer.getText().toString());
                    if (duration < 5) {
                        mediaUtil.stopRecordUnSave();
                        Toast.makeText(getContext(), R.string.audio_time_short, Toast.LENGTH_SHORT).show();
                        if (recordListener != null) {
                            recordListener.onError(getResources().getString(R.string.audio_time_short));
                        }
                    } else {
                        mediaUtil.stopRecordSave();
                        Toast.makeText(getContext(), R.string.audio_file_save, Toast.LENGTH_SHORT).show();
                        if (recordListener != null) {
                            recordListener.onEnd();
                        }
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
            default:

        }
        super.dispatchTouchEvent(event);
        return true;
    }

    private int getDuration(String str) {
        String value = "0";
        String a = str.substring(0, 1);
        String b = str.substring(1, 2);
        String c = str.substring(3, 4);
        String d = str.substring(4);
        String mDuration;
        if (value.equals(a) && b.equals(value)) {
            if (c.equals(value) && Integer.valueOf(d) < 1) {
                return -2;
            } else if (c.equals(value) && Integer.valueOf(d) > 1) {
                mDuration = d;
            } else {
                mDuration = c + d;
            }
        } else {
            mDuration = maxRecordTimeSecond + "";
        }
        return Integer.valueOf(mDuration);
    }

    private void startAnim(boolean isStart) {
        if (tintFloatView == null) {
            View contentView = LayoutInflater.from(getContext()).inflate(R.layout.pop_audio, null);
            chronometer = contentView.findViewById(R.id.time_display);
            infoView = contentView.findViewById(R.id.tv_info);
            iconView = contentView.findViewById(R.id.mic_icon);
            chronometer.setOnChronometerTickListener(tickListener);
            tintFloatView = new PopupWindow(contentView, SystemUtil.dp2px(getContext(), 100), SystemUtil.dp2px(getContext(), 100));
            tintFloatView.setAnimationStyle(android.R.style.Animation_Dialog);
        }

        infoView.setText(R.string.audio_up_cancel);
        setBackground(getResources().getDrawable(R.color.color_primary));
        iconView.setBackground(null);
        iconView.setBackground(getResources().getDrawable(R.drawable.ic_mic_white_24dp));
        if (isStart) {
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.setFormat("%S");
            chronometer.start();
        }
        if (!tintFloatView.isShowing()) {
            tintFloatView.showAtLocation(this, Gravity.CENTER, 0, 0);
        }
    }

    private void stopAnim() {
        if (tintFloatView != null) {
            tintFloatView.dismiss();
        }
        setBackground(getResources().getDrawable(R.color.color_primary));
        chronometer.stop();
    }

    private void moveAnim() {
        if (tintFloatView != null) {
            tintFloatView.dismiss();
        }
        infoView.setText(R.string.audio_release_cancel);
        iconView.setBackground(null);
        iconView.setBackground(getResources().getDrawable(R.drawable.ic_undo_black_24dp));
    }

    Chronometer.OnChronometerTickListener tickListener = new Chronometer.OnChronometerTickListener() {
        @Override
        public void onChronometerTick(Chronometer chronometer) {
            if (SystemClock.elapsedRealtime() - chronometer.getBase() > maxRecordTimeSecond * 1000) {
                stopAnim();
                mediaUtil.stopRecordSave();
                Toast.makeText(getContext(), R.string.audio_record_out_time, Toast.LENGTH_SHORT).show();
                String path = mediaUtil.getTargetFilePath();
                Toast.makeText(getContext(), getContext().getString(R.string.audio_file_save_to) + path, Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void setOnRecordListener(OnRecordListener listener) {
        recordListener = listener;
    }

    public interface OnRecordListener {

        void onStart();

        void onEnd();

        void onError(String msg);
    }
}
