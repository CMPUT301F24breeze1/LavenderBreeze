package com.example.myapplication.organization;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.QRCodeGenerator;
import com.example.myapplication.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrgViewEventQrCode#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrgViewEventQrCode extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_EVENT_ID = "eventId";
    private String eventId;
    private FirebaseFirestore db;
    private ImageView qrCodeIV;

    public OrgViewEventQrCode() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param eventID Parameter 1.
     * @return A new instance of fragment org_view_event_qrcode.
     */
    // TODO: Rename and change types and number of parameters
    public static OrgViewEventQrCode newInstance(String eventID) {
        OrgViewEventQrCode fragment = new OrgViewEventQrCode();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_org_view_event_qrcode, container, false);

        if (getArguments() != null) {
            eventId = getArguments().getString("eventId");
            Log.d("OrgViewEventQrCode", "Received eventId: " + eventId); // Log for debugging
        }
        Log.d("OrgEvent", "In onCreateView, eventId: " + eventId);

        db = FirebaseFirestore.getInstance();
        Button buttonGoToQRCode = view.findViewById(R.id.button_go_to_org_event_from_org_view_qrcode);
        buttonGoToQRCode.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_OrgViewEventQrCode_to_OrgEvent)
        );

        qrCodeIV = view.findViewById(R.id.qr_code_image_view);
        fetchQRCodeHash();
        // Inflate the layout for this fragment
        return view;
    }

    private void fetchQRCodeHash(){
        if (eventId == null || eventId.isEmpty()) {
            Log.d("EventId null", "Failed to fetch eventId");
            return;
        }
        DocumentReference docRef = db.collection("events").document(eventId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String qrCodeHash = documentSnapshot.getString("qrCodeHash");
                if (qrCodeHash != null) {
                    Log.d("OrgViewEventQrCode", "QR Code Hash: " + qrCodeHash);
                    displayQrCode(qrCodeHash);
                } else {
                    Toast.makeText(getContext(), "QR Code not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Event not found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e ->
                Toast.makeText(getContext(), "Error fetching QR Code", Toast.LENGTH_SHORT).show()
        );
    }

    private void displayQrCode(String qrCodeHash){
        // Initialize QRCodeGenerator with the base64-encoded QR code hash
        QRCodeGenerator qrCodeGenerator = new QRCodeGenerator(qrCodeHash);

        // Set the ImageView to display the generated QR code
        qrCodeGenerator.setQrCodeImageView(qrCodeIV);

        // Display the QR code from the base64 hash in the ImageView
        qrCodeGenerator.displayQRCodeFromBase64(qrCodeHash);
    }
}