package com.example.blnumphone.activity;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.blnumphone.Bean.MessgeInfo;
import com.example.blnumphone.MyDialog.MyDialog;
import com.example.blnumphone.R;
import com.example.blnumphone.dbdao.BlnumDao;
import com.example.blnumphone.dbdao.TelInfodb;
import com.example.blnumphone.myAdapter.MessgeAdapter;

import java.util.ArrayList;


public class MessgeActivity extends AppCompatActivity implements View.OnClickListener{


    private Button rb_phone;
    private Button rb_xinxi;

    private Button btn_blnum;
    private Button btn_messges;
    private Button btn_add;
    private Button btn_record;
    private Button btn_setting;
    private Button messge_del;

    private ListView lv;
    private static ArrayList<MessgeInfo> list;
    private static MessgeAdapter messgeAdapter;


    MyDialog myDialog;
    TelInfodb telInfodb;
    BlnumDao blnumDao;
    MessgeInfo messgeInfo;
    Cursor cursor;
    SQLiteDatabase sqLiteDatabase;
    SharedPreferences prefere;

    public static void refresh(MessgeInfo messageInfo){
        list.add(messageInfo);
        messgeAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messge);
        myDialog = new MyDialog(this);
        telInfodb = new TelInfodb(this);
        blnumDao = new BlnumDao(this);

        list = new ArrayList<MessgeInfo>();
        messgeAdapter = new MessgeAdapter(getApplicationContext(),list);
        lv = findViewById(R.id.messge_lv);
        Messgesinit();
        SettingMes();
        showblacknum();



    }


    public void Messgesinit(){
        rb_phone = (Button)findViewById(R.id.rb_phone);                 //电话拦截记录
        rb_xinxi = (Button)findViewById(R.id.rb_xinxi);                 //信息拦截记录
        btn_blnum = (Button)findViewById(R.id.btn_blnum);               //黑名单
        btn_messges = (Button)findViewById(R.id.btn_messges);           //拦截记录
        btn_add = (Button)findViewById(R.id.btn_add);                   //添加黑名单
        btn_record = (Button)findViewById(R.id.btn_record);             //录音记录
        btn_setting = (Button)findViewById(R.id.btn_setting);           //设置
        messge_del = (Button)findViewById(R.id.messge_del);             //清空


        rb_phone.setOnClickListener(this);
        rb_xinxi.setOnClickListener(this);
        btn_blnum.setOnClickListener(this);
        btn_messges.setOnClickListener(this);
        btn_add.setOnClickListener(this);
        btn_record.setOnClickListener(this);
        btn_setting.setOnClickListener(this);
        messge_del.setOnClickListener(this);
    }

    private void SettingMes(){
        prefere = getApplicationContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        SettingActivity.bsSwitch = prefere.getBoolean("bsSwitch",SettingActivity.bsSwitch);
        SettingActivity.bsSwitch2 = prefere.getBoolean("bsSwitch2",SettingActivity.bsSwitch2);
        SettingActivity.bsSwitch3 = prefere.getBoolean("bsSwitch3",SettingActivity.bsSwitch3);
        SettingActivity.str_et_selfsetting = prefere.getString("str_et_selfsetting",SettingActivity.str_et_selfsetting);
    }


    private void showblacknum(){
        sqLiteDatabase = telInfodb.getReadableDatabase();
        cursor = sqLiteDatabase.query("interceptmessge",null,null,null,null,null,null);
        if (cursor.getCount() == 0){
            Toast.makeText(getApplicationContext(),"没有数据！",Toast.LENGTH_LONG).show();
        }else {
            cursor.moveToFirst();
        }
        while (!cursor.isAfterLast())
        {
            messgeInfo = new MessgeInfo();
            messgeInfo.setPhone(cursor.getString(cursor.getColumnIndex("iptmessge")));
            messgeInfo.setMessge(cursor.getString(cursor.getColumnIndex("messges")));
            messgeInfo.setTime(cursor.getString(cursor.getColumnIndex("iptmestime")));

            list.add(messgeInfo);
            cursor.moveToNext();
            Log.i(">>>",">>>>");
            Log.i("Simon",list.toString());

        }
        if (list != null){
            lv.setAdapter(messgeAdapter);
            messgeAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btn_messges:
                break;
            case R.id.btn_blnum:
                Intent blnum = new Intent(this,BlnumActivity.class);         //黑名单页
                startActivity(blnum);
                break;
            case R.id.rb_phone:
                Intent phone = new Intent(this,MainActivity.class);         //黑名单页
                startActivity(phone);
                break;
            case R.id.btn_add:
                myDialog.addBlackNumDialog();
                break;
            case R.id.btn_record:
                Intent record = new Intent(this,RecordActivity.class);         //录音记录页
                startActivity(record);
                break;
            case R.id.btn_setting:
                Intent setting = new Intent(this,SettingActivity.class);         //设置页
                startActivity(setting);
                break;
            case R.id.messge_del:
                if (list.size()==0){
                    Toast.makeText(MessgeActivity.this,"信息拦截记录很干净喔！",Toast.LENGTH_LONG).show();
                }else {
                    new AlertDialog.Builder(MessgeActivity.this)
                            .setIcon(R.mipmap.ic_launcher)
                            .setTitle("提示")
                            .setMessage("确定要清空信息拦截记录吗")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    blnumDao.Messgedeleteall();
                                    list.clear();

                                    messgeAdapter.notifyDataSetChanged();
                                    Toast.makeText(MessgeActivity.this, "信息拦截记录已清空！", Toast.LENGTH_LONG).show();
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                }
                break;
        }
    }

}
