package com.example.gpsk1.qrcode;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "resultDB.db";//db이름
    public static final String DATABASE_TABLE = "results";//table이름
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TYPE = "_type";
    public static final String COLUMN_RESULT = "_result";

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "create table if not exists "+DATABASE_TABLE+"("+ COLUMN_ID +
                " integer primary key autoincrement," +COLUMN_TYPE + " text,"+COLUMN_RESULT+" text)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists "+DATABASE_TABLE);
        onCreate(db);
    }
    public void addResult(Result result){
        ContentValues values=new ContentValues();
        values.put(COLUMN_TYPE, result.getType());
        values.put(COLUMN_RESULT, result.getResult());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(DATABASE_TABLE, null, values);
        db.close();
    }
    public boolean deleteResult(String result){
        boolean resultFlag = false;
        String query="select * from "+DATABASE_TABLE +
                " where "+COLUMN_RESULT+"= \'"+result+"\'";
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
    public Result findResult(String result){
        String query="select * from "+DATABASE_TABLE + " where "+
                COLUMN_RESULT+"= \'"+result+"\'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Result result1 = new Result();
        if(cursor.moveToFirst()){
            result1.setID(Integer.parseInt(cursor.getString(0)));
            result1.setResult(cursor.getString(2));
            result1.setType(cursor.getString(1));
            cursor.close();
        } else {
            result1 = null;
        }
        db.close();
        return result1;
    }
    public boolean updateProduct(Result argresult) {
        Result result = findResult(argresult.getResult());
        if (result != null) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_RESULT, result.getResult());
            values.put(COLUMN_TYPE, argresult.getType());
            db.update(DATABASE_TABLE, values, COLUMN_ID + "= \'" +
                            result.getID() + "\'"
                    , null);
            db.close();
            return true;
        }
        return false;
    }
    public Cursor findAll(){
        String query="select * from "+DATABASE_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery(query, null);
    }
}
