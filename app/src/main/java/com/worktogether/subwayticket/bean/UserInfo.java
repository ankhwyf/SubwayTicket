package com.worktogether.subwayticket.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Ankhwyf on 16/12/17.
 * JavaBean不需要对objectId、createdAt、updatedAt、ACL四个属性进行定义。
 */

public class UserInfo extends BmobObject {

    private  String mobilePhoneNumber;
    private  String password;
    private Boolean mobilePhoneNumberVerified;
    private String username;

    //对应数据库中的表名
   public UserInfo(){
       this.setTableName("_User");
   }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMobilePhoneNumber() {
        return mobilePhoneNumber;
    }

    public void setMobilePhoneNumber(String mobilePhoneNumber) {
        this.mobilePhoneNumber = mobilePhoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getMobilePhoneNumberVerified() {
        return mobilePhoneNumberVerified;
    }

    public void setMobilePhoneNumberVerified(Boolean mobilePhoneNumberVerified) {
        this.mobilePhoneNumberVerified = mobilePhoneNumberVerified;
    }
}
