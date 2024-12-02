package com.example.myapplication.view;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.myapplication.R;
import com.example.myapplication.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.util.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * The home class represents the main fragment of the application. It provides the
 * user with navigation options based on their role (organizer, entrant, or admin).
 *
 * <p>Features:</p>
 * - Organizers can navigate to manage facilities, events, or their profile.
 * - Entrants can navigate to their profile and interact with registered events.
 * - Admin users can view and manage user accounts.
 *
 * <p>This fragment interacts with Firestore to determine user roles and dynamically
 * update the UI based on the logged-in user's role.</p>
 */
public class home extends Fragment {

    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private List<DocumentSnapshot> users;

    /**
     * Default empty constructor for the home fragment.
     * Required for use with the navigation component.
     */
    public home() {
        // Required empty public constructor
    }


    /**
     * Initializes the fragment.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Inflates the fragment's view, initializes UI components, and sets up navigation
     * and role-based visibility.
     *
     * @param inflater           The LayoutInflater object to inflate views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to. The fragment should not add the view itself.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return The root view for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Find the button and set an onClickListener to navigate to org_event_lst.xml
        Button org = view.findViewById(R.id.button_go_to_add_facility);
        org.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_home_to_orgAddFacility)
        );

        Button entrant = view.findViewById(R.id.button_go_to_entrant_profile);
        entrant.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_home_to_entrantProfile3)
        );

        Button admin = view.findViewById(R.id.button_go_to_admin);
        admin.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_home_to_adminUsersList)
        );
        fetchUsers(admin);
        return view;
    }

    /**
     * Fetches user data from Firestore and determines if the current user
     * has admin privileges. If the user is an admin, the admin button becomes visible.
     *
     * @param admin The button for navigating to the admin user list, which will
     *              be set to visible if the user is an admin.
     */
    private void fetchUsers(Button admin){
        Task<QuerySnapshot> task = usersRef.get();
        task.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                users = task.getResult().getDocuments();
                Log.d("Firestore", "Documents Retrieved");
                for (int i = 0; i < users.size(); i++) {
                    DocumentSnapshot current = users.get(i);

                    User user = new User(current.getId(), loadedUser -> {
                        if (loadedUser != null) {
                            Log.d("Kenny", "Loaded User: "+loadedUser.getName());
                            if(loadedUser.getDeviceID().equals(Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID)) && loadedUser.getIsAdmin()){
                                Log.d("Kenny", "admin detected");
                                setAdminVisible(admin);

                            }
                            Log.d("OrgEventSelectedLst", "Loaded User: " + loadedUser.getName());

                        }
                    });
                }

            }
        });
    }

    /**
     * Makes the admin button visible when an admin user is detected.
     *
     * @param admin The button to navigate to the admin user list.
     */
    private void setAdminVisible(Button admin){
        admin.setVisibility(View.VISIBLE);
    }
}
