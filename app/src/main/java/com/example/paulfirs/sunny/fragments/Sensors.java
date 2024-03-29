package com.example.paulfirs.sunny.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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

import static com.example.paulfirs.sunny.WorkActivity.GET_SENSORS;
import static com.example.paulfirs.sunny.WorkActivity.PERIODIC_COMMAND;
import static com.example.paulfirs.sunny.WorkActivity.getAppContext;


public class Sensors extends Fragment  {
    private final static String TAG = "myLogs";


    static TextView input_Temp;
    static ProgressBar temp;

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
        got_CO2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] mCatsName ={"2k", "5k", "abc"};

                AlertDialog.Builder builder;

                builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Settings MH-Z19"); // заголовок для диалога

                builder.setItems(mCatsName, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {

                        final byte[] tx_data = new byte[WorkActivity.BUF_SIZE];
                        tx_data[1] = GET_SENSORS;
                        tx_data[2] = (byte) (item + 1);
                        WorkActivity.txByte(tx_data);
                    }
                });
                builder.setCancelable(false);
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
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
        final byte[] tx_data = new byte[WorkActivity.BUF_SIZE];
        tx_data[1] = GET_SENSORS;
        WorkActivity.txByte(tx_data);
        PERIODIC_COMMAND = GET_SENSORS;
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

        PERIODIC_COMMAND = (byte) 0xff;
        Log.d(TAG, "Fragment_Sensors onDetach");
    }



    public static void getData(byte[] rx_data) {

        int co2 = (((int) rx_data[5] * 256) + (rx_data[6] & 0xFF));

        if(co2 != 0) {
            got_CO2.setText(String.valueOf(co2));
            ppm.setProgress(co2);

            input_Temp.setText(rx_data[2] + "");
            temp.setProgress((int) rx_data[2]);
        }
    }
}