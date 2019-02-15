package com.example.gpsk1.qrcode;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gpsk1.qrcode.adapter.CursorAdapter;
import com.example.gpsk1.qrcode.adapter.ResultAdapter;
import com.example.gpsk1.qrcode.adapter.ResultViewHolder;
import com.example.gpsk1.qrcode.model.Result;
import com.example.gpsk1.qrcode.util.DBHandler;

import java.util.List;

public class ResultActivity extends AppCompatActivity {
    private CursorAdapter cursorAdapter;
    private ListView listView;
    private RecyclerView recyclerview;
    private ResultAdapter rAdapter;
    private LinearLayoutManager layoutManager;
    private ImageButton back;
    private static final String TAG = "ResultActivity";
    private AlertDialog.Builder builder ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        listView = (ListView) findViewById(R.id.listview);
        recyclerview = (RecyclerView)findViewById(R.id.recyclerview);
        back = (ImageButton) findViewById(R.id.back);
        updateList();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView typ = (TextView)view.findViewById(R.id.listtype);
                TextView res = (TextView)view.findViewById(R.id.listresult);
                TextView tim = (TextView)view.findViewById(R.id.listtime);
                Log.i(TAG,String.valueOf(res.getText()));
                Log.i(TAG,String.valueOf(typ.getText()));
                if(String.valueOf(typ.getText()).equals(R.string.format_qrcode))
                    showQRcodeDialog(String.valueOf(res.getText()),String.valueOf(tim.getText()));
                else
                    showBarcodeDialog(String.valueOf(res.getText()),String.valueOf(tim.getText()));
            }
        });
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerview.setLayoutManager(layoutManager);
        rAdapter.setItemClick(new ResultAdapter.ItemClick() {
            @Override
            public void onClick(View view, int position) {
                TextView typ = (TextView)view.findViewById(R.id.listtype);
                TextView res = (TextView)view.findViewById(R.id.listresult);
                TextView tim = (TextView)view.findViewById(R.id.listtime);
                if(String.valueOf(typ.getText()).equals(getResources().getString(R.string.format_qrcode)))
                    showQRcodeDialog(String.valueOf(res.getText()),String.valueOf(tim.getText()));
                else
                    showBarcodeDialog(String.valueOf(res.getText()),String.valueOf(tim.getText()));
            }
        });

    }

    /**
     * 리스트를 새로고침합니다.
     *
     * @return void
     */
    public void updateList(){
        DBHandler dbHandler = new DBHandler(this, null, null, 2);
        Cursor cursor = dbHandler.findAll();
        if(rAdapter==null)
        {
            //cursorAdapter = new CursorAdapter(this, cursor);
            //listView.setAdapter(cursorAdapter);
            rAdapter = new ResultAdapter(this,cursor);
            recyclerview.setAdapter(rAdapter);
        }
        //cursorAdapter.changeCursor(cursor);
        rAdapter.changeCursor(cursor);
    }
    /**
     * 선택 항목을 리스트에서 삭제합니다.
     *
     * @return void
     */
    public void deleteList(String url,String time){
        DBHandler dbHandler = new DBHandler(this, null, null, 2);
        dbHandler.deleteResult(url,time);
    }
    public void goBack(View view){
        super.onBackPressed();
    }
    /**
     * 다이얼로그를 띄웁니다.
     *
     * @param url 선택 항목 qr코드 결과
     * @param time 선택 항목 시간정보
     * @return void
     */
    public void showQRcodeDialog(final String url,final String time){
        builder = new AlertDialog.Builder(this);
        builder.setMessage(url+getResources().getString(R.string.move));
        builder.setPositiveButton(R.string.yes,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        intent.setPackage(getResources().getString(R.string.goCrome));
                        startActivity(intent);
                    }
                });
        builder.setNegativeButton(R.string.no,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateList();
                        dialog.dismiss();
                    }
                });
        builder.setNeutralButton(R.string.delete,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteList(url,time);
                        updateList();
                        dialog.dismiss();
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);//뒤로가기키 막기
        dialog.setCanceledOnTouchOutside(false);//배경 터치 막기
        dialog.show();
    }
    /**
     * 다이얼로그를 띄웁니다.
     *
     * @param num 선택 항목 바코드 결과
     * @param time 선택 항목 시간정보
     * @return void
     */
    public void showBarcodeDialog(final String num,final String time){
        builder = new AlertDialog.Builder(this);
        builder.setMessage(num+'\n'+getResources().getString(R.string.result_barcode));
        builder.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.setNeutralButton(R.string.delete,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteList(num,time);
                        updateList();
                        dialog.dismiss();
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);//뒤로가기키 막기
        dialog.setCanceledOnTouchOutside(false);//배경 터치 막기
        dialog.show();
    }

}
