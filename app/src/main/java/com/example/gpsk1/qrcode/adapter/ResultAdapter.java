package com.example.gpsk1.qrcode.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gpsk1.qrcode.R;
import com.example.gpsk1.qrcode.model.Result;

public class ResultAdapter extends CursorRecyclerViewAdapter<RecyclerView.ViewHolder>{

    //클릭시 실행 함수
    private ItemClick itemClick;
    public interface ItemClick{
        public void onClick(View view,int position);
    }
    public void setItemClick(ItemClick itemClick){
        this.itemClick = itemClick;
    }
    public ResultAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final Cursor cursor) {
        Result result = Result.bindCursor(cursor);
        ((ResultViewHolder)viewHolder).setResult(result,cursor.getPosition());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(itemClick!=null){
                    itemClick.onClick(v,cursor.getPosition());
                }
            }
        });

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row,parent,false);
        return new ResultViewHolder(v);
    }

}
