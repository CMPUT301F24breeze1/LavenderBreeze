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
 * Fragment which displays the QR Code for a particular Event
 * Makes use of eventId to identify event and fetch the QR Code associated with that event
 */
public class OrgViewEventQrCode extends Fragment {
    private static final String ARG_EVENT_ID = "eventId";
    private String eventId;
    private FirebaseFirestore db;
    private ImageView qrCodeIV;

    public OrgViewEventQrCode() {
        // Required empty public constructor
    }

    /**
     * Passing of required information from Event page (eventId) to indentify a particular event
     * @param eventID
     * @return
     */
    // TODO: Rename and change types and number of parameters
    public static OrgViewEventQrCode newInstance(String eventID) {
        OrgViewEventQrCode fragment = new OrgViewEventQrCode();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventID);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Initialization of the fragment
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Inflation of view, initialization of buttons and navigation, and Firestore Firebase
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
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

    /**
     * Retrieval QR Code from database if available.
     */
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

    /**
     * Display QR Code as ImageView
     * @param qrCodeHash
     */
    private void displayQrCode(String qrCodeHash){
        // Initialize QRCodeGenerator with the base64-encoded QR code hash
        QRCodeGenerator qrCodeGenerator = new QRCodeGenerator(qrCodeHash);

        // Set the ImageView to display the generated QR code
        qrCodeGenerator.setQrCodeImageView(qrCodeIV);

        // Display the QR code from the base64 hash in the ImageView
        qrCodeGenerator.displayQRCodeFromBase64(qrCodeHash);
    }
}