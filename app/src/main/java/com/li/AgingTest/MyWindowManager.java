package com.li.AgingTest;

/**
 * Created by gms on 17-6-20.
 */
import android.content.Context;
import android.content.Intent;
import android.view.WindowManager;
import android.widget.TextView;


public class MyWindowManager {
    private static MyFloatWindowBigView bigWindow;
    private static WindowManager mWindowManager;
    private static MyFloatWindowSmallView smallWindow;

    public static void createSmallWindow(Context context){
        WindowManager windowManager = getWindowManager(context);

        if (windowManager!=null) {
            if (smallWindow == null) {
                smallWindow = new MyFloatWindowSmallView(context);
                if (smallWindow!=null) {
                    windowManager.addView(smallWindow, smallWindow.mParams);
                }else {
                    android.util.Log.i("lixin" ,"smallWindow==null");
                }
            }
        }else{
            android.util.Log.i("lixin" ,"windowManager==null");
        }
        
        
    }
    public static void createBigWindow(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void removeBigWindow(Context context) {
        if (bigWindow != null) {
            getWindowManager(context).removeView(bigWindow);
            bigWindow = null;
        }
    }

    public static void removeSmallWindow(Context context) {
        if (smallWindow != null) {
            getWindowManager(context).removeView(smallWindow);
            smallWindow = null;
        }
    }


    public static void updateTime(Context context,int mTime){
        if(smallWindow!=null){
            android.util.Log.i("lixin MyWindowManager","mTime="+mTime);
            ((TextView) smallWindow.findViewById(R.id.time)).setText(intToTime(mTime));
        }
    }

    private static WindowManager getWindowManager(Context context) {
        if (mWindowManager==null){
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }

    public static boolean isWindowShowing(){
        return (smallWindow==null&&bigWindow==null)?false:true;
    }

    private static String intToTime(int time){
      int min = (time % 3600)/60;
       int sec = (time %3600)%60;
        String hourStr = String.format("%02d",
                new Object[]{Integer.valueOf(time / 3600)});
        String minStr = String.format("%02d",
                new Object[]{Integer.valueOf(min)});
        return hourStr + ":" + minStr + ":" + String.format("%02d",
                new Object[]{Integer.valueOf(sec)});
    }

}
