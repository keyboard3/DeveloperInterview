package com.github.keyboard3.developerinterview.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

/**
 * Created by keyboard3 on 2017/9/7.
 */

public class SystemUtil {
    public static void setClipboard(Context context, String title, String text) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(title, text);
        clipboardManager.setPrimaryClip(clipData);
    }

    public static String getClipboard(Context context) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        String message = (String) clipboardManager.getPrimaryClip().getDescription().getLabel() + " " + (String) clipboardManager.getPrimaryClip().getItemAt(0).getText();
        return message;
    }

    public static void sendText(Context context, String text) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "不存在打开应用" + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public static void openText(Context context, String text) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "不存在打开应用" + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
