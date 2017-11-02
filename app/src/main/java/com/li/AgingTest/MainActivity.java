package com.li.AgingTest;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.provider.Settings.System;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static  String TAG2="lixin";
    private static  int MAX_MARK = 7200;
    private static  int MIN_MARK = 60;
    private static int mTime = MIN_MARK;
    private static StringBuilder sPsResult = new StringBuilder("");
    private static StringBuilder sResult = new StringBuilder("");
    public static final String logcatfile= Environment.getExternalStorageDirectory().getPath()+"/MonkeyTestResult.txt";
    public static StringBuffer sBuilder = null;
    private int mIndex;
    ArrayList<Integer> hourEntry;
    Spinner hourSp;
    public Context mContext;
    ArrayList<Integer> minEntry;
    Spinner minSp;
    String res;
    ArrayList<Integer> secEntry;
    Spinner secSp;

    public PackageManager mPackageManager;
    private String TAG= "lixin MainActivity";
    public static ContentResolver mContentResolver;
    public HashMap<String, String> mCheckedModules;
    public StringBuffer commandStr = new StringBuffer();

    private BroadcastReceiver killReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getAction().equals("com.reallytek.killmonkey")){

            }
        }
    };
    private String intToTime(int time){
        int min = (time % 3600) / 60;
        int sec = (time % 3600) % 60;
        String hourStr = String.format("%02d", new Object[]{Integer.valueOf(time / 3600)});
        String minStr = String.format("%02d", new Object[]{Integer.valueOf(min)});
        String secStr = String.format("%02d", new Object[]{Integer.valueOf(sec)});
        String preStr = getString(R.string.back_time);
        String hStr = getString(R.string.time_h);
        String mStr = getString(R.string.time_m);
        String sStr = getString(R.string.time_s);
        return new StringBuilder((((preStr.length() + hStr.length()) +
                mStr.length()) + sStr.length()) + 8)
                .append(preStr).append(hourStr)
                .append(hStr).append(minStr)
                .append(mStr).append(secStr).append(sStr).toString();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 23) {
            if(!Settings.System.canWrite(this)){
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent,10);
            }
        }
        this.mContext=this;

        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent,10);
            }

        }
        initDate();
    }

    private void initDate() {
        mPackageManager = getPackageManager();

        TextView mbackTime = (TextView) findViewById(R.id.backTime);
        Intent intent = getIntent();
        if (!(intent == null || intent.getExtras() == null)) {
            mbackTime.setText(intToTime(intent.getExtras().getInt("back_time")));
            mbackTime.setVisibility(View.VISIBLE);
        }
        hourSp = (Spinner) findViewById(R.id.testHour);
        hourSp.setSelection(2);
        hourEntry = loadIntegerArray(getResources(), R.array.select_min_values);
        minSp = (Spinner) findViewById(R.id.testMin);
        minSp.setSelection(0);
        minEntry = loadIntegerArray(getResources(), R.array.select_min_values);
        secSp = (Spinner) findViewById(R.id.testSec);
        secSp.setSelection(0);
        secEntry = loadIntegerArray(getResources(), R.array.select_sec_values);
        ((Button) findViewById(R.id.startBtn)).setOnClickListener(this);

    }


    private ArrayList<Integer> loadIntegerArray(Resources r, int resNum) {
        ArrayList<Integer> list=new ArrayList();
        for (int valueOf : r.getIntArray(resNum)) {
            list.add(Integer.valueOf(valueOf));
        }
        return list;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.startBtn:

            {
                startMTKLogger();
                mContentResolver = getContentResolver();
                mTime = ((Integer) secEntry.get(
                        secSp.getSelectedItemPosition())).intValue() +
                        ((((Integer) hourEntry.get(
                                hourSp.getSelectedItemPosition())).intValue() * 3600) +
                                (((Integer) minEntry.get(
                                        minSp.getSelectedItemPosition())).intValue() * 60));
                Log.i(TAG,"onClick");
                new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(MainActivity.this,MyFloatWindowService.class);
                                intent.putExtra("time",MainActivity.mTime);
                                // Toast.makeText(MainActivity.this,"onClick",Toast.LENGTH_SHORT).show();
                                startService(intent);
                            }
                        }
                ).start();
                WriteFlgs();
                finish();


            }
            break;

        }
    }

    private void WriteFlgs(){
        IOException e;
        try {
            Log.i("Agingtest", "start Writer");
            BufferedWriter bufWriter = new BufferedWriter(new FileWriter("/sys/devices/platform/battery/CHG_CAPACITY_TEST"));
            try {
                bufWriter.write("2");
                Log.i("Agingtest", "Writer complete");
                bufWriter.close();
                BufferedWriter bufferedWriter = bufWriter;
            } catch (IOException e2) {
                e = e2;
                e.printStackTrace();
            }
        } catch (IOException e3) {
            e = e3;
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(this.killReceiver,new IntentFilter("com.reallytek.killmonkey"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(killReceiver);
    }


    public static int execCommand(String command1) {


        command1="monkey --throttle 500 --ignore-crashes --ignore-timeouts --ignore-security-exceptions --ignore-native-crashes --monitor-native-crashes -v " +
                 "-p com.android.contacts " +
                 "-p com.android.dialer " +
                 "-p com.android.calendar " +
                 "-p com.mediatek.todos " +
                 "-p com.android.deskclock " +
                 "-p com.android.gallery3d " +
                 "-p com.android.music " +
                 "-p com.mediatek.filemanager " +
                 "-p com.mediatek.videoplayer " +
                 "-p com.android.settings " +
                 "-p com.android.systemui "+
                 "1000000";
        android.util.Log.i(TAG2," MainActivity execCommand() command1="+command1);
        sResult.delete(0, sResult.length());
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (process == null) {
            return -1;
        }
        //正则表达式
        Matcher m = Pattern.compile("[0-9]").matcher(process.toString());
        StringBuffer bf = new StringBuffer(64);
        while (m.find()) {
            bf.append(m.group());
        }
        savePid(Integer.parseInt(bf.toString(), 10));

        String path ="/MonkeyTestResultERROR.txt";
        String path2 ="/MonkeyTestResult.txt";
        new StreamGobbler(process.getErrorStream(),"ERROR",path).start();
        new StreamGobbler(process.getInputStream(), "STDOUT",path2).start();
        return 0;
    }

    public void startMTKLogger() {
        Intent intent = new Intent();
        intent.setAction("com.mediatek.mtklogger.ADB_CMD");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("cmd_name", "start");
        intent.putExtra("cmd_target", 7);
        sendBroadcast(intent);
    }
    static void savePid(int pid){
        System.putInt(mContentResolver, "monkey_test_pid", pid);
        android.util.Log.i("lixin","savePid() pid="+pid);
    }
}
