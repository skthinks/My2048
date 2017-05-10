package com.grofers.skthinks.my2048;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Skthinks on 19/10/16.
 */

public class TimeTicker extends Service {

    private final static String TAG = "BroadcastService";

    public static final String COUNTDOWN_BR = "your_package_name.countdown_br";
    Intent bi = new Intent(COUNTDOWN_BR);

    CountDownTimer cdt = null;
    Timer timer = new Timer();
    private long time_in_secs;

    private IBinder mBinder = new MyBinder();

    /*@Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }*/

    @Override
    public IBinder onBind(Intent intent) {
        //Log.v(LOG_TAG, "in onBind");
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        //Log.v(LOG_TAG, "in onRebind");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        //Log.v(LOG_TAG, "in onUnbind");
        return true;
    }



    @Override
    public void onCreate() {
        super.onCreate();
        /*cdt = new CountDownTimer(30000, 60) {
            @Override
            public void onTick(long l) {
                bi.putExtra("countdown", l);
                sendBroadcast(bi);
            }

            @Override
            public void onFinish() {
                Log.i(TAG, "Timer finished");
            }
        };
        cdt.start();*/
    }

    @Override
    public void onDestroy() {

        //cdt.cancel();
        timer.cancel();
        Log.i(TAG, "Timer cancelled");
        super.onDestroy();
    }


    // onCreate is called just once. onStartCommand is called everytime the service is started
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                time_in_secs++;
                bi.putExtra("countdown", time_in_secs);
                sendBroadcast(bi);
            }
        },1000,1000);
        return super.onStartCommand(intent, flags, startId);
    }

    public class MyBinder extends Binder {
        public TimeTicker getService() {
            return TimeTicker.this;
        }

        public long getTime(){
            return time_in_secs;
        }

        public void setTime(long time){
            time_in_secs = time;
        }
    }
}
