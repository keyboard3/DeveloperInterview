package com.github.keyboard3.developerinterview.entity;

/**
 * Created by keyboard3 on 2017/9/12.
 */

public class PluginInfo {
    public String name;
    public String packageName;
    public String mainClass;
    public String desc;

    public PluginInfo(String name, String packageName, String mainClass, String desc) {
        this.name = name;
        this.packageName = packageName;
        this.mainClass = mainClass;
        this.desc = desc;
    }
}
