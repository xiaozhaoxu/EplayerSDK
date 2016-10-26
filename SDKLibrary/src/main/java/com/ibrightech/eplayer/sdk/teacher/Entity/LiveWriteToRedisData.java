package com.ibrightech.eplayer.sdk.teacher.Entity;

/**
 * Created by zhaoxu2014 on 16/5/11.
 */
public class LiveWriteToRedisData {
    long uuid;//": 28915,
    String userAccount;//": "1000184",
    String roomId;//": "5732c96f4657c4b6410004a8",
    String customer;//": "ynx",
    String userRole;//": "teacher",
    String extendId;//": "2016051113555872045",
    int validateType;//": 5,
    int userIdentity;//": 2,
    String fromStr;//": "other|dW5rbm93bnVuZGVmaW5lZA=="

    public long getUuid() {
        return uuid;
    }

    public void setUuid(long uuid) {
        this.uuid = uuid;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getExtendId() {
        return extendId;
    }

    public void setExtendId(String extendId) {
        this.extendId = extendId;
    }

    public int getValidateType() {
        return validateType;
    }

    public void setValidateType(int validateType) {
        this.validateType = validateType;
    }

    public int getUserIdentity() {
        return userIdentity;
    }

    public void setUserIdentity(int userIdentity) {
        this.userIdentity = userIdentity;
    }

    public String getFromStr() {
        return fromStr;
    }

    public void setFromStr(String fromStr) {
        this.fromStr = fromStr;
    }
}
