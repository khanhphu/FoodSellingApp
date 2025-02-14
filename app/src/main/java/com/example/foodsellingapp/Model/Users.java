package com.example.foodsellingapp.Model;

public class Users {
    //variables
    private String uid;
    private String userType;
    private String email;
    private String name;
    private String profileImage;
    private long timestamp;
    //constructor

    public Users() {
    }

    public Users(String uid, String userType, String email, String name, String profileImage, long timestamp) {
        this.uid = uid;
        this.userType = userType;
        this.email = email;
        this.name = name;
        this.profileImage = profileImage;
        this.timestamp = timestamp;
    }
    //getter-setter

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
