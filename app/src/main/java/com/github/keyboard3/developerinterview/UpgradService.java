package com.github.keyboard3.developerinterview;

import com.allenliu.versionchecklib.core.AVersionService;
import com.github.keyboard3.developerinterview.entity.Version;
import com.github.keyboard3.developerinterview.utils.VersionUtil;
import com.google.gson.Gson;

/**
 * Created by keyboard3 on 2017/9/6.
 */

public class UpgradService extends AVersionService {
    @Override
    public void onResponses(AVersionService service, String response) {
        Gson gson = new Gson();
        Version entity = gson.fromJson(response, Version.class);
        if (entity.getVersionShort().compareTo(VersionUtil.getVersion(getApplicationContext())) > 0) {
            showVersionDialog(entity.getInstallUrl(), entity.getName() + entity.getVersionShort(), entity.getChangelog());
        }
    }
}
