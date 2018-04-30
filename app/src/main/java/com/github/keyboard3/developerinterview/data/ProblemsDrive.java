package com.github.keyboard3.developerinterview.data;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.ArrayMap;
import android.util.SparseArray;

import com.github.keyboard3.developerinterview.callback.Callback;
import com.github.keyboard3.developerinterview.callback.EmptyCallback;
import com.github.keyboard3.developerinterview.entity.Problem;
import com.github.keyboard3.developerinterview.pattern.BaseProblemState;
import com.github.keyboard3.developerinterview.util.ListUtil;
import com.google.common.io.CharStreams;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

/**
 * @author keyboard3
 * @date 2017/9/8
 */

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class ProblemsDrive {
    private static Map<Integer, SparseArray<Problem>> stateIdToProblemsMap = new ArrayMap<>();
    private SparseArray<Problem> idToProblems;
    public List<Problem> problems;
    private BaseProblemState problemState;
    private Context applicationContext;
    public String problemJsonPath;

    public ProblemsDrive(@NonNull Context applicationContext,@NonNull BaseProblemState problemState) {
        this.applicationContext = applicationContext;
        this.problemState = problemState;

        idToProblems = problemState.getProblemsMap(stateIdToProblemsMap);
        problemJsonPath = problemState.getProblemJsonPath();

        initProblemJsonDir();
    }

    private void initProblemJsonDir() {
        File problemJsonFile = new File(problemJsonPath);
        if (!problemJsonFile.getParentFile().exists()) {
            problemJsonFile.getParentFile().mkdirs();
        }
    }

    public void initProblemsByType() {
        if (idToProblems == null)
            asyncFetchProblems(new EmptyCallback());
    }

    public void asyncFetchProblems(@NonNull  final Callback<List<Problem>> callback) {
        idToProblems = new SparseArray<>();
        final File problemJsonFile = new File(problemJsonPath);
        try {
            if (problemJsonFile.exists()) {
                InputStream inputStream = new FileInputStream(problemJsonFile);
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                String content = CharStreams.toString(inputStreamReader);
                problems = new Gson().fromJson(content, new TypeToken<List<Problem>>() {}.getType());

                saveMemoryCache(problems);
                callback.success(problems);
            } else {
                problemState.getProblemsFromHttp(applicationContext
                        , list -> {
                    if (!ListUtil.isEmpty(list)) {
                        problems = list;

                        FileOutputStream outputStream = new FileOutputStream(problemJsonFile);
                        outputStream.write(new Gson().toJson(list).getBytes());
                        outputStream.close();

                        saveMemoryCache(list);
                    }
                    callback.success(list);
                }, throwable -> callback.fail(throwable));
            }
        } catch (IOException e) {
            callback.fail(e);
        }
    }

    private void saveMemoryCache(List<Problem> list) {
        for (Problem item : list) {
            idToProblems.put(item.getId(), item);
        }
        problemState.putEntryToMap(stateIdToProblemsMap, idToProblems);
    }

    public Problem queryProblem(@NonNull String problemId) {
        Integer id = Integer.parseInt(problemId);
        initProblemsByType();
        Problem problem = idToProblems.get(id);
        return problem == null ? new Problem() : problem;
    }
}
