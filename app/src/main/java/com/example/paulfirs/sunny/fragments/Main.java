package com.example.paulfirs.sunny.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.paulfirs.sunny.R;
import com.example.paulfirs.sunny.WorkActivity;


public class Main extends Fragment {
    private final static String TAG = "myLogs";
    static Spinner spinner;
    static TextView rxData;
    static String text = "";
    static EditText[] data = new EditText[7];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.remote, container, false);
        spinner = (Spinner) rootView.findViewById(R.id.comandlist);
        rxData = (TextView)rootView.findViewById(R.id.rxData);
        for(int i = 0; i<7; i++){
            data[i] = (EditText) rootView.findViewById(R.id.data1+i);
        }

        return rootView;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "Main onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "Main onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Main onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "Main onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "Main onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "Main onDestroyView");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        text = "";
        Log.d(TAG, "Main onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "Main onDetach");
    }

    public static void sendData(){
        byte[] tx_data = new byte[WorkActivity.BUF_SIZE];
        tx_data[0] = (byte) spinner.getSelectedItemId();

        //проверка на пустоту EditText-ов с байтами данных
        for(int i = 0; i<7; i++){
            if(data[i].getText().toString().isEmpty())//Если кто-то пуст,
                tx_data[i+1] = 0; //то присвоить в пакете данных на его место 0
            else {
                int value_dataN = Integer.valueOf(String.valueOf(data[i].getText()));//Если есть число в поле, записать его в переменную
                if (!(value_dataN > 255)) { //проверка, чтоб оно было 8 бит
                    if (!(value_dataN > 127))//устранение проблемы unsigned
                        tx_data[i + 1] = Byte.valueOf(String.valueOf(data[i].getText()));
                    else tx_data[i + 1] = (byte) (-256 + value_dataN);//Вычисление signed значения введеного байта
                }
                else {
                    data[i].setText("255");//Если введенное значение больше 255 (больше 1 байта) в поле вводится 255
                    tx_data[i + 1] = (byte) 255;//и присваивается значение 255 соответственному байту в пакете
                }
            }
        }
        WorkActivity.txByte(tx_data);
    }

    public static void getData(byte[] rx_data){//Отображение пришедших данных
        if(text.length()>1343)
            text = text.substring(0, 1343);
        text = For_Fragments.byteArrayToHex(rx_data) + "\n" + text;
        rxData.setText(text);
    }
}
