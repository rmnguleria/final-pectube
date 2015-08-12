package com.theraiway.database;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by raman on 22/6/15.
 */
public class User {
    String userId;
    String Name;
    String SID;
    String Sex;
    String MobileNo;
    ArrayList<String> Clubs ;
    String Category;
    Date createdOn;
    Boolean Hosteller;
    Boolean Verified;

    public User(){
        Clubs = new ArrayList<>();
    }

    public Boolean getHosteller() {
        return Hosteller;
    }

    public void setHosteller(Boolean hosteller) {
        Hosteller = hosteller;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSID() {
        return SID;
    }

    public void setSID(String SID) {
        this.SID = SID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSex() {
        return Sex;
    }

    public void setSex(String sex) {
        Sex = sex;
    }

    public String getMobileNo() {
        return MobileNo;
    }

    public void setMobileNo(String mobileNo) {
        MobileNo = mobileNo;
    }

    public ArrayList<String> getClubs() {
        return Clubs;
    }

    public void setClubs(ArrayList<String> clubs) {
        Clubs = clubs;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Boolean getVerified() {
        return Verified;
    }

    public void setVerified(Boolean verified) {
        Verified = verified;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }
}
