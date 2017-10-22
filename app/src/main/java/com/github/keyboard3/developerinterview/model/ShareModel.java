package com.github.keyboard3.developerinterview.model;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.github.keyboard3.developerinterview.ConfigConst;
import com.github.keyboard3.developerinterview.ProblemDetailActivity;
import com.github.keyboard3.developerinterview.WebViewActivity;
import com.github.keyboard3.developerinterview.data.DataFactory;
import com.github.keyboard3.developerinterview.entity.Problem;
import com.github.keyboard3.developerinterview.pattern.BaseProblemState;
import com.github.keyboard3.developerinterview.pattern.ProblemStateFactory;
import com.github.keyboard3.developerinterview.util.SystemUtil;

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

    public static Problem problemOpenComingIntent(Activity activity, Uri data) {
        Problem mEntity = null;
        String id = data.getQueryParameter(ConfigConst.INTENT_ID);
        String type = data.getQueryParameter(ConfigConst.INTENT_TYPE);
        String source = data.getQueryParameter(ConfigConst.INTENT_SOURCE);
        String title = data.getQueryParameter(ConfigConst.INTENT_TITLE);
        if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(type)) {
            BaseProblemState problemType = ProblemStateFactory.getProblemType(Integer.parseInt(type));
            mEntity = new DataFactory(activity.getApplicationContext(), problemType).queryProblem(id);
            if (mEntity == null) {
                if (!TextUtils.isEmpty(source) && !TextUtils.isEmpty(title)) {
                    Intent intent = new Intent(activity, WebViewActivity.class);
                    intent.putExtra(ConfigConst.INTENT_KEY, source);
                    intent.putExtra(ConfigConst.INTENT_SEARCH_KEY, title);
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
