package com.example.gpsk1.qrcode.model;

import android.database.Cursor;

public class Result {
    private int _id;        //결과 고유ID
    private String _type;   //스캔한 결과 타입
    private String _result; //스캔한 결과
    private String _time;   //스캔한 시간

    public Result(){

    }
    public Result(int id,String type, String result, String time){
        this._id = id;
        this._type = type;
        this._result = result;
        this._time = time;
    }
    public Result(int id,String type, String result){
        this._id = id;
        this._type = type;
        this._result = result;
    }
    public Result(String type, String result, String time){
        this._type = type;
        this._result = result;
        this._time = time;
    }
    public Result(String type, String result){
        this._type = type;
        this._result = result;
    }
    public void setID(int id){
        this._id = id;
    }
    public void setType(String type){
        this._type = type;
    }
    public void setResult(String result){
        this._result = result;
    }
    public void setTime(String time){
        this._time = time;
    }

    public int getID(){
        return _id;
    }
    public String getType(){
        return this._type;
    }
    public String getResult(){
        return this._result;
    }
    public String getTime(){
        return this._time;
    }

    public static Result bindCursor(Cursor cursor){
        Result result = new Result();
        result._id = cursor.getInt(cursor.getColumnIndex("_id"));
        result._time = cursor.getString(cursor.getColumnIndex("_time"));
        result._type = cursor.getString(cursor.getColumnIndex("_type"));
        result._result = cursor.getString(cursor.getColumnIndex("_result"));
        return result;
    }

}
