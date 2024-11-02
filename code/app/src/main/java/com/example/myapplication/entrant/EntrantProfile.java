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
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.myapplication.model.UserViewModel;
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
    private UserViewModel userViewModel;
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
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view,@Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);

        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            Log.d("EntrantProfile", "Received updated user: " + bundle.getSerializable("updated_user"));
            user = (User) bundle.getSerializable("updated_user");
            updateUserData(); // Refresh UI with updated User data
        });
            // Update UI with new user data
         BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottomNavigationView);
         NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
         NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_entrant_profile, container, false);
        user = User.getInstance(requireContext(), new User.OnUserDataLoadedListener() {
            @Override
            public void onUserDataLoaded() {
                // This is called once data is fully loaded
                updateUserData();
            }
        });
        // Find the button and set an onClickListener to navigate to org_event_lst.xml
        ImageButton edit = view.findViewById(R.id.editButton);

        edit.setOnClickListener(v ->
        {
            Bundle bundle = new Bundle();
            bundle.putSerializable("user", user);
            Navigation.findNavController(v).navigate(R.id.action_entrantProfile3_to_entrantEditProfile, bundle);
        });

        ImageButton notifications = view.findViewById(R.id.notificationButton);
        notifications.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_entrantProfile3_to_entrantNotification)
        );

        // Reference the views
        personName = view.findViewById(R.id.personName);
        emailAddress = view.findViewById(R.id.emailAddress);
        contactPhoneNumber = view.findViewById(R.id.contactPhoneNumber);
        emailNotificationSwitch = view.findViewById(R.id.emailNotificationSwitch);
        return view;
    }
    private void updateUserData() {
        // Update the TextViews and Switch with User's data
        if (user != null) {
            personName.setText(user.getName());
            emailAddress.setText(user.getEmail());
            contactPhoneNumber.setText(user.getPhoneNumber());
        }
        // Example of setting an email notification switch (if stored in User class)
        //emailNotificationSwitch.setChecked(user.getIsEntrant());
    }
}