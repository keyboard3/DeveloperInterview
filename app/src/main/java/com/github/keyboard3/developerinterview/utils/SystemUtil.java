package com.github.keyboard3.developerinterview.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.github.keyboard3.developerinterview.R;

/**
 * Created by keyboard3 on 2017/9/7.
 */

public class SystemUtil {
    public static void setClipboard(Context context, String title, String text) {
        try {
            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText(title, text);
            clipboardManager.setPrimaryClip(clipData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getClipboard(Context context) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        String message = "";
        try {
            message = clipboardManager.getPrimaryClip().getDescription().getLabel() + ""
                    + clipboardManager.getPrimaryClip().getItemAt(0).getText();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

    public static void sendText(Activity context, String text) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        try {
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.com_send)));
        } catch (Exception e) {
            Toast.makeText(context, context.getString(R.string.com_no_exist_app) + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public static void openBrowser(Activity context, String text) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(text));
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, context.getString(R.string.com_no_exist_app) + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
