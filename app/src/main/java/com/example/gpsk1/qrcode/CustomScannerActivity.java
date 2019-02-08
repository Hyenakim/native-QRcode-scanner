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
import android.graphics.Matrix;
import android.media.ExifInterface;
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
    private Uri imgUri;
    private String mCurrentPhotoPath;
    private File tempFile;
    private static final int PICK_FROM_ALBUM = 1;
    private String presult; //스캔 결과 값 ex)http://www.naver.com
    private String ptype;   //스캔 결과 타입 ex)QR_CODE
    private String ptime;   //스캔 결과 시간정보 ex)2019-02-08 01:34:24

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_scanner);
        init();
        tedPermission();

        switchFlashLightButtonCheck = true;
        if(!hasFlash()){
            switchFlashLightButton.setVisibility(View.GONE);
        }
        barcodeScannerView.setTorchListener(this);
        barcodeScannerView.setFocusable(false);
        capture.initializeFromIntent(getIntent(),savedInstanceState);
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
    public void init(){
        ptype = null;
        presult = null;
        ptime = null;
        switchFlashLightButton = (ImageButton)findViewById(R.id.switch_flashlight);
        photoView = (ImageView)findViewById(R.id.showView);
        barcodeScannerView = (DecoratedBarcodeView)findViewById(R.id.zxing_barcode_scanner);

        backPressCloseHandler = new BackPressCloseHandler(this);
        capture = new CaptureManager(this, barcodeScannerView);
    }
    /**
     * 권한을 확인합니다.
     * @return void
     */
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

    /**
     * 메인버튼
     *
     * 1. 손전등 버튼
     * 2. 히스토리 버튼
     * 3. 앨범 버튼
     */
    //손전등버튼 onClick
    public void switchFlashLight(View view){
        if(switchFlashLightButtonCheck){
            barcodeScannerView.setTorchOn();
        }else{
            barcodeScannerView.setTorchOff();
        }
    }
    //앨범버튼 onClick
    public void addPhoto(View view){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent,PICK_FROM_ALBUM);
    }
    //기록버튼 onClick
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
                deleteFile(tempFile);
                cropImage(imgUri);
                break;
            }
            case Crop.REQUEST_CROP:{
                //이미지 크롭 후
                barcodeScannerView.pause();
                if(resultCode == RESULT_OK){
                    //크롭된 이미지 띄우기
                    String tmpPath = tempFile.getPath();
                    Bitmap img = BitmapFactory.decodeFile(tmpPath);
                    try {
                        //크롭 후 이미지 자동회전 저장 방지하기 위해
                        ExifInterface exif = new ExifInterface(tmpPath);
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                        int exifDegree = exifOrientationToDegrees(orientation);
                        img = rotate(img, exifDegree);
                    }catch (IOException e) {
                        Log.i(TAG, e.getMessage());
                    }
                    photoView.setImageBitmap(img);
                    photoView.setVisibility(View.VISIBLE);
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
                }
            }
        }
    }
    /**
     * EXIF정보를 회전각도로 변환하는 메서드
     *
     * @param exifOrientation EXIF 회전각
     * @return 실제 각도
     */
    public int exifOrientationToDegrees(int exifOrientation)
    {
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90)
        {
            return 90;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180)
        {
            return 180;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270)
        {
            return 270;
        }
        return 0;
    }
    /**
     * 이미지를 회전시킵니다.
     *
     * @param bitmap 비트맵 이미지
     * @param degrees 회전 각도
     * @return 회전된 이미지
     */
    public Bitmap rotate(Bitmap bitmap, int degrees)
    {
        if(degrees != 0 && bitmap != null)
        {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);

            try
            {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if(bitmap != converted)
                {
                    bitmap.recycle();
                    bitmap = converted;
                }
            }
            catch(OutOfMemoryError ex)
            {
                // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
            }
        }
        return bitmap;
    }
    /**
     * 이미지를 자릅니다.
     *
     * @param photoUri 선택한 이미지
     * @return void
     */
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
    /**
     * 파일을 생성합니다.
     *
     * @return 생성된 파일
     */
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
    /**
     * 파일을 제거합니다.
     *
     * @param file 선택된 파일
     * @return 제거 실패-false, 성공-true
     */
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
    /**
     * 이미지를 스캔합니다.
     *
     * @param bMap 비트맵 이미지
     * @return 스캔 결과 (url, 바코드type 등등)
     */
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
    /**
     * 결과를 db에 저장합니다.
     *
     * @return void
     */
    public void newResult(){
        DBHandler dbHandler = new DBHandler(this, null, null, 2);

        com.example.gpsk1.qrcode.Result product = new com.example.gpsk1.qrcode.Result(ptype, presult, ptime);
        dbHandler.addResult(product);
    }
    /**
     * 다이얼로그를 띄웁니다.
     *
     * @param url qr코드 스캔 결과
     * @return void
     */
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
    /**
     * 다이얼로그를 띄웁니다.
     *
     * @param num 바코드 스캔 결과
     * @return void
     */
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
    /**
     * 다이얼로그를 띄웁니다.
     *
     * @return void
     */
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
