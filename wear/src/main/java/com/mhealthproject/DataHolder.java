package com.mhealthproject;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by emir on 9/15/15.
 */
public class DataHolder {

    final private static String TAG = "DataHolder";
    private String[] app;
    private long[] user;
    private String[] appList;
    private int[] pid;
    private String touchEvents;
    private String[] powerData;

    private boolean safe;
    private int appInt;



    private Service mContext;
    public BluetoothAdapter bluetoothAdapter;
    private GoogleApiClient googleApiClient;
    final private static Object mLogLock = new Object();





    public String getTouchEvents() {return touchEvents;}
    public void setTouchEvents(String data) {this.touchEvents = data;
        Log.i(TAG, "DataHolder touchEvents data: " + touchEvents);}


    public boolean getSendTouchEvents() {return safe;}
    public void setSendToucEvents(boolean safe) {this.safe = safe;
         Log.i(TAG, "DataHolder safe: " + safe);}

    public int getRunningAppInt() {return appInt;}
    public void setRunningAppInt(int appInt) {this.appInt = appInt;
            Log.i(TAG, "Running app is: " + appInt);}


    private static final DataHolder holder = new DataHolder();
    public static DataHolder getInstance() {return holder;}
}

