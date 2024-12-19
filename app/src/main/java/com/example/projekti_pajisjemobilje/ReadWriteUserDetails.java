package com.example.projekti_pajisjemobilje;

public class ReadWriteUserDetails {
    public String  doB, gender, mobile;
    //constuctor
    public ReadWriteUserDetails(){};
    public ReadWriteUserDetails( String textDoB, String textGender, String textMobile) {

        this.doB = textDoB;
        this.gender = textGender;
        this.mobile = textMobile;

    }
}
