package com.example.projekti_pajisjemobilje;

public class ReadWriteUserDetails {
    public String fullName, doB, gender, mobile;
    public ReadWriteUserDetails(String textFullName, String textDoB, String textGender, String textMobile) {
        this.fullName = textFullName; // Emri i parametrave tani përputhet saktë
        this.doB = textDoB;
        this.gender = textGender;
        this.mobile = textMobile;
    }
}
