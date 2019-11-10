package com.example.paulfirs.sunny;


import android.content.Context;
import android.util.Log;

import com.example.paulfirs.sunny.fragments.For_Fragments;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Timer;

import static com.example.paulfirs.sunny.fragments.For_Fragments.byteArrayToHex;

public class ConnectedThread extends Thread {
    private String ip;
    private int port;
    private final static String TAG = "myLogs";
    private Socket socket = null;


    private static Timer timer;


    // while this is true, the server will continue running
    private boolean mRun = false;
    // used to send messages


    ConnectedThread(Context context, String ip, String port) {

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

                    /*timer = new Timer();//таймер ответного сообщения.
                    timer.schedule(new TimerTask(){
                        @Override
                        public void run() {
                            Log.d(TAG, "Ответ не пришел");
                            stopClient();
                        }
                    }, 700);*/

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

    }

    public void run() {

        mRun = true;

        try {
            timer = null;
            //create a socket to make the connection with the server
            socket = new Socket(InetAddress.getByName(ip), port);

            Log.d(TAG, "Запуск клиента");
            try {

                InputStream input = socket.getInputStream();
                //in this while the client listens for the messages sent by the server
                while (mRun) {
                    byte[] buf = new byte[11];
                    int readBytes = input.read(buf);
                    if(readBytes == -1) {
                        Log.d(TAG, "Закрытие сокета");
                        Log.d(TAG, "readBytes = " + readBytes);
                        stopClient();
                    }
                    else {
                        Log.d(TAG, "\tRX data: " + byteArrayToHex(buf) + ": " + "readBytes = " + readBytes);
                        if (timer != null) {//сброс таймера по ответу
                            timer.cancel();
                            timer = null;
                            Log.d(TAG, "Ответ пришел");
                        }
                        WorkActivity.h.obtainMessage(1, readBytes, -1, buf).sendToTarget(); /* Отправляем в очередь сообщенийHandler*/
                    }
                }

            } catch (IOException e) {
                Log.e("TCP", "S: Error", e);
            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.

                //socket.close();

                Log.d(TAG, "Потеряно соединение");

            }

        } catch (IOException e) {
            Log.e("TCP", "C: Error", e);
        }
    }
}
