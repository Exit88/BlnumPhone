package com.example.blnumphone.myAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.blnumphone.Bean.MessgeInfo;
import com.example.blnumphone.R;


import java.util.ArrayList;

public class MessgeAdapter extends BaseAdapter {
    ArrayList<MessgeInfo> list;
    Context context;
    TextView tv_phone;
    TextView tv_time;
    TextView tv_mesges;
    public MessgeAdapter(Context context,ArrayList<MessgeInfo> list){
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.messge_item_layout,null);
            tv_phone = (TextView)view.findViewById(R.id.mes_phone);
            tv_time = (TextView)view.findViewById(R.id.mes_time);
            tv_mesges = (TextView)view.findViewById(R.id.mes_messge);
        }
        tv_phone.setText(list.get(i).getPhone());
        System.out.println("rrrrrrrrrrrrrrrrrrrrrrr"+list.get(i).getPhone());
        tv_time.setText(list.get(i).getTime());
        tv_mesges.setText(list.get(i).getMessge());

        return view;
    }
}
