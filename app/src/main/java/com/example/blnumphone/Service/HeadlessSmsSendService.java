package com.example.blnumphone.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class HeadlessSmsSendService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
