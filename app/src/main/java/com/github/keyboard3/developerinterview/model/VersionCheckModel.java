package com.github.keyboard3.developerinterview.model;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;

import com.github.keyboard3.developerinterview.ConfigConst;
import com.github.keyboard3.developerinterview.entity.Version;
import com.github.keyboard3.developerinterview.http.HttpClient;
import com.github.keyboard3.developerinterview.util.VersionUtil;

import io.reactivex.functions.Consumer;

/**
 * 版本检测相关逻辑
 *
 * @author keyboard3
 * @date 2017/9/22
 */

public class VersionCheckModel {
    public static void versionCheck(final Context context) {
        HttpClient httpClient = HttpClient.getInstance(context);
        httpClient.upgrade(ConfigConst.FIR_HOST_APPID, ConfigConst.FIR_API_TOKEN, new Consumer<Version>() {
            @Override
            public void accept(Version entity) throws Exception {
                boolean checkSameVersion = entity.getVersionShort().compareTo(VersionUtil.getVersion(context)) == 0;
                if (checkSameVersion)
                    new AlertDialog.Builder(context)
                            .setTitle(entity.getVersionShort())
                            .setMessage(entity.getChangelog())
                            .show();
                 else
                    Toast.makeText(context, "检测最新版本为" + entity.getVersionShort(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
