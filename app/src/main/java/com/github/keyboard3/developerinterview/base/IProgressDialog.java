package com.github.keyboard3.developerinterview.base;

/**
 * 进度提示框的接口
 *
 * @author keyboard3
 * @date 2017/9/11
 */

public interface IProgressDialog {
    /**
     * 弹出进度提示框
     */
    void showDialog();

    /**
     * 隐藏进度提示框
     */
    void hideDialog();

    /**
     * 进度提示框是否正在显示
     *
     * @return
     */
    boolean isShowing();
}
