package com.github.keyboard3.developerinterview.fragments;


import android.app.Fragment;
import android.app.ProgressDialog;

import com.github.keyboard3.developerinterview.interfaces.IProgressDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment implements IProgressDialog {
    private ProgressDialog progressDialog;

    @Override
    public void showDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("加载中...");
            progressDialog.setIndeterminate(true);
        }
        progressDialog.show();
    }

    @Override
    public void hideDialog() {
        if (progressDialog != null) progressDialog.hide();
    }
}
