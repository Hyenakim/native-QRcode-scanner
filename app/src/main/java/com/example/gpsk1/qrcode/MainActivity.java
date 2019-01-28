package com.example.gpsk1.qrcode;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {

    private Button start;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CustomScannerActivity.class);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, ">>> requestCode = " + requestCode + ", resultCode = " + resultCode);

        if(requestCode == IntentIntegrator.REQUEST_CODE){
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            Log.i(TAG, ">>> result.getContents()   :  " + result.getContents());
            Log.i(TAG, ">>> result.getFormatName()   :  " + result.getFormatName());

        }
    }
}
