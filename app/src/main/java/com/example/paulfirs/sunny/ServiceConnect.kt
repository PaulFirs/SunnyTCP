package com.example.paulfirs.sunny

import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.provider.MediaStore
import android.support.v4.app.NotificationCompat
import android.util.Log


class ServiceConnect : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    var runnable:Runnable? = null
    val TAG = "myLogs"

    override fun onCreate() {
        ShowLog("Service onCreate")


        try {
            val handler:Handler = Handler()
            runnable = Runnable {

                Log.d(TAG, "Runnable")
                handler.postDelayed(runnable, 3000)
                Log.d(TAG, "3000")
                showSmallNotification()
            }

            handler.postDelayed(runnable, 5000)

            Log.d(TAG, "!!")
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }


    private fun showSmallNotification() {

        val CHANNEL_ID = "Cat channel"
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(com.example.paulfirs.sunny.R.mipmap.ic_launcher_round)
                .setContentTitle("Это твой дом!")
                .setContentText("Все в порядке, зацени!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(Uri.withAppendedPath(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, "6"))
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(com.example.paulfirs.sunny.R.string.app_name)
            val description = getString(com.example.paulfirs.sunny.R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationManager:NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager//NotificationManagerCompat.from(application)

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(101, builder.build())
    }


    override fun onDestroy() {
        ShowLog("Service onDestroy")
        super.onCreate()
    }

    fun ShowLog(message: String){
        Log.d(TAG, message)
    }
}
