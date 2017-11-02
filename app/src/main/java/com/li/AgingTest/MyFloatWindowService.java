package com.li.AgingTest;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import java.util.Timer;
import java.util.TimerTask;
import android.provider.Settings.System;

public class MyFloatWindowService extends Service {
    private  String TAG="lixin MyFloatWindowService";
    private String time="time";
    private Handler handler = new Handler();
    int mStopTime;
    int mTime;
    private Timer timer;
    StringBuilder commond = new StringBuilder();
    private IBinder myBinder= new MyBinder();

   private Runnable runnable=new Runnable() {
       @Override
       public void run() {
           MyFloatWindowService myFloatWindowService = MyFloatWindowService.this;
           myFloatWindowService.mTime++;
           MyWindowManager.updateTime(getApplicationContext(),
                  mTime);
           android.util.Log.i("lixin"," MyFloatWindowService mTime="+mTime);
       }
   };
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    public class MyBinder extends Binder{

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MainActivity.mContentResolver = getApplicationContext().getContentResolver();
        try {

            MainActivity.execCommand("");
            android.util.Log.i("lixin","MyFloatWindowService onStartCommand() commond="+commond.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (timer==null){
            timer = new Timer();
            timer.scheduleAtFixedRate(new RefreshTask(),0,1000);
        }
        mTime=0;
        mStopTime = intent.getIntExtra(time,0);
        android.util.Log.i("lixin"," onStartCommand mStopTime="+mStopTime);
        startForeground(1,new Notification());//将一个Serivce放到前台执行使其不会被Android内存管理而销毁

        return super.onStartCommand(intent, flags, startId);

    }

    class RefreshTask extends TimerTask{

        @Override
        public void run() {
            if (!MyWindowManager.isWindowShowing()){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                     MyWindowManager.createSmallWindow(getApplicationContext());
                     android.util.Log.i("lixin"," RefreshTask createSmallWindow");
                    //    Toast.makeText(getApplicationContext(),"MyFloatWindowService",Toast.LENGTH_SHORT).show();
                    }
                });
            }else if (MyWindowManager.isWindowShowing()){
                if (System.getInt(MyFloatWindowService.this.getApplicationContext().getContentResolver(), "monkey_test_pid", 0) == 0){
                    android.util.Log.i("lixin"," RefreshTask DataP.getDataP().getPid()" +
                            System.getInt(MyFloatWindowService.this.getApplicationContext().getContentResolver(), "monkey_test_pid", 0));
                    timer.cancel();
                    timer=null;
                    stopForeground(true);
                    stopService(new Intent(getApplicationContext(),
                            MyFloatWindowService.class));
                    MyWindowManager.removeBigWindow(getApplicationContext());
                    Intent intent =new Intent(getApplicationContext(),MainActivity.class);
                    intent.putExtra("back_time",mTime);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else if (mTime>=mStopTime){
                    android.util.Log.i("lixin"," RefreshTask mTime>=mStopTime  DataP.getDataP().getPid()= "+System.getInt(MyFloatWindowService.this.getApplicationContext().getContentResolver(), "monkey_test_pid", 0));
                    Process.killProcess(System.getInt(MyFloatWindowService.this.getApplicationContext().getContentResolver(), "monkey_test_pid", 0));
                    System.putInt(MyFloatWindowService.this.getApplicationContext().getContentResolver(), "monkey_test_pid", 0);
                }else{
                    handler.post(runnable);
                }

            }
        }
    }
}
