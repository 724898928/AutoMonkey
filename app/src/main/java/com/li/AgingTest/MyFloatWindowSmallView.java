package com.li.AgingTest;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by gms on 17-6-20.
 */
public class MyFloatWindowSmallView  extends LinearLayout{

    private static int statusBarHeight;
    public static int viewHeight;
    public static int viewWidth;
    public WindowManager.LayoutParams mParams;
    private TextView mTime = ((TextView) findViewById(R.id.time));
    private WindowManager windowManager;
    private float xDownInScreen;
    private float xInScreen;
    private float xInView;
    private float yDownInScreen;
    private float yInScreen;
    private float yInView;

    public MyFloatWindowSmallView(Context context) {
        super(context);
       windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        LayoutInflater.from(context).inflate(R.layout.float_window_small,this);
        View view =findViewById(R.id.small_window_layout);
        viewWidth = view.getLayoutParams().width;
        viewHeight =view.getLayoutParams().height;
        statusBarHeight = getStatusBarHeight();

        mParams =new WindowManager.LayoutParams();
        mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        mParams.format = PixelFormat.RGBA_8888;
        mParams.flags =  WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mParams.gravity = Gravity.LEFT | Gravity.TOP;
        mParams.width =MyFloatWindowSmallView.viewWidth;;
        mParams.height =MyFloatWindowSmallView.viewHeight;;
        mParams.x = screenWidth ;
        mParams.y =screenHeight/2;
    }
    public boolean onTouchEvent(MotionEvent event){
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                yInView = event.getY();
                xDownInScreen = event.getRawX();
                yDownInScreen = event.getRawY() - ((float) getStatusBarHeight());
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - ((float) getStatusBarHeight());
                break;
            case MotionEvent.ACTION_UP:
                if (xDownInScreen == xInScreen && yDownInScreen == yInScreen) {
                    openBigWindow();
                    break;
                }
            case MotionEvent.ACTION_MOVE:
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - ((float) getStatusBarHeight());
                updateViewPosition();
                break;
        }
        return true;
    }
    private void openBigWindow() {
        MyWindowManager.createBigWindow(getContext());
        MyWindowManager.removeSmallWindow(getContext());
    }

    private int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                statusBarHeight = getResources().getDimensionPixelSize(((Integer) c.getField("status_bar_height").get(c.newInstance())).intValue());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }
    private void updateViewPosition() {
        mParams.x = (int) (xInScreen - xInView);
        mParams.y = (int) (yInScreen - yInView);
        windowManager.updateViewLayout(this, mParams);
    }



}
