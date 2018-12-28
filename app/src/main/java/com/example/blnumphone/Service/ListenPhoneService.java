package com.example.blnumphone.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;
import com.example.blnumphone.BroadcastReceiver.SmsReceiver;
import com.example.blnumphone.R;
import com.example.blnumphone.Utils.AudioRecoderUtils;
import com.example.blnumphone.activity.MainActivity;
import com.example.blnumphone.activity.MessgeActivity;
import com.example.blnumphone.activity.SettingActivity;
import com.example.blnumphone.dbdao.BlnumDao;
import com.example.blnumphone.dbdao.TelInfodb;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class ListenPhoneService extends Service {

    public static String inNumber;
    public static String innotiNumber;

    private AudioManager audioManager;
    private TelephonyManager telephonyManager;
    private NotificationManager notificationManager;
    private Notification.Builder builder3;


    private ArrayList<String> list;
    public String code;
    TelInfodb telNumInfo = new TelInfodb(this);
    BlnumDao blnumDao = new BlnumDao(this);
    SQLiteDatabase sqLiteDatabase;

    AudioRecoderUtils audioRecoderUtils;
    SharedPreferences prefere;

    public ListenPhoneService(){
        audioRecoderUtils = new AudioRecoderUtils();

        audioRecoderUtils.setOnAudioStatusUpdateListener(new AudioRecoderUtils.OnAudioStatusUpdateListener() {
            //录音结束，filePath为保存路径
            @Override
            public void onStop(String filePath) {
                Toast.makeText(getApplicationContext(), "录音保存在：" + filePath, Toast.LENGTH_SHORT).show();
                System.out.println("wwwwwwwwwwwwwwwwww"+filePath);
            }
        });
    }


    @Override
    public void onCreate(){
        super.onCreate();
        SettingMes();
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        telephonyManager = (TelephonyManager)getSystemService(Service.TELEPHONY_SERVICE);
        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

    }

    private void SettingMes(){
        prefere = getApplicationContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        SettingActivity.bsSwitch = prefere.getBoolean("bsSwitch",SettingActivity.bsSwitch);
        SettingActivity.bsSwitch2 = prefere.getBoolean("bsSwitch2",SettingActivity.bsSwitch2);
        SettingActivity.bsSwitch3 = prefere.getBoolean("bsSwitch3",SettingActivity.bsSwitch3);
        SettingActivity.str_et_selfsetting = prefere.getString("str_et_selfsetting",SettingActivity.str_et_selfsetting);
    }

    private void searchblacknum(){
        sqLiteDatabase = telNumInfo.getReadableDatabase();
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
        sqLiteDatabase = telNumInfo.getReadableDatabase();
        Cursor codecursor = sqLiteDatabase.rawQuery("select code from telnuminfo where telnum=?",new String[]{incomingNumber});
        codecursor.moveToNext();
        code = codecursor.getString(codecursor.getColumnIndex("code"));
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+code);
        codecursor.close();
        sqLiteDatabase.close();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        //searchTelNum();
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {//去电广播
            //在这里，例如我们可以插入IP电话字头

        }else {
            //方法一
            //获得来电电话
            // String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

            //获得电话状态
            // String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            //有以下状态
           /*
            (1)TelephonyManager.EXTRA_STATE_IDLE://空闲状态
            (2)TelephonyManager.EXTRA_STATE_OFFHOOK://接起电话
            (3)TelephonyManager.EXTRA_STATE_RINGING://响铃时
            */
//           if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
//
//               sqLiteDatabase = telNumInfo.getReadableDatabase();
//               String array[]={"telnum"};
//               Cursor cursor=sqLiteDatabase.query("telnuminfo",array,null,null,null,null,null);
//               int blacknumindex=cursor.getColumnIndex("telnum");
//               Log.i(">>>",blacknumindex+"");
//               cursor.moveToFirst();
//               ArrayList<String> list=new ArrayList<>();
//               while (!cursor.isAfterLast())
//               {
//                   String blacknum=cursor.getString(blacknumindex);
//                   list.add(blacknum);
//                   cursor.moveToNext();
//                   Log.i(">>>",">>>>");
//                   Log.i("Simon",list.toString());
//               }
//               if (list!=null&&list.contains(number)) {
//                   audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
//                   stopCall();
//                   audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
//               }
//
//           }else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
//               //接起电话
//               recordCall();//开始录音
//           }else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
//               stopCall();//停止录音
//           }


            //方法二
            //设置一个监听器，监听电话状态
            telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);


        }
        return super.onStartCommand(intent,flags,startId);
    }

    //挂断电话
    private void stopCall(){
        try {
            //Android 的设计将ServiceManager隐藏了，所以只能使用反射机制获得
            Method method = Class.forName("android.os.ServiceManager").getMethod("getService",String.class);

            //获得系统电话服务
            IBinder binder = (IBinder)method.invoke(null,new Object[]{"phone"});
            ITelephony telephony = ITelephony.Stub.asInterface(binder);
            telephony.endCall();//挂断电话
            stopSelf();//停止服务
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    PhoneStateListener listener = new PhoneStateListener(){
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onCallStateChanged(int state,String incomingNumber){
            super.onCallStateChanged(state, incomingNumber);
            switch (state){
                //手机空闲了
                case (TelephonyManager.CALL_STATE_IDLE):
                    stopCall();
                    audioRecoderUtils.stopRecord();//停止录音
                    break;

                //接起电话
                case (TelephonyManager.CALL_STATE_OFFHOOK):
                    inNumber = incomingNumber;
                    audioRecoderUtils.startRecord();//开始录音
                    break;

                //响铃时
                case (TelephonyManager.CALL_STATE_RINGING):
                    innotiNumber = incomingNumber;
                    searchblacknum();
                    if (SettingActivity.bsSwitch == true){      //黑名单开关
                        if (SettingActivity.bsSwitch2 == false){    //400拦截开关
                            if (SettingActivity.bsSwitch3 == false){        //自定义开关
                                if (list!=null&&list.contains(incomingNumber)) {        //如果该号码属于黑名单
                                    searchnumcode(incomingNumber);
                                    if (code.equals("1")){          //code==1,电话拦截
                                        try {
                                            blnumDao.insertIptTelNum(incomingNumber);
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                        //Toast.makeText(ListenPhoneService.this,incomingNumber,Toast.LENGTH_LONG).show();
                                        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                        stopCall();
                                        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

                                        showNotificationManager();      //通知栏

                                    }else if (code.equals("2")){            //code==2,信息拦截

                                        //Toast.makeText(ListenPhoneService.this,"22222",Toast.LENGTH_LONG).show();

                                    }else if (code.equals("3")){            //code==3,都拦截
                                        //Toast.makeText(ListenPhoneService.this,"33333",Toast.LENGTH_LONG).show();
                                        try {
                                            blnumDao.insertIptTelNum(incomingNumber);
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                        //Toast.makeText(ListenPhoneService.this,incomingNumber,Toast.LENGTH_LONG).show();
                                        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                        stopCall();
                                        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

                                        showNotificationManager();      //通知栏
                                    }
                                }
                            }else {
                                if ((list!=null&&list.contains(incomingNumber))||(incomingNumber.matches("^(" + SettingActivity.str_et_selfsetting + ").*"))){
                                    if (incomingNumber.matches("^(" + SettingActivity.str_et_selfsetting + ").*")){
                                        //Toast.makeText(ListenPhoneService.this,"88888",Toast.LENGTH_LONG).show();
                                        blnumDao.insertIptTelNum(incomingNumber);
                                        //Toast.makeText(ListenPhoneService.this,incomingNumber,Toast.LENGTH_LONG).show();
                                        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                        stopCall();
                                        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

                                        showNotificationManager();      //通知栏

                                    }else {
                                        searchnumcode(incomingNumber);
                                        if (code.equals("1")){          //code==1,电话拦截
                                            blnumDao.insertIptTelNum(incomingNumber);
                                           // Toast.makeText(ListenPhoneService.this,incomingNumber,Toast.LENGTH_LONG).show();
                                            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                            stopCall();
                                            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

                                            showNotificationManager();      //通知栏

                                        }else if (code.equals("2")){            //code==2,信息拦截
                                            //Toast.makeText(ListenPhoneService.this,"22222",Toast.LENGTH_LONG).show();

                                        }else if (code.equals("3")){            //code==3,都拦截
                                            //Toast.makeText(ListenPhoneService.this,"33333",Toast.LENGTH_LONG).show();
                                            try {
                                                blnumDao.insertIptTelNum(incomingNumber);
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }
                                            //Toast.makeText(ListenPhoneService.this,incomingNumber,Toast.LENGTH_LONG).show();
                                            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                            stopCall();
                                            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

                                            showNotificationManager();      //通知栏
                                        }
                                    }
                                }
                            }
                        }else {
                            if (SettingActivity.bsSwitch3 == false){
                                // Toast.makeText(ListenPhoneService.this,"66666"+incomingNumber,Toast.LENGTH_LONG).show();
                                if ((list!=null&&list.contains(incomingNumber))||(incomingNumber.matches("^(400).*"))) {
                                    //  Toast.makeText(ListenPhoneService.this,"23333"+incomingNumber,Toast.LENGTH_LONG).show();
                                    if (incomingNumber.matches("^(400).*")) {
                                        blnumDao.insertIptTelNum(incomingNumber);
                                        //Toast.makeText(ListenPhoneService.this, incomingNumber, Toast.LENGTH_LONG).show();
                                        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                        stopCall();
                                        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

                                        showNotificationManager();      //通知栏

                                    } else {
                                        searchnumcode(incomingNumber);
                                        if (code.equals("1")) {          //code==1,电话拦截
                                            blnumDao.insertIptTelNum(incomingNumber);
                                            //Toast.makeText(ListenPhoneService.this, incomingNumber, Toast.LENGTH_LONG).show();
                                            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                            stopCall();
                                            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

                                            showNotificationManager();      //通知栏


                                        } else if (code.equals("2")) {            //code==2,信息拦截

                                            //Toast.makeText(ListenPhoneService.this, "22222", Toast.LENGTH_LONG).show();

                                        } else if (code.equals("3")) {            //code==3,都拦截
                                            //Toast.makeText(ListenPhoneService.this, "33333", Toast.LENGTH_LONG).show();
                                            try {
                                                blnumDao.insertIptTelNum(incomingNumber);
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }
                                            //Toast.makeText(ListenPhoneService.this,incomingNumber,Toast.LENGTH_LONG).show();
                                            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                            stopCall();
                                            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

                                            showNotificationManager();      //通知栏
                                        }
                                    }
                                }
                            }else {
                                if ((list!=null&&list.contains(incomingNumber))||(incomingNumber.matches("^(400).*"))
                                        ||(incomingNumber.matches("^(" + SettingActivity.str_et_selfsetting + ").*"))){

                                    if (incomingNumber.matches("^(400).*")||(incomingNumber.matches("^(" + SettingActivity.str_et_selfsetting + ").*"))){
                                        blnumDao.insertIptTelNum(incomingNumber);
                                        //Toast.makeText(ListenPhoneService.this,incomingNumber,Toast.LENGTH_LONG).show();
                                        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                        stopCall();
                                        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

                                        showNotificationManager();      //通知栏

                                    } else {
                                        searchnumcode(incomingNumber);
                                        if (code.equals("1")){          //code==1,电话拦截
                                            blnumDao.insertIptTelNum(incomingNumber);
                                            //Toast.makeText(ListenPhoneService.this,incomingNumber,Toast.LENGTH_LONG).show();
                                            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                            stopCall();
                                            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

                                            showNotificationManager();      //通知栏

                                        }else if (code.equals("2")){            //code==2,信息拦截

                                            //Toast.makeText(ListenPhoneService.this,"22222",Toast.LENGTH_LONG).show();

                                        }else if (code.equals("3")){            //code==3,都拦截
                                            //Toast.makeText(ListenPhoneService.this,"33333",Toast.LENGTH_LONG).show();
                                            try {
                                                blnumDao.insertIptTelNum(incomingNumber);
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }
                                            //Toast.makeText(ListenPhoneService.this,incomingNumber,Toast.LENGTH_LONG).show();
                                            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                            stopCall();
                                            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

                                            showNotificationManager();      //通知栏
                                        }
                                    }
                                }
                            }
                        }
                    }
                    break;
            }
        }
    };

    //通知栏
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void showNotificationManager(){
        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, resultIntent, 0);
        builder3=new Notification.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.logo))
                .setContentTitle("骚扰拦截为您拦截了一个骚扰电话")
                .setContentText("被拦截号码："+innotiNumber)
                .setOnlyAlertOnce(false)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setContentIntent(resultPendingIntent);
        notificationManager.notify(0, builder3.build());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

