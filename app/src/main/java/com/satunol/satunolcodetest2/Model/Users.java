package com.satunol.satunolcodetest2.Model;

public class Users {
    String userId, email, name, photoProfile;

    public Users(String userId, String email, String name, String photoProfile) {
        this.userId = userId;
        this.email = email;
        this.name = email;
        this.photoProfile = photoProfile;
    }

    public Users() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getPhotoProfile() {
        return photoProfile;
    }

    public void setPhotoProfile(String photoProfile) {
        this.photoProfile = photoProfile;
    }
}

