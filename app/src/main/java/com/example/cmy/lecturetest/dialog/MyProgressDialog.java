package com.example.cmy.lecturetest.dialog;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by cmy on 2016/12/4.
 */
public class MyProgressDialog {
    private ProgressDialog pd;
    private Context context;

    public MyProgressDialog(Context context) {
        this.context = context;
        pd = new ProgressDialog(context);
    }

    public void show(String title, String message) {
        pd.setTitle(title);
        pd.setMessage(message);
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
    }

    public void hide() {
        pd.setCanceledOnTouchOutside(true);
        pd.hide();
    }
}
