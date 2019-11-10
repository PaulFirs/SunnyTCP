package com.example.paulfirs.sunny.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.example.paulfirs.sunny.R;
import com.example.paulfirs.sunny.WorkActivity;


public class Servo extends Fragment {
    private final static String TAG = "myLogs";
    SeekBar servo;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.servo_frag, container, false);

        servo = (SeekBar) rootView.findViewById(R.id.move_servo);
        servo.setOnSeekBarChangeListener(seekBarChangeListener);
        move(0);
        return rootView;
    }
    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            move(servo.getProgress());
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "Servo onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "Servo onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Servo onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "Servo onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "Servo onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "Servo onDestroyView");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Servo onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "Servo onDetach");
    }

    public void move(int rad) {
        byte[] tx_data = new byte[WorkActivity.BUF_SIZE];

        tx_data[0] = 1;
        tx_data[1] = (byte) (rad + 30);
        if(rad%4 == 0) {
            WorkActivity.txByte(tx_data);
        }
    }

}
