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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 设置
 * 导入、导出、版本检测
 *
 * @author keyboard3
 */
public class SettingActivity extends BaseActivity {

    private static final String TAG = "SettingActivity";
    private static final int FILE_CODE = 101;
    private static final int PERMISSION_READ_EXTERNAL_STORAGE = 102;

    private AlertDialog outputProblemsDialog;
    private AlertDialog forceTransferProblemsDialog;
    private Dialog inputProblemsDialog;
    @BindView(R.id.tv_version) TextView versionView;

    private LinkedList<Problem> oldProblems = new LinkedList<>();
    private LinkedList<Problem> newProblems = new LinkedList<>();
    private String curTypeStr = "";
    private String[] problemTypes;
    private File oldTargetFile;
    private int validNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        setTitle(R.string.title_setting);
        initVersionView();
        initProblemDialogs();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK && requestCode == FILE_CODE) {
                createSelectedProblemTypeFile();

                validNum = ProblemsIOModel.computeDiffList(oldProblems, newProblems, oldTargetFile, this, data.getData());
                if (validNum == -1) return;

                if (checkAllProblemType()) {
                    ProblemsIOModel.inputToLocalFile(oldProblems, oldTargetFile);
                } else {
                    forceTransferProblemsDialog.show();//类型不全部正确 强转对话框提示
                }

                Toast.makeText(this, getString(R.string.setting_input_success) + validNum, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PERMISSION_READ_EXTERNAL_STORAGE
                && hasAllPermissionsGranted(grantResults)) {
            inputProblemsDialog.show();
        }
    }

    @OnClick({R.id.ll_input,R.id.ll_output,R.id.ll_version})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_input:
                if (!checkPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                                    , PERMISSION_READ_EXTERNAL_STORAGE)) {
                    inputProblemsDialog.show();
                }
                break;
            case R.id.ll_output:
                outputProblemsDialog.show();
                break;
            case R.id.ll_version:
                if (!checkPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                                    , PERMISSION_READ_EXTERNAL_STORAGE)) {
                    VersionCheckModel.versionCheck(getApplicationContext());
                }
                break;
            default:
        }
    }

    private void initVersionView() {
        versionView.setText(getString(R.string.setting_version) + VersionUtil.getVersion(this));
    }

    private void initProblemDialogs() {
        problemTypes = new String[ProblemStateFactory.mapString.keySet().size()];
        ProblemStateFactory.mapString.keySet().toArray(problemTypes);

        outputProblemsDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.setting_select_type)
                .setItems(problemTypes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String type = problemTypes[which];
                        FileUtil.openFile(SettingActivity.this,
                                new File(Environment.getExternalStorageDirectory()
                                        + "/" + ConfigConst.APP_DIR
                                        + "/" + type + "/"
                                        , type + ".json"));
                        dialog.dismiss();
                    }
                }).create();
        inputProblemsDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.setting_select_type)
                .setItems(problemTypes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        curTypeStr = problemTypes[which];
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("text/*");
                        startActivityForResult(intent, FILE_CODE);
                        dialog.dismiss();
                    }
                }).create();
        forceTransferProblemsDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.com_tint)
                .setMessage(R.string.setting_check_type)
                .setPositiveButton(R.string.com_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for (Problem item : newProblems) {
                            if (!item.getTypeName().equals(curTypeStr)) {
                                item.setType(curTypeStr);
                            }
                        }
                        ProblemsIOModel.inputToLocalFile(oldProblems, oldTargetFile);
                    }
                }).setNegativeButton(R.string.com_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create();
    }

    private void createSelectedProblemTypeFile() {
        oldTargetFile = new File(ConfigConst.STORAGE_DIRECTORY + "/" + curTypeStr + "/" + curTypeStr + ".json");
    }

    private boolean checkAllProblemType() {
        boolean problemTypeOk = true;
        for (Problem item : newProblems) {
            if (!item.getTypeName().equals(curTypeStr)) {
                problemTypeOk = false;
                break;
            }
        }
        return problemTypeOk;
    }

    public static class RefreshEvent {
    }
}
