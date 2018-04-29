package com.github.keyboard3.developerinterview.callback;

/**
 * 本地回调类
 *
 * @author keyboard3
 * @date 2017/10/22
 */

public interface Callback<T> {
    void success(T item);
    void fail(Throwable error);
}
