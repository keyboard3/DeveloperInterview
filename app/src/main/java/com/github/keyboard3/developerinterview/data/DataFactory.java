package com.github.keyboard3.developerinterview.data;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;

import com.github.keyboard3.developerinterview.ConfigConst;
import com.github.keyboard3.developerinterview.callback.Callback;
import com.github.keyboard3.developerinterview.entity.Problem;
import com.github.keyboard3.developerinterview.http.HttpClient;
import com.github.keyboard3.developerinterview.pattern.BaseProblemState;
import com.google.common.io.CharStreams;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author keyboard3
 * @date 2017/9/8
 */

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class DataFactory {
    private static final String TAG = "DataFactory";
    /**
     * 存入了所有类型的题目集合
     */
    private static Map<Integer, SparseArray<Problem>> listMap = new ArrayMap<>();
    private SparseArray<Problem> problems;
    public List<Problem> problemList;
    private BaseProblemState problemState;
    private Context applicationContext;
    public String problemJsonPath;

    public DataFactory(Context applicationContext, BaseProblemState problemState) {
        if (problemState == null || applicationContext == null) {
            return;
        }
        this.applicationContext = applicationContext;
        this.problemState = problemState;
        problemJsonPath = ConfigConst.STORAGE_DIRECTORY + "/" + problemState.getTypeStr() + "/" + problemState.getTypeStr() + ".json";

        //创建文件夹
        File dir = new File(problemJsonPath);
        if (!dir.getParentFile().exists()) {
            dir.getParentFile().mkdirs();
        }
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
     * 根据
     *
     * @return
     */
    public void initProblemsByType() {
        if (problems == null) {
            problems = listMap.get(problemState.getType());
            if (problems == null) {
                init2LocalProblems(null);
            }
        }
    }

    /**
     * 就是初始化本地的题目
     */
    public void init2LocalProblems(final Callback<List<Problem>> callback) {
        final Gson gson = new Gson();
        final File file = new File(problemJsonPath);
        problems = new SparseArray<>();
        try {

            if (!file.exists()) {
                HttpClient.getInstance(applicationContext).getProblems(problemState.getTypeStr() + ".json", new Consumer<List<Problem>>() {
                    @Override
                    public void accept(List<Problem> list) throws Exception {
                        problemList = list;
                        Log.d(TAG, "获取成功:" + list.size());
                        //一次性写入文件
                        FileOutputStream outputStream = new FileOutputStream(file);
                        outputStream.write(gson.toJson(list).getBytes());
                        //保存到缓存中
                        saveMemoryCache(list);
                        if (callback != null) {
                            callback.success(list);
                        }
                    }
                });
            } else {
                Log.d(TAG, "init2LocalProblems 本地读取");
                InputStream input = new FileInputStream(file);
                String content = CharStreams.toString(new InputStreamReader(input));
                problemList = new Gson().fromJson(content, new TypeToken<List<Problem>>() {
                }.getType());
                saveMemoryCache(problemList);
                if (callback != null) {
                    callback.success(problemList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveMemoryCache(List<Problem> list) {
        for (Problem item : list) {
            problems.put(item.getId(), item);
        }
        listMap.put(problemState.getType(), problems);
    }

    /**
     * 异步查询题目
     *
     * @param id
     * @return
     */
    public void asyncQueryProblem(String id, final Callback callback) {
        Integer Id = Integer.parseInt(id);
        io.reactivex.Observable.just(Id)
                .observeOn(Schedulers.io())
                .flatMap(new Function<Integer, ObservableSource<Problem>>() {
                    @Override
                    public ObservableSource<Problem> apply(Integer id) throws Exception {

                        return (ObservableSource<Problem>) problems.get(id);
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Problem>() {
                    @Override
                    public void accept(Problem problem) throws Exception {
                        callback.success(problem);
                    }
                });
    }

    public Problem queryProblem(String sid) {
        Integer id = Integer.parseInt(sid);
        initProblemsByType();
        return problems.get(id);
    }
}
