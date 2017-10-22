package com.github.keyboard3.developerinterview.base;


import android.app.Fragment;
import android.app.ProgressDialog;

import com.github.keyboard3.developerinterview.R;
import com.wang.avi.AVLoadingIndicatorView;

/**
 * 基类的Fragment 让子类更加专注的实现自己的业务
 *
 * @author keyboard3
 * @date 2017/9/3
 */
public class BaseFragment extends Fragment implements IProgressDialog {
    private ProgressDialog mProgressDialog;
    private AVLoadingIndicatorView mAdvanceProgressView;

    private boolean mAdvanceDialogToggle;

    protected void toggleDialogAdvance(boolean toggle) {
        mAdvanceDialogToggle = toggle;
    }

    @Override
    public void showDialog() {
        if (mAdvanceDialogToggle && mAdvanceProgressView == null) {
            mAdvanceProgressView = getView().findViewById(R.id.avi);
        } else if (!mAdvanceDialogToggle && mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getString(R.string.com_loading));
            mProgressDialog.setIndeterminate(true);
        }
        if (mAdvanceDialogToggle) {
            if (mAdvanceProgressView == null) {
                return;
            }
            mAdvanceProgressView.show();
        } else {
            if (mProgressDialog == null) {
                return;
            }
            mProgressDialog.show();
        }
    }

    @Override
    public void hideDialog() {
        if (mAdvanceDialogToggle) {
            if (mAdvanceProgressView == null) {
                return;
            }
            mAdvanceProgressView.hide();
        } else {
            if (mProgressDialog == null) {
                return;
            }
            mProgressDialog.hide();
        }
    }

    @Override
    public boolean isShowing() {
        return mProgressDialog.isShowing();
    }
}
