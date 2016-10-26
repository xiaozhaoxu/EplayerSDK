package com.ibrightech.eplayer.sdk.teacher.Entity;

/**
 * Created by zhaoxu2014 on 16/5/11.
 */
public class LiveUser {

  int validateType;//": 5,
  long userId;//": 2891
  String  nickname;//": "1000184",
  String name;//": "1000184",
  int userType;//": 2,
  String userRole;//": "teacher",
   int userIdentity;

    public int getValidateType() {
        return validateType;
    }

    public void setValidateType(int validateType) {
        this.validateType = validateType;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public int getUserIdentity() {
        return userIdentity;
    }

    public void setUserIdentity(int userIdentity) {
        this.userIdentity = userIdentity;
    }
}
