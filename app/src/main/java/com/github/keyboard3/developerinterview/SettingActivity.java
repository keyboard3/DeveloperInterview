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
import android.widget.TextView;
import android.widget.Toast;

import com.github.keyboard3.developerinterview.base.BaseActivity;
import com.github.keyboard3.developerinterview.entity.Problem;
import com.github.keyboard3.developerinterview.model.ProblemsIOModel;
import com.github.keyboard3.developerinterview.model.VersionCheckModel;
import com.github.keyboard3.developerinterview.pattern.ProblemStateFactory;
import com.github.keyboard3.developerinterview.util.FileUtil;
import com.github.keyboard3.developerinterview.util.VersionUtil;

import java.io.File;
import java.util.LinkedList;

/**
 * 设置
 * 导入、导出、版本检测
 * todo 3.爬虫同步工匠若水的题目
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "SettingActivity";
    private static final int FILE_CODE = 101;
    private static final int P_READ_EXTERNAL_STORAGE = 102;

    private AlertDialog mOutputDialog;
    private AlertDialog mForceTransferDialog;
    private Dialog mInputDialog;

    private LinkedList<Problem> mOldList = new LinkedList<>();
    private LinkedList<Problem> mNewList = new LinkedList<>();
    private File mOldTargetFile;
    private String mCurTypeStr = "";
    private String[] mProblemTypes;
    private int mValidNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setTitle(R.string.title_setting);
        initView();
        initDialog();
    }

    private void initView() {
        findViewById(R.id.ll_input).setOnClickListener(this);
        findViewById(R.id.ll_output).setOnClickListener(this);
        findViewById(R.id.ll_version).setOnClickListener(this);

        TextView tvVersion = findViewById(R.id.tv_version);
        tvVersion.setText(getString(R.string.setting_version) + VersionUtil.getVersion(this));
    }

    private void initDialog() {
        mProblemTypes = new String[ProblemStateFactory.mapString.keySet().size()];
        ProblemStateFactory.mapString.keySet().toArray(mProblemTypes);//todo 1 RxJava 过滤数据

        mOutputDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.setting_select_type)
                .setItems(mProblemTypes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //选择题目类型 调用系统的发送文件功能
                        String type = mProblemTypes[which];
                        FileUtil.openFile(SettingActivity.this,
                                new File(Environment.getExternalStorageDirectory()
                                        + "/" + Config.APP_DIR
                                        + "/" + type + "/"
                                        , type + ".json"));
                        dialog.dismiss();
                    }
                }).create();
        mInputDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.setting_select_type)
                .setItems(mProblemTypes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //导入 选中需导入题目类型并打开题目文件的
                        mCurTypeStr = mProblemTypes[which];
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("text/*");
                        startActivityForResult(intent, FILE_CODE);
                        dialog.dismiss();
                    }
                }).create();
        //修改导入文件的题目类型
        mForceTransferDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.com_tint)
                .setMessage(R.string.setting_check_type)
                .setPositiveButton(R.string.com_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for (Problem item : mNewList) {
                            if (!item.getTypeName().equals(mCurTypeStr)) {
                                item.setType(mCurTypeStr);
                            }
                        }
                        ProblemsIOModel.input2localFile(mOldList, mOldTargetFile);
                    }
                }).setNegativeButton(R.string.com_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == FILE_CODE) {
            try {
                initSelectedFile();
                mValidNum = ProblemsIOModel.computeDiffList(mOldList, mNewList, mOldTargetFile, this, data.getData());
                if (mValidNum == -1) return;

                if (problemTypeOk()) {
                    ProblemsIOModel.input2localFile(mOldList, mOldTargetFile);
                } else {
                    mForceTransferDialog.show();//类型不全部正确 强转对话框提示
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Toast.makeText(this, getString(R.string.setting_input_success) + mValidNum, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_input:
                //申请权限
                if (!checkPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, P_READ_EXTERNAL_STORAGE)) {
                    mInputDialog.show();
                }
                break;
            case R.id.ll_output:
                mOutputDialog.show();
                break;
            case R.id.ll_version:
                if (!checkPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, P_READ_EXTERNAL_STORAGE)) {
                    VersionCheckModel.versionCheck(getApplicationContext());
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case P_READ_EXTERNAL_STORAGE:
                if (hasAllPermissionsGranted(grantResults)) {
                    mInputDialog.show();
                }
                break;
        }
    }

    private void initSelectedFile() {
        mOldTargetFile = new File(Config.STORAGE_DIRECTORY + "/" + mCurTypeStr + "/" + mCurTypeStr + ".json");
    }

    private boolean problemTypeOk() {
        boolean problemTypeOk = true;
        for (Problem item : mNewList) {
            if (!item.getTypeName().equals(mCurTypeStr)) {
                problemTypeOk = false;
                break;
            }
        }
        return problemTypeOk;
    }

    public static class refreshEvent {
    }
}
