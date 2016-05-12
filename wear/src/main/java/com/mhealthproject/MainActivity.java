package com.mhealthproject;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Bundle;
//import android.support.wearable.activity.Wea;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.wearable.view.BoxInsetLayout;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {


    private Button startService;
    private Button stopService;
    private Button movingBall;
    private Button catchCollor;
    private Button touchMultiple;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        setContentView(R.layout.activity_main);
        //setAmbientEnabled();

        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);

        }


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BODY_SENSORS) != PackageManager.PERMISSION_GRANTED) {



            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.BODY_SENSORS},
                    1);
        }






        startService = (Button) this.findViewById(R.id.startServiceId);
        if (startService != null) {
            startService.setOnClickListener(startServiceListener);
        }

        stopService = (Button) this.findViewById(R.id.stopServiceId);
        if (stopService != null) {
            stopService.setOnClickListener(stopServiceListener);
        }

        movingBall = (Button) this.findViewById(R.id.movingBallId);
        if (movingBall != null) {
            movingBall.setOnClickListener(movingBallListener);
        }

        catchCollor = (Button) this.findViewById(R.id.catchCollorId);
        if (catchCollor != null) {
            catchCollor.setOnClickListener(catchCollorListener);
        }

        touchMultiple = (Button) this.findViewById(R.id.touchMultipleId);
        if (touchMultiple != null) {
            touchMultiple.setOnClickListener(touchMultipleListener);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // onStop();
                    // finish();
                    Toast.makeText(this,"Grant has been given",Toast.LENGTH_SHORT).show();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }



    private View.OnClickListener startServiceListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this,SensorService.class);
            MainActivity.this.startService(intent);
        }
    };

    private View.OnClickListener stopServiceListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this,SensorService.class);
            MainActivity.this.stopService(intent);

        }
    };




    private View.OnClickListener movingBallListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this,MovingBallActivity.class);
            MainActivity.this.startActivity(intent);

        }
    };

    private View.OnClickListener catchCollorListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this,CatchColorActivity.class);
            MainActivity.this.startActivity(intent);

        }
    };

    private View.OnClickListener touchMultipleListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this,TouchMultipleActivity.class);
            MainActivity.this.startActivity(intent);

        }
    };
}
