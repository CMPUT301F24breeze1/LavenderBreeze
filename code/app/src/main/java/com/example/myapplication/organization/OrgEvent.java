package com.example.myapplication.organization;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrgEvent#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrgEvent extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "eventId";
    private static final String ARG_PARAM2 = "waitlist";
    private static final String ARG_PARAM3 = "selected";
    private static final String ARG_PARAM4 = "capacity";
    private static final String ARG_PARAM5 = "eventName";
    private static final String ARG_PARAM6 = "eventDescription";
    private static final String ARG_PARAM7 = "eventStart";
    private static final String ARG_PARAM8 = "eventEnd";
    private static final String ARG_PARAM9 = "price";
    private static final String ARG_PARAM10 = "registrationStart";
    private static final String ARG_PARAM11 = "registrationEnd";
    private static final String ARG_PARAM12 = "qrCodeHash";
    private static final String ARG_PARAM13 = "declined";


    // TODO: Rename and change types of parameters
    private String eventId;
    private ArrayList<String> waitlist;
    private ArrayList<String> selected;
    private int capacity;
    private String eventName;
    private String eventDescription;
    private String eventStart;
    private String eventEnd;
    private int price;
    private String registrationStart;
    private String registrationEnd;
    private String qrCodeHash;
    private ArrayList<String> declined;

    public OrgEvent() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment org_event.
     */
    // TODO: Rename and change types and number of parameters
    public static OrgEvent newInstance(String param1,ArrayList<String> param2,ArrayList<String> param3, int param4, String param5,
                                       String param6, String param7, String param8, int param9, String param10, String param11, String param12,
                                        ArrayList<String> param13) {
        OrgEvent fragment = new OrgEvent();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putStringArrayList(ARG_PARAM2,param2);
        args.putStringArrayList(ARG_PARAM3,param3);
        args.putInt(ARG_PARAM4,param4);
        args.putString(ARG_PARAM5, param5);
        args.putString(ARG_PARAM6, param6);
        args.putString(ARG_PARAM7, param7);
        args.putString(ARG_PARAM8, param8);
        args.putInt(ARG_PARAM9, param9);
        args.putString(ARG_PARAM10, param10);
        args.putString(ARG_PARAM11, param11);
        args.putString(ARG_PARAM12, param12);
        args.putStringArrayList(ARG_PARAM13, param13);

        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getString(ARG_PARAM1);
            waitlist = getArguments().getStringArrayList(ARG_PARAM2);
            selected = getArguments().getStringArrayList(ARG_PARAM3);
            capacity = getArguments().getInt(ARG_PARAM4);
            eventName = getArguments().getString(ARG_PARAM5);
            eventDescription = getArguments().getString(ARG_PARAM6);
            eventStart = getArguments().getString(ARG_PARAM7);
            eventEnd = getArguments().getString(ARG_PARAM8);
            price = getArguments().getInt(ARG_PARAM9);
            registrationStart = getArguments().getString(ARG_PARAM10);
            registrationEnd = getArguments().getString(ARG_PARAM11);
            qrCodeHash = getArguments().getString(ARG_PARAM12);
            declined = getArguments().getStringArrayList(ARG_PARAM13);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_org_event, container, false);

        // Declaring and setting all event information
        TextView eventNameTextView = view.findViewById(R.id.text_view_org_event_event_name);
        TextView eventDescriptionTextView = view.findViewById(R.id.text_view_org_event_event_description);
        TextView eventStartTextView = view.findViewById(R.id.text_view_org_event_start_date);
        TextView eventEndTextView = view.findViewById(R.id.text_view_org_event_end_date);
        TextView eventCapacityTextView = view.findViewById(R.id.text_view_org_event_capacity);
        TextView eventPriceTextView = view.findViewById(R.id.text_view_org_event_price);
        TextView eventRegistrationStartTextView = view.findViewById(R.id.text_view_org_event_registration_start);
        TextView eventRegistrationEndTextView = view.findViewById(R.id.text_view_org_event_registration_end);

        eventNameTextView.setText(eventName);
        eventDescriptionTextView.setText(eventDescription);
        eventStartTextView.setText(eventStart);
        eventEndTextView.setText(eventEnd);
        eventCapacityTextView.setText(String.valueOf(capacity));
        eventPriceTextView.setText(String.valueOf(price));
        eventRegistrationStartTextView.setText(registrationStart);
        eventRegistrationEndTextView.setText(registrationEnd);


        //Create bundle containing EventId to be passed to next fragment if necessary
        Bundle bundle = new Bundle();
        bundle.putString("eventId", eventId);
        bundle.putStringArrayList("waitlist", waitlist);
        bundle.putStringArrayList("selected", selected);
        bundle.putInt("capacity", capacity);
        bundle.putString("eventName", eventName);
        bundle.putString("eventDescription", eventDescription);
        bundle.putString("eventStart", eventStart);
        bundle.putString("eventEnd", eventEnd);
        bundle.putInt("price", price);
        bundle.putString("registrationStart", registrationStart);
        bundle.putString("registrationEnd", registrationEnd);
        bundle.putString("qrCodeHash", qrCodeHash);
        bundle.putStringArrayList("declined", declined);

        Button buttonGoToQRCode = view.findViewById(R.id.button_go_to_qrcode_from_org_event);
        buttonGoToQRCode.setOnClickListener(v ->{
            Log.d("OrgEvent", "Navigating with eventId: " + eventId);
            Navigation.findNavController(v).navigate(R.id.action_org_event_to_org_view_event_qrcode, bundle);
        });

        // Button to navigate back to Events List
        Button buttonGoToEventsLst = view.findViewById(R.id.button_go_to_event_lst_from_org_event);
        buttonGoToEventsLst.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_OrgEvent_to_OrgEventLst)
        );

        // Button to navigate to the Edit Event fragment
        Button buttonGoToEditEvent = view.findViewById(R.id.button_go_to_edit_event_from_org_event);
        buttonGoToEditEvent.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_org_event_to_org_edit_event2)
        );

        // Button to navigate to the Waiting List fragment
        Button buttonGoToWaitingList = view.findViewById(R.id.button_go_to_waiting_list_from_org_event);
        buttonGoToWaitingList.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_org_event_to_org_event_waiting_lst,bundle)
        );

        // Button to navigate to the Selected List fragment
        Button buttonGoToSelectedList = view.findViewById(R.id.button_go_to_selected_list_from_org_event);
        buttonGoToSelectedList.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_org_event_to_org_event_selected_lst,bundle)
        );

        return view;

    }
}