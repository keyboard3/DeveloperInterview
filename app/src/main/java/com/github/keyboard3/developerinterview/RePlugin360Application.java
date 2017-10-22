package com.github.keyboard3.developerinterview;

import android.content.Context;

import com.qihoo360.replugin.RePluginApplication;
import com.qihoo360.replugin.RePluginCallbacks;

/**
 * @author keyboard3
 * @date 2017/9/11
 */

public class RePlugin360Application extends RePluginApplication {
    @Override
    protected RePluginCallbacks createCallbacks() {
        return new com.github.keyboard3.developerinterview.callback.HostCallbacks(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}
