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
    private ProgressDialog progressDialog;
    private AVLoadingIndicatorView advanceProgressView;
    private boolean isAdvanceDialogOpen;

    protected void toggleDialogAdvance(boolean toggle) {
        isAdvanceDialogOpen = toggle;
    }

    @Override
    public void showDialog() {
        if (isAdvanceDialogOpen && advanceProgressView == null) {
            advanceProgressView = getView().findViewById(R.id.avi);
        } else if (!isAdvanceDialogOpen && progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getString(R.string.com_loading));
            progressDialog.setIndeterminate(true);
        }
        if (isAdvanceDialogOpen) {
            if (advanceProgressView == null) {
                return;
            }
            advanceProgressView.show();
        } else {
            if (progressDialog == null) {
                return;
            }
            progressDialog.show();
        }
    }

    @Override
    public void hideDialog() {
        if (isAdvanceDialogOpen) {
            if (advanceProgressView == null) {
                return;
            }
            advanceProgressView.hide();
        } else {
            if (progressDialog == null) {
                return;
            }
            progressDialog.hide();
        }
    }

    @Override
    public boolean isShowing() {
        return progressDialog.isShowing();
    }
}
