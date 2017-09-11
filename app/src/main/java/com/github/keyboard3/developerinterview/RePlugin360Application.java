package com.github.keyboard3.developerinterview;

import android.content.Context;

import com.github.keyboard3.developerinterview.hotRepair.HostCallbacks;
import com.qihoo360.replugin.RePluginApplication;
import com.qihoo360.replugin.RePluginCallbacks;

/**
 * Created by keyboard3 on 2017/9/11.
 */

public class RePlugin360Application extends RePluginApplication {
    @Override
    protected RePluginCallbacks createCallbacks() {
        return new HostCallbacks(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}
