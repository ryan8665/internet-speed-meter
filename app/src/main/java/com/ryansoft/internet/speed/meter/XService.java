package com.ryansoft.internet.speed.meter;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.VpnService;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public class XService extends VpnService {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ParcelFileDescriptor parcelFileDescriptor = configureTunnelWithPushOpts();
        boolean b = parcelFileDescriptor.canDetectErrors();
        Log.e("aaaaaaaa",b+"");

        return Service.START_STICKY;
    }


    private ParcelFileDescriptor configureTunnelWithPushOpts()
    {
        VpnService.Builder builder = this.new Builder();

        builder.setMtu       (820);
        builder.addAddress   ( "104.237.255.40", 32);
        builder.addDnsServer ( "4.2.2.4");
        builder.addRoute     ( "0.0.0.0", 0 );


        // Note: Blocking mode is now enabled in native
        // code under the setFileDescriptor function.
        // builder.setBlocking(true);

        builder.setConfigureIntent(
                PendingIntent.getActivity(
                        this,
                        0,
                        new Intent(
                                this,
                                MainActivity.class
                        ),
                        PendingIntent.FLAG_UPDATE_CURRENT)
        );

        final ParcelFileDescriptor vpnInterface;

        synchronized (this) {
            builder.setSession("104.237.255.40");
            vpnInterface = builder.establish();
        }
        Log.e("aaaaaaaa","adafafasf");
        return vpnInterface;
    }
}