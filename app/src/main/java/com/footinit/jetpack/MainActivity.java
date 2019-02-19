package com.footinit.jetpack;

import android.os.Bundle;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @OnClick(R.id.startButton)
    void onStartPeriodicWork() {
        if (isWorkScheduled(NotificationWorker.TAG)) {
            Toast.makeText(getApplicationContext(),
                    "Work already scheduled", Toast.LENGTH_SHORT).show();
        } else {
            PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                    NotificationWorker.class, 15, TimeUnit.MINUTES)
                    .addTag(NotificationWorker.TAG)
                    .build();

            WorkManager.getInstance().enqueue(request);
            Toast.makeText(getApplicationContext(),
                    "Work scheduled", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.stopButton)
    void onStopPeriodicWork() {
        if (isWorkScheduled(NotificationWorker.TAG)) {
            WorkManager.getInstance().cancelAllWorkByTag(NotificationWorker.TAG);
            Toast.makeText(getApplicationContext(),
                    "Work cancelled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(),
                    "Work not present", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Stetho.initializeWithDefaults(getApplicationContext());
        ButterKnife.bind(this);
    }

    private boolean isWorkScheduled(String tag) {
        ListenableFuture<List<WorkInfo>> status = WorkManager.getInstance().getWorkInfosByTag(tag);

        try {
            boolean isRunning = false;
            List<WorkInfo> workList = status.get();

            for (WorkInfo workInfo : workList) {
                WorkInfo.State state = workInfo.getState();

                if (state == WorkInfo.State.RUNNING || state == WorkInfo.State.ENQUEUED) {
                    return isRunning = true;
                }
            }
            return isRunning;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}
