package com.example.paulfirs.sunny.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.paulfirs.sunny.R;
import com.example.paulfirs.sunny.WorkActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;



public class Alarm extends Fragment implements CompoundButton.OnCheckedChangeListener {
    private final static String TAG = "myLogs";
    private static TimePicker time_Alarm;

    static CheckBox switch_chan;
    static CheckBox switch_sound;
    static CheckBox switch_curtains;
    static CheckBox switch_window;


    static TextView got_Alarm;

    static Switch on_alarm;

    public static boolean startUpApp = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.alarmer, container, false);
        time_Alarm = rootView.findViewById(R.id.time_Alarm);
        Calendar now = Calendar.getInstance();
        time_Alarm.setIs24HourView(true);
        time_Alarm.setCurrentHour(now.get(Calendar.HOUR_OF_DAY));
        time_Alarm.setCurrentMinute(now.get(Calendar.MINUTE));



        switch_chan = rootView.findViewById(R.id.switch_Chan);
        switch_sound = rootView.findViewById(R.id.switch_Sound);
        switch_curtains = rootView.findViewById(R.id.switch_Curtains);
        switch_window = rootView.findViewById(R.id.switch_Window);


        got_Alarm = rootView.findViewById(R.id.got_Alarm);    //поле для отображения времени будильника
        on_alarm = rootView.findViewById(R.id.on_Alarm);
        on_alarm.setOnCheckedChangeListener(this);
        return rootView;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "Alarm onActivityCreated");
    }

    @Override
    public void onStart() {


        super.onStart();
        Log.d(TAG, "Alarm onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        byte[] tx_data = new byte[WorkActivity.BUF_SIZE];

        tx_data[0] = WorkActivity.GET_TIME;//При включении фрагмента отправляется запрос на все данные
        WorkActivity.txByte(tx_data);

        Log.d(TAG, "Alarm onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "Alarm onPause");

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "Alarm onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "Alarm onDestroyView");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        startUpApp = true;
        Log.d(TAG, "Alarm onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "Alarm onDetach");
    }

    public static String normalTime(byte[] badTime){
        String normal = "";
        for(int i = 2; i!=0; i--) {
            byte full = badTime[i];
            byte high = (byte) ((full >> 4) & 0x0F);
            byte low = (byte) (full & 0x0F);
            normal = ":" + high + low + normal;
        }
        return normal.substring(1);
    }

    public static byte[] timeForDS3231(int time_phone){
        byte[] time_DS3231 = new byte[3];
        /*
        mass time_DS3231:
        0 element - hours
        1 element - minutes
        2 element - seconds
         */
        for(int i = 0; i < 3; i++) {
            byte high = (byte) ((time_phone>>(8*i))&0xFF);
            time_DS3231[2-i] = high;
        }
        return time_DS3231;
    }

    public static void synchronize_time(byte[] badTime) {
        String time = normalTime(badTime);
        byte[] tx_data = new byte[WorkActivity.BUF_SIZE];
        SimpleDateFormat timePhone = new SimpleDateFormat("HH:mm");
        Log.d(TAG, "time: in Modle, in Phone: " + time.substring(0, 5) + ", " + timePhone.format(new Date()));
        if(Objects.equals(time.substring(0, 5), timePhone.format(new Date()))){
            Log.d(TAG, "Время синхронизированно");
            startUpApp = true;
            tx_data[0] = WorkActivity.GET_ALARM;
            WorkActivity.txByte(tx_data);
        }
        else{
            //WorkActivity.showToast("Повторная синхронизация");
            SimpleDateFormat dateToDS3231 = new SimpleDateFormat("HHmmss");

            int date = Integer.parseInt(dateToDS3231.format(new Date()), 16);
            byte[] time_DS3231 = timeForDS3231(date);
            tx_data[0] = WorkActivity.SET_TIME;
            tx_data[1] = time_DS3231[0];
            tx_data[2] = time_DS3231[1];
            tx_data[3] = time_DS3231[2];
            WorkActivity.txByte(tx_data);
        }

    }

    public static void send_alarm(byte status_alarm) {
        byte[] tx_data = new byte[WorkActivity.BUF_SIZE];
        int hours = time_Alarm.getCurrentHour();
        int minutes = time_Alarm.getCurrentMinute();

        String str_alarm;
        if(hours < 10)
            if(minutes < 10)
                str_alarm = "0" + hours + "0" + minutes + "00";
            else
                str_alarm = "0" + hours + minutes + "00";
        else if(minutes < 10)
            str_alarm = hours + "0" + minutes + "00";
        else
            str_alarm = String.valueOf(hours) + String.valueOf(minutes) + "00";

        //Log.d(TAG, "***SEND data: " + str_alarm + "***");
        byte[] alarm_DS3231 = timeForDS3231(Integer.parseInt(str_alarm, 16));
        tx_data[0] = WorkActivity.SET_ALARM;
        tx_data[1] = alarm_DS3231[0];
        tx_data[2] = alarm_DS3231[1];
        tx_data[3] = alarm_DS3231[2];
        tx_data[4] = status_alarm;

        tx_data[5] = (switch_chan.isChecked())? (byte) (tx_data[5] | 0x01): tx_data[5];
        tx_data[5] = (switch_sound.isChecked())?  (byte) (tx_data[5] | 0x02): tx_data[5];
        tx_data[5] = (switch_curtains.isChecked())? (byte) (tx_data[5] | 0x04): tx_data[5];
        tx_data[5] = (switch_window.isChecked())? (byte) (tx_data[5] | 0x08): tx_data[5];

        WorkActivity.txByte(tx_data);
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        byte stateAlarm = 0;
        if (on_alarm.isChecked()) {
            stateAlarm = 0x01;
        }
        if(!startUpApp)
            send_alarm(stateAlarm);
    }

    public static void data_alarm(byte[] rx_data){
        if (rx_data[4] == 0x00)
            on_alarm.setChecked(false);
        else
            on_alarm.setChecked(true);
        startUpApp = false;
        got_Alarm.setText(normalTime(rx_data));


        switch_chan.setChecked(((rx_data[5] & 0x01) == 0x01)? true: false);
        switch_sound.setChecked(((rx_data[5] & 0x02) == 0x02)? true: false);
        switch_curtains.setChecked(((rx_data[5] & 0x04) == 0x04)? true: false);
        switch_window.setChecked(((rx_data[5] & 0x08) == 0x08)? true: false);
        got_Alarm.setText(normalTime(rx_data));
    }

}
