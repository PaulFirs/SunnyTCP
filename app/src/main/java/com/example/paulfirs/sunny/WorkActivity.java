package com.example.paulfirs.sunny;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.paulfirs.sunny.fragments.Alarm;
import com.example.paulfirs.sunny.fragments.Datas;
import com.example.paulfirs.sunny.fragments.For_Fragments;
import com.example.paulfirs.sunny.fragments.Graphics;
import com.example.paulfirs.sunny.fragments.Main;
import com.example.paulfirs.sunny.fragments.Sensors;
import com.example.paulfirs.sunny.fragments.Servo;
import com.example.paulfirs.sunny.fragments.Settings;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.paulfirs.sunny.ConnectedThread.socket;
import static com.example.paulfirs.sunny.fragments.Settings.APP_PREFERENCES_IP;
import static com.example.paulfirs.sunny.fragments.Settings.APP_PREFERENCES_PORT;
import static com.example.paulfirs.sunny.fragments.Settings.getSettings;
import static com.example.paulfirs.sunny.fragments.Settings.initSettings;
import static java.security.AccessController.getContext;

public class WorkActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {
    private final static String TAG = "myLogs";



    public final static byte BUF_SIZE 		= 9;
    static byte[] reTX_data;
    //Список отправляемых команд

    //public final static byte CHECK_CONNECT		= 0x00;
    public final static byte SWITCH_LIGHT 		= 0x00;
    public final static byte GET_TIME 			= 0x01;
    public final static byte SET_TIME 			= 0x02;
    public final static byte GET_ALARM			= 0x03;
    public final static byte SET_ALARM			= 0x04;
    public final static byte GET_SENSORS		= 0x05;

    public static byte PERIODIC_COMMAND	= (byte) 0xff;


    public final static byte WRITE_SECTOR_SD    = 0x04;
    public final static byte READ_SECTOR_SD     = 0x05;


    public final static byte ERROR              = 0x7F;

    /*
     * Errors
     */
    public final static byte NOT_FULL_DATA_MODLE        = 0x01;
    public final static byte NOT_EQUAL_CRC              = 0x02;
    public final static byte I2C_ERR                    = 0x03;

    public final static byte NOT_FULL_DATA_PHONE        = 0x41;


    static Handler hendlerRXdata;
    static Handler handlerShow;

    public static Context context;

    public static ConnectedThread MyThread = null;
    SwipeRefreshLayout mSwipeRefreshLayout;//индикация подключения

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scrl_panel);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (!isMyServiceRunning(ServiceConnect.class)) {
            Log.d(TAG, "Запуск сервиса из активности");
            //startService(new Intent(this, ServiceConnect.class));
        } else {
            Log.d(TAG, "Сервис уже существует");
            //stopService(intentService);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        context = getApplicationContext();

        //Это scrl_panel
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //индикация подключения
        //индикация подключения
        mSwipeRefreshLayout = findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        handlerShow = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {

                switch (msg.what){
                    case(0):
                        mSwipeRefreshLayout.setRefreshing(false);
                        break;
                    case(1):
                        mSwipeRefreshLayout.setRefreshing(true);
                        break;

                }
//                ProgressDialog progressDialog = new ProgressDialog(WorkActivity.this, R.style.MyTheme);
//                progressDialog.setCancelable(false);
//                progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
                //progressDialog.show();

            }
        };

//инициализация переменной для работы с настройками программы
        initSettings();
        String ip = getSettings(APP_PREFERENCES_IP);
        String port = getSettings(APP_PREFERENCES_PORT);

        if(!ip.isEmpty() || !port.isEmpty()) {
            MyThread = new ConnectedThread(ip, port);
            MyThread.start();


            CreateFragment(Main.class);
            navigationView.setCheckedItem(R.id.nav_monitor);
            setTitle(getString(R.string.nav_Monitor));

        }
        else{
            CreateFragment(Settings.class);
            navigationView.setCheckedItem(R.id.nav_settings);
            setTitle(getString(R.string.nav_Settings));
        }




        hendlerRXdata = new Handler() {
            public void handleMessage(android.os.Message msg) {

                byte[] rx_data = (byte[]) msg.obj;		//массив принятых данных

                try {
                    if (rx_data[1] == ERROR)//Recived error
                        notific_error(rx_data[2]);//notivication about error

                    if (getTitle().toString().equals("Monitor"))//если пользователь в окне отладки, то выводить все что пришло
                        Main.getData(rx_data);//вывести на экран данные
                    else//если пользователь не в окне отладки
                        //if (rx_data[BUF_SIZE - 1] == For_Fragments.CRC8(rx_data)) { //проверка на целостность данных

                            switch (rx_data[1]) {
                                case GET_SENSORS:
                                    int co2 = (((int) rx_data[5] * 256) + (rx_data[6] & 0xFF));
                                    writeFile(new SimpleDateFormat("HH:mm:ss").format(new Date()) + "-" + co2, "CO2 " + new SimpleDateFormat("dd.MM.yyyy").format(new Date()));
                                    writeFile(new SimpleDateFormat("HH:mm:ss").format(new Date()) + "-" + rx_data[2], "Temp " + new SimpleDateFormat("dd.MM.yyyy").format(new Date()));
                                    break;
                            }

                            switch (getTitle().toString()) {            //Определение фрагмента

                                case "Curtains":
                                    switch (rx_data[1]) {                                   //читаем первый принятый байт-команду
                                        case SET_TIME:
                                            byte[] get_time = new byte[WorkActivity.BUF_SIZE];
                                            get_time[1] = WorkActivity.GET_TIME;
                                            WorkActivity.txByte(get_time);
                                            break;

                                        case GET_TIME:                                      //телефон принимает время
                                            Alarm.synchronize_time(rx_data);     //функция преобразования времени в "богоподобный" вид (т.к. из модуля времени приходят не стандартые данные)
                                            break;


                                        case SET_ALARM:
                                            byte[] get_alarm = new byte[WorkActivity.BUF_SIZE];
                                            get_alarm[1] = WorkActivity.GET_ALARM;
                                            WorkActivity.txByte(get_alarm);
                                            break;

                                        case GET_ALARM:
                                            Alarm.data_alarm(rx_data);
                                            break;
                                    }
                                    break;
                                case "Sensors":
                                    switch (rx_data[1]) {
                                        case GET_SENSORS:
                                            Sensors.getData(rx_data);
                                            break;
                                    }
                                    break;
                            }
                        //}
                } catch (ArrayIndexOutOfBoundsException e) {}

            }
        };
    }



    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @Override
    protected void onDestroy() {
        Log.d(TAG, "WorkActivity onDestroy");
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_monitor) {
            CreateFragment(Main.class);
        } else if (id == R.id.nav_curtains) {
            CreateFragment(Alarm.class);
        } else if (id == R.id.nav_sensors) {
            CreateFragment(Sensors.class);
        } else if (id == R.id.nav_datafile) {
            CreateFragment(Graphics.class);
        } else if (id == R.id.nav_datas) {
            CreateFragment(Datas.class);
        } else if (id == R.id.nav_servo) {
            CreateFragment(Servo.class);
        }else if (id == R.id.nav_settings) {
            CreateFragment(Settings.class);
        }

        // Выделяем выбранный пункт меню в шторке
        item.setChecked(true);
        // Выводим выбранный пункт в заголовке
        setTitle(item.getTitle());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void CreateFragment(Class fragmentClass){
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Вставляем фрагмент, заменяя текущий фрагмент
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
    }

    //Button in Main
    public void sendData(View v){

        Log.d(TAG, "Notification");
        Main.sendData();
    }



    //Button in Alarm
    public void sendAlarm(View v) {
        Alarm.send_alarm((byte) 0x02);
    }

    public void switch_on_off(View v) {
        byte[] tx_data = new byte[BUF_SIZE];
        tx_data[1] = SWITCH_LIGHT;
        txByte(tx_data);
    }

    public void writeFile(String data, String fileName) {
        try {
            // отрываем поток для записи
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    openFileOutput(fileName, MODE_APPEND)));
            // пишем данные
            bw.write(data + "\n");
            // закрываем поток
            bw.close();
            Log.d(TAG, "Файл записан");
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void txByte(byte[] tx_data){
        tx_data[0] = For_Fragments.CRC8(tx_data);
        reTX_data = tx_data;
        try {
            if(socket.isConnected()) {
                MyThread.sendMessage(tx_data);
            }
        }
        catch(NullPointerException e){Toast.makeText(getAppContext(), "Нет соединения", Toast.LENGTH_LONG).show();}
    }

    public static void notific_error(byte err) {
        switch (err) {
            case NOT_FULL_DATA_MODLE:
                showToast("Пришел не полный пакет данных в модуль");
                break;
            case NOT_FULL_DATA_PHONE:
                showToast("Пришел не полный пакет данных в телефон");
                break;
            case NOT_EQUAL_CRC:
                showToast("Не совпал CRC в модуле");
                break;
            case I2C_ERR:
                showToast("Проблема в шине I2C");
                break;
        }
    }

    public static Context getAppContext() {
        return WorkActivity.context;
    }

    public static void showToast(String str){
        Toast.makeText(getAppContext(), str, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRefresh() {
        txByte(reTX_data);
        //MyThread.stopClient();
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
