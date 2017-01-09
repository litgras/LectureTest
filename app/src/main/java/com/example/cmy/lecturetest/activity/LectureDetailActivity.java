package com.example.cmy.lecturetest.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cmy.lecturetest.R;
import com.example.cmy.lecturetest.application.CustomApplication;
import com.example.cmy.lecturetest.dialog.MyProgressDialog;
import com.example.cmy.lecturetest.socket.MySocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LectureDetailActivity extends Activity {
    private CustomApplication application;
    private ActionBar actionBar;

    private String lecName;
    private TextView tv_lectitle;
    private TextView tv_speaker;
    private TextView tv_lecDetail;
    private TextView tv_speDetail;
    private TextView tv_note;
    private TextView tv_time;
    private TextView tv_location;
    private Button btn_appoint;
    private Button btn_cancel;
    private MyProgressDialog dialog;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x00://获取讲座详细信息成功
                    Bundle bundle = msg.getData();
                    if ("false".equals(bundle.getString("me"))) {
                        btn_appoint.setEnabled(true);
                        btn_cancel.setEnabled(false);
                    }
                    else {
                        btn_appoint.setEnabled(false);
                        btn_cancel.setEnabled(true);
                    }
                    tv_lectitle.setText(lecName);
                    tv_speaker.setText(bundle.getString("teaName"));
                    tv_lecDetail.setText(bundle.getString("lecIntro"));
                    tv_speDetail.setText(bundle.getString("teaIntro"));
                    tv_time.setText(bundle.getString("time"));
                    tv_note.setText(bundle.getString("notation"));
                    tv_location.setText(bundle.getString("room"));
                    break;
                case 0x01://获取讲座详细信息失败
                    TextView tv_failed = new TextView(LectureDetailActivity.this);
                    tv_failed.setText(R.string.conn_err);
                    LinearLayout root = (LinearLayout) findViewById(R.id.root);
                    root.addView(tv_failed);
                    break;
                case 0x10://预约成功
                    dialog.hide();
//                    Toast.makeText(LectureDetailActivity.this, R.string.appoint_success, Toast.LENGTH_SHORT).show();
                    btn_appoint.setEnabled(false);
                    btn_cancel.setEnabled(true);
                    Intent intent = new Intent(LectureDetailActivity.this, MyorderActivity.class);
                    intent.putExtra("lecName", lecName);
                    startActivity(intent);
                    LectureDetailActivity.this.finish();
                    break;
                case 0x11://人数已满
                    dialog.hide();
                    Toast.makeText(LectureDetailActivity.this, R.string.appoint_failed, Toast.LENGTH_SHORT).show();
                    break;
                case 0x12://预约失败
                    dialog.hide();
                    Toast.makeText(LectureDetailActivity.this, R.string.appoint_error, Toast.LENGTH_SHORT).show();
                    break;
                case 0x20://取消成功
                    dialog.hide();
                    btn_cancel.setEnabled(false);
                    btn_appoint.setEnabled(true);
                    Intent intent1 = new Intent(LectureDetailActivity.this, MyorderActivity.class);
                    intent1.putExtra("lecName", lecName);
                    startActivity(intent1);
                    LectureDetailActivity.this.finish();
                    break;
                case 0x21://取消失败
                    dialog.hide();
                    Toast.makeText(LectureDetailActivity.this, R.string.cancel_failed, Toast.LENGTH_SHORT).show();
                    break;
                case 0x30://预约中对话框
                    dialog = new MyProgressDialog(LectureDetailActivity.this);
                    dialog.show("预约", "正在预约，请稍后...");
                    break;
                case 0x31://取消中对话框
                    dialog = new MyProgressDialog(LectureDetailActivity.this);
                    dialog.show("取消", "正在取消，请稍后...");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_detail);
        actionBar = getActionBar();
        // 设置是否显示应用程序图标
        actionBar.setDisplayShowHomeEnabled(true);
        // 将应用程序图标设置为可点击的按钮，并在图标上添加向左箭头
        actionBar.setDisplayHomeAsUpEnabled(true);

        application = (CustomApplication) getApplication();
        tv_lectitle = (TextView) findViewById(R.id.tv_lectitle);
        tv_speaker = (TextView) findViewById(R.id.tv_speaker);
        tv_note = (TextView) findViewById(R.id.tv_note);
        tv_lecDetail = (TextView) findViewById(R.id.tv_lecDetail);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_speDetail = (TextView) findViewById(R.id.tv_speDetail);
        tv_location = (TextView) findViewById(R.id.tv_location);
        btn_appoint = (Button) findViewById(R.id.btn_appoint);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        TextPaint tp;
        tp = ((TextView) findViewById(R.id.tv1)).getPaint();
        tp.setFakeBoldText(true);
        tp = ((TextView) findViewById(R.id.tv2)).getPaint();
        tp.setFakeBoldText(true);
        tp = ((TextView) findViewById(R.id.tv3)).getPaint();
        tp.setFakeBoldText(true);
        tp = ((TextView) findViewById(R.id.tv4)).getPaint();
        tp.setFakeBoldText(true);
        tp = ((TextView) findViewById(R.id.tv5)).getPaint();
        tp.setFakeBoldText(true);
        tp = ((TextView) findViewById(R.id.tv6)).getPaint();
        tp.setFakeBoldText(true);
        tp = ((TextView) findViewById(R.id.tv7)).getPaint();
        tp.setFakeBoldText(true);
        Intent intent = getIntent();
        lecName = intent.getStringExtra("lecName");

        new Thread(new Runnable() {
            @Override
            public void run() {
                MySocket mySocket = new MySocket();
                try {
                    Map<String, String> parasSent = new HashMap<>();
                    parasSent.put("action", "getLecDetail");
                    parasSent.put("lecName", lecName);
                    parasSent.put("studNum", application.getUsername());
                    Map<String, String> parasRec = mySocket.deal(parasSent);
                    String result = parasRec.get("status");
                    //获取详细信息成功
                    if ("success".equals(result)) {
                        Message message = new Message();
                        message.what = 0x00;
                        Bundle bundle = new Bundle();
                        bundle.putString("teaName",parasRec.get("teaName"));
                        bundle.putString("lecIntro",parasRec.get("lecIntro"));
                        bundle.putString("teaIntro",parasRec.get("teaIntro"));
                        bundle.putString("time",parasRec.get("time"));
                        bundle.putString("notation",parasRec.get("notation"));
                        bundle.putString("room",parasRec.get("room"));
                        bundle.putString("me", parasRec.get("me"));
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }
                    //获取详细信息失败
                    else if ("failed".equals(result)) {
                        handler.sendEmptyMessage(0x01);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(0x01);
                } finally {
                    mySocket.close();
                }
            }
        }).start();
        btn_appoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(0x30);//预约中对话框
                        MySocket mySocket = new MySocket();
                        try {
                            Map<String, String> parasSent = new HashMap<>();
                            parasSent.put("action", "appoint");
                            parasSent.put("lecName", lecName);
                            parasSent.put("studNum", application.getUsername());
                            parasSent.put("psw", application.getPsw());
                            Map<String, String> parasRec = mySocket.deal(parasSent);
                            String result = parasRec.get("status");
                            //预约成功
                            if ("success".equals(result)) {
                                // 创建消息
                                handler.sendEmptyMessage(0x10);
                            }
                            //人数已满
                            else if ("failed".equals(result)) {
                                handler.sendEmptyMessage(0x11);
                            }
                            //预约失败
                            else if ("error".equals(result)) {
                                handler.sendEmptyMessage(0x12);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            handler.sendEmptyMessage(0x12);
                        } finally {
                           mySocket.close();
                        }
                    }
                }).start();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(0x31);//取消中对话框
                        MySocket mySocket = new MySocket();
                        try {
                            Map<String, String> parasSent = new HashMap<>();
                            parasSent.put("action", "cancel");
                            parasSent.put("lecName", lecName);
                            parasSent.put("studNum", application.getUsername());
                            parasSent.put("psw", application.getPsw());
                            Map<String, String> parasRec = mySocket.deal(parasSent);
                            String result = parasRec.get("status");
                            //取消成功
                            if ("success".equals(result)) {
                                // 创建消息
                                handler.sendEmptyMessage(0x20);
                            }
                            //取消失败
                            else if ("error".equals(result)) {
                                handler.sendEmptyMessage(0x21);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            handler.sendEmptyMessage(0x21);
                        } finally {
                            mySocket.close();
                        }
                    }
                }).start();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return true;
    }
}
