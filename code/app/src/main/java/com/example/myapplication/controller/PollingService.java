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

/**
 * A background service that periodically polls a Firestore database for new notifications
 * and displays them as local notifications to the user.
 */
public class PollingService extends Service {

    /**
     * The ID for the notification channel used by this service.
     */
    private static final String CHANNEL_ID = "polling_notification_channel";

    /**
     * The interval in milliseconds between each poll for new notifications.
     */
    private static final long POLLING_INTERVAL = 5000;

    /**
     * Firebase Firestore instance used for database operations.
     */
    private FirebaseFirestore db;

    /**
     * The unique identifier of the device, used to query the database.
     */
    private String deviceId;

    /**
     * The timestamp of the last notification processed, used to avoid duplicate notifications.
     */
    private Long lastTimestamp = 0L;

    /**
     * The handler used to schedule periodic polling tasks.
     */
    private Handler handler;

    /**
     * Called when the service is created. Initializes Firestore, retrieves the device ID,
     * and sets up the handler for polling tasks.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("PollingService", "onCreate: Service created");
        db = FirebaseFirestore.getInstance();
        deviceId = DeviceUtils.getDeviceId(this);
        handler = new Handler();
        createNotificationChannel(this);
    }

    /**
     * A runnable that polls the Firestore database for new notifications and schedules
     * itself to run again after the specified interval.
     */
    private final Runnable pollingRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d("PollingService", "pollingRunnable: Checking for new notifications");
            checkForNewNotifications();
            handler.postDelayed(this, POLLING_INTERVAL); // Schedule next run
        }
    };

    /**
     * Called when the service is started. Begins the polling process by scheduling the polling runnable.
     *
     * @param intent  The intent that started the service.
     * @param flags   Additional data about the start request.
     * @param startId A unique ID representing this specific request to start the service.
     * @return The desired behavior if the service is killed by the system. START_STICKY keeps the service running.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("PollingService", "onStartCommand: Service is starting");
        handler.post(pollingRunnable); // Start the polling runnable
        return START_STICKY; // Keep the service running
    }

    /**
     * Creates a notification channel required for displaying notifications on Android O and above.
     *
     * @param context The application context used to create the channel.
     */
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

    /**
     * Checks the Firestore database for new notifications. If a new notification is found,
     * it updates the lastTimestamp and displays the notification locally.
     */
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

    /**
     * Displays a local notification with the given title and message.
     *
     * @param title   The title of the notification.
     * @param message The content of the notification.
     */
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

    /**
     * Called when a client binds to the service. This service does not support binding.
     *
     * @param intent The intent used to bind to the service.
     * @return Always returns null as this is not a bound service.
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // Not a bound service
    }

    /**
     * Called when the service is destroyed. Stops the polling process by removing callbacks to the handler.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(pollingRunnable); // Stop the polling runnable when service stops
    }
}
