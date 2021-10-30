package com.ym.learnkotlin.basicservice.power;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

public class PowerService extends Service {
    private AlarmManager alarmManager;
    public PowerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    }

    private void powerOff(long powerOffTime){
        Intent shutDownIntent = new Intent(Intent.ACTION_SHUTDOWN);
        shutDownIntent.putExtra("android.intent.extra.KEY_CONFIRM", true);
        shutDownIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //TV911App.getApp().sendBroadcast(shutDownIntent);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,
                shutDownIntent,PendingIntent.FLAG_CANCEL_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, powerOffTime, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//  4.4
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, powerOffTime, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, powerOffTime, pendingIntent);
        }
        //alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP,powerOffTime,pendingIntent);
//      alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, date.getTime(), pendingIntent);

    }
}