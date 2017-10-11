package com.github.keyboard3.developerinterview.model;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;

import com.allenliu.versionchecklib.core.AllenChecker;
import com.github.keyboard3.developerinterview.Config;
import com.github.keyboard3.developerinterview.http.HttpClient;
import com.github.keyboard3.developerinterview.entity.Version;
import com.github.keyboard3.developerinterview.util.VersionUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by keyboard3 on 2017/9/22.
 */

public class VersionCheckModel {
    public static void versionCheck(final Context context) {
        //弹出更新内容
        HttpClient.getInstance().upgrad(Config.FIR_HOST_APPID, Config.FIR_API_TOKEN, new Callback<Version>() {
            @Override
            public void onResponse(Call<Version> call, Response<Version> response) {
                if (response.isSuccessful()) {
                    Version entity = response.body();
                    if (entity.getVersionShort().compareTo(VersionUtil.getVersion(context)) == 0) {
                        new AlertDialog.Builder(context)
                                .setTitle(entity.getVersionShort())
                                .setMessage(entity.getChangelog())
                                .show();
                    } else {
                        AllenChecker.startVersionCheck(context, HttpClient.getInstance().mHostBuilder.build());
                        Toast.makeText(context, "检测最新版本为" + entity.getVersionShort(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Version> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
