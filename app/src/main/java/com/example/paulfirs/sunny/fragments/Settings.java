package com.example.paulfirs.sunny.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.paulfirs.sunny.ConnectedThread;
import com.example.paulfirs.sunny.R;

import static com.example.paulfirs.sunny.WorkActivity.MyThread;
import static com.example.paulfirs.sunny.WorkActivity.getAppContext;


public class Settings extends Fragment {
    private final static String TAG = "myLogs";


//Настройки программы

    public static final String APP_PREFERENCES = "mysettings";// имя файла настроек
    public static final String APP_PREFERENCES_IP = "ip"; // название настройки
    public static final String APP_PREFERENCES_PORT= "port"; // название настройки
    public static SharedPreferences mSettings;//переменная для работы с файлом настройки

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
        ip_ET.setText(getSettings(APP_PREFERENCES_IP));
        port_ET.setText(getSettings(APP_PREFERENCES_PORT));
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "Settings onPause");
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
        // Запоминаем данные
        String ip = ip_ET.getText().toString();
        setSettings(APP_PREFERENCES_IP, ip);
        String port = port_ET.getText().toString();
        setSettings(APP_PREFERENCES_PORT, port);

        if(MyThread == null){
            MyThread = new ConnectedThread(ip, port);
            MyThread.start();
        }
        else{
            MyThread.setIP_setPORT(ip, port);
            MyThread.stopClient();
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "Settings onDetach");
    }

    public static void setSettings(String preference, String data){
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(preference, data);
        editor.apply();
    }

    public static void initSettings(){
        mSettings = getAppContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public static String getSettings(String preference){
        if (mSettings.contains(preference)) {
            // Получаем число из настроек
            return mSettings.getString(preference, "");
            // Выводим на экран данные из настроек
        }
        return "";
    }
}
