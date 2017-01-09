package com.example.cmy.lecturetest.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cmy.lecturetest.R;
import com.example.cmy.lecturetest.application.CustomApplication;
import com.example.cmy.lecturetest.socket.MySocket;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class QRCodeActivity extends Activity {
    private String lecName = null;
    private TextView tv_lecName = null;
    private ImageView imageView = null;
    private Bitmap bmp = null;
    private ActionBar actionBar;
    private CustomApplication application = null;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x0:   //获取二维码成功
                    imageView.setImageBitmap(bmp);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        actionBar = getActionBar();
        // 设置是否显示应用程序图标
        actionBar.setDisplayShowHomeEnabled(true);
        // 将应用程序图标设置为可点击的按钮，并在图标上添加向左箭头
        actionBar.setDisplayHomeAsUpEnabled(true);

        application = (CustomApplication) getApplication();
        lecName = getIntent().getStringExtra("lecName");
        tv_lecName = (TextView) findViewById(R.id.tv_lecName);
        imageView = (ImageView) findViewById(R.id.image01);

        tv_lecName.setText(lecName);
        new Thread(new Runnable() {
            @Override
            public void run() {
                MySocket mySocket = null;
                try {
                    mySocket = new MySocket();
                    Map<String, String> parasSent = new HashMap<>();
                    parasSent.put("action", "getQRCode");
                    parasSent.put("studNum", application.getUsername());
                    parasSent.put("lecName", lecName);
                    mySocket.sendString(parasSent);
                    DataInputStream dataInput = new DataInputStream(mySocket.getInputStream());
                    int size = dataInput.readInt();
                    byte[] data = new byte[size];
                    int len = 0;
                    while (len < size) {
                        len += dataInput.read(data, len, size - len);
                    }
                    ByteArrayOutputStream outPut = new ByteArrayOutputStream();
                    bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, outPut);
                    handler.sendEmptyMessage(0x0); //获取二维码成功

                    //Bitmap bitmap = BitmapFactory.decodeStream(dataInput);
                    //myHandler.obtainMessage().sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    mySocket.close();
                }
            }
        }).start();
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
