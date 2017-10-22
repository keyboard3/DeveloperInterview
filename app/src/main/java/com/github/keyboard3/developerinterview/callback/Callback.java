package com.github.keyboard3.developerinterview.callback;

/**
 * 本地回调类
 *
 * @author keyboard3
 * @date 2017/10/22
 */

public interface Callback<T> {
    /**
     * 异步成功回调
     *
     * @param item
     */
    void success(T item);
}
