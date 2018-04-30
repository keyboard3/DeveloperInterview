package com.github.keyboard3.developerinterview.entity;

import android.support.annotation.NonNull;

import com.github.keyboard3.developerinterview.ConfigConst;
import com.github.keyboard3.developerinterview.pattern.ProblemStateFactory;

import java.io.Serializable;

/**
 * 题目类实体
 *
 * @author keyboard3
 * @date 2017/9/3
 */

public class Problem implements Serializable, Comparable<Problem> {

    static Problem getEmptyProblem() {
        Problem problem = new Problem();
        problem.id = "";
        problem.title = "";
        problem.content = "";
        problem.answer = "";
        problem.type = 1;
        problem.source = "";
        return problem;
    }

    public String id;
    public String title;
    public String content;
    public String answer;
    public String source;
    /**
     * 1java 2android 3html
     */
    public int type;

    public int getId() {
        return Integer.parseInt(id);
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
