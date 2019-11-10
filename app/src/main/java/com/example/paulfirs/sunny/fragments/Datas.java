package com.example.paulfirs.sunny.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.paulfirs.sunny.R;


public class Datas extends Fragment {
    private final static String TAG = "myLogs";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.datas, container, false);

        return rootView;
    }

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


}
