package com.github.keyboard3.developerinterview;

import android.os.Environment;

/**
 * Created by keyboard3 on 2017/9/3.
 */

public class Config {
    public static final String INTENT_ENTITY = "intent_entity";
    public static final String INTENT_KEY = "intent_key";
    public static final String INTENT_LIST_POSITION = "intent_list_position";
    public static final String INTENT_LIST_TOP = "intent_list_top";
    public static final String StorageDirectory = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/Interview";
    public static final String ProblemJava = "ProblemJava";
    public static final String ProblemAndroid = "ProblemAndroid";
    public static final String ProblemHtml = "ProblemHtml";
    public static final String ProblemAlgorithm = "ProblemAlgorithm";
}
