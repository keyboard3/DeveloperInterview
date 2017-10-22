package com.github.keyboard3.developerinterview.entity;

import java.io.Serializable;

/**
 * fir更新json信息
 *
 * @author keyboard3
 * @date 2017/9/6
 */

public class Version implements Serializable {

    /**
     * name : 猿面试
     * version : 3
     * changelog : 滑动删除
     * 向上滑动
     * 保存浏览位置
     * updated_at : 1504590243
     * versionShort : 1.4
     * build : 3
     * installUrl : http://download.fir.im/v2/app/install/59acbc2d959d6940060002ee?download_token=f5bde66bb46dd67c2517a94d1a165d63&source=update
     * install_url : http://download.fir.im/v2/app/install/59acbc2d959d6940060002ee?download_token=f5bde66bb46dd67c2517a94d1a165d63&source=update
     * direct_install_url : http://download.fir.im/v2/app/install/59acbc2d959d6940060002ee?download_token=f5bde66bb46dd67c2517a94d1a165d63&source=update
     * update_url : http://fir.im/interview
     * binary : {"fsize":4010812}
     */

    private String name;
    private String version;
    private String changelog;
    private int updated_at;
    private String versionShort;
    private String build;
    private String installUrl;
    private String install_url;
    private String direct_install_url;
    private String update_url;
    private BinaryBean binary;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getChangelog() {
        return changelog;
    }


    public String getVersionShort() {
        return versionShort;
    }


    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public String getInstallUrl() {
        return installUrl;
    }

    public static class BinaryBean {
        /**
         * fsize : 4010812
         */

        private int fsize;

        public int getFsize() {
            return fsize;
        }

        public void setFsize(int fsize) {
            this.fsize = fsize;
        }
    }
}
