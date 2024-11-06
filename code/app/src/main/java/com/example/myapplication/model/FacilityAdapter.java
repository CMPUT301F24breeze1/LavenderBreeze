package com.example.myapplication.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;

import java.util.ArrayList;

public class FacilityAdapter extends ArrayAdapter<Facility> {
    private ArrayList<Facility> facilities;
    private Context context;

    public FacilityAdapter(Context context, ArrayList<Facility> facilities) {
        super(context, 0, facilities);
        this.facilities = facilities;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.facilities_list_content, parent, false);
        }

        Facility facility = facilities.get(position);

        // Bind data to the views
        TextView facilityName = view.findViewById(R.id.facility_name);
        facilityName.setText(facility.getFacilityName());

        return view;
    }
}
