package com.example.blnumphone.dbdao;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.blnumphone.MyDialog.MyDialog;
import com.example.blnumphone.R;
import com.example.blnumphone.activity.MainActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public final class BlnumDao  {

    private EditText editText;                      //黑名单号码输入框
    static ArrayList<String> listcode;
    Date date;
    SimpleDateFormat simpleDateFormat;

    TelInfodb telNumInfo;
    SQLiteDatabase sqLiteDatabase;
    ContentValues values;
    String addtime;

    public BlnumDao(Context context){
        telNumInfo = new TelInfodb(context);

        //获取当前时间
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");// HH:mm:ss
        date = new Date(System.currentTimeMillis());
    }

    /*
    向数据库telnuminfo插入拦截号码
    */
    public void insertTelNum(String telnum,String code){
        addtime = simpleDateFormat.format(date);
        sqLiteDatabase = telNumInfo.getWritableDatabase();
        values = new ContentValues();
        values.put("telnum",telnum);
        values.put("code",code);
        values.put("addtime",addtime);
        sqLiteDatabase.insert("telnuminfo",null,values);
        System.out.print(">>>>>>>>>>>>>>>>>>>保存成功！");
        sqLiteDatabase.close();
    }

    /*
    向数据库telnuminfo查询code
     */
    public void searchCode(){
        sqLiteDatabase = telNumInfo.getReadableDatabase();
        String array[]={"code"};
        Cursor cursor=sqLiteDatabase.query("telnuminfo",array,null,null,null,null,null);
        int blacknumcode=cursor.getColumnIndex("code");
        Log.i(">>>",blacknumcode+"");
        cursor.moveToFirst();
        listcode=new ArrayList<>();
        while (!cursor.isAfterLast())
        {
            String numcode=cursor.getString(blacknumcode);
            listcode.add(numcode);
            cursor.moveToNext();
            Log.i(">>>",">>>>");
            Log.i("Simon",listcode.toString());
        }
    }

    /*
    清空数据库telnuminfo
     */
    public void deleteall(){
        sqLiteDatabase = telNumInfo.getWritableDatabase();
        sqLiteDatabase.delete("telnuminfo",null,null);
        sqLiteDatabase.close();
    }

    /*
    响数据库intercepttelnum插入被拦截号码
     */
    public void insertIptTelNum(String ipttelnum){
        addtime = simpleDateFormat.format(date);
        sqLiteDatabase = telNumInfo.getWritableDatabase();
        values = new ContentValues();
        values.put("ipttelnum",ipttelnum);
        values.put("ipttime",addtime);
        try {
            sqLiteDatabase.insert("intercepttelnum",null,values);
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.print(">>>>>>>>>>>>>>>>>>>保存成功！");
        sqLiteDatabase.close();
    }

    /*
   清空数据库intercepttelnum
    */
    public void maindeleteall(){
        sqLiteDatabase = telNumInfo.getWritableDatabase();
        sqLiteDatabase.delete("intercepttelnum",null,null);
        sqLiteDatabase.close();
    }


    /*
   向数据库interceptmessge插入拦截号码,信息
   */
    public void insertTelMes(String telmesges,String messge,String time){
        sqLiteDatabase = telNumInfo.getWritableDatabase();
        values = new ContentValues();
        values.put("iptmessge",telmesges);
        values.put("messges",messge);
        values.put("iptmestime",time);
        sqLiteDatabase.insert("interceptmessge",null,values);
        System.out.print(">>>>>>>>>>>>>>>>>>>保存成功！");
        sqLiteDatabase.close();
    }

    /*
  清空数据库interceptmessge
   */
    public void Messgedeleteall(){
        sqLiteDatabase = telNumInfo.getWritableDatabase();
        sqLiteDatabase.delete("interceptmessge",null,null);
        sqLiteDatabase.close();
    }

}
