package com.example.myapplication.controller;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionHelper {
    public static final int PERMISSION_REQUEST_CODE = 100;

    private final Activity activity;
    private final FusedLocationProviderClient fusedLocationClient;

    public PermissionHelper(Activity activity) {
        this.activity = activity;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
    }

    /**
     * Checks if the required permissions are granted.
     *
     * @return List of permissions that are not granted
     */
    public List<String> getUngrantedPermissions() {
        List<String> permissionsNeeded = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS);
        }

        return permissionsNeeded;
    }

    /**
     * Requests the permissions that are not granted.
     */
    public void requestPermission(List<String> permissions) {
        if (!permissions.isEmpty()) {

            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
            Log.d("test", "requestPermissions: ");
        }
    }

    /**
     * Handles the result of the permission request.
     */
    public void handlePermissionResult(String[] permissions, int[] grantResults,
                                       PermissionResultCallback callback) {
        boolean locationGranted = false;
        boolean notificationGranted = false;

        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permission)) {
                    locationGranted = true;
                } else if (Manifest.permission.POST_NOTIFICATIONS.equals(permission)) {
                    notificationGranted = true;
                }
            }
        }

        callback.onPermissionsResult(locationGranted, notificationGranted);
    }

    /**
     * Fetches the user's current location and stores it in Firebase Firestore.
     */
    public void fetchAndStoreLocation(FirebaseFirestore firestore, String eventId, String userId) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(activity, "Location permission is not granted.", Toast.LENGTH_SHORT).show();
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        List<Double> locationData = Arrays.asList(latitude, longitude);

                        firestore.collection("events").document(eventId)
                                .update("coordinates." + userId, locationData)
                                .addOnSuccessListener(documentReference ->
                                        Toast.makeText(activity, "Location saved successfully!", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e ->
                                        Toast.makeText(activity, "Failed to save location: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(activity, "Unable to retrieve location.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Callback interface to handle the result of permissions.
     */
    public interface PermissionResultCallback {
        void onPermissionsResult(boolean locationGranted, boolean notificationGranted);
    }
}
