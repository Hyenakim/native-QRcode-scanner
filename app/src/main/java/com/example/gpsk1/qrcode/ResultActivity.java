package com.example.gpsk1.qrcode;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.zxing.BarcodeFormat;

import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;

public class ResultActivity extends AppCompatActivity {
    private CursorAdapter cursorAdapter;
    private ListView listView;
    private ImageButton back;
    private static final String TAG = "ResultActivity";
    private AlertDialog.Builder builder ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        listView = (ListView) findViewById(R.id.listview);
        back = (ImageButton) findViewById(R.id.back);
        updateList();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView typ = (TextView)view.findViewById(R.id.listtype);
                TextView res = (TextView)view.findViewById(R.id.listresult);
                Log.i(TAG,String.valueOf(res.getText()));
                Log.i(TAG,String.valueOf(typ.getText()));
                String tmp = "포맷 : QR_CODE";
                if(String.valueOf(typ.getText()).equals(tmp))
                    showQRcodeDialog(String.valueOf(res.getText()));
                else
                    showBarcodeDialog(String.valueOf(res.getText()));
            }
        });
    }
    /*
    * 리스트 새로고침
    * */
    public void updateList(){
        DBHandler dbHandler = new DBHandler(this, null, null, 2);
        Cursor cursor = dbHandler.findAll();
        if(cursorAdapter==null)
        {
            cursorAdapter = new CursorAdapter(this, cursor);
            listView.setAdapter(cursorAdapter);
        }
        cursorAdapter.changeCursor(cursor);
    }
    /*
    * 리스트 삭제
    * */
    public void deleteList(String url){
        DBHandler dbHandler = new DBHandler(this, null, null, 2);
        dbHandler.deleteResult(url);
    }
    public void goBack(View view){
        super.onBackPressed();
    }
    /*
    * 다이얼로그
    * qrcode 선택시 호출
    * 예 - url이동
    * 아니오 - 돌아가기
    * 기록삭제 - 선택 항목 삭제
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
                        updateList();
                        dialog.dismiss();
                    }
                });
        builder.setNeutralButton("기록삭제",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteList(url);
                        updateList();
                        dialog.dismiss();
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
    * 바코드 선택 시 호출
    * 확인 - 돌아가기
    * 기록삭제 - 선택 항목 삭제
    * */
    public void showBarcodeDialog(final String num){
        Log.i(TAG,"다이얼로그시작");
        builder = new AlertDialog.Builder(this);
        builder.setMessage(num+'\n'+"바코드 결과입니다.");
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.setNeutralButton("기록삭제",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteList(num);
                        updateList();
                        dialog.dismiss();
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);//뒤로가기키 막기
        dialog.setCanceledOnTouchOutside(false);//배경 터치 막기
        dialog.show();
        Log.i(TAG,"다이얼로그끝");
    }

}
