package com.github.keyboard3.developerinterview;

import android.content.Context;
import android.os.Environment;

import com.github.keyboard3.developerinterview.entity.Problem;

/**
 * Created by keyboard3 on 2017/9/3.
 */

public class Config {
    public static final String INTENT_ENTITY = "intent_entity";
    public static final String INTENT_KEY = "intent_key";
    public static final String INTENT_SEARCH_KEY = "search_key";
    public static final String INTENT_LIST_POSITION = "intent_list_position";
    public static final String INTENT_LIST_TOP = "intent_list_top";
    public static final String INTENT_ID = "id";
    public static final String INTENT_TITLE = "title";
    public static final String INTENT_SOURCE = "source";
    public static final String INTENT_TYPE = "type";

    public static final String APP_DIR = "Interview";
    public static final String STORAGE_DIRECTORY = Environment
            .getExternalStorageDirectory().getAbsolutePath() + "/Interview";
    public static final String FIR_HOST_APPID = "59acbc2d959d6940060002ee";
    public static final String FIR_SELF_APPID = "59b63f33548b7a28a000008b";
    public static final String FIR_API_TOKEN = "aefd90754f9288ff2e54949a8bc21e20";
    public static final String UPGRAD_HOST_URL = "http://api.fir.im/apps/latest/"
            + FIR_HOST_APPID + "?api_token=" + FIR_API_TOKEN;
    public static final String UPGRAD_SELF_URL = "http://api.fir.im/apps/latest/"
            + FIR_SELF_APPID + "?api_token=" + FIR_API_TOKEN;

    public static final String PACKAGE_SELFVIEW = "com.github.keyboard3.selfview";

    public static String getShareInnerLink(Context context, Problem problem) {
        return "content://" + context.getPackageName() + "/problem?id="
                + problem.id + "&type=" + problem.type + "$title="
                + problem.title + "&source=" + problem.source;
    }
}
