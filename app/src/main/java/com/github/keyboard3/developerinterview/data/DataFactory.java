package com.github.keyboard3.developerinterview.data;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.ArrayMap;

import com.github.keyboard3.developerinterview.ConfigConst;
import com.github.keyboard3.developerinterview.entity.Problem;
import com.github.keyboard3.developerinterview.pattern.BaseProblemState;
import com.github.keyboard3.developerinterview.util.FileUtil;
import com.google.common.io.CharStreams;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * @author keyboard3
 * @date 2017/9/8
 */

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class DataFactory {
    private static Map<Integer, List<Problem>> listMap = new ArrayMap<>();
    private BaseProblemState BaseProblemState;
    private Context applicationContext;
    private String problemJsonPath;

    public DataFactory(Context applicationContext, BaseProblemState BaseProblemState) {
        this.applicationContext = applicationContext;
        this.BaseProblemState = BaseProblemState;
        problemJsonPath = ConfigConst.STORAGE_DIRECTORY + "/" + BaseProblemState.getTypeStr() + "/" + BaseProblemState.getTypeStr() + ".json";
    }

    /**
     * 初始化查询资源
     *
     * @param path
     * @return
     */
    public DataFactory iniSource(String path) {
        problemJsonPath = path;
        return this;
    }

    /**
     * 查询到题目集合，保证集合是有序的
     *
     * @return
     */
    public List<Problem> queryByType() {
        if (BaseProblemState == null) {
            return null;
        }
        List<Problem> list = new ArrayList();
        if (!listMap.containsKey(BaseProblemState.getType()) || listMap.get(BaseProblemState.getType()) == null) {
            try {
                Gson gson = new Gson();
                String content;
                File file = new File(problemJsonPath);
                if (!file.exists()) {
                    //将assets目录的问题文件复制到sdcard
                    AssetManager assets = applicationContext.getAssets();
                    InputStream open = assets.open(BaseProblemState + ".json");
                    FileUtil.copyFile(open, file);
                }
                InputStream input = new FileInputStream(file);
                content = CharStreams.toString(new InputStreamReader(input));
                TreeSet<Problem> data = gson.fromJson(content, new TypeToken<TreeSet<Problem>>() {
                }.getType());
                list.addAll(data);
                listMap.put(BaseProblemState.getType(), list);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            list = listMap.get(BaseProblemState.getType());
        }
        return list;
    }

    /**
     * 二分查询算法查询题目 【算法】
     *
     * @param id
     * @return
     */
    public Problem queryProblem(String id) {
        Integer Id = Integer.parseInt(id);
        Problem entity = null;
        List<Problem> list = queryByType();
        int left = 0;
        int right = list.size() - 1;
        int mid;
        while (left <= right) {
            mid = (left + right + 1) / 2;
            Problem item = list.get(mid);
            if (Id < item.getId()) {
                right = mid - 1;
            } else if (Id > item.getId()) {
                left = mid + 1;
            } else {
                entity = list.get(mid);
                break;
            }
        }
        return entity;
    }
}
