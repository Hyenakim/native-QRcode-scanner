package com.example.gpsk1.qrcode.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.gpsk1.qrcode.R;
import com.example.gpsk1.qrcode.model.Result;

public class ResultViewHolder extends RecyclerView.ViewHolder {

    private Result result;
    private TextView ptime;
    private TextView ptype;
    private TextView presult;
    private int pPosition;


    public ResultViewHolder(View view) {
        super(view);
        ptime = (TextView)view.findViewById(R.id.listtime);
        ptype = (TextView)view.findViewById(R.id.listtype);
        presult = (TextView)view.findViewById(R.id.listresult);

    }

    public void setResult(Result result, int position){
        this.result = result;
        pPosition = position;
        ptime.setText(result.getTime());
        ptype.setText("포맷 : "+result.getType());
        presult.setText(result.getResult());
    }
}
