package com.github.keyboard3.developerinterview.callback;


import com.orhanobut.logger.Logger;

/**
 * Created by keyboard3 on 2018/4/30.
 */

public class EmptyCallback implements Callback {
    @Override
    public void success(Object item) {
        Logger.d("空回调成功方法:"+item);
    }

    @Override
    public void fail(Throwable error) {
        Logger.e("空回调失败方法:"+error.getMessage());
    }
}
