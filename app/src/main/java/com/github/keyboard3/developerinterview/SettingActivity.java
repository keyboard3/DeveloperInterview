package com.github.keyboard3.developerinterview;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.keyboard3.developerinterview.Http.HttpClient;
import com.github.keyboard3.developerinterview.entity.Problem;
import com.github.keyboard3.developerinterview.entity.Version;
import com.github.keyboard3.developerinterview.pattern.AlgorithmType;
import com.github.keyboard3.developerinterview.pattern.AndroidType;
import com.github.keyboard3.developerinterview.pattern.HtmlType;
import com.github.keyboard3.developerinterview.pattern.JavaType;
import com.github.keyboard3.developerinterview.pattern.ProblemTypeFactory;
import com.github.keyboard3.developerinterview.utils.FileUtil;
import com.github.keyboard3.developerinterview.utils.ListUtil;
import com.github.keyboard3.developerinterview.utils.VersionUtil;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.werb.mediautilsdemo.CustomPermissionChecker;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 设置
 * 导入、导出、版本检测
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private static String TAG = "SettingActivity";
    private AlertDialog outputDialog;
    private Dialog inputDialog;
    private static final int File_CODE = 101;
    private static final int P_READ_EXTERNAL_STORAGE = 102;
    private CustomPermissionChecker permissionChecker;
    String curTypeStr = "";
    private AlertDialog trainsferDialog;
    private LinkedList<Problem> oldList;
    private LinkedList<Problem> newList;
    private Gson gson;
    private File oldTargeFile;
    private int validNum;
    private String[] problemTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setTitle("设置");

        //类型初始化到本地share中，每次从share中读取类型初始化到本地
        findViewById(R.id.ll_input).setOnClickListener(this);
        findViewById(R.id.ll_output).setOnClickListener(this);
        findViewById(R.id.ll_version).setOnClickListener(this);

        TextView tvVersion = findViewById(R.id.tv_version);
        tvVersion.setText("版本v" + VersionUtil.getVersion(this));
        permissionChecker = new CustomPermissionChecker(this);
        gson = new Gson();

        initDialog();
        //todo 3.爬虫同步工匠若水的题目
    }

    private void initDialog() {
        problemTypes = new String[ProblemTypeFactory.mapString.keySet().size()];
        ProblemTypeFactory.mapString.keySet().toArray(problemTypes);//todo 1 RxJava 过滤数据

        outputDialog = new AlertDialog.Builder(this).setTitle("选择题目类型")
                .setItems(problemTypes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //选择题目类型 调用系统的发送文件功能
                        String type = problemTypes[which];
                        File imagePath = new File(Environment.getExternalStorageDirectory(), "Interview/" + type + "/");
                        File newFile = new File(imagePath, type + ".json");
                        FileUtil.openFile(SettingActivity.this, newFile);
                        dialog.dismiss();
                    }
                }).create();
        inputDialog = new AlertDialog.Builder(this).setTitle("选择题目类型")
                .setItems(problemTypes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //导入 选中需导入题目类型并打开题目文件的
                        String type = problemTypes[which];
                        curTypeStr = type;
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("text/*");
                        startActivityForResult(intent, File_CODE);
                        dialog.dismiss();
                    }
                }).create();
        //修改导入文件的题目类型
        trainsferDialog = new AlertDialog.Builder(this).setTitle("提醒").setMessage("检测到您导入的题目文件和选择的题目类型不符合，是否强行改成所选类型！")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for (Problem item : newList) {
                            if (!item.getTypeName().equals(curTypeStr)) {
                                item.setType(curTypeStr);
                            }
                        }
                        input2localFile();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create();
    }

    private void input2localFile() {
        //保存到本地
        FileUtil.copyFile(new ByteArrayInputStream(gson.toJson(oldList).getBytes()), oldTargeFile);
        EventBus.getDefault().post(new refreshEvent());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == File_CODE) {
            try {
                initSelectedFile();
                validNum = computeDiffList(data.getData());
                if (validNum == -1) return;
                if (!isProblemTypeAllRight()) {
                    trainsferDialog.show();//类型不全部正确 强转对话框提示
                } else {
                    input2localFile();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Toast.makeText(this, "导入成功" + validNum, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 两个有序链表合并有序算法 【算法】
     * todo 可以放到子线程中计算
     *
     * @param newSource
     * @return
     */
    private int computeDiffList(Uri newSource) {
        String content = "";
        int num = 0;
        try {
            Type type = new TypeToken<LinkedList<Problem>>() {
            }.getType();
            //新题目
            content = CharStreams.toString(new InputStreamReader(getContentResolver().openInputStream(newSource)));
            newList = gson.fromJson(content, type);
            if (ListUtil.isEmpty(newList)) {
                Toast.makeText(this, "对不起题目为空或者题目json格式不符合系统格式，请您重新检查！", Toast.LENGTH_SHORT).show();
                return -1;
            }
            //旧题目
            content = CharStreams.toString(new InputStreamReader(new FileInputStream(oldTargeFile)));
            oldList = gson.fromJson(content, type);
            if (ListUtil.isEmpty(oldList)) {
                oldList = newList;
                return oldList.size();
            }
            int oldNum = oldList.size();
            //默认二者都是按照顺序来的
            //遍历new 插入old
            ListIterator<Problem> newIterator = newList.listIterator();
            ListIterator<Problem> oldIterator = oldList.listIterator();
            Problem nextOld = oldIterator.next();
            while (newIterator.hasNext()) {
                Problem nextNew = newIterator.next();
                if (Integer.parseInt(nextNew.id) - Integer.parseInt(nextOld.id) < 0) {
                    num++;
                    oldIterator.previous();
                    oldIterator.add(nextNew);
                    oldIterator.next();
                } else {
                    if (newIterator.hasNext()) {
                        nextOld = oldIterator.next();
                    }
                }
            }
            if (oldNum == oldList.size()) return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return num;
    }

    private void initSelectedFile() {
        oldTargeFile = new File(Config.StorageDirectory + "/" + curTypeStr + "/" + curTypeStr + ".json");
    }

    /**
     * 是否题目的所有类型都正确
     *
     * @return
     */
    private boolean isProblemTypeAllRight() {
        boolean isProblemTypeAllRight = true;
        for (Problem item : newList) {
            if (!item.getTypeName().equals(curTypeStr)) {
                isProblemTypeAllRight = false;
                break;
            }
        }
        return isProblemTypeAllRight;
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
                    versionCheck();
                }
                break;
        }
    }

    private void versionCheck() {
        //弹出更新内容
        HttpClient.getInstance().upgrad(Config.firAppId, Config.firApi_token, new Callback<Version>() {
            @Override
            public void onResponse(Call<Version> call, Response<Version> response) {
                if (response.isSuccessful()) {
                    Version entity = response.body();
                    if (entity.getVersionShort().compareTo(VersionUtil.getVersion(getApplicationContext())) == 0) {
                        new AlertDialog.Builder(SettingActivity.this)
                                .setTitle(entity.getVersionShort())
                                .setMessage(entity.getChangelog())
                                .show();
                    } else {
                        Toast.makeText(SettingActivity.this, "检测最新版本为" + entity.getVersionShort(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Version> call, Throwable t) {
                Toast.makeText(SettingActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
