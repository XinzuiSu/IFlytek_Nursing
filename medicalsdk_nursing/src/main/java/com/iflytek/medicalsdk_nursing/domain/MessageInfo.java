package com.iflytek.medicalsdk_nursing.domain;

/**
 * Created by suxiaofeng on 2017/3/31.
 */

public class MessageInfo {

    /**
     * chatId : 34
     * content : 这是一条信息
     * creatTime : 1490781391000
     * id : 10
     * messageFlag : 0
     * userId : 112
     */

    private int chatId;
    private String content;
    private long creatTime;
    private int id;
    private String messageFlag;
    private String userId;
    private String userName;
    private String userFlag;

    public String getUserFlag() {
        return userFlag;
    }

    public void setUserFlag(String userFlag) {
        this.userFlag = userFlag;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(long creatTime) {
        this.creatTime = creatTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessageFlag() {
        return messageFlag;
    }

    public void setMessageFlag(String messageFlag) {
        this.messageFlag = messageFlag;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
