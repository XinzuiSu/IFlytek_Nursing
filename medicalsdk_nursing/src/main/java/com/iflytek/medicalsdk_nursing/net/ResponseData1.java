package com.iflytek.medicalsdk_nursing.net;

import com.google.gson.annotations.SerializedName;

/**
 * Created by suxiaofeng on 2017/3/30.
 */

public class ResponseData1 {

    @SerializedName(value = "data")
    private String data; // 数据
    @SerializedName(value = "flag")
    private String flag; // 标签
    @SerializedName(value = "result")
    private String result; // 错误提示

    public ResponseData1() {
    }

    public String getData() {
        return data;
    }

    public String getFlag() {
        return flag;
    }

    public String getResult() {
        return result;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public void setResult(String result) {
        this.result = result;
    }

//    public class DataBean{
//
//        private String chatCode;
//        private String chatId;
//        private String creatTime;
//        private String creater;
//        private String roomStatus;
//        private String targetId;
//        private String targetName;
//
//        public String getChatCode() {
//            return chatCode;
//        }
//
//        public void setChatCode(String chatCode) {
//            this.chatCode = chatCode;
//        }
//
//        public String getChatId() {
//            return chatId;
//        }
//
//        public void setChatId(String chatId) {
//            this.chatId = chatId;
//        }
//
//        public String getCreatTime() {
//            return creatTime;
//        }
//
//        public void setCreatTime(String creatTime) {
//            this.creatTime = creatTime;
//        }
//
//        public String getCreater() {
//            return creater;
//        }
//
//        public void setCreater(String creater) {
//            this.creater = creater;
//        }
//
//        public String getRoomStatus() {
//            return roomStatus;
//        }
//
//        public void setRoomStatus(String roomStatus) {
//            this.roomStatus = roomStatus;
//        }
//
//        public String getTargetId() {
//            return targetId;
//        }
//
//        public void setTargetId(String targetId) {
//            this.targetId = targetId;
//        }
//
//        public String getTargetName() {
//            return targetName;
//        }
//
//        public void setTargetName(String targetName) {
//            this.targetName = targetName;
//        }
//    }
}
