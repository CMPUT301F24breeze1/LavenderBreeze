package com.example.myapplication.controller;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.myapplication.DeviceUtils;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotificationCheckService {
    private FirebaseFirestore db;
    private String deviceId;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private boolean isRunning = false;
    private static final long CHECK_INTERVAL = 2000; // 2 seconds
    private Long lastTimestamp = 0L;
    private Context context;
    private final ActivityResultLauncher<String> permissionLauncher;

    public NotificationCheckService(Context context, ActivityResultLauncher<String> permissionLauncher) {
        this.context = context;
        this.permissionLauncher = permissionLauncher;
    }
    public void onCreate() {
        db = FirebaseFirestore.getInstance();
        deviceId = DeviceUtils.getDeviceId(context);

    }

    public void startPollingForNotifications() {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    try {
                        checkForNewNotifications();
                        Thread.sleep(CHECK_INTERVAL);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        Log.e("NotificationService", "Polling interrupted", e);
                    }
                }
            }
        });
    }

    private void checkForNewNotifications() {

        db.collection("users")
                .document(deviceId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {


                    if (documentSnapshot.exists()) {
                        Long timestamp = documentSnapshot.getLong("timestamp");

                        if (timestamp != null && timestamp > lastTimestamp) {
                            String title = documentSnapshot.getString("title");
                            String message = documentSnapshot.getString("msg");
                            lastTimestamp = timestamp;
                            showLocalNotification(title, message);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("NotificationService", "Error checking notifications", e));
    }

    private void showLocalNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "test")
                .setSmallIcon(R.drawable.ic_notif)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = context.getString(R.string.app_name);
                String description = "Example Notification";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel("test", name, importance);
                channel.setDescription(description);
                notificationManager.createNotificationChannel(channel);

                notificationManager.notify(10, builder.build());
            }
        }
    }
}
