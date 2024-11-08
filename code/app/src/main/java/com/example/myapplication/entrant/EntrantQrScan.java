package com.example.myapplication.entrant;

import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.model.Event;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class EntrantQrScan extends Fragment {

    public EntrantQrScan() {
        // Required empty public constructor?
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_entrant_qr_scan, container, false);

        ImageButton eventList = view.findViewById(R.id.button_go_to_entrant_event_list);
        eventList.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_entrantQrScan_to_entrantEventsList)
        );

        Button scanQR = view.findViewById(R.id.scanQR);
        scanQR.setOnClickListener(v -> {
            ScanOptions options = new ScanOptions(); // QR Scan API object
            options.setCaptureActivity(Camera.class);
            barLauncher.launch(options); // QR Scan API function
        });
        return view;
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result->{
        if (result.getContents() != null) { //Waits until results are gathered, a.k.a. a QR is scanned.
            //Log.d("QR", result.getContents());
            //Log.d("QR", String.valueOf(result.getContents().length()));
            if (result.getContents().length() == 20) {
                switchPage(result.getContents());
            }
            else {
                Toast.makeText(requireContext(), "Invalid QR Code", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Log.d("QR", "No QR code");
        }
    });

    private void switchPage(String eventID) {
        // The QR Code is the eventID encoded, so we put it into a bundle
        Bundle bundle = new Bundle();
        bundle.putSerializable("eventID", eventID);
        Navigation.findNavController(requireView()).navigate(R.id.action_entrantQrScan_to_entrantJoinPage, bundle);
    }
}