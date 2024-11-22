package com.example.myapplication.view.admin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.myapplication.R;
import com.example.myapplication.controller.UserListAdapter;
import com.example.myapplication.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminUsersList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminUsersList extends Fragment {


    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private List<DocumentSnapshot> users;
    // Display lists
    private ListView userList;
    private Spinner statusSpinner;
    private UserListAdapter entrantAdapter;
    private ArrayList<User> userDataList = new ArrayList<>();


    public AdminUsersList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AdminUsersList.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminUsersList newInstance() {
        AdminUsersList fragment = new AdminUsersList();
        Bundle args = new Bundle();
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

        View view = inflater.inflate(R.layout.fragment_admin_users_list, container, false);


        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");



        entrantAdapter = new UserListAdapter(this.getContext(), userDataList);

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
                            userDataList.add(loadedUser);
                            Log.d("OrgEventSelectedLst", "Loaded User: " + loadedUser.getName());
                            entrantAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

        userList = view.findViewById(R.id.list_view_admin_users_list);
        userDataList = new ArrayList<User>();
        entrantAdapter = new UserListAdapter(this.getContext(), userDataList);
        userList.setAdapter(entrantAdapter);

        Button home = view.findViewById(R.id.button_go_to_home_from_admin_users_list);
        home.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_adminUsersList_to_home));

        Button events = view.findViewById(R.id.events);
        events.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_adminUsersList_to_adminEventsList));

        Button facilities = view.findViewById(R.id.facilities);
        facilities.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_adminUsersList_to_adminFacilitiesList));

        Button pictures = view.findViewById(R.id.images);
        pictures.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_adminUsersList_to_adminPicturesList));
        return view;
    }
}