package com.example.paulfirs.sunny;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ReceiverServiceConnect extends BroadcastReceiver {
    private final static String TAG = "myLogs";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Запуск сервиса из ресивера");
        context.startService(new Intent(context, ServiceConnect.class));
    }
}
