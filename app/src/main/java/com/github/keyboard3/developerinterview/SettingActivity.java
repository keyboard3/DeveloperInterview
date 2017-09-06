package com.github.keyboard3.developerinterview;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.allenliu.versionchecklib.core.AllenChecker;
import com.github.keyboard3.developerinterview.Http.HttpClient;
import com.github.keyboard3.developerinterview.entity.Problem;
import com.github.keyboard3.developerinterview.utils.FileUtil;
import com.github.keyboard3.developerinterview.utils.ListUtil;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.werb.mediautilsdemo.CustomPermissionChecker;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * 设置界面
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {
    String[] problemTypes = {Config.ProblemJava, Config.ProblemAndroid, Config.ProblemHtml, Config.ProblemAlgorithm};
    private static String TAG = "SettingActivity";
    private AlertDialog outputDialog;
    private Dialog inputDialog;
    private static final int File_CODE = 101;
    private static final int P_READ_EXTERNAL_STORAGE = 102;
    private CustomPermissionChecker permissionChecker;
    String curTypeStr = "";
    private AlertDialog trainsferDialog;
    private List<Problem> list;
    private Gson gson;
    private File selectfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setTitle("设置");

        //类型初始化到本地share中，每次从share中读取类型初始化到本地
        findViewById(R.id.ll_input).setOnClickListener(this);
        findViewById(R.id.ll_output).setOnClickListener(this);
        findViewById(R.id.ll_version).setOnClickListener(this);

        permissionChecker = new CustomPermissionChecker(this);
        gson = new Gson();

        initDialog();
        //todo 2.增加  增量 导入
    }

    private void initDialog() {
        outputDialog = new AlertDialog.Builder(this).setTitle("选择题目类型")
                .setItems(problemTypes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String type = problemTypes[which];
                        //选择题目类型 调用系统的发送文件功能
                        File imagePath = new File(Environment.getExternalStorageDirectory(), "Interview/" + type + "/");
                        File newFile = new File(imagePath, type + ".json");
                        FileUtil.openFile(SettingActivity.this, newFile);
                    }
                }).create();
        //只有导入才需要读取权限
        inputDialog = new AlertDialog.Builder(this).setTitle("选择题目类型")
                .setItems(problemTypes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String type = problemTypes[which];
                        curTypeStr = type;
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("text/*");
                        startActivityForResult(intent, File_CODE);
                        dialog.dismiss();
                    }
                }).create();
        //保存到本地
        trainsferDialog = new AlertDialog.Builder(this).setTitle("提醒").setMessage("检测到您导入的题目文件和选择的题目类型不符合，是否强行改成所选类型！")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for (Problem item : list) {
                            if (!item.getTypeName().equals(curTypeStr)) {
                                item.setType(curTypeStr);
                            }
                        }
                        //保存到本地
                        FileUtil.copyFile(new ByteArrayInputStream(gson.toJson(list).getBytes()), selectfile);
                        Toast.makeText(SettingActivity.this, "导入成功！", Toast.LENGTH_SHORT).show();
                        EventBus.getDefault().post(new refreshEvent());
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == File_CODE) {
            try {
                //获取到文件路径 和文件类型  先尝试进行内容gson解析，解析成功之后 设置题目类型
                //将解析的内容重新序列化到本地
                final String content = CharStreams.toString(new InputStreamReader(getContentResolver().openInputStream(data.getData())));
                list = gson.fromJson(content, new TypeToken<List<Problem>>() {
                }.getType());
                if (ListUtil.isEmpty(list)) {
                    Toast.makeText(this, "对不起题目为空或者题目json格式不符合系统格式，请您重新检查！", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean isTypeOk = true;
                for (Problem item : list) {
                    if (!item.getTypeName().equals(curTypeStr)) {
                        isTypeOk = false;
                        break;
                    }
                }
                selectfile = new File(Config.StorageDirectory + "/" + curTypeStr + "/" + curTypeStr + ".json");
                if (!isTypeOk) {
                    trainsferDialog.show();
                } else {
                    FileUtil.copyFile(new ByteArrayInputStream(gson.toJson(list).getBytes()), selectfile);
                    Toast.makeText(this, "导入成功！", Toast.LENGTH_SHORT).show();
                    //发送异步通知problemsFragment 重新加载页面
                    EventBus.getDefault().post(new refreshEvent());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_input:
                //申请权限
                if (permissionChecker.isLackPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE})) {
                    permissionChecker.requestPermissions(P_READ_EXTERNAL_STORAGE);
                } else {
                    inputDialog.show();
                }
                break;
            case R.id.ll_output:
                outputDialog.show();
                break;
            case R.id.ll_version:
                if (permissionChecker.isLackPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
                    permissionChecker.requestPermissions(P_READ_EXTERNAL_STORAGE);
                } else {
                    AllenChecker.startVersionCheck(this, HttpClient.getInstance().builder.build());
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case P_READ_EXTERNAL_STORAGE:
                if (permissionChecker.hasAllPermissionsGranted(grantResults)) {
                    inputDialog.show();
                } else {
                    permissionChecker.showDialog();
                }
                break;
        }
    }

    public static class refreshEvent {
    }
}
