package com.github.keyboard3.developerinterview.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.github.keyboard3.developerinterview.pattern.JavaState;

/**
 * Created by keyboard3 on 2017/9/4.
 */

public class SharePreferencesHelper {

    private final SharedPreferences sharedPreferences;

    public SharePreferencesHelper(Activity activity, String name) {
        sharedPreferences = activity.getSharedPreferences(JavaState.typeStr, Context.MODE_PRIVATE);
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public void putInt(String key, Integer value) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt(key, value);
        edit.commit();
    }

    public SharedPreferences getSP() {
        return sharedPreferences;
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, "");
    }
}
