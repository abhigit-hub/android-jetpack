package com.footinit.jetpack

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import butterknife.ButterKnife
import butterknife.OnClick
import com.facebook.stetho.Stetho
import com.footinit.jetpack.extensions.toast
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    @OnClick(R.id.startButton)
    internal fun onStartPeriodicWork() {
        if (isWorkScheduled(NotificationWorker.TAG)) {
            applicationContext.toast("Work already scheduled")
        } else {
            val request = PeriodicWorkRequest.Builder(
                    NotificationWorker::class.java, 15, TimeUnit.MINUTES)
                    .addTag(NotificationWorker.TAG)
                    .build()

            WorkManager.getInstance().enqueue(request)
            applicationContext.toast("Work scheduled")
        }
    }

    @OnClick(R.id.stopButton)
    internal fun onStopPeriodicWork() {
        if (isWorkScheduled(NotificationWorker.TAG)) {
            WorkManager.getInstance().cancelAllWorkByTag(NotificationWorker.TAG)
            applicationContext.toast("Work cancelled")
        } else {
            applicationContext.toast("Work not present")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Stetho.initializeWithDefaults(applicationContext)
        ButterKnife.bind(this@MainActivity)
    }

    private fun isWorkScheduled(tag: String): Boolean {
        val status: ListenableFuture<List<WorkInfo>> =
                WorkManager.getInstance().getWorkInfosByTag(tag)

        try {
            var isRunning = false
            val workList: List<WorkInfo> = status.get()

            workList.forEach { workInfo: WorkInfo ->
                val state: WorkInfo.State = workInfo.state

                if (state == WorkInfo.State.RUNNING || state == WorkInfo.State.ENQUEUED) {
                    isRunning = true
                    return isRunning
                }
            }
            return isRunning
        } catch (e: ExecutionException) {
            e.printStackTrace()
            return false
        } catch (e: InterruptedException) {
            e.printStackTrace()
            return false
        }
    }
}