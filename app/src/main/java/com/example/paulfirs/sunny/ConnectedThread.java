package com.example.paulfirs.sunny;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Looper;
import android.util.Log;

import com.example.paulfirs.sunny.fragments.For_Fragments;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.paulfirs.sunny.WorkActivity.PERIODIC_COMMAND;
import static com.example.paulfirs.sunny.WorkActivity.getAppContext;
import static com.example.paulfirs.sunny.fragments.For_Fragments.byteArrayToHex;

public class ConnectedThread extends Thread {
    private String ip;
    private int port;
    private final static String TAG = "myLogs";
    static Socket socket = null;


    private static Timer timer;
    private static Timer timerResp;

    // while this is true, the server will continue running
    private boolean mRun = false;
    // used to send messages

    ConnectedThread(String ip, String port) {
        this.ip = ip;
        this.port = Integer.parseInt(port);
    }

    /**
     * Sends the message entered by client to the server
     */
    void sendMessage(final byte[] buf) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {

                    while (timer != null);

                    socket.getOutputStream().write(buf);
                    Log.d(TAG, "\tTX data: " + For_Fragments.byteArrayToHex(buf));
                    stopTimerResp();
                    timer = new Timer();//таймер ответного сообщения.
                    timer.schedule(new TimerTask(){
                        @Override
                        public void run() {
                            Log.d(TAG, "Ответ не пришел");
                            stopClient();
                        }
                    }, 700);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    /**
     * Close the connection and release the members
     */
    public void stopClient() {
        mRun = false;
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        stopTimerResp();
        Log.d(TAG, "Отключено");
    }

    public void run() {
        while (true) {
            mRun = true;

            try {
                timer = null;
                //create a socket to make the connection with the server


                Log.d(TAG, "Подключение...");
                socket = new Socket(InetAddress.getByName(ip), port);

//                ProgressDialog progressDialog = new ProgressDialog(getAppContext(), R.style.MyTheme);
//                progressDialog.setCancelable(false);
//                progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
//                progressDialog.show();

                Log.d(TAG, "Подключено");

                setTimerResp();
                try {

                    InputStream input = socket.getInputStream();
                    //in this while the client listens for the messages sent by the server
                    while (mRun) {
                        byte[] buf = new byte[12];
                        int readBytes = input.read(buf);
                        if (readBytes == -1) {
                            Log.d(TAG, "Закрытие сокета");
                            Log.d(TAG, "readBytes = " + readBytes);
                            stopClient();
                        } else {
                            Log.d(TAG, "\tRX data: " + byteArrayToHex(buf) + ": " + "readBytes = " + readBytes);
                            if (timer != null) {//сброс таймера по ответу
                                timer.cancel();
                                timer = null;
                                Log.d(TAG, "Ответ пришел");
                            }
                            setTimerResp();

                            WorkActivity.h.obtainMessage(1, readBytes, -1, buf).sendToTarget(); /* Отправляем в очередь сообщенийHandler*/
                        }
                    }

                }catch (IOException e) {
                    Log.d(TAG, e.toString());
                    Log.e("TCP", "S: Error", e);
                } finally {
                    //the socket must be closed. It is not possible to reconnect to this socket
                    // after it is closed, which means a new socket instance has to be created.
                    if (mRun)
                        stopClient();
                }

            } catch (IOException e) {
                Log.d(TAG, e.toString());
            }
        }
    }


    void setTimerResp(){
        timerResp = new Timer();//таймер ответного сообщения.
        timerResp.schedule(new TimerTask(){
            @Override
            public void run() {
                Log.d(TAG, "Отправка поддерживающего сообщения");

                byte[] tx_data = new byte[WorkActivity.BUF_SIZE];
                tx_data[1] = PERIODIC_COMMAND;
                sendMessage(tx_data);
            }
        }, 15000);
    }
    void stopTimerResp(){
        if (timerResp != null) {//сброс таймера по ответу
            timerResp.cancel();
            timerResp = null;
            Log.d(TAG, "Отключение timerResp при отправке полезной команды");
        }
    }
}
