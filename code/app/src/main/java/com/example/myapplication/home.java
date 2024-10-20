package com.example.myapplication;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class home extends Fragment {

    public home() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Find the button and set an onClickListener to navigate to org_event_lst.xml
        Button button = view.findViewById(R.id.button_go_to_org_event_list);
        button.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_home_to_org_events_lst)
        );

        return view;
    }
}
