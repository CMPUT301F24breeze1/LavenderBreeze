package com.example.myapplication.controller;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;

//
public class PollingService extends Service {

    private static final String CHANNEL_ID = "polling_notification_channel";
    private static final long POLLING_INTERVAL = 5000;

    private FirebaseFirestore db;
    private String deviceId;
    private Long lastTimestamp = 0L;
    private Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("PollingService", "onCreate: Service created");
        db = FirebaseFirestore.getInstance();
        deviceId = DeviceUtils.getDeviceId(this);
        handler = new Handler();
        createNotificationChannel(this);
    }

    private Runnable pollingRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d("PollingService", "pollingRunnable: Checking for new notifications");
            checkForNewNotifications();
            handler.postDelayed(this, POLLING_INTERVAL); // Schedule next run
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("PollingService", "onStartCommand: Service is starting");
        handler.post(pollingRunnable); // Start the polling runnable
        return START_STICKY; // Keep the service running
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null && notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        "Polling service",
                        NotificationManager.IMPORTANCE_DEFAULT
                );
                channel.setDescription("Notifications from polling Firestore");
                channel.enableLights(true);
                channel.setLightColor(android.graphics.Color.BLUE);
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void checkForNewNotifications() {
        db.collection("users")
                .document(deviceId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long timestamp = documentSnapshot.getLong("timestamp");
                        Boolean toggleNotif = documentSnapshot.getBoolean("toggleNotif");

                        if (timestamp != null && timestamp > lastTimestamp && toggleNotif != null && toggleNotif) {
                            String title = documentSnapshot.getString("title");
                            String message = documentSnapshot.getString("msg");
                            lastTimestamp = timestamp;
                            showLocalNotification(title, message);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("PollingService", "checkForNewNotifications: Error checking notifications", e));
    }

    private void showLocalNotification(String title, String message) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notif)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManagerCompat.notify(10, builder.build());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // Not a bound service
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(pollingRunnable); // Stop the polling runnable when service stops
    }
}
