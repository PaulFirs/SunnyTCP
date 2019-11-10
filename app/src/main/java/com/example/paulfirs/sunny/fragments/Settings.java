package com.example.paulfirs.sunny.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.paulfirs.sunny.R;

import static com.example.paulfirs.sunny.WorkActivity.APP_PREFERENCES_IP;
import static com.example.paulfirs.sunny.WorkActivity.APP_PREFERENCES_PORT;
import static com.example.paulfirs.sunny.WorkActivity.mSettings;


public class Settings extends Fragment {
    private final static String TAG = "myLogs";

    EditText ip_ET;
    EditText port_ET;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.settings, container, false);

        ip_ET = rootView.findViewById(R.id.ip_ET);
        port_ET = rootView.findViewById(R.id.port_ET);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "Settings onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "Settings onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Settings onResume");
        if (mSettings.contains(APP_PREFERENCES_IP)) {
            // Получаем число из настроек
            ip_ET.setText(mSettings.getString(APP_PREFERENCES_IP, ""));
            // Выводим на экран данные из настроек
        }
        if (mSettings.contains(APP_PREFERENCES_PORT)) {
            // Получаем число из настроек
            port_ET.setText(mSettings.getString(APP_PREFERENCES_PORT, ""));
            // Выводим на экран данные из настроек
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "Settings onPause");
        // Запоминаем данные
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_IP, ip_ET.getText().toString());
        editor.putString(APP_PREFERENCES_PORT, port_ET.getText().toString());
        editor.apply();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "Settings onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "Settings onDestroyView");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Settings onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "Settings onDetach");
    }


}
