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
    private Button rankStress;
    private Button catchCollor;
    private Button touchMultiple;

    private Button stress1;
    private Button stress2;
    private Button stress3;
    private Button stress4;
    private Button stress5;

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

        stress1 = (Button) this.findViewById(R.id.stress1Id);
        if (stress1 != null) {
            stress1.setOnClickListener(stress1Listener);
        }

        stress2 = (Button) this.findViewById(R.id.stress2Id);
        if (stress2 != null) {
            stress2.setOnClickListener(stress2Listener);
        }

        stress3 = (Button) this.findViewById(R.id.stress3Id);
        if (stress3 != null) {
            stress3.setOnClickListener(stress3Listener);
        }

        stress4 = (Button) this.findViewById(R.id.stress4Id);
        if (stress4 != null) {
            stress4.setOnClickListener(stress4Listener);
        }

        stress5 = (Button) this.findViewById(R.id.stress5Id);
        if (stress5 != null) {
            stress5.setOnClickListener(stress5Listener);
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




    private View.OnClickListener stress1Listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DataHolder.getInstance().setStressRank(1);
            Toast.makeText(MainActivity.this,"Thanks for your ranking(1)",Toast.LENGTH_SHORT).show();

        }
    };

    private View.OnClickListener stress2Listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DataHolder.getInstance().setStressRank(2);
            Toast.makeText(MainActivity.this,"Thanks for your ranking(2)",Toast.LENGTH_SHORT).show();

        }
    };

    private View.OnClickListener stress3Listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DataHolder.getInstance().setStressRank(3);
            Toast.makeText(MainActivity.this,"Thanks for your ranking(3)",Toast.LENGTH_SHORT).show();


        }
    };

    private View.OnClickListener stress4Listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DataHolder.getInstance().setStressRank(4);
            Toast.makeText(MainActivity.this,"Thanks for your ranking(4)",Toast.LENGTH_SHORT).show();


        }
    };

    private View.OnClickListener stress5Listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DataHolder.getInstance().setStressRank(5);
            Toast.makeText(MainActivity.this,"Thanks for your ranking(5)",Toast.LENGTH_SHORT).show();


        }
    };


}
