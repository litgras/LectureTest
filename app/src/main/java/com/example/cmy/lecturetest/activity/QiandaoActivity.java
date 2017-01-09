package com.example.cmy.lecturetest.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.cmy.lecturetest.R;
import com.example.cmy.lecturetest.application.CustomApplication;
import com.example.cmy.lecturetest.socket.MySocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cmy on 2016/12/6.
 */
public class QiandaoActivity extends Activity {
    private CustomApplication application;
    private SimpleAdapter adapter;
    private ListView list;
    private ActionBar actionBar;
    private String[] arr;

    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x00://获取成功
                    Bundle bundle = msg.getData();
                    String names = bundle.getString("names");
                    if("null".equals(names)){
                        setContentView(R.layout.nullmyorder);
                    }
                    else {
                        arr = names.split(";");
                        List<Map<String, Object>> listItems = new ArrayList<>();
                        for(int i = 0; i< arr.length; i++){
                            Map<String, Object> listItem = new HashMap<>();
                            listItem.put("name", arr[i]);
                            listItems.add(listItem);
                        }
                        adapter = new SimpleAdapter(QiandaoActivity.this, listItems, R.layout.array_item,
                                new String[]{"name"},
                                new int[]{ R.id.name});
                        list.setAdapter(adapter);
                    }
                    break;
                case 0x01://获取失败
                    setContentView(R.layout.error);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myorder);
        application = (CustomApplication) getApplication();
        list = (ListView) findViewById(R.id.mylist);
        actionBar = getActionBar();
        // 设置是否显示应用程序图标
        actionBar.setDisplayShowHomeEnabled(true);
        // 将应用程序图标设置为可点击的按钮，并在图标上添加向左箭头
        actionBar.setDisplayHomeAsUpEnabled(true);
        getList();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(QiandaoActivity.this, QRCodeActivity.class);
                intent.putExtra("lecName", arr[position]);
                startActivity(intent);
                QiandaoActivity.this.finish();
            }
        });
    }
    public void getList(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                MySocket mySocket = new MySocket();
                try {
                    Map<String, String> parasSent = new HashMap<>();
                    parasSent.put("action", "getMyorder");
                    parasSent.put("studNum", application.getUsername());
                    parasSent.put("studPsw", application.getPsw());
                    Map<String, String> parasRec = mySocket.deal(parasSent);
                    String result = parasRec.get("status");
                    //获取成功
                    if ("success".equals(result)) {
                        Message message = new Message();
                        message.what = 0x0;
                        Bundle bundle = new Bundle();
                        bundle.putString("names",parasRec.get("lecNames"));
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }
                    //获取失败
                    else if ("error".equals(result)) {
                        handler.sendEmptyMessage(0x1);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(0x1);
                } finally {
                    mySocket.close();
                }
            }
        }).start();
    }
    /*@Override
    protected void onResume() {
        super.onResume();
        getList();
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return true;
    }

}
