package com.example.myapplication.organization;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.example.myapplication.DeviceUtils;
import com.example.myapplication.QRCodeGenerator;
import com.example.myapplication.model.Event;
import com.example.myapplication.model.User;
import com.example.myapplication.model.UserIDCallback;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.myapplication.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrgAddEvent#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrgAddEvent extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EditText editTextEventName, editTextEventDescription, editTextLocation,
            editTextCapacity, editTextPrice, editTextPosterUrl,
            editTextEventStart, editTextEventEnd,
            editTextRegistrationStart, editTextRegistrationEnd;

    private String eventQRCode;
    private FirebaseFirestore database;

    public OrgAddEvent() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment org_add_event.
     */
    // TODO: Rename and change types and number of parameters
    public static OrgAddEvent newInstance(String param1, String param2) {
        OrgAddEvent fragment = new OrgAddEvent();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_org_add_event, container, false);

        // Initialize Firestore database
        database = FirebaseFirestore.getInstance();

        // Bind views
        editTextEventName = view.findViewById(R.id.editTextEventName);
        editTextEventDescription = view.findViewById(R.id.editTextEventDescription);
        editTextLocation = view.findViewById(R.id.editTextLocation);
        editTextCapacity = view.findViewById(R.id.editTextCapacity);
        editTextPrice = view.findViewById(R.id.editTextPrice);
        editTextPosterUrl = view.findViewById(R.id.editTextPosterUrl);
        editTextEventStart = view.findViewById(R.id.editTextEventStart);
        editTextEventEnd = view.findViewById(R.id.editTextEventEnd);
        editTextRegistrationStart = view.findViewById(R.id.editTextRegistrationStart);
        editTextRegistrationEnd = view.findViewById(R.id.editTextRegistrationEnd);

        Button buttonAddEvent = view.findViewById(R.id.buttonAddEvent);
        buttonAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEvent();
                Navigation.findNavController(v).navigate(R.id.action_org_add_event_to_org_event);
            }
        });

        return view;
    }

    public boolean validateEventData(String eventName, String eventDescription, String eventLocation, String eventPosterURL,
                                     int eventCapacity, int eventPrice,
                                     Date eventStart, Date eventEnd, Date registrationStart, Date registrationEnd) {

        // Check required fields
        if (eventName == null || eventName.isEmpty()) return false;
        if (eventDescription == null || eventDescription.isEmpty()) return false;
        if (eventLocation == null || eventLocation.isEmpty()) return false;
        if (eventPosterURL == null || eventPosterURL.isEmpty()) return false;

        // Validate capacity and price
        if (eventCapacity <= 0) return false;
        if (eventPrice < 0) return false;

        // Validate date fields
        if (eventStart == null || eventEnd == null || registrationStart == null || registrationEnd == null) return false;
        if (eventStart.after(eventEnd)) return false;
        if (registrationStart.after(registrationEnd)) return false;
        if (registrationEnd.after(eventStart)) return false;

        // All validations passed
        return true;
    }


    public void createEvent(){
        // Retrieve information
        String eventName = editTextEventName.getText().toString();
        String eventDescription = editTextEventDescription.getText().toString();
        String eventLocation = editTextLocation.getText().toString();
        String eventPosterURL = editTextPosterUrl.getText().toString();
        int eventCapacity = Integer.parseInt(editTextCapacity.getText().toString());
        int eventPrice = Integer.parseInt(editTextPrice.getText().toString());
        Date eventStart = parseDate(editTextEventStart.getText().toString());
        Date eventEnd = parseDate(editTextEventEnd.getText().toString());
        Date eventRegistrationStart = parseDate(editTextRegistrationStart.getText().toString());
        Date eventRegistrationEnd = parseDate(editTextRegistrationEnd.getText().toString());

        if (!validateEventData(eventName, eventDescription, eventLocation, eventPosterURL,
                eventCapacity, eventPrice, eventStart, eventEnd, eventRegistrationStart, eventRegistrationEnd)) {
            Toast.makeText(requireContext(), "Invalid input. Please check your data.", Toast.LENGTH_SHORT).show();
            return;
        }

        QRCodeGenerator qrCode = new QRCodeGenerator(eventName);
        String eventQRCode = qrCode.getQRCodeAsBase64();

        String organizerID = DeviceUtils.getDeviceId(requireContext());
        Event event = new Event(eventName, eventDescription, eventStart, eventEnd, eventRegistrationStart,
                        eventRegistrationEnd, eventLocation, eventCapacity, eventPrice, eventPosterURL,
                       eventQRCode, organizerID);
        event.saveEvent();

    }

    public Date parseDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            //Log.e("org_add_event", "Date parsing error", e);
            //Toast.makeText(getContext(), "Invalid date format. Please use yyyy-MM-dd HH:mm", Toast.LENGTH_SHORT).show();
            return null; // Return null if parsing fails
        }
    }
}