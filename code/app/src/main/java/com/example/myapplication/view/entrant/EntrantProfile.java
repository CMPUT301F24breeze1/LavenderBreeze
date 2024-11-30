// From chatgpt, openai, "write a java implementation with java documentation of EntrantProfile
//class with methods to show the profile of the entrant
//given here is the xml code for it", 2024-11-02
package com.example.myapplication.view.entrant;

import android.os.Bundle;

import com.example.myapplication.model.User;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;

/**
 * A fragment that displays the profile information of an entrant, including name, email,
 * phone number, and profile picture. It allows navigation to an edit profile page and
 * manages updates to the profile data.
 */
public class EntrantProfile extends Fragment {
    private User user;
    private TextView personName, emailAddress, contactPhoneNumber;
    private ImageButton edit;
    private ImageView profilePicture;
    private SwitchCompat notifSwitch;
    ImageButton homeButton, profileButton, eventsButton;
    /**
     * Default constructor required for instantiating the fragment.
     */
    public EntrantProfile() {
        // Required empty public constructor
    }
    /**
     * Called after the fragment's view has been created. It listens for updated user data
     * and initializes the bottom navigation buttons.
     * @param view the fragment's root view
     * @param savedInstanceState Bundle containing the saved instance state
     */
    @Override
    public void onViewCreated(@NonNull View view,@Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            User updatedUser = (User) bundle.getSerializable("updated_user");
            if (updatedUser != null) {
                user = updatedUser; // Update the local user instance
                updateUserData(); // Refresh the displayed user data
            }
        });
        if(user == null) {
            if (getContext() != null) {
                user = new User(requireContext(), this::updateUserData);
            }
        }
        // Update UI with new user data
        intializeBottomNavButton(view);
    }
    /**
     * Inflates the view for the fragment, sets up the UI components, and configures
     * the edit button to navigate to the profile editing page.
     * @param inflater LayoutInflater to inflate the view
     * @param container ViewGroup container for the fragment
     * @param savedInstanceState Bundle containing the saved instance state
     * @return the inflated view for the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_entrant_profile, container, false);

        // Reference the views
        personName = view.findViewById(R.id.personName);
        emailAddress = view.findViewById(R.id.emailAddress);
        contactPhoneNumber = view.findViewById(R.id.contactPhoneNumber);
        notifSwitch = view.findViewById(R.id.emailNotificationSwitch);
        profilePicture = view.findViewById(R.id.profilePicture);

        // Find the button and set an onClickListener to navigate to org_event_lst.xml
        edit = view.findViewById(R.id.editButton);
        edit.setOnClickListener(v ->
        {
            Bundle result = new Bundle();
            result.putSerializable("updated_user", user);
            Navigation.findNavController(v).navigate(R.id.action_entrantProfile3_to_entrantEditProfile,result);
        });

        updateUserData();
        return view;
    }
    /**
     * Updates the displayed user data in the UI components.
     */
    private void updateUserData() {
        // Update the TextViews and Switch with User's data
        if (user != null) {
            personName.setText(user.getName());
            emailAddress.setText(user.getEmail());
            contactPhoneNumber.setText(user.getPhoneNumber());
            Log.d("User", "Deterministic Picture " + user.getDeterministicPicture());
            if (!user.getDeterministicPicture()) user.loadProfilePictureInto(profilePicture,requireContext());
            else user.loadDeterministicProfilePictureInto(profilePicture,requireContext());
            notifSwitch.setChecked(user.isToggleNotif());
        }
    }
    /**
     * Initializes the bottom navigation buttons.
     * @param view the fragment's root view
     */
    public void intializeBottomNavButton(View view){
        homeButton = view.findViewById(R.id.homeButton);
        profileButton = view.findViewById(R.id.profileButton);
        profileButton.setBackgroundResource(R.color.backgroundColoredHighlight);
        eventsButton = view.findViewById(R.id.eventsButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_entrantProfile3_to_home); // ID of the destination in nav_graph.xml
            }
        });
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_entrantProfile3_self); // ID of the destination in nav_graph.xml
            }
        });
        eventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_entrantProfile3_to_entrantEventsList); // ID of the destination in nav_graph.xml
            }
        });
    }
}