package com.example.cmy.lecturetest.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.cmy.lecturetest.R;
import com.example.cmy.lecturetest.application.CustomApplication;
import com.example.cmy.lecturetest.dialog.ChgpassDialog;
import com.example.cmy.lecturetest.socket.MySocket;
import com.example.cmy.lecturetest.utils.ClientUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LectureListActivity extends Activity {
    private CustomApplication application;
    private SimpleAdapter adapter;
    private ListView list;
    List<Map<String, String>> listItems;

    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x00://获取成功
                    Bundle bundle = msg.getData();
                    String names = bundle.getString("names");
                    if("null".equals(names)){
                        setContentView(R.layout.nulllist);
                    }
                    else {
                        listItems = ClientUtils.stringToListmap(names);
                        adapter = new SimpleAdapter(LectureListActivity.this, listItems, R.layout.array_item,
                                new String[]{"lecName","lecDate"},
                                new int[]{ R.id.name, R.id.date});
                        list.setAdapter(adapter);
                    }
                    break;
                case 0x01://获取失败
                    setContentView(R.layout.error);
                    break;
                case 0x10://修改密码成功
                    String newpass = (String)msg.getData().get("newpass");
                    application.setPsw((String)msg.getData().get("newpass"));
                    Toast.makeText(LectureListActivity.this, "修改密码成功", Toast.LENGTH_SHORT).show();
                    break;
                case 0x11://修改密码失败
                    Toast.makeText(LectureListActivity.this, "修改密码失败", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_list);
        application = (CustomApplication) getApplication();
        list = (ListView) findViewById(R.id.list);
        new Thread(new Runnable() {
            @Override
            public void run() {
                MySocket mySocket = new MySocket();
                try {
                    Map<String, String> parasSent = new HashMap<>();
                    parasSent.put("action", "getLecNames");
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
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(LectureListActivity.this, LectureDetailActivity.class);
                intent.putExtra("lecName", listItems.get(position).get("lecName"));
                startActivity(intent);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflator = new MenuInflater(this);
        //装填R.menu.my_menu对应的菜单，并添加到menu中
        inflator.inflate(R.menu.menu_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_myorder://我的预约
                //跳转到讲座列表界面
                Intent intent = new Intent(this, MyorderActivity.class);
                startActivity(intent);
                break;
            case R.id.item_signin://签到
                //跳转到讲座列表界面
                Intent intent3 = new Intent(this, QiandaoActivity.class);
                startActivity(intent3);
                break;
            case R.id.item_changepsw://修改密码
                ChgpassDialog chgpassDialog = new ChgpassDialog(this);
                chgpassDialog.show();
                break;
            case R.id.item_logout://切换账号
                //删除sharedprefence的用户信息，跳到登录界面
                application.clearUser();
                Intent intent2 = new Intent(this, LoginActivity.class);
                this.startActivity(intent2);
                this.finish();
                break;
            case R.id.item_exit://退出
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
