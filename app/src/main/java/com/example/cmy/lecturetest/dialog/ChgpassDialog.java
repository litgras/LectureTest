package com.example.cmy.lecturetest.dialog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.Toast;

import com.example.cmy.lecturetest.R;
import com.example.cmy.lecturetest.activity.LectureListActivity;
import com.example.cmy.lecturetest.application.CustomApplication;
import com.example.cmy.lecturetest.socket.MySocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cmy on 2016/12/5.
 */
public class ChgpassDialog {
    private LectureListActivity activity;
    private AlertDialog dialog;
    private AlertDialog.Builder builder;
    private EditText et_oldpass;
    private EditText et_newpass;
    private EditText et_confirmpass;
    private String oldPass;
    private String newPass;
    private String confirmPass;
    private CustomApplication application;
    public ChgpassDialog(LectureListActivity activity){
        this.activity = activity;
        application = CustomApplication.getInstance();
    }
    public void show()
    {
        TableLayout chgpassForm = (TableLayout)activity.getLayoutInflater()
                .inflate( R.layout.changepass, null);
        et_oldpass = (EditText) chgpassForm.findViewById(R.id.et_oldpass);
        et_newpass = (EditText) chgpassForm.findViewById(R.id.et_newpass);
        et_confirmpass = (EditText) chgpassForm.findViewById(R.id.et_confirmpass);
        new AlertDialog.Builder(activity)
                // 设置对话框的标题
                .setTitle("修改密码")
                // 设置对话框显示的View对象
                .setView(chgpassForm)
                // 为对话框设置一个“确定”按钮
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        // 执行修改密码处理
                        oldPass = et_oldpass.getText().toString().trim();
                        newPass = et_newpass.getText().toString().trim();
                        confirmPass = et_confirmpass.getText().toString().trim();
                        if(validate(oldPass, newPass, confirmPass)) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    MySocket mySocket = new MySocket();
                                    try {
                                        Map<String, String> parasSent = new HashMap<>();
                                        parasSent.put("action", "chgpass");
                                        parasSent.put("studNum", application.getUsername());
                                        parasSent.put("oldpass", oldPass);
                                        parasSent.put("newpass", newPass);
                                        Map<String, String> parasRec = mySocket.deal(parasSent);
                                        String result = parasRec.get("status");
                                        //修改成功
                                        if ("success".equals(result)) {
                                            Message message = new Message();
                                            message.what = 0x10;
                                            Bundle bundle = new Bundle();
                                            bundle.putString("newpass", newPass);
                                            message.setData(bundle);
                                            activity.handler.sendMessage(message);
                                        }
                                        //修改失败
                                        else if ("error".equals(result)) {
                                            activity.handler.sendEmptyMessage(0x11);
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        activity.handler.sendEmptyMessage(0x11);
                                    } finally {
                                        mySocket.close();
                                    }
                                }
                            }).start();
                        }
                    }
                })
                // 为对话框设置一个“取消”按钮
                .setNegativeButton("取消", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which)
                    {
                        // 取消修改，不做任何事情
                    }
                })
                // 创建并显示对话框
                .create()
                .show();
    }

    private boolean validate(String oldPass, String newPass, String confirmPass) {
        boolean flag = true;
        //旧密码输入错误
        String psw = application.getPsw();
        if(!application.getPsw().equals(oldPass)){
            flag = false;
            Toast.makeText(activity, "旧密码错误", Toast.LENGTH_SHORT).show();
        }
        //新密码为空
        else if("".equals(newPass)){
            flag = false;
            Toast.makeText(activity, "新密码不能为空", Toast.LENGTH_SHORT).show();
        }
        else if(!newPass.equals(confirmPass)){
            flag = false;
            Toast.makeText(activity, "两次新密码输入不一致", Toast.LENGTH_SHORT).show();
        }
        return flag;
    }
}
