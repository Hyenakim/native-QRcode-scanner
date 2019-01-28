package com.example.gpsk1.qrcode;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;

import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

public class CustomScannerActivity extends Activity implements DecoratedBarcodeView.TorchListener{

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private BackPressCloseHandler backPressCloseHandler;
    private Boolean switchFlashLightButtonCheck;
    private ImageButton switchFlashLightButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_scanner);

        switchFlashLightButtonCheck = true;

        backPressCloseHandler = new BackPressCloseHandler(this);

        switchFlashLightButton = (ImageButton)findViewById(R.id.switch_flashlight);

        if(!hasFlash()){
            switchFlashLightButton.setVisibility(View.GONE);
        }

        barcodeScannerView = (DecoratedBarcodeView)findViewById(R.id.zxing_barcode_scanner);
        barcodeScannerView.setTorchListener(this);
        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(),savedInstanceState);
        capture.decode();
    }

    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    public void switchFlashLight(View view){
        if(switchFlashLightButtonCheck){
            barcodeScannerView.setTorchOn();
        }else{
            barcodeScannerView.setTorchOff();
        }
    }

    @Override
    public void onTorchOn() {
        switchFlashLightButton.setImageResource(R.drawable.round_flash_on_black_36);
        switchFlashLightButtonCheck = false;
    }

    @Override
    public void onTorchOff() {
        switchFlashLightButton.setImageResource(R.drawable.round_flash_off_black_36);
        switchFlashLightButtonCheck = true;
    }
}
