package com.example.cmy.lecturetest.application;

import android.app.Application;
import android.content.SharedPreferences;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * Created by cmy on 2016/11/30.
 */
public class CustomApplication extends Application {

    private static SocketAddress address;
    private static SharedPreferences sp;
    private static CustomApplication instance;
    public static CustomApplication getInstance(){
        return instance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        address = new InetSocketAddress("10.187.86.69", 30000);
        sp = getSharedPreferences("pref", MODE_PRIVATE);
    }

    public static void setPsw(String psw) {
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("psw", psw);
        edit.commit();
    }
    public static String getPsw() {
        return sp.getString("psw", null);
    }

    public static void clearUser(){
        SharedPreferences.Editor edit = sp.edit();
        edit.remove("username").remove("psw");
        edit.commit();
    }

    public static void saveUser(String studNum, String psw){
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("username", studNum).putString("psw", psw);
        edit.commit();
    }

    public static String getUsername() {
        return sp.getString("username", null);
    }

    public SocketAddress getAddress() {
        return address;
    }
}
