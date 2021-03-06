package com.example.gpsk1.qrcode.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.gpsk1.qrcode.model.Result;

public class DBHandler extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "resultDB.db";   //db이름
    public static final String DATABASE_TABLE = "result3";      //table이름
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TYPE = "_type";
    public static final String COLUMN_RESULT = "_result";
    public static final String COLUMN_TIME = "_time";

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "create table if not exists "+DATABASE_TABLE+"("+ COLUMN_ID +
                " integer primary key autoincrement," +COLUMN_TYPE + " text,"+COLUMN_RESULT+" text,"+COLUMN_TIME+" text)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists "+DATABASE_TABLE);
        onCreate(db);
    }
    /**
     * 결과를 db 에 저장합니다.
     *
     * @param result 스캔 결과
     * @return void
     */
    public void addResult(Result result){
        ContentValues values=new ContentValues();

        values.put(COLUMN_TIME,result.getTime());
        values.put(COLUMN_TYPE, result.getType());
        values.put(COLUMN_RESULT, result.getResult());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(DATABASE_TABLE, null, values);
        db.close();
    }
    /**
     * 결과를 db 에서 삭제합니다.
     *
     * @param result 스캔 결과
     * @param time 스캔 시간정보
     * @return 삭제 성공-true 실패-false
     */
    public boolean deleteResult(String result,String time){
        boolean resultFlag = false;
        String query="select * from "+DATABASE_TABLE +
                " where "+COLUMN_RESULT+"= \'"+result+"\'"+" and "+COLUMN_TIME+"= \'"+time+"\'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Result result1 = new Result();
        if(cursor.moveToFirst()){
            result1.setID(Integer.parseInt(cursor.getString(0)));
            db.delete(DATABASE_TABLE, COLUMN_ID + "=?",
                    new String[]{String.valueOf(result1.getID())});
            cursor.close();
            db.close();
            return true;
        }
        db.close();
        return resultFlag;
    }
    /**
     * 모든 db 내용을 가져옵니다.
     *
     * @return cursor db 전체
     */
    public Cursor findAll(){
        String query="select * from "+DATABASE_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery(query, null);
    }
}
