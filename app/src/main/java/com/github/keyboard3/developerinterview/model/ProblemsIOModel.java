package com.github.keyboard3.developerinterview.model;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.github.keyboard3.developerinterview.R;
import com.github.keyboard3.developerinterview.SettingActivity;
import com.github.keyboard3.developerinterview.entity.Problem;
import com.github.keyboard3.developerinterview.util.FileUtil;
import com.github.keyboard3.developerinterview.util.ListUtil;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * 问题IO读写相关逻辑
 *
 * @author keyboard3
 * @date 2017/9/22
 */

public class ProblemsIOModel {
    /**
     * 两个有序链表合并有序算法 【算法】
     * todo 可以放到子线程中计算
     *
     * @param newSource
     * @return
     */
    public static int computeDiffList(LinkedList<Problem> mOldList, LinkedList<Problem> mNewList, File mOldTargetFile, Context context, Uri newSource) {
        Gson gson = new Gson();
        String content = "";
        int num = 0;
        try {
            Type type = new TypeToken<LinkedList<Problem>>() {
            }.getType();
            //新题目
            content = CharStreams.toString(new InputStreamReader(context.getContentResolver().openInputStream(newSource)));
            mNewList.addAll(gson.<Collection<? extends Problem>>fromJson(content, type));

            if (ListUtil.isEmpty(mNewList)) {
                Toast.makeText(context, R.string.setting_check_format, Toast.LENGTH_SHORT).show();
                return -1;
            }
            //旧题目
            content = CharStreams.toString(new InputStreamReader(new FileInputStream(mOldTargetFile)));
            mOldList.addAll(gson.<Collection<? extends Problem>>fromJson(content, type));
            if (ListUtil.isEmpty(mOldList)) {
                mOldList = mNewList;
                return mOldList.size();
            }
            int oldNum = mOldList.size();
            //默认二者都是按照顺序来的
            //遍历new 插入old
            ListIterator<Problem> newIterator = mNewList.listIterator();
            ListIterator<Problem> oldIterator = mOldList.listIterator();
            Problem nextOld = oldIterator.next();
            while (newIterator.hasNext()) {
                Problem nextNew = newIterator.next();
                if (nextNew.getId() - nextOld.getId() < 0) {
                    num++;
                    oldIterator.previous();
                    oldIterator.add(nextNew);
                    oldIterator.next();
                } else {
                    if (nextNew.getId() == nextOld.getId() && nextNew.hashCode() != nextOld.hashCode()) {
                        oldIterator.set(nextNew);
                    }
                    if (newIterator.hasNext()) {
                        nextOld = oldIterator.next();
                    }
                }
            }
            if (oldNum == mOldList.size()) return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return num;
    }

    public static void input2localFile(LinkedList<Problem> mOldList, File mOldTargetFile) {
        //保存到本地
        FileUtil.copyFile(new ByteArrayInputStream(new Gson().toJson(mOldList).getBytes()), mOldTargetFile);
        EventBus.getDefault().post(new SettingActivity.RefreshEvent());
    }
}
