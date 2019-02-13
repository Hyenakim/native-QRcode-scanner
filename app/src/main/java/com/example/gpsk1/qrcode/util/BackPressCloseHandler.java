package com.example.gpsk1.qrcode.util;

import android.app.Activity;
import android.os.Build;
import android.widget.Toast;

public class BackPressCloseHandler {
    private long backKeyPressedTime = 0;
    private Toast toast;
    private Activity activity;

    public BackPressCloseHandler(Activity context) {
        this.activity = context;
    }
    public void onBackPressed(){
        if(System.currentTimeMillis() > backKeyPressedTime + 2000){
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if(System.currentTimeMillis() <= backKeyPressedTime + 2000){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                activity.finishAffinity();
            }
            toast.cancel();
        }
    }
    public void showGuide(){
        toast = Toast.makeText(activity,"뒤로 버튼을 한번 더 누르시면 종료합니다.",Toast.LENGTH_SHORT); toast.show();
    }
}
