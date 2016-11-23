package com.mhealthproject;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Date;

import static android.view.WindowManager.*;

/**
 * Created by begum and emir on 1/29/16.
 * This class is for collecting the sensors and saving them as logs.
 * This class collects 3 sensors step counter, accelerometer, heart rate
 * This class is a service class and implements SensorEventListener which provides us to listen sensors from SYSTEM_SERVICE
 */

public class SensorService extends Service implements SensorEventListener {

    final public static String TAG = "SensorService";

    private static Logger mLogger = new Logger();

    private NotificationManager notification;           // notification manager object notification

    private SensorManager mSensorManager;               // sensor manager and sensor event listener objects

    private PowerManager powerManager;                  // wake lock to make this app runs even in screen off
    public PowerManager.WakeLock wakeLock;

    private String accellerometerSensor = "";
    private String heartRateSensor = "";
    private String stepCounterSensor = "";
    private String gyroscopeSensor = "";
    private int accellerometerEvent = 0;
    private int heartRateEvent = 0;
    private int stepCounterEvent = 0;
    private String touchEvents = "";
    private int gyroscopeEvent = 0;

    private WindowManager mWindowManager;
    private LinearLayout mDummyView;
    private OutputStream outputStream;
    private boolean justSent = false;


    Thread keyThread = new Thread();
    StringBuilder text = new StringBuilder();

    CatchColorView catchColorView;
    MovingBallView movingBallView;
    TouchMultipleView touchMultipleView;

    private float lastTime = System.currentTimeMillis();


    //InputStreamReader reader;


    @Override
    public void onCreate() {

        Log.i(TAG, "SensorService");

        Toast.makeText(this, "Service is created...", Toast.LENGTH_LONG).show();
/*
        mWindowManager = (WindowManager) this.getSystemService(this.WINDOW_SERVICE);
        mDummyView = new LinearLayout(this);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(1, WindowManager.LayoutParams.MATCH_PARENT);
        mDummyView.setLayoutParams(params);
        mDummyView.setOnTouchListener((View.OnTouchListener) this);

        params = new WindowManager.LayoutParams(
                1,
                1,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSPARENT
        );
        params.gravity = Gravity.LEFT | Gravity.TOP;
        mWindowManager.addView(mDummyView, params);

*/


        try {


            Logger.createLogFile(this);
            Logger.createLogFileToUpload(this);
            mLogger.logEntry("Logger On");
            mLogger.logEntryPhone("Logger On");

            catchColorView = new CatchColorView(this);
            movingBallView = new MovingBallView(this);
            touchMultipleView = new TouchMultipleView(this);


            notification = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);

            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

            powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    "MyWakelockTag");
            wakeLock.acquire();

            IntentFilter filter = new IntentFilter();               // to listen system intents, if we need anything.
            registerReceiver(mBroadcastIntentReceiver, filter);

            startSensorListeners();
            mHandler.postDelayed(mRefresh, 500);
            //  keyThread = new Thread(new KeyLogger());
            //   keyThread.start();
            //  getTouchEvents2();


        } catch (Exception e) {
            Toast.makeText(this, "An error occured in SensorService onCreate", Toast.LENGTH_LONG).show();
        }

    }


    private void startSensorListeners() {
        Log.d(TAG, "startSensorListeners");

        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE), SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER), SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL);

        Log.d(TAG, "Sensor started: " + mSensorManager);

    }

    private void stopSensorListeners() {
        Log.d(TAG, "stopSensorListeners");
        mSensorManager.unregisterListener(SensorService.this);
        Log.d(TAG, "Sensor stoppped: " + mSensorManager);
        wakeLock.release();

    }


    @Override
    // in each sensor changes this override function keeps the everything avout the sensor. And we get 2 of these info
    public void onSensorChanged(SensorEvent event) {
        String key = event.sensor.getName();
        float values = event.values[0];
        Sensor sensor = event.sensor;


        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accellerometerEvent++;                  // this is event counts when sensor changes, this counts all the events changings. It shouldnt be necessary though
            accellerometerSensor = values + " " + event.values[1] + " " + event.values[2] + " " + accellerometerEvent; // This is the string for value and seen event count.

        }

        if (sensor.getType() == Sensor.TYPE_HEART_RATE) {
            heartRateEvent++;
            heartRateSensor = values + " " + heartRateEvent;

        }

        if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            stepCounterEvent++;
            stepCounterSensor = values + " " + stepCounterEvent;

        }

        if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gyroscopeEvent++;
            try {
                gyroscopeSensor = values + " " + event.values[1] + " " + event.values[2] + " " + gyroscopeEvent;
            }catch (Exception e){}

        }

    }

    @Override
    // since we will always capture the data whether it changes or not, we don't need this method
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        return;
    }

    @Nullable
    @Override     // its default method created by service class
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        Toast.makeText(this, "service onDestroy", Toast.LENGTH_LONG).show();

        keyThread.interrupt();
        mHandler.removeCallbacksAndMessages(null);
        stopSelf();
    }


    private OutputStream getTouchEvents2() {

        try {
            Process mProcess = new ProcessBuilder()
                    .command("su")
                    .redirectErrorStream(true).start();

            OutputStream out = mProcess.getOutputStream();

            String cmd = "getevent /dev/input/event1 \n";
            Log.d(TAG, "Native command = " + cmd);
            out.write(cmd.getBytes());
            Log.d(TAG, out.toString());
            return out;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    BroadcastReceiver mBroadcastIntentReceiver = new BroadcastReceiver() {     // just captures the screen on and off. An example if we need to track actions
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                Log.i(TAG, ">>>>>>>>>> caught ACTION_SCREEN_OFF <<<<<<<<<<<");
                mLogger.logEntry("Screen Off");

            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                Log.i(TAG, ">>>>>>>>>> caught ACTION_SCREEN_ON <<<<<<<<<<<");
                mLogger.logEntry("Screen_On");

            }
        }
    };


    /* So far i have tried to write getevent -lt to /data/data/files/mhealth.. to be able to get all touch events and get them in syncronize way
    * But first of all it is so costly getting them from inputsteamreader and writing to files make the watch freeze and consume the vm memory
    * Other thing i have tried, i wrote them in sdcard and get them from there, but it is again the same as above
    * One thing we can do, we can write them to /sdcard and send this /sdcard file to server at the end and make computation offline
    * as shown below
    */
    class KeyLogger implements Runnable {
        @Override
        public void run() {
            try {
                final Process process = Runtime.getRuntime().exec(new String[]{"su", "-c", "getevent -lt /dev/input/event0 > /sdcard/geteventFile"});
                // final Process process = Runtime.getRuntime().exec(new String[] { "su", "-c", "getevent -lt /dev/input/event0" });
                final InputStreamReader reader = new InputStreamReader(process.getInputStream());
                Log.d("", "it is gonna get getevent1");
                Runnable run = new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            try {
                                //  final Process process = Runtime.getRuntime().exec(new String[] { "su", "-c", "getevent -lt /dev/input/event0 > /sdcard/doubletab" });
                                //final Process process = Runtime.getRuntime().exec(new String[] { "su", "-c", "getevent -lt /dev/input/event0 > /sdcard/doubletab" });
                                //final InputStreamReader reader = new InputStreamReader(process.getInputStream());
                                // final Process process = Runtime.getRuntime().exec(new String[] { "su", "-c", "getevent -lt /dev/input/event0" });
                                // final InputStreamReader reader = new InputStreamReader(process.getInputStream());
                                while (reader.ready()) {

                                    //  Log.d("", "xyx Touch " + reader.read());
                                    Log.d("", "xyx Touch " + reader.read());
                                }
/*
                                try {
                                    BufferedReader br = new BufferedReader(reader);
                                    String line;

                                    while ((line = br.readLine()) != null) {
                                        text.append(line);
                                        text.append('\n');
                                    }

                                    br.close();
                                }
                                catch (IOException e) {
                                    //You'll need to add proper error handling here
                                    Log.d(TAG, "Handler problem occured with buffered reader");
                                }

*/
                                //  }
                                // final Process process1 = Runtime.getRuntime().exec(new String[] { "^C" });
                                // final InputStreamReader reader2 = new InputStreamReader(process1.getInputStream());
                                //  final InputStreamReader reader2 = new InputStreamReader(process.getInputStream());
                                // reader.reset();
                                Thread.sleep(4000);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                run.run();

                //while (cont && reader.read() != -1) {
                //ABS_MT_POSITION_X
                //ABS_MT_POSITION_Y
                //ABS_MT_TOUCH_MAJOR
                //ABS_MT_TOUCH_MINOR
                //ABS_MT_TRACKING_ID
                //ABS_MT_WIDTH_MAJOR
                //SYN_REPORT
                //003c

                //Log.d("", "xyx KeyPress");
                //}
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("", "There is a problem in avg logger");
            }
        }
    }



/*
        Handler mHandler = new Handler();   // handler is more convenient for massage passing between objects and also UI friendly.
                                              // so if we need to put some info or even in notifications we may need handler instead of thread.
        Runnable mRefresh = new Runnable() {
            @Override
            public void run() {

                try {
                    startSensorListeners();     // it starts sensor listening
                    mHandler.postDelayed(mRefresh, 1000);





                        File sdcard = Environment.getExternalStorageDirectory();

                        File file = new File(sdcard,"doubletab");

                        StringBuilder text = new StringBuilder();

                        try {
                            BufferedReader br = new BufferedReader(new FileReader(file));
                            String line;

                            while ((line = br.readLine()) != null) {
                                text.append(line);
                                text.append('\n');
                            }
                            br.close();
                            file.delete();
                        }
                        catch (IOException e) {
                            //You'll need to add proper error handling here
                        }

                     //  mLogger.logEntry(touchEvents);
                        Log.d(TAG, text.toString());
                      //  getTouchEventsStop();
                        touchEvents ="";


                    try {

                        mLogger.logEntry("Accelerometer " + accellerometerSensor);
                        mLogger.logEntry("Heart Rate " + heartRateSensor);
                        mLogger.logEntry("Touch Events: \n" + text.toString());
                        //   stopSensorListeners();      // it stops listening to reduce CPU usage/burden. Because listening costs alot
                        Log.d(TAG,"Accelerometer " + accellerometerSensor +"\n"+ "Heart Rate " + heartRateSensor  );

                    } catch (Exception e) {
                        Log.i(TAG, "Error occured while collecting sensors " + e);
                    }




                } catch (Exception e) {
                    Log.i(TAG, "Error occured " + e);

                }
            }
        };

*/

    private void writeToFile(String data) {

        Date date = new Date();
        File logFile = new File("sdcard/mHealthLogs.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(data);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    private void writeToFile_CA(String data) {

        Date date = new Date();
        File logFile = new File("sdcard/mHealth2.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(data);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    Handler mHandler = new Handler();   // handler is more convenient for massage passing between objects and also UI friendly.
    // so if we need to put some info or even in notifications we may need handler instead of thread.
    Runnable mRefresh = new Runnable() {
        @Override
        public void run() {


            try {


                //    startSensorListeners();     // it starts sensor listening

                String touch_events = "";


                try {

                    //mLogger.logEntry("Accelerometer " + accellerometerSensor);
                    // writeToFile("Accelerometer: " + accellerometerSensor);
                    writeToFile_CA("Accelerometer " + accellerometerSensor);
                    //  mLogger.logEntry("Heart Rate " + heartRateSensor);
                    writeToFile_CA("Step Counter: " + stepCounterSensor);
                    // writeToFile("Heart Rate " + heartRateSensor);
                    writeToFile_CA("Heart Rate: " + heartRateSensor);
                    //  writeToFile("Gyro: " + gyroscopeSensor);
                    writeToFile_CA("Gyro: " + gyroscopeSensor);
                    //  mLogger.logEntry("Touch Events " + touch_events); // they will come from getInstance (a common share data holder class)

                    //  mLogger.logEntry("Time: " + SystemClock.currentThreadTimeMillis());
                    //  writeToFile("Time: " + SystemClock.currentThreadTimeMillis());
                    //  mLogger.logEntry("Date: " +  DateFormat.getDateTimeInstance().format(new Date()));
                    //   stopSensorListeners();      // it stops listening to reduce CPU usage/burden. Because listening costs alot
                    writeToFile_CA("Date: " + DateFormat.getDateTimeInstance().format(new Date()));
                    writeToFile_CA("TimeInMilSec: " + System.currentTimeMillis());
                    Log.d(TAG, "Accelerometer " + accellerometerSensor + "\n" + "Heart Rate " + heartRateSensor + "\n" + "Touch Events: " + touch_events + "\n" + DateFormat.getDateTimeInstance().format(new Date()));


                } catch (Exception e) {
                    Log.i(TAG, "Error occured while collecting sensors " + e);
                }

                // deletes 1 sec after sending and creates new log file
                if (justSent) {
                    //  Logger.deleteUploadFile(SensorService.this);
                    //   Logger.createLogFile(SensorService.this);
                    //   Logger.createLogFileToUpload(SensorService.this);
                    justSent = false;
                }

                //Send to phone in every 10 sec
                if (System.currentTimeMillis() - lastTime >= 5000) {
                    Log.d("Logger", "Get ave is called");
                    //    mLogger.getAverageData(SensorService.this);
                    Log.d("Logger", "Get ave is returnd");

                    try {
                        notifyUserToReportStress();
                    }catch (Exception e){
                        Log.d(TAG,"Notification: " +e);
                    }


                //    notifyUserToReportStress2();
                    lastTime = System.currentTimeMillis();
                    //   SendToPhone file_upload = new SendToPhone(SensorService.this);
                    //  file_upload.start();
                    justSent = true;
                }


                // stopSensorListeners();
                mHandler.postDelayed(mRefresh, 200);


            } catch (Exception e) {
                Log.i(TAG, "Error occured " + e);

            }
        }
    };


    private void notifyUserToReportStress() {
        // In this sample, we'll use the same text for the ticker and the expanded notification

        CharSequence text = getText(R.string.are_you_stressed);

        // Set the icon, scrolling text and timestamp

        Log.d(TAG, "It is in the notification");


        int notificationId = 001;
        Intent viewIntent = new Intent(this, MainActivity.class);
        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(this, 0, viewIntent, 0);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.red)
                        .setContentTitle(getText(R.string.stress_test))
                        .setContentText(text)
                        .setContentIntent(viewPendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(notificationId, notificationBuilder.build());


        //    alertGlobal = true;


    }

    private void notifyUserToReportStress2() {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.red)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");
        // Creates an explicit intent for an Activity in your app
        //  Intent resultIntent = new Intent(this, ResultActivity.class);

        // The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        int notificationId = 001;
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
      //  stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(notificationId, mBuilder.build());

    }
}

