package com.example.gpsk1.qrcode;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CursorAdapter extends android.widget.CursorAdapter{

    public CursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.row, parent, false);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final TextView ptime = (TextView)view.findViewById(R.id.listtime);
        final TextView ptype = (TextView)view.findViewById(R.id.listtype);
        final TextView presult = (TextView)view.findViewById(R.id.listresult);
        ptime.setText(cursor.getString(cursor.getColumnIndex("_time")));
        ptype.setText("포맷 : "+cursor.getString(cursor.getColumnIndex("_type")));
        presult.setText(cursor.getString(cursor.getColumnIndex("_result")));
    }
}
