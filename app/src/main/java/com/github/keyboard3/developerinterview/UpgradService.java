package com.github.keyboard3.developerinterview;

import android.util.Log;

import com.allenliu.versionchecklib.core.AVersionService;
import com.github.keyboard3.developerinterview.entity.Version;
import com.github.keyboard3.developerinterview.utils.VersionUtil;
import com.google.gson.Gson;

/**
 * Created by keyboard3 on 2017/9/6.
 */

public class UpgradService extends AVersionService {
    private static String TAG = "UpgradService";

    @Override
    public void onResponses(AVersionService service, String response) {
        Log.d(TAG, "versionParams.onlyDownload:" + versionParams.onlyDownload + " response:" + response);
        Gson gson = new Gson();
        Version entity = gson.fromJson(response, Version.class);
        if (!versionParams.onlyDownload) {
            if (entity.getVersionShort().compareTo(VersionUtil.getVersion(getApplicationContext())) > 0) {
                showVersionDialog(entity.getInstallUrl(), entity.getName() + entity.getVersionShort(), entity.getChangelog());
            }
        } else {
            showVersionDialog(entity.getInstallUrl(), entity.getName() + entity.getVersionShort(), entity.getChangelog());
        }
    }
}
