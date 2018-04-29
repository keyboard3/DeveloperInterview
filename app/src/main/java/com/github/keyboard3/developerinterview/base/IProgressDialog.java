package com.github.keyboard3.developerinterview.base;

/**
 * 进度提示框的接口
 *
 * @author keyboard3
 * @date 2017/9/11
 */

public interface IProgressDialog {
    void showDialog();
    void hideDialog();
    boolean isShowing();
}
