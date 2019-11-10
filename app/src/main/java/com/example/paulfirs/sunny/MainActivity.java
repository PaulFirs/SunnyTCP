package com.example.paulfirs.sunny;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {


    public static Boolean DEMO_VERSION		    = true;

    public static ConnectedThread MyThread = null;

    CheckBox demo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_wnd);
        demo = (CheckBox) findViewById(R.id.demo);
    }

    public void onResume() {
        super.onResume();
        //Log.d(TAG, "***onResume***");
        try {
            //MyThread.cancel();
        }
        catch (NullPointerException e){}

    }



    public void connect(View v){
        if (demo.isChecked()){
            DEMO_VERSION = true;
            Intent activity_Work = new Intent(this, WorkActivity.class);
            this.startActivity(activity_Work);
        }
        else {
            DEMO_VERSION = false;

            EditText ip_ET = (EditText) findViewById(R.id.ip_ET);
            EditText port_ET = (EditText) findViewById(R.id.port_ET);
            MyThread = new ConnectedThread(MainActivity.this, ip_ET.getText().toString(), port_ET.getText().toString());
            MyThread.start();
        }
    }
}
