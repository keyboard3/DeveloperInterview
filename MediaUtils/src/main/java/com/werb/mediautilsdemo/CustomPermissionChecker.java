package com.werb.mediautilsdemo;

import android.app.Activity;
import android.support.v4.app.ActivityCompat;

import com.werb.permissionschecker.PermissionChecker;

/**
 * Created by keyboard3 on 2017/9/3.
 */

public class CustomPermissionChecker extends PermissionChecker {
    Activity activity;
    String[] permissions;

    public CustomPermissionChecker(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    public boolean isLackPermissions(String[] permissions) {
        this.permissions = permissions;
        return super.isLackPermissions(permissions);
    }

    public void requestPermissions(int requestCode) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }
}
