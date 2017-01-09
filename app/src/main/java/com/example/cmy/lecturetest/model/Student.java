package com.example.cmy.lecturetest.model;

import java.io.Serializable;

/**
 * Created by cmy on 2016/11/20.
 */
public class Student implements Serializable {
    private String studNum;
    private String studName;
    private boolean gender;
    private String studPsw;
    private String phoneNum;
    private String dept;

    public Student() {
    }

    public Student(String studNum, String studPsw) {
        this.studNum = studNum;
        this.studPsw = studPsw;
    }

    public String getStudNum() {
        return studNum;
    }

    public void setStudNum(String studNum) {
        this.studNum = studNum;
    }

    public String getStudName() {
        return studName;
    }

    public void setStudName(String studName) {
        this.studName = studName;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public String getStudPsw() {
        return studPsw;
    }

    public void setStudPsw(String studPsw) {
        this.studPsw = studPsw;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }
}
