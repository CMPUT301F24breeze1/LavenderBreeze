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

import com.example.myapplication.DeviceUtils;
import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;
//
public class PollingService extends Service {

    private static final String CHANNEL_ID = "polling_notification_channel";
    private static final long POLLING_INTERVAL = 2000;

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
        createNotificationChannel();
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

    private void createNotificationChannel() {
        Log.d("PollingService", "createNotificationChannel: Creating notification channel");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.app_name),  // Channel name visible to user
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            channel.setDescription("Notifications from polling Firestore");
            channel.enableLights(true);
            channel.setLightColor(android.graphics.Color.BLUE);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
                Log.d("PollingService", "createNotificationChannel: Channel created");
            }
        }
    }

    private void checkForNewNotifications() {
        Log.d("PollingService", "checkForNewNotifications: Polling Firestore for updates");
        db.collection("users")
                .document(deviceId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Log.d("PollingService", "checkForNewNotifications: Successfully fetched document");
                    if (documentSnapshot.exists()) {
                        Long timestamp = documentSnapshot.getLong("timestamp");
                        Log.d("PollingService", "checkForNewNotifications: Document timestamp = " + timestamp);

                        if (timestamp != null && timestamp > lastTimestamp) {
                            String title = documentSnapshot.getString("title");
                            String message = documentSnapshot.getString("msg");
                            lastTimestamp = timestamp;
                            showLocalNotification(title, message);
                        } else {
                            Log.d("PollingService", "checkForNewNotifications: No new notifications");
                        }
                    } else {
                        Log.d("PollingService", "checkForNewNotifications: Document does not exist");
                    }
                })
                .addOnFailureListener(e -> Log.e("PollingService", "checkForNewNotifications: Error checking notifications", e));
    }

    private void showLocalNotification(String title, String message) {
        Log.d("PollingService", "showLocalNotification: Preparing to show notification");
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            Log.w("PollingService", "showLocalNotification: Notification permission not granted");
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notifications from polling Firestore");
            notificationManager.createNotificationChannel(channel);
            Log.d("PollingService", "showLocalNotification: Notification channel created");
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notif)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        notificationManager.notify(10, builder.build());
        Log.d("PollingService", "showLocalNotification: Notification displayed");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // Not a bound service
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("PollingService", "onDestroy: Service is being destroyed");
        handler.removeCallbacks(pollingRunnable); // Stop the polling runnable when service stops
    }
}
