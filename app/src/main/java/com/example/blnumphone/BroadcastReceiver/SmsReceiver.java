package com.example.blnumphone.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;


import com.example.blnumphone.Bean.MessgeInfo;
import com.example.blnumphone.activity.MessgeActivity;
import com.example.blnumphone.dbdao.BlnumDao;
import com.example.blnumphone.dbdao.TelInfodb;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SmsReceiver extends BroadcastReceiver {


    private SQLiteDatabase sqLiteDatabase;
    private ArrayList<String> list = new ArrayList<>();
    public String code;
    String address,msg;

    TelInfodb telInfodb;
    BlnumDao blnumDao;



    @Override
    public void onReceive(Context context, Intent intent) {
        telInfodb = new TelInfodb(context);
        blnumDao = new BlnumDao(context);

        Object[] pdus = (Object[]) intent.getExtras().get("pdus");

        for (Object pdu : pdus) {
            SmsMessage message = SmsMessage.createFromPdu((byte[]) pdu);
            address = message.getOriginatingAddress();
            msg = message.getMessageBody();
        }

        searchblacknum();
        if (list != null&&list.contains(address)) {// 在黑名单中
           searchnumcode(address);
            if (code.contains("2")||code.contains("3")) {// 2, 3才拦截

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
                Date date = new Date(System.currentTimeMillis());//获取当前时间
                simpleDateFormat.format(date);
                blnumDao.insertTelMes(address,msg,simpleDateFormat.format(date));
                Toast.makeText(context,"成功拦截："+address+"  "+msg,Toast.LENGTH_LONG).show();
                MessgeInfo msgInfo = new MessgeInfo();
                msgInfo.setMessge(msg);
                msgInfo.setPhone(address);
                msgInfo.setTime(simpleDateFormat.format(date));
                MessgeActivity.refresh(msgInfo);
                abortBroadcast();
            }
        }
    }

    private void searchblacknum(){
        sqLiteDatabase = telInfodb.getReadableDatabase();
        String array[]={"telnum"};
        Cursor cursor=sqLiteDatabase.query("telnuminfo",array,null,null,null,null,null);
        int blacknumindex=cursor.getColumnIndex("telnum");
        Log.i(">>>",blacknumindex+"");
        cursor.moveToFirst();
        list=new ArrayList<>();
        while (!cursor.isAfterLast())
        {
            String blacknum=cursor.getString(blacknumindex);
            list.add(blacknum);
            cursor.moveToNext();
            Log.i(">>>",">>>>");
            Log.i("Simon",list.toString());
        }
    }

    public void searchnumcode(String incomingNumber){
        sqLiteDatabase = telInfodb.getReadableDatabase();
        Cursor codecursor = sqLiteDatabase.rawQuery("select code from telnuminfo where telnum=?",new String[]{incomingNumber});
        codecursor.moveToNext();
        code = codecursor.getString(codecursor.getColumnIndex("code"));
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+code);
        codecursor.close();
        sqLiteDatabase.close();
    }
}


