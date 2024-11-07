package com.example.myapplication.entrant;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.test.internal.runner.intent.IntentMonitorImpl;

import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.model.Event;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.lang.ref.Reference;

public class EntrantQrScan extends Fragment {

    //private Event event;

    public EntrantQrScan() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_entrant_qr_scan, container, false);

        Button eventList = view.findViewById(R.id.button_go_to_entrant_event_list);
        eventList.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_entrantQrScan_to_entrantEventsList)
        );

        Button scanQR = view.findViewById(R.id.scanQR);
        scanQR.setOnClickListener(v -> {
            ScanOptions options = new ScanOptions();
            options.setCaptureActivity(Camera.class);
            barLauncher.launch(options);
        });
        return view;
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result->{
        if (result.getContents() != null) {
            Log.d("QR", result.getContents());
            Log.d("QR", String.valueOf(result.getContents().length()));
            if (result.getContents().length() == 20) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("eventID", result.getContents());
                Navigation.findNavController(requireView()).navigate(R.id.action_entrantQrScan_to_entrantJoinPage, bundle);
            }
            else {
                Toast.makeText(requireContext(), "Invalid QR Code", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Log.d("QR", "No QR code");
        }
    });
}