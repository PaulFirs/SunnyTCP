package com.example.paulfirs.sunny.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.paulfirs.sunny.R;
import com.example.paulfirs.sunny.WorkActivity;

import java.util.Timer;
import java.util.TimerTask;



public class Sensors extends Fragment  {
    private final static String TAG = "myLogs";


    static TextView input_Temp;
    static ProgressBar temp;

    static Timer timer;
    static TextView got_CO2;
    static ProgressBar ppm;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.sensors, container, false);


        input_Temp = rootView.findViewById(R.id.input_Temp);
        temp = rootView.findViewById(R.id.temp);

        got_CO2 = rootView.findViewById(R.id.input_CO2);
        ppm = rootView.findViewById(R.id.ppm);
        return rootView;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "Fragment_Sensors onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "Fragment_Sensors onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Fragment_Sensors onResume");
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    final byte[] tx_data = new byte[WorkActivity.BUF_SIZE];
                    tx_data[0] = WorkActivity.GET_SENSORS;
                    WorkActivity.txByte(tx_data);
                }
            }, 0, 10000);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "Fragment_Sensors onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "Fragment_Sensors onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "Fragment_Sensors onDestroyView");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Fragment_Sensors onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (timer != null) {//сброс таймера по ответу
            timer.cancel();
            timer = null;
            Log.d(TAG, "Ответ пришел");
        }
        Log.d(TAG, "Fragment_Sensors onDetach");
    }



    public static void getData(byte[] rx_data) {

        input_Temp.setText(rx_data[1] + "");
        temp.setProgress((int)rx_data[1]);

        int co2 = (((int) rx_data[4] * 256) + (rx_data[5] & 0xFF));
        got_CO2.setText(String.valueOf(co2));
        ppm.setProgress(co2);
    }
}