package com.example.cmy.lecturetest.socket;

import com.example.cmy.lecturetest.application.CustomApplication;
import com.example.cmy.lecturetest.utils.ClientUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

/**
 * Created by cmy on 2016/12/3.
 */
public class MySocket {
    private Socket socket;
    private PrintWriter pw;
    private BufferedReader br;
    private CustomApplication application;

    public MySocket(){
        socket = new Socket();
        application = CustomApplication.getInstance();
    }
    public Map<String, String> deal(Map<String, String> parasMap) throws IOException {
        socket.connect(application.getAddress(), 5000);
        String sendMsg = ClientUtils.mapToString(parasMap);
        pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"), true);
        pw.println(sendMsg);
        br = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        String recMsg = br.readLine();
        Map<String, String> paraMaps = ClientUtils.stringToMap(recMsg);
        return paraMaps;
    }

    public void sendString(Map<String, String> parasMap) throws IOException {
        socket.connect(application.getAddress(), 5000);
        String sendMsg = ClientUtils.mapToString(parasMap);
        pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"), true);
        pw.println(sendMsg);
    }
    public void close(){
        try {
            if (br != null)
                br.close();
            if (pw != null)
                pw.close();
            if (socket != null)
                socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public InputStream getInputStream() throws IOException {
        return socket.getInputStream();
    }
}
