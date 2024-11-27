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
 * Fragment where user log in as organizer to create, edit, and view event/facility
 * Or where user log in as entrant to scan QR code to enter an event, or events they are registered in
 */
public class home extends Fragment {

    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private List<DocumentSnapshot> users;

    public home() {
        // Required empty public constructor
    }


    /**
     * Initialize fragment
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Inflate view, initialize and connect UI components. Defined navigation
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

    private void setAdminVisible(Button admin){
        admin.setVisibility(View.VISIBLE);
    }
}
