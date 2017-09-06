package com.github.keyboard3.developerinterview.entity;

import com.github.keyboard3.developerinterview.Config;
import com.github.keyboard3.developerinterview.R;

import java.io.Serializable;

/**
 * Created by keyboard3 on 2017/9/3.
 */

public class Problem implements Serializable {
    public static final int typeJava = 1;
    public static final int typeAndroid = 2;
    public static final int typeHtml = 3;
    public static final int typeOther = 4;
    public static final int typeAlgorithm = 5;

    public Problem(String id, String title, String content, String answer, String source, int type, String audio) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.answer = answer;
        this.type = type;
        this.source = "";
    }

    public static Problem newJava(String id, String title, String content, String answer) {
        return new Problem(id, title, content, answer, "", 1, "");
    }

    public String id;
    public String title;
    public String content;
    public String answer;
    public String source;
    public int type;//1java 2android 3html

    public void setType(String typeName) {
        //todo 2 用策略模式 实现动态增加类型
        switch (typeName) {
            case Config.ProblemJava:
                type = typeJava;
                break;
            case Config.ProblemAndroid:
                type = typeAndroid;
                break;
            case Config.ProblemHtml:
                type = typeHtml;
            case Config.ProblemAlgorithm:
                type = typeAlgorithm;
                break;
        }
    }

    public int getTypeIcon() {
        //todo 2.策略模式 要支持动态扩展
        int rId;
        switch (type) {
            case typeJava:
                rId = R.mipmap.ic_java;
                break;
            case typeAndroid:
                rId = R.mipmap.ic_android;
                break;
            case typeHtml:
                rId = R.mipmap.ic_html;
                break;
            case typeAlgorithm:
                rId = R.mipmap.ic_algorithm;
                break;
            default:
                rId = R.mipmap.ic_other;
        }
        return rId;
    }

    public String getStorageDir() {
        return Config.StorageDirectory + "/" + getTypeName();
    }

    public String getTypeName() {
        String dir;
        switch (type) {
            case typeJava:
                dir = Config.ProblemJava;
                break;
            case typeAndroid:
                dir = Config.ProblemAndroid;
                break;
            case typeHtml:
                dir = Config.ProblemHtml;
                break;
            case typeAlgorithm:
                dir = Config.ProblemAlgorithm;
            default:
                dir = "";
        }
        return dir;
    }

    @Override
    public String toString() {
        return "Problem{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", answer='" + answer + '\'' +
                ", type=" + type +
                '}';
    }
}
