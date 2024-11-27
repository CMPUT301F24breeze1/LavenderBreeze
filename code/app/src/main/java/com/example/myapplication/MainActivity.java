package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.controller.DeviceUtils;
import com.example.myapplication.controller.PermissionHelper;
import com.example.myapplication.controller.PollingService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private FusedLocationProviderClient fusedLocationClient;
    private PermissionHelper permissionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Clear notification fields for the current user in Firestore
        String userId = DeviceUtils.getDeviceId(this);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(userId)
                .update("msg", null, "title", null, "timestamp", null)
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "User notification fields cleared successfully."))
                .addOnFailureListener(e -> Log.e("Firebase", "Failed to clear user notification fields.", e));

        // Check and request permissions
        permissionHelper = new PermissionHelper(this);

        // Check and request permissions
        List<String> ungrantedPermissions = permissionHelper.getUngrantedPermissions();

        if (!ungrantedPermissions.isEmpty()) {
            permissionHelper.requestPermissions(ungrantedPermissions, "ClJvtRS4z8Vs3lBBYei3", db);
        } else {
            // All permissions granted, proceed with location
            permissionHelper.fetchAndStoreLocation(db, "ClJvtRS4z8Vs3lBBYei3");
        }

        // Start Polling Service
        Intent intent = new Intent(MainActivity.this, PollingService.class);
        startService(intent);
        Log.d("ServiceStatus", "Attempting to start PollingService");

        // Handle system bar insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
