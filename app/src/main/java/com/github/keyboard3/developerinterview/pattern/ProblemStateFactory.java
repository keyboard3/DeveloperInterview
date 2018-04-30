package com.github.keyboard3.developerinterview.pattern;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.ArrayMap;
import android.util.SparseArray;

import java.lang.reflect.Field;
import java.util.Map;


/**
 * 问题的状态模式和工厂模式
 *
 * @author keyboard3
 * @date 2017/9/7
 */

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class ProblemStateFactory {
    static SparseArray<? super BaseProblemState> problemIdToStateMap = new SparseArray<>();
    static Map<String, ? super BaseProblemState> problemNameToStateMap = new ArrayMap<>();
    static SparseArray<? super BaseProblemState> problemMenuIdToStateMap = new SparseArray<>();
    static Class<?> [] problemsClass = { JavaState.class
                                        ,AndroidState.class
                                        ,HtmlState.class
                                        ,AlgorithmState.class
                                        ,OtherState.class
                                        ,ProductState.class};
    public static BaseProblemState [] problemStates = new BaseProblemState[problemsClass.length];
    static EmptyProblemState emptyProblemState = new EmptyProblemState();
    static {
        try {
            for (int i = 0; i < problemsClass.length; i++) {
                Class<?> problemStateClass = problemsClass[i];
                problemStates[i] = (BaseProblemState) problemStateClass.newInstance();

                Field idField = problemStateClass.getDeclaredField("ID");
                Field nameField = problemStateClass.getDeclaredField("NAME");
                Field menuIdField = problemStateClass.getDeclaredField("MENU_ID");

                int problemId = idField.getInt(problemStateClass);
                String problemName = nameField.get(problemStateClass).toString();
                int menuId = menuIdField.getInt(problemStateClass);

                problemIdToStateMap.put(problemId, problemStates[i]);
                problemNameToStateMap.put(problemName, problemStates[i]);
                problemMenuIdToStateMap.put(menuId, problemStates[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static BaseProblemState getProblemStateById(int id) {
        Object object = problemIdToStateMap.get(id);
        return (BaseProblemState) (object == null ? emptyProblemState: object);
    }

    public static BaseProblemState getProblemStateByName(String name) {
        Object object = problemNameToStateMap.get(name);
        return (BaseProblemState) (object == null ? emptyProblemState: object);
    }

    public static BaseProblemState getProblemStateByMenu(int menuId) {
        Object object = problemMenuIdToStateMap.get(menuId);
        return (BaseProblemState) (object == null ? emptyProblemState: object);
    }
}
