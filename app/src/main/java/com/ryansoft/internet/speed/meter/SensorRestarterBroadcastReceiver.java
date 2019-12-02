package com.ryansoft.internet.speed.meter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

public class SensorRestarterBroadcastReceiver extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(SensorRestarterBroadcastReceiver.class.getSimpleName(), "Service Stops!!!");

        Intent intent1 = new Intent(context, SpeedMeter.class);
        try {
            context.startService(intent1);
        }catch ( Exception e1){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent1);
            }else {
                context.startService(intent1);
            }
        }
    }
}
