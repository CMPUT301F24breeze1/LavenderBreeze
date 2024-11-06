package com.example.myapplication.organization;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.model.EntrantAdapter;
import com.example.myapplication.model.Event;
import com.example.myapplication.model.EventsListAdapter;
import com.example.myapplication.model.User;
import com.example.myapplication.model.UserListAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrgEventSelectedLst#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrgEventSelectedLst extends Fragment {

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



    // TODO: Rename and change types of parameters

    // Event Info
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

    // Firebase stuff
    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private List<DocumentSnapshot> users;

    // Display lists
    private ListView SelectedEntrantsList;
    private Spinner statusSpinner;
    private UserListAdapter entrantAdapter;
    private ArrayList<String> userIds = new ArrayList<>();
    private ArrayList<Bundle> acceptedEntrants = new ArrayList<>();
    private ArrayList<Bundle> declinedEntrants = new ArrayList<>();
    private ArrayList<Bundle> sentEntrants = new ArrayList<>();

    public OrgEventSelectedLst() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment org_event_selected_lst.
     */
    // TODO: Rename and change types and number of parameters
    public static OrgEventSelectedLst newInstance(String param1, ArrayList<String> param2, ArrayList<String> param3, int param4, String param5,
                                                  String param6, String param7, String param8, int param9, String param10, String param11, String param12) {
        OrgEventSelectedLst fragment = new OrgEventSelectedLst();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putStringArrayList(ARG_PARAM2, param2);
        args.putStringArrayList(ARG_PARAM3, param3);
        args.putInt(ARG_PARAM4, param4);
        args.putString(ARG_PARAM5, param5);
        args.putString(ARG_PARAM6, param6);
        args.putString(ARG_PARAM7, param7);
        args.putString(ARG_PARAM8, param8);
        args.putInt(ARG_PARAM9, param9);
        args.putString(ARG_PARAM10, param10);
        args.putString(ARG_PARAM11, param11);
        args.putString(ARG_PARAM12, param12);
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_org_event_selected_lst, container, false);

        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");

        Task<QuerySnapshot> task = usersRef.get();

        task.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                users = task.getResult().getDocuments();
                Log.d("Firestore", "Documents Retrieved");
                for(int i = 0; i < users.size(); i++){
                    DocumentSnapshot current = users.get(i);
                    String userName = current.getString("name");

                    Bundle bundle = new Bundle();
                    bundle.putString("name",userName);
                    if (selected.contains(current.getId())){
                        bundle.putString("status","selected");
                    } else {
                        bundle.putString("status","not registered");
                    }

                    userIds.add(current.getId());
                    sentEntrants.add(bundle);


                    // I set the event description as the doc ID to make it easier to pass when clicked

                }
                entrantAdapter.notifyDataSetChanged();
            }
        });

        SelectedEntrantsList = view.findViewById(R.id.list_view_event_selected_list);
        sentEntrants = new ArrayList<Bundle>();

        entrantAdapter = new UserListAdapter(this.getContext(), sentEntrants);
        SelectedEntrantsList.setAdapter(entrantAdapter);

        Button buttonGoToEvent = view.findViewById(R.id.button_go_to_event_from_org_event_selected_lst);
        buttonGoToEvent.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_OrgEventSelectedLst_to_OrgEvent, getArguments())
        );
        // Inflate the layout for this fragment
        return view;
    }


}


