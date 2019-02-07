package com.example.gpsk1.qrcode;

public class Result {
    private int _id;
    private String _type;
    private String _result;
    private String _time;

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

}
