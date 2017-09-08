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
    public static final String StorageDirectory = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/Interview";
    public static final String firAppId = "59acbc2d959d6940060002ee";
    public static final String firApi_token = "aefd90754f9288ff2e54949a8bc21e20";
    public static final String upgradUrl = "http://api.fir.im/apps/latest/" + firAppId + "?api_token=" + firApi_token;
    public static final String INTENT_ID = "id";
    public static final String INTENT_TITLE = "title";
    public static final String INTENT_SOURCE = "source";
    public static final String INTENT_TYPE = "type";

    public static String getShareInnerLink(Context context, Problem problem) {
        return String.format("cotent://" + context.getPackageName() + "/problem?id=%1$&type=%2$&title=%3$&source=%4$",
                problem.id, problem.type, problem.title, problem.source);
    }
}
