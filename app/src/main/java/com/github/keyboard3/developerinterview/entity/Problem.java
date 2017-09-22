package com.github.keyboard3.developerinterview.entity;

import android.support.annotation.NonNull;

import com.github.keyboard3.developerinterview.Config;
import com.github.keyboard3.developerinterview.pattern.ProblemTypeFactory;

import java.io.Serializable;

/**
 * Created by keyboard3 on 2017/9/3.
 */

public class Problem implements Serializable, Comparable<Problem> {

    public Problem(String id, String title, String content, String answer, String source, int type, String audio) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.answer = answer;
        this.type = type;
        this.source = "";
    }

    public String id;
    public String title;
    public String content;
    public String answer;
    public String source;
    public int type;//1java 2android 3html

    public int getId() {
        return Integer.parseInt(id);
    }

    public void setType(String typeName) {
        type = ProblemTypeFactory.getProblemType(typeName).getType();
    }

    public int getTypeIcon() {
        return ProblemTypeFactory.getProblemType(type).getTypeIcon();
    }

    public String getStorageDir() {
        return Config.STORAGE_DIRECTORY + "/" + getTypeName();
    }

    public String getTypeName() {
        return ProblemTypeFactory.getProblemType(type).getTypeStr();
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

    @Override
    public int compareTo(@NonNull Problem problem) {
        return getId() - problem.getId();
    }
}
