package com.kfang.fenceme;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by kfang on 2/13/2017.
 */

public class TimerHandler extends Service {
    @Override
    public IBinder onBind(Intent i) {
        return null;
    }

    @Override
    public int onStartCommand(Intent i, int flags, int startId) {
        // Put your timer code here
    }
}
