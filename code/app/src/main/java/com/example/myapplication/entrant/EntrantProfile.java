package com.example.myapplication.entrant;

import android.os.Bundle;
import com.example.myapplication.model.User;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.myapplication.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EntrantProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EntrantProfile extends Fragment {
    private User user;
    private TextView personName, emailAddress, contactPhoneNumber;
    private SwitchCompat emailNotificationSwitch;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EntrantProfile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EntrantProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static EntrantProfile newInstance(String param1, String param2) {
        EntrantProfile fragment = new EntrantProfile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view,@Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
         BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottomNavigationView);
         NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_entrant_profile, container, false);
         user = User.getInstance(requireContext());

        // Find the button and set an onClickListener to navigate to org_event_lst.xml
        ImageButton edit = view.findViewById(R.id.editButton);
        edit.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_entrantProfile3_to_entrantEditProfile)
        );

        ImageButton notifications = view.findViewById(R.id.notificationButton);
        notifications.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_entrantProfile3_to_entrantNotification)
        );
        // Reference the views
        personName = view.findViewById(R.id.personName);

        emailAddress = view.findViewById(R.id.emailAddress);
        contactPhoneNumber = view.findViewById(R.id.contactPhoneNumber);
        emailNotificationSwitch = view.findViewById(R.id.emailNotificationSwitch);
//        Log.d("User", "Entrant profile: " + user.getName());
        // Set user data in the UI
        updateUserData();

//        Button events = view.findViewById(R.id.button_go_to_entrant_event_list);
//        events.setOnClickListener(v ->
//                Navigation.findNavController(v).navigate(R.id.action_entrantProfile3_to_entrantEventsList)
//        );

        return view;
    }
    private void updateUserData() {
        // Update the TextViews and Switch with User's data
        personName.setText(user.getName());
        emailAddress.setText(user.getEmail());
        contactPhoneNumber.setText(user.getPhoneNumber());

        // Example of setting an email notification switch (if stored in User class)
        //emailNotificationSwitch.setChecked(user.getIsEntrant());
    }
}