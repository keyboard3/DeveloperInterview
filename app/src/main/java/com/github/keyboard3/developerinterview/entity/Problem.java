package com.github.keyboard3.developerinterview.entity;

import com.github.keyboard3.developerinterview.Config;
import com.github.keyboard3.developerinterview.R;
import com.github.keyboard3.developerinterview.pattern.AlgorithmType;
import com.github.keyboard3.developerinterview.pattern.AndroidType;
import com.github.keyboard3.developerinterview.pattern.HtmlType;
import com.github.keyboard3.developerinterview.pattern.JavaType;
import com.github.keyboard3.developerinterview.pattern.ProblemType;
import com.github.keyboard3.developerinterview.pattern.ProblemTypeFactory;

import java.io.Serializable;

/**
 * Created by keyboard3 on 2017/9/3.
 */

public class Problem implements Serializable {

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

    public void setType(String typeName) {
        type = ProblemTypeFactory.getProblemType(typeName).getType();
    }

    public int getTypeIcon() {
        return ProblemTypeFactory.getProblemType(type).getTypeIcon();
    }

    public String getStorageDir() {
        return Config.StorageDirectory + "/" + getTypeName();
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
}
