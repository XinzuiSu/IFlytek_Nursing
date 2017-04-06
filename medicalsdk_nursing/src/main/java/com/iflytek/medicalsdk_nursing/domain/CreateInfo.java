package com.iflytek.medicalsdk_nursing.domain;

/**
 * Created by suxiaofeng on 2017/3/31.
 */

public class CreateInfo {

    /**
     * chatCode : 4742
     * chatId : 27
     * creatTime : 1490767363021
     * creater : 111
     * roomStatus : 1
     * targetId : 222
     * targetName : 患者
     */

    private int chatCode;
    private int chatId;
    private long creatTime;
    private String creater;
    private String roomStatus;
    private String targetId;
    private String targetName;

    public int getChatCode() {
        return chatCode;
    }

    public void setChatCode(int chatCode) {
        this.chatCode = chatCode;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public long getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(long creatTime) {
        this.creatTime = creatTime;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getRoomStatus() {
        return roomStatus;
    }

    public void setRoomStatus(String roomStatus) {
        this.roomStatus = roomStatus;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }
}
