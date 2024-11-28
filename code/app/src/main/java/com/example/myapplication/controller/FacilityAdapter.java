package com.example.myapplication.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;
import com.example.myapplication.model.Facility;

import org.w3c.dom.Text;

import java.util.ArrayList;
/**
 * A {@link ArrayAdapter< Facility >} subclass.
 * Use the {@link UserAdapter(Context, ArrayList)} constructor to
 * create an instance of this fragment.
 */
public class FacilityAdapter extends ArrayAdapter<Facility> {
    private ArrayList<Facility> facilities;
    private Context context;

    /**
     * Constructs an EventAdapter.
     * @param context the context in which the adapter is being used
     * @param facilities a list of Facility objects to be displayed
     */
    public FacilityAdapter(Context context, ArrayList<Facility> facilities) {
        super(context, 0, facilities);
        this.facilities = facilities;
        this.context = context;
    }

    /**
     * Provides a view for an adapter view, displaying the Facility name
     *
     * @param position the position of the item within the adapter's data set
     * @param convertView the old view to reuse, if possible
     * @param parent the parent view that this view will eventually be attached to
     * @return the view corresponding to the data at the specified position
     */
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
        TextView facilityAddress = view.findViewById(R.id.facility_address);
        TextView facilityEvents = view.findViewById(R.id.number_of_events);
        facilityAddress.setText(facility.getFacilityAddress());
        facilityEvents.setText(String.valueOf(facility.getEvents().size()));
        facilityName.setText(facility.getFacilityName());

        return view;
    }
}
