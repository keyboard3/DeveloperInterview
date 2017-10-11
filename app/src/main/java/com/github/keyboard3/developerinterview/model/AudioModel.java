package com.github.keyboard3.developerinterview.model;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.github.keyboard3.developerinterview.R;
import com.github.keyboard3.developerinterview.adapter.AudioAdapter;
import com.github.keyboard3.developerinterview.entity.Problem;
import com.github.keyboard3.developerinterview.util.FileUtil;
import com.github.keyboard3.developerinterview.util.ListUtil;
import com.github.keyboard3.developerinterview.util.SharePreferencesHelper;

import java.io.File;
import java.util.List;

/**
 * Created by keyboard3 on 2017/9/22.
 */

public class AudioModel {
    public static boolean shareAudio(AudioAdapter adapter, Context applicationContext) {
        List<String> list = adapter.getSelectedItems();
        if (list.size() > 1) {
            Toast.makeText(applicationContext, R.string.audio_select_only, Toast.LENGTH_SHORT).show();
            return true;
        } else if (ListUtil.isEmpty(list)) {
            Toast.makeText(applicationContext, R.string.audio_please_select, Toast.LENGTH_SHORT).show();
            return true;
        }
        //保存到sharePre
        FileUtil.openFile(applicationContext, list.get(0));
        return false;
    }

    public static boolean setAudio(Activity activity, AudioAdapter adapter, Problem mEntity) {
        List<String> singleItems = adapter.getSelectedItems();
        if (singleItems.size() > 1) {
            Toast.makeText(activity.getApplicationContext(), R.string.audio_select_only, Toast.LENGTH_SHORT).show();
            return true;
        } else if (ListUtil.isEmpty(singleItems)) {
            Toast.makeText(activity.getApplicationContext(), R.string.audio_please_select, Toast.LENGTH_SHORT).show();
            return true;
        }

        SharePreferencesHelper spHelper = new SharePreferencesHelper(activity, mEntity.getTypeName());
        spHelper.putString(mEntity.id, singleItems.get(0));
        Toast.makeText(activity.getApplicationContext(), R.string.audio_set_success, Toast.LENGTH_SHORT).show();
        return false;
    }

    public static boolean deleteAudio(Context context, AudioAdapter adapter) {
        List<String> selectedItems = adapter.getSelectedItems();
        if (ListUtil.isEmpty(selectedItems)) {
            Toast.makeText(context, "请选中", Toast.LENGTH_SHORT).show();
            return true;
        }
        for (String path : selectedItems) {
            File file = new File(path);
            file.delete();
        }
        return false;
    }

    public static void getAudioDataFromFile(List<String> mAudioList, String mPath, AudioAdapter adapter) {
        mAudioList.clear();
        //从指定目录下所有文件的名字  组成绝对文件的地址
        File dir = new File(mPath);
        if (dir == null) return;

        File[] files = dir.listFiles();
        if (files == null) return;

        for (File item : files) {
            mAudioList.add(item.getAbsolutePath());
        }
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }
}
