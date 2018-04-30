package com.github.keyboard3.developerinterview.model;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.github.keyboard3.developerinterview.ConfigConst;
import com.github.keyboard3.developerinterview.ProblemDetailActivity;
import com.github.keyboard3.developerinterview.WebViewActivity;
import com.github.keyboard3.developerinterview.data.ProblemsDrive;
import com.github.keyboard3.developerinterview.entity.Problem;
import com.github.keyboard3.developerinterview.pattern.BaseProblemState;
import com.github.keyboard3.developerinterview.pattern.ProblemStateFactory;
import com.github.keyboard3.developerinterview.util.SystemUtil;

import java.io.IOException;

/**
 * 分享处理逻辑
 *
 * @author keyboard3
 * @date 2017/9/22
 */

public class ShareModel {
    public static void mainOpenComingIntent(Activity activity, Uri data) {
        if (data != null && data.toString().contains("content://" + activity.getPackageName())) {
            String id = data.getQueryParameter(ConfigConst.INTENT_ID);
            String type = data.getQueryParameter(ConfigConst.INTENT_TYPE);
            String source = data.getQueryParameter(ConfigConst.INTENT_SOURCE);
            String title = data.getQueryParameter(ConfigConst.INTENT_TITLE);
            if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(type)) {
                Intent intent = new Intent(activity, ProblemDetailActivity.class);
                intent.putExtra(ConfigConst.INTENT_KEY, data.toString());
                activity.startActivity(intent);
            } else if (!TextUtils.isEmpty(source) && !TextUtils.isEmpty(title)) {
                Intent intent = new Intent(activity, WebViewActivity.class);
                intent.putExtra(ConfigConst.INTENT_KEY, source);
                intent.putExtra(ConfigConst.INTENT_SEARCH_KEY, title);
                activity.startActivity(intent);
            }
        }
    }

    public static Problem problemOpenComingIntent(@NonNull Activity activity,@NonNull Uri data) {
        String id = data.getQueryParameter(ConfigConst.INTENT_ID);
        String type = data.getQueryParameter(ConfigConst.INTENT_TYPE);
        String source = data.getQueryParameter(ConfigConst.INTENT_SOURCE);
        String title = data.getQueryParameter(ConfigConst.INTENT_TITLE);

        BaseProblemState problemState = ProblemStateFactory.getProblemStateById(Integer.parseInt(type));
        ProblemsDrive problemsDrive = new ProblemsDrive(activity.getApplicationContext(), problemState);
        Problem problem = problemsDrive.queryProblem(id);

        Intent intent = new Intent(activity, WebViewActivity.class);
        intent.putExtra(ConfigConst.INTENT_KEY, source);
        intent.putExtra(ConfigConst.INTENT_SEARCH_KEY, title);
        activity.startActivity(intent);
        activity.finish();

        return problem;
    }

    public static void openInnerUri(Activity activity) {
        String uri = SystemUtil.getClipboard(activity.getApplicationContext());
        ShareModel.mainOpenComingIntent(activity, Uri.parse(uri));
    }
}
