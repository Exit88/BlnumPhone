package com.example.blnumphone.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.blnumphone.Service.ListenPhoneService;


public class TelReceiver extends BroadcastReceiver {

    public TelReceiver(){

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("test:","999999999999999999999999999");
        Intent i = new Intent(context,ListenPhoneService.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setAction(intent.getAction());
        i.putExtra(TelephonyManager.EXTRA_INCOMING_NUMBER,intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER));
        i.putExtra(TelephonyManager.EXTRA_STATE,intent.getStringExtra(TelephonyManager.EXTRA_STATE));//电话状态
        context.startService(i);//启动服务

    }
}
