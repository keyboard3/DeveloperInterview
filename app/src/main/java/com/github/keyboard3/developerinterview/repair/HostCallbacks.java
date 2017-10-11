package com.github.keyboard3.developerinterview.repair;

import android.content.Context;
import android.content.Intent;

import com.allenliu.versionchecklib.core.AllenChecker;
import com.github.keyboard3.developerinterview.ConfigConsts;
import com.github.keyboard3.developerinterview.http.HttpClient;
import com.qihoo360.replugin.RePluginCallbacks;

/**
 * Created by keyboard3 on 2017/9/11.
 */

public class HostCallbacks extends RePluginCallbacks {
    private static String TAG = "HostCallbacks";

    public HostCallbacks(Context context) {
        super(context);
    }

    @Override
    public boolean onPluginNotExistsForActivity(final Context context, String plugin, Intent intent, int process) {
        if (plugin.equals(ConfigConsts.PACKAGE_SELFVIEW)) {
            AllenChecker.startVersionCheck(context, HttpClient.getInstance().mSelfBuilder.build());
        }
        return super.onPluginNotExistsForActivity(context, plugin, intent, process);
    }
}
