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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.example.myapplication.controller.DeviceUtils;
import com.example.myapplication.controller.PollingService;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private static final int NOTIF_CODE = 99;
    // Ensure your ActivityResultLauncher is registered in a context that has access to an Activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        String userId = DeviceUtils.getDeviceId(this); // Replace with logic to get the current user ID
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .document(userId)
                .update("msg", null, "title", null, "timestamp", null)
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "User notification fields cleared successfully."))
                .addOnFailureListener(e -> Log.e("Firebase", "Failed to clear user notification fields.", e));


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIF_CODE);
        }

        Intent intent = new Intent(MainActivity.this, PollingService.class);
        startService(intent);

        Log.d("ServiceStatus", "Attempting to start PollingService");
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIF_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "ALLOWED", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "DENIED", Toast.LENGTH_LONG).show();
            }
        }
    }
}