package com.github.keyboard3.developerinterview.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 文件工具
 *
 * @author keyboard3
 * @date 2017/9/4
 */

public class FileUtil {
    public static void copyFile(@NonNull InputStream ins, @NonNull File file) {
        OutputStream os;
        try {
            os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openFile(@NonNull final Context context, @NonNull final String path) {
        openFile(context, new File(path));
    }

    /**
     * 打开发送文件
     *
     * @param context
     * @param target
     */
    public static void openFile(@NonNull final Context context, @NonNull final File target) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", target);
            intent.setDataAndType(contentUri, "*/*");
        } else {
            intent.setDataAndType(Uri.fromFile(target), "*/*");
        }

        if (context.getPackageManager().queryIntentActivities(intent, 0).isEmpty()) {
            Toast.makeText(context, "queryIntentActivities", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "不存在打开应用" + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
