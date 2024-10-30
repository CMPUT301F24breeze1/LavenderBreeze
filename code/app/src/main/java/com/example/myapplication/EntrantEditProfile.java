package com.example.myapplication;

import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EntrantEditProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EntrantEditProfile extends Fragment {
    private Button doneEditButton;
    private ImageButton editPhotoButton, editNameButton, editEmailButton, editPhoneButton;
    private ImageView profilePicture;
    private TextView personName, emailAddress, contactPhoneNumber;
    private SwitchCompat emailNotificationSwitch;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EntrantEditProfile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EntrantEditProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static EntrantEditProfile newInstance(String param1, String param2) {
        EntrantEditProfile fragment = new EntrantEditProfile();
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
        View view = inflater.inflate(R.layout.fragment_entrant_edit_profile, container, false);

        // Initialize views
        doneEditButton = view.findViewById(R.id.doneEdit);
        editPhotoButton = view.findViewById(R.id.editPhotoButton);
        editNameButton = view.findViewById(R.id.editNameButton);
        editEmailButton = view.findViewById(R.id.editEmailButton);
        editPhoneButton = view.findViewById(R.id.editPhoneButton);
        profilePicture = view.findViewById(R.id.profilePicture);
        personName = view.findViewById(R.id.personName);
        emailAddress = view.findViewById(R.id.emailAddress);
        contactPhoneNumber = view.findViewById(R.id.contactPhoneNumber);
        emailNotificationSwitch = view.findViewById(R.id.emailNotificationSwitch);

        doneEditButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_entrantEditProfile_to_entrantProfile3)
        );

        // Find the button and set an onClickListener to navigate to org_event_lst.xml
//        Button profile = view.findViewById(R.id.button_go_to_entrant_profile);
//        profile.setOnClickListener(v ->
//                Navigation.findNavController(v).navigate(R.id.action_entrantEditProfile_to_entrantProfile3)
//        );
        return view;
    }
}