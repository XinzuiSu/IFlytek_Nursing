package com.iflytek.medicalsdk_nursing.domain;

/**
 * Created by suxiaofeng on 2017/3/28.
 */

public class ItemtInfo {
    private String name;
    private String time;
    private String content;
    //0,自己   1,他人
    private int isMySelf;

    public ItemtInfo(String name, String time, String content, int isMySelf) {
        this.name = name;
        this.time = time;
        this.content = content;
        this.isMySelf = isMySelf;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int isMySelf() {
        return isMySelf;
    }

    public void setMySelf(int mySelf) {
        isMySelf = mySelf;
    }
}
