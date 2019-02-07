package com.example.gpsk1.qrcode;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.encoder.QRCode;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Hashtable;
import java.util.List;

public class CustomScannerActivity extends Activity implements DecoratedBarcodeView.TorchListener{

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private BackPressCloseHandler backPressCloseHandler;
    private Boolean switchFlashLightButtonCheck;
    private ImageButton switchFlashLightButton;
    private static final String TAG = "CustomScannerActivity";
    private AlertDialog.Builder builder ;
    private ImageView photoView;
    private String presult;
    private String ptype;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"1");
        setContentView(R.layout.activity_custom_scanner);
        init();
        //initializeDb(this);

        switchFlashLightButtonCheck = true;

        backPressCloseHandler = new BackPressCloseHandler(this);

        switchFlashLightButton = (ImageButton)findViewById(R.id.switch_flashlight);

        photoView = (ImageView)findViewById(R.id.showView);

        if(!hasFlash()){
            switchFlashLightButton.setVisibility(View.GONE);
        }

        barcodeScannerView = (DecoratedBarcodeView)findViewById(R.id.zxing_barcode_scanner);
        barcodeScannerView.setTorchListener(this);
        barcodeScannerView.setFocusable(false);
        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(),savedInstanceState);

        //capture.decode();
        barcodeScannerView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                barcodeScannerView.pause();
                Log.i(TAG,result.getBarcodeFormat().toString());
                Log.i(TAG,result.toString());
                //db저장
                ptype = result.getBarcodeFormat().toString();
                presult = result.toString();
                newResult();
                if(result.getBarcodeFormat()== BarcodeFormat.QR_CODE)
                    showQRcodeDialog(result.toString());
                else
                    showBarcodeDialog(result.toString());
                //Toast.makeText(CustomScannerActivity.this, result.toString(), Toast.LENGTH_LONG).show();
            }
            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {
                Log.i(TAG,resultPoints.toString());

            }
        });
    }
    public void init(){
        ptype = null;
        presult = null;
    }
    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"2");

        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG,"3");
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"4");
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

    //ImageButton onClick 함수 모음
    //손전등버튼
    public void switchFlashLight(View view){
        if(switchFlashLightButtonCheck){
            barcodeScannerView.setTorchOn();
        }else{
            barcodeScannerView.setTorchOff();
        }
    }
    //앨범버튼
    public void addPhoto(View view){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }
    //기록버튼
    public void showHistory(View view){
        Intent intent = new Intent(getApplicationContext(),ResultActivity.class);
        startActivity(intent);
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
    //qrcode 다이얼로그 //예-크롬창 이동/아니오-뒤로가기
    public void showQRcodeDialog(final String url){
        Log.i(TAG,"다이얼로그시작");
        builder = new AlertDialog.Builder(this);
        builder.setMessage(url+"로 이동하시겠습니까?");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        intent.setPackage("com.android.chrome");
                        startActivity(intent);
                    }
                });
        builder.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        barcodeScannerView.resume();
                        photoView.setVisibility(View.INVISIBLE);
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);//뒤로가기키 막기
        dialog.setCanceledOnTouchOutside(false);//배경 터치 막기
        dialog.show();
        Log.i(TAG,"다이얼로그끝");
    }
    //barcode 다이얼로그 //확인-뒤로가기
    public void showBarcodeDialog(final String num){
        Log.i(TAG,"다이얼로그시작");
        builder = new AlertDialog.Builder(this);
        builder.setMessage(num);
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        barcodeScannerView.resume();
                        photoView.setVisibility(View.INVISIBLE);
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);//뒤로가기키 막기
        dialog.setCanceledOnTouchOutside(false);//배경 터치 막기
        dialog.show();
        Log.i(TAG,"다이얼로그끝");
    }
    public void showError(){
        Log.i(TAG,"다이얼로그시작");
        builder = new AlertDialog.Builder(this);
        builder.setMessage("스캔된 내용이 없습니다. 다시 시도해주세요.");
        builder.setPositiveButton("돌아가기",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        barcodeScannerView.resume();
                        photoView.setVisibility(View.INVISIBLE);
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);//뒤로가기키 막기
        dialog.setCanceledOnTouchOutside(false);//배경 터치 막기
        dialog.show();
        Log.i(TAG,"다이얼로그끝");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            barcodeScannerView.pause();
            if(resultCode == RESULT_OK){
                photoView.setVisibility(View.VISIBLE);
                try {
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);

                    photoView.setImageBitmap(img);
                    in.close();
                    Result decoded = scanQRImage(img);
                    Log.i("QrTest", "Decoded string="+decoded);

                    if(decoded == null)
                        showError();
                    else if(decoded.getBarcodeFormat() == BarcodeFormat.QR_CODE)
                        showQRcodeDialog(decoded.getText());
                    else
                        showBarcodeDialog(decoded.getText());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
    public static Result scanQRImage(Bitmap bMap) {
        String contents = null;

        int[] intArray = new int[bMap.getWidth()*bMap.getHeight()];
        //copy pixel data from the Bitmap into the 'intArray' array
        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());

        LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        MultiFormatReader reader = new MultiFormatReader();
        Result result;

        Hashtable<DecodeHintType, Object> decodeHints = new Hashtable<DecodeHintType, Object>();
        decodeHints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        decodeHints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
        try {
            Log.i(TAG,"try");
            result = reader.decode(bitmap,decodeHints);
            //contents = result.getText();
        }
        catch (Exception e) {
            Log.i(TAG,"catch");
            //Log.e("QrTest", "Error decoding barcode", e);
            return null;
        }
        return result;
    }
    public void newResult(){
        DBHandler dbHandler = new DBHandler(this, null, null, 1);
        com.example.gpsk1.qrcode.Result product = new com.example.gpsk1.qrcode.Result(ptype, presult);
        dbHandler.addResult(product);
        //updateList();
    }
    /*public void deleteResult(){
        DBHandler dbHandler = new DBHandler(this, null, null, 1);
        boolean result = dbHandler.deleteResult(pname.getText().toString());
        if(result){
            Toast.makeText(this, "Record Deleted", Toast.LENGTH_SHORT).show();
            updateList();
        }else{
            Toast.makeText(this, "No Match Found", Toast.LENGTH_SHORT).show();
        }
    }*/
}
