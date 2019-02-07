package com.example.gpsk1.qrcode;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.nfc.Tag;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

import org.w3c.dom.Text;

import java.util.Collection;

public class MainActivity extends AppCompatActivity{

    private Button start;
    private MainActivity activity;
    private static final String TAG = "MainActivity";


    private IntentIntegrator integrator;
    private boolean restart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"메인화면oncreate");

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG,"메인화면start");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "메인화면resume");
        integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CustomScannerActivity.class);
        integrator.initiateScan();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "메인화면restart");

    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG,"메인화면onpause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG,"메인화면restart");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d("onActivityResult", "onActivityResult: .");
        if (resultCode == Activity.RESULT_OK) {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
            String re = scanResult.getContents();
            String message = re;
            Log.d("onActivityResult", "onActivityResult: ." + re);
            Toast.makeText(this, re, Toast.LENGTH_LONG).show();
        }
    }


}
