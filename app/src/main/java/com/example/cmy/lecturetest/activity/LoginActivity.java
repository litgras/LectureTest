package com.example.cmy.lecturetest.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cmy.lecturetest.R;
import com.example.cmy.lecturetest.application.CustomApplication;
import com.example.cmy.lecturetest.dialog.MyProgressDialog;
import com.example.cmy.lecturetest.socket.MySocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends Activity {
    MyProgressDialog dialog;
    Button btn_login;
    EditText et_username;
    EditText et_psw;
    String studNum;
    String psw;
    private CustomApplication application;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x0: //登录成功
                    //保存此用户信息
                    application.saveUser(studNum, psw);
                    //跳转到讲座列表界面
                    Intent intent = new Intent(LoginActivity.this, LectureListActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
                    break;
                case 0x1://登录失败
                    dialog.hide();
                    Toast.makeText(LoginActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
                    break;
                case 0x2://连接异常
                    dialog.hide();
                    Toast.makeText(LoginActivity.this, R.string.login_error, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        application = (CustomApplication) getApplication();
        btn_login = (Button) findViewById(R.id.btn_login);
        et_username = (EditText) findViewById(R.id.et_userName);
        et_psw = (EditText) findViewById(R.id.et_psw);
        dialog = new MyProgressDialog(this);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                showSpinner();//正在登录对话框
                dialog.show("登录","正在登录，请稍后...");
                studNum = et_username.getText().toString().trim();
                psw = et_psw.getText().toString().trim();
                //如果通过验证，启动另一个线程登录
                if (validate(studNum, psw)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            MySocket mySocket = new MySocket();
                            try {
                                Map<String, String> parasSent = new HashMap<>();
                                parasSent.put("action", "login");
                                parasSent.put("studNum", studNum);
                                parasSent.put("studPsw", psw);
                                Map<String, String> parasRec = mySocket.deal(parasSent);
                                String result = parasRec.get("status");
                                //登陆成功
                                if ("success".equals(result)) {
                                    handler.sendEmptyMessage(0x0);
                                }
                                //登陆失败
                                else if ("failed".equals(result)) {
                                    handler.sendEmptyMessage(0x1);
                                }
                                //服务器异常
                                else if ("error".equals(result)) {
                                    handler.sendEmptyMessage(0x2);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                handler.sendEmptyMessage(0x2);
                            } finally {
                                mySocket.close();
                            }
                        }
                    }).start();
                }
            }
        });
    }

    //判断输入格式是否正确，如果正确返回true,错误返回false
    private boolean validate(String studNum, String psw) {
        //用户名或密码为空
        if ("".equals(studNum) || "".equals(psw)) {
            Toast.makeText(LoginActivity.this, "用户名或密码不能为空!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
