package com.example.blnumphone.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.blnumphone.MyDialog.MyDialog;
import com.example.blnumphone.R;
import com.example.blnumphone.dbdao.BlnumDao;
import com.example.blnumphone.dbdao.TelInfodb;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.Manifest.permission.MODIFY_AUDIO_SETTINGS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private NotificationManager notificationManager;

    //顶部标题栏
    private Button rb_phone;            //电话拦截记录
    private Button rb_xinxi;            //短信拦截记录

    private Button btn_blnum;           //黑名单
    private Button btn_messges;         //信息拦截记录
    private Button btn_add;             //添加黑名单号码
    private Button btn_record;          //录音记录
    private Button btn_setting;         //设置
    private Button main_deleteall;      //清空电话拦截记录
    private Button cancle;               //取消多选删除
    private Button delete;               //多选删除
    private LinearLayout linelayout;    //底部导航条
    private LinearLayout layout;        //长按listview操作条

    MyDialog myDialog;
    TelInfodb telInfodb;
    BlnumDao blnumDao;
    SQLiteDatabase sqLiteDatabase;

    private ListView listview;
    private Context context;
    private List<String> list = new ArrayList<String>();
    private List<String> timelist = new ArrayList<String>();
    private List<String> selectid = new ArrayList<String>();
    private boolean isMulChoice = false; //是否多选
    private Adapter  adapter;

    SharedPreferences prefere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        telInfodb = new TelInfodb(this);
        blnumDao = new BlnumDao(this);
        myDialog = new MyDialog(this);
        context = this;

        SettingMes();
        Maininit();
        showIptNum();
        showIptTime();
        myPermission();

//        String defaultSmsApp = null;
//        String currentPn = getPackageName();//获取当前程序包名
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
//            defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(this);//获取手机当前设置的默认短信应用的包名
//        }
//        if (!defaultSmsApp.equals(currentPn)) {
//            Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
//            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, currentPn);
//            startActivity(intent);
//        }
    }

    //拦截记录列表
    public void showIptNum() {
        sqLiteDatabase = telInfodb.getReadableDatabase();
        String array[] = {"ipttelnum"};
        Cursor cursor = sqLiteDatabase.query("intercepttelnum", array, null, null, null, null, null);
        int blacknumindex = cursor.getColumnIndex("ipttelnum");
      //  Log.i(">>>blacknumindex", blacknumindex + "");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String blacknum = cursor.getString(blacknumindex);
          //  System.out.println("aaaaa>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+blacknum);
            list.add(blacknum);
            cursor.moveToNext();
        }if (list != null) {
            listview.setAdapter(new Adapter(context));
        }
    }

    //拦截记录列表
    public void showIptTime() {
        sqLiteDatabase = telInfodb.getReadableDatabase();
        String time[] = {"ipttime"};
        Cursor cursor = sqLiteDatabase.query("intercepttelnum", time, null, null, null, null, null);
        int blacknumindextime = cursor.getColumnIndex("ipttime");
        //  Log.i(">>>blacknumindex", blacknumindex + "");
        cursor.moveToLast();
        while (!cursor.isBeforeFirst()) {
            String blacknumtime = cursor.getString(blacknumindextime);
            // System.out.println("aaaaa>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+blacknum);
            timelist.add(blacknumtime);
            cursor.moveToPrevious();
        }if (timelist != null) {
            listview.setAdapter(new Adapter(context));
        }
    }

    private void SettingMes(){
        prefere = getApplicationContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        SettingActivity.bsSwitch = prefere.getBoolean("bsSwitch",SettingActivity.bsSwitch);
        SettingActivity.bsSwitch2 = prefere.getBoolean("bsSwitch2",SettingActivity.bsSwitch2);
        SettingActivity.bsSwitch3 = prefere.getBoolean("bsSwitch3",SettingActivity.bsSwitch3);
        SettingActivity.str_et_selfsetting = prefere.getString("str_et_selfsetting",SettingActivity.str_et_selfsetting);
    }

    private void myPermission(){
        // 判断环境兼容，检查自己的权限，是否被同意
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
            //如果不同意，就去请求权限   参数1：上下文，2：权限，3：请求码
            ActivityCompat.requestPermissions(MainActivity.this,new String []{
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.PROCESS_OUTGOING_CALLS,
                    Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS, WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_NOTIFICATION_POLICY, RECORD_AUDIO, MODIFY_AUDIO_SETTINGS,
                    Manifest.permission.RECORD_AUDIO,

                    Manifest.permission.RECEIVE_BOOT_COMPLETED},1);
        }else {
            //同意就拨打
            //  ListenPhoneService.onStartCommand();
        }

        if(ContextCompat.checkSelfPermission(MainActivity.this, RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{RECORD_AUDIO,MODIFY_AUDIO_SETTINGS}, 2);
        }

        if(ContextCompat.checkSelfPermission(MainActivity.this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{READ_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE }, 3);
        }

        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                && !notificationManager.isNotificationPolicyAccessGranted()) {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            getApplicationContext().startActivity(intent);
            return;
        }
    }

    public void Maininit(){
        rb_phone = (Button)findViewById(R.id.rb_phone);                 //电话拦截记录
        rb_xinxi = (Button)findViewById(R.id.rb_xinxi);                 //信息拦截记录
        btn_blnum = (Button)findViewById(R.id.btn_blnum);               //黑名单
        btn_messges = (Button)findViewById(R.id.btn_messges);           //拦截记录
        btn_add = (Button)findViewById(R.id.btn_add);                   //添加黑名单
        btn_record = (Button)findViewById(R.id.btn_record);             //录音记录
        btn_setting = (Button)findViewById(R.id.btn_setting);           //设置
        main_deleteall = (Button)findViewById(R.id.main_deleteall);     //清空
        cancle   = (Button)findViewById(R.id.cancle);
        delete   = (Button)findViewById(R.id.delete);

        linelayout = (LinearLayout)findViewById(R.id.linelayout);
        layout = (LinearLayout)findViewById(R.id.relative);

        listview = (ListView)findViewById(R.id.list);

        adapter = new Adapter(context);
        listview.setAdapter(adapter);


        rb_phone.setOnClickListener(this);
        rb_xinxi.setOnClickListener(this);
        btn_blnum.setOnClickListener(this);
        btn_messges.setOnClickListener(this);
        btn_add.setOnClickListener(this);
        btn_record.setOnClickListener(this);
        btn_setting.setOnClickListener(this);
        cancle.setOnClickListener(this);
        delete.setOnClickListener(this);
        main_deleteall.setOnClickListener(this);

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
            case R.id.rb_xinxi:
                Intent xinxi = new Intent(this,MessgeActivity.class);         //信息拦截页
                startActivity(xinxi);
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
            case R.id.main_deleteall:
                new AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("确定要清空记录吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                blnumDao.maindeleteall();
                                layout.setVisibility(View.INVISIBLE);
                                linelayout.setVisibility(View.VISIBLE);
                                list.clear();
                                adapter.notifyDataSetChanged();
                                Toast.makeText(MainActivity.this,"记录已清空！",Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
            case R.id.cancle:
                isMulChoice = false;
                selectid.clear();
                adapter = new Adapter(context);
                listview.setAdapter(adapter);
                layout.setVisibility(View.INVISIBLE);
                linelayout.setVisibility(View.VISIBLE);
                break;
            case R.id.delete:
                new AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("确定要删除所选的"+selectid.size()+"项吗？")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                isMulChoice =false;
                                for(int i=0;i<selectid.size();i++){
                                    for(int j=0;j<list.size();j++){
                                        if(selectid.get(i).equals(list.get(j))){
                                            //list.remove(j);
                                            sqLiteDatabase = telInfodb.getReadableDatabase();
                                            String [] args=new String[]{list.get(j).toString()};
                                            sqLiteDatabase.delete("intercepttelnum","ipttelnum=?",args);
                                            Toast.makeText(MainActivity.this,"删除成功！",Toast.LENGTH_LONG).show();
                                            sqLiteDatabase.close();
                                            list.remove(j);
                                        }
                                    }
                                }
                                selectid.clear();
                                adapter = new Adapter(context);
                                listview.setAdapter(adapter);
                                layout.setVisibility(View.INVISIBLE);
                                linelayout.setVisibility(View.VISIBLE);
                            }
                        })
                        .setNegativeButton("否", null)
                        .show();
                break;
            default:
                break;
        }
    }

    /**
     * @author jingzi
     * 自定义Adapter
     */
    class Adapter extends BaseAdapter {
        private Context context;
        private LayoutInflater inflater=null;
        private HashMap<Integer, View> mView ;
        public  HashMap<Integer, Integer> visiblecheck ;//用来记录是否显示checkBox
        public  HashMap<Integer, Boolean> ischeck;
        public Adapter(Context context)
        {
            this.context = context;
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mView = new HashMap<Integer, View>();
            visiblecheck = new HashMap<Integer, Integer>();
            ischeck = new HashMap<Integer, Boolean>();
            if(isMulChoice){
                for(int i=0;i<list.size();i++){
                    ischeck.put(i, false);
                    visiblecheck.put(i, CheckBox.VISIBLE);
                }
            }else{
                for(int i=0;i<list.size();i++)
                {
                    ischeck.put(i, false);
                    visiblecheck.put(i, CheckBox.INVISIBLE);
                }
            }
        }

        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return list.get(position);
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View view = mView.get(position);
            if(view==null)
            {
                view = inflater.inflate(R.layout.main_item_layout, null);
                TextView txt = (TextView)view.findViewById(R.id.txtName);
                TextView time = (TextView)view.findViewById(R.id.tv_ipttime);
                final CheckBox ceb = (CheckBox)view.findViewById(R.id.check);

                txt.setText(list.get(position));
                time.setText(timelist.get(position));

                ceb.setChecked(ischeck.get(position));
                System.out.println("ttttttttttttttttttttt"+ischeck.get(position));
                ceb.setVisibility(visiblecheck.get(position));

                view.setOnLongClickListener(new Onlongclick());

                view.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        if(isMulChoice){
                            if(ceb.isChecked()){
                                ceb.setChecked(false);
                                selectid.remove(list.get(position));
                            }else{
                                ceb.setChecked(true);
                                selectid.add(list.get(position));
                            }
                        }else {
                            Toast.makeText(context, "点击了"+list.get(position), Toast.LENGTH_LONG).show();
                        }
                    }
                });

                mView.put(position, view);
            }
            return view;
        }

        class Onlongclick implements View.OnLongClickListener {

            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub

                isMulChoice = true;
                selectid.clear();
                layout.setVisibility(View.VISIBLE);
                for(int i=0;i<list.size();i++)
                {
                    linelayout.setVisibility(View.INVISIBLE);
                    adapter.visiblecheck.put(i, CheckBox.VISIBLE);
                }
                adapter = new Adapter(context);
                listview.setAdapter(adapter);
                return true;
            }
        }
    }
}
