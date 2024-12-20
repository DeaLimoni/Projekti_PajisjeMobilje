package com.example.projekti_pajisjemobilje;

public class ReadWriteUserDetails {
    public String dob, gender, mobile;

    public ReadWriteUserDetails() {

    }

    public ReadWriteUserDetails(String dob, String gender, String mobile) {
        this.dob = dob;
        this.gender = gender;
        this.mobile = mobile;
    }

    // Getters and Setters
    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}


