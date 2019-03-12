package com.footinit.jetpack

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.*

class NotificationWorker(context: Context, workerParams: WorkerParameters) :
        Worker(context, workerParams) {

    companion object {
        val TAG = NotificationWorker::class.java.simpleName
    }

    override fun doWork(): Result {
        displayNotification()
        return Result.success()
    }

    private fun displayNotification() {
        val notificationManager = applicationContext
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(TAG, TAG, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(applicationContext, TAG)
                .setContentTitle("WorkManager")
                .setContentText("Periodic Request" + Random().nextInt(500))
                .setSmallIcon(R.mipmap.ic_launcher)

        notificationManager.notify(Random().nextInt(1000), builder.build())
    }
}