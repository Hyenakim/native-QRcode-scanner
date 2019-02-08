package com.example.gpsk1.qrcode;

import android.Manifest;
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
import android.os.Environment;
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
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.soundcloud.android.crop.Crop;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

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
    private String ptime;
    private Uri imgUri;
    private String mCurrentPhotoPath;
    private File tempFile;
    private static final int PICK_FROM_ALBUM = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"1");
        setContentView(R.layout.activity_custom_scanner);
        init();
        tedPermission();

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
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String time = sdf.format(date);
                ptime = time;
                newResult();
                //결과 다이얼로그로 띄우기
                if(result.getBarcodeFormat()== BarcodeFormat.QR_CODE)
                    showQRcodeDialog(result.toString());
                else
                    showBarcodeDialog(result.toString());
            }
            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {
                Log.i(TAG,resultPoints.toString());

            }
        });
    }
    /*
    * 권한 확인 (마시멜로우 필요)
    * */
    private void tedPermission() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // 권한 요청 실패
            }
        };
        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }
    public void init(){
        ptype = null;
        presult = null;
        ptime = null;
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

    /*
    * 메인 화면 이미지버튼 onClick
    * 1. 손전등 버튼
    * 2. 기록 버튼
    * 3. 앨범 버튼
    * */
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
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent,PICK_FROM_ALBUM);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            if(tempFile != null) {
                if (tempFile.exists()) {
                    if (tempFile.delete()) {
                        Log.e(TAG, tempFile.getAbsolutePath() + " 삭제 성공");
                        tempFile = null;
                    }
                }
            }
            return;
        }
        switch (requestCode){
            case PICK_FROM_ALBUM:{
                //이미지 선택 후
                imgUri = data.getData();
                cropImage(imgUri);
                break;
            }
            case Crop.REQUEST_CROP:{
                //이미지 크롭 후
                barcodeScannerView.pause();
                if(resultCode == RESULT_OK){
                    //크롭된 이미지 띄우기
                    photoView.setVisibility(View.VISIBLE);
                    String tmpPath = tempFile.getPath();
                    Bitmap img = BitmapFactory.decodeFile(tmpPath);
                    photoView.setImageURI(Crop.getOutput(data));
                    //스캔 시작
                    Result decoded = scanQRImage(img);
                    Log.i("QrTest", "Decoded string="+decoded);
                    //결과 다이얼로그 띠우기
                    if(decoded != null){
                        //db저장
                        ptype = decoded.getBarcodeFormat().toString();
                        presult = decoded.toString();
                        long now = System.currentTimeMillis();
                        Date date = new Date(now);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        String time = sdf.format(date);
                        ptime = time;
                        newResult();
                    }
                    if(decoded == null)
                        showError();
                    else if(decoded.getBarcodeFormat() == BarcodeFormat.QR_CODE)
                        showQRcodeDialog(decoded.getText());
                    else
                        showBarcodeDialog(decoded.getText());
                    //임시 파일 삭제
                    deleteFile(tempFile);
                }
            }
        }
    }
    /*
    * 이미지 크롭 함수
    * */
    private void cropImage(Uri photoUri) {
        Log.d(TAG, "tempFile : " + tempFile);
        /**
         *  갤러리에서 선택한 경우에는 tempFile 이 없으므로 새로 생성해줍니다.
         */
        if(tempFile == null) {
            tempFile = createImageFile();
        }
        //크롭 후 저장할 Uri
        Uri savingUri = Uri.fromFile(tempFile);

        Crop.of(photoUri, savingUri).asSquare().start(this);
    }
    /*
    * 파일 생성 및 파일 반환
    * */
    private File createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  // prefix
                    ".jpg",         // suffix
                    storageDir      // directory

            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Save a file: path for use with ACTION_VIEW intents
        if (image != null) {
            mCurrentPhotoPath = "file:" + image.getAbsolutePath();
            Log.i("Ihdh", "Image" + mCurrentPhotoPath);
        }
        return image;
    }
    /*
    * 파일 삭제
    * */
    public static boolean deleteFile(File file){
        if(file!=null && file.isDirectory()){
            String[] children = file.list();
            for(String aChilderen : children){
                boolean success = deleteFile(new File(file,aChilderen));
                if(!success)
                    return false;
            }
        }
        return file!=null && file.delete();
    }
    /*
    * 이미지 스캔 및 스캔 결과 반환
    * */
    public static Result scanQRImage(Bitmap bMap) {
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
        }
        catch (Exception e) {
            Log.i(TAG,"catch");
            return null;
        }
        return result;
    }
    /*
    * 스캔 후 db저장
    * */
    public void newResult(){
        DBHandler dbHandler = new DBHandler(this, null, null, 2);

        com.example.gpsk1.qrcode.Result product = new com.example.gpsk1.qrcode.Result(ptype, presult, ptime);
        dbHandler.addResult(product);
    }
    /*
    * 다이얼로그
    * qrcode스캔 시 호출됨
    * 예 - 크롬창 이동
    * 아니오 - 뒤로가기
    * */
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
    /*
     * 다이얼로그
     * barcode스캔시 호출됨
     * 확인 - 뒤로가기
     * */
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
    /*
    * 다이얼로그
    * 스캔 실패시 호출됨
    * 돌아가기 - 뒤로가기
    * */
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
}
