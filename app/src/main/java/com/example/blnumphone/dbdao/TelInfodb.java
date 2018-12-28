package com.example.blnumphone.dbdao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TelInfodb extends SQLiteOpenHelper {
    public TelInfodb(Context context) {
        super(context, "telnuminfo.db", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE telnuminfo(id integer primary key autoincrement,telnum VARCHAR(13) unique,code VARCHAR(6) not null,addtime DATETIME)");         //黑名单表
        sqLiteDatabase.execSQL("CREATE TABLE intercepttelnum(_id INTEGER PRIMARY KEY AUTOINCREMENT,ipttelnum VARCHAR(13) unique,ipttime DATETIME)");        //电话拦截记录表
        sqLiteDatabase.execSQL("CREATE TABLE interceptmessge(_id INTEGER PRIMARY KEY AUTOINCREMENT,iptmessge VARCHAR(13),messges VARCHAR(500),iptmestime DATETIME)");    //短信拦截记录表
        sqLiteDatabase.execSQL("CREATE TABLE history(id integer primary key autoincrement,name varchar(12) NOT NULL UNIQUE)");                      //查询历史表
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
