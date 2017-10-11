package com.github.keyboard3.developerinterview.model;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.github.keyboard3.developerinterview.Config;
import com.github.keyboard3.developerinterview.ProblemDetailActivity;
import com.github.keyboard3.developerinterview.WebViewActivity;
import com.github.keyboard3.developerinterview.data.DataFactory;
import com.github.keyboard3.developerinterview.entity.Problem;
import com.github.keyboard3.developerinterview.pattern.ProblemType;
import com.github.keyboard3.developerinterview.pattern.ProblemTypeFactory;
import com.github.keyboard3.developerinterview.util.SystemUtil;

/**
 * 分享处理逻辑类
 * Created by keyboard3 on 2017/9/22.
 */

public class ShareModel {
    public static void mainOpenComingIntent(Activity activity, Uri data) {
        if (data != null && data.toString().contains("content://" + activity.getPackageName())) {
            String id = data.getQueryParameter(Config.INTENT_ID);
            String type = data.getQueryParameter(Config.INTENT_TYPE);
            String source = data.getQueryParameter(Config.INTENT_SOURCE);
            String title = data.getQueryParameter(Config.INTENT_TITLE);
            if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(type)) {
                Intent intent = new Intent(activity, ProblemDetailActivity.class);
                intent.putExtra(Config.INTENT_KEY, data.toString());
                activity.startActivity(intent);
            } else if (!TextUtils.isEmpty(source) && !TextUtils.isEmpty(title)) {
                Intent intent = new Intent(activity, WebViewActivity.class);
                intent.putExtra(Config.INTENT_KEY, source);
                intent.putExtra(Config.INTENT_SEARCH_KEY, title);
                activity.startActivity(intent);
            }
        }
    }

    public static Problem problemOpenComingIntent(Activity activity, Uri data) {
        Problem mEntity = null;
        String id = data.getQueryParameter(Config.INTENT_ID);
        String type = data.getQueryParameter(Config.INTENT_TYPE);
        String source = data.getQueryParameter(Config.INTENT_SOURCE);
        String title = data.getQueryParameter(Config.INTENT_TITLE);
        if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(type)) {
            ProblemType problemType = ProblemTypeFactory.getProblemType(Integer.parseInt(type));
            mEntity = new DataFactory(activity.getApplicationContext(), problemType).queryProblem(id);
            if (mEntity == null) {
                if (!TextUtils.isEmpty(source) && !TextUtils.isEmpty(title)) {
                    Intent intent = new Intent(activity, WebViewActivity.class);
                    intent.putExtra(Config.INTENT_KEY, source);
                    intent.putExtra(Config.INTENT_SEARCH_KEY, title);
                    activity.startActivity(intent);
                    activity.finish();
                    return mEntity;
                }
            }
        }
        return mEntity;
    }

    public static void openInnerUri(Activity activity) {
        String uri = SystemUtil.getClipboard(activity.getApplicationContext());
        ShareModel.mainOpenComingIntent(activity, Uri.parse(uri));
    }
}
