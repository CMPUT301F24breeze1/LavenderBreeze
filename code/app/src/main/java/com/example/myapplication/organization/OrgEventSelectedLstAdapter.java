// OrgEventSelectedListAdapter.java
package com.example.myapplication.organization;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.myapplication.R;

import java.util.List;

public class OrgEventSelectedLstAdapter extends ArrayAdapter<String> {
    private List<String> selectedEntrants;
    private List<String> acceptedEntrants;
    private List<String> declinedEntrants;
    private String filterType;

    public OrgEventSelectedLstAdapter(Context context, List<String> selectedEntrants, List<String> acceptedEntrants, List<String> declinedEntrants) {
        super(context, 0, selectedEntrants);
        this.selectedEntrants = selectedEntrants;
        this.acceptedEntrants = acceptedEntrants;
        this.declinedEntrants = declinedEntrants;
        this.filterType = "all"; // Default filter type

    }

    public void updateList(List<String> newEntrants, String filterType) {
        clear();
        addAll(newEntrants);
        this.filterType = filterType;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.selected_entrant_item, parent, false);
        }

        TextView entrantName = convertView.findViewById(R.id.entrant_name);
        View statusLabel = convertView.findViewById(R.id.status_label);

        String entrant = getItem(position);
        entrantName.setText(entrant);

        // Set color and text based on filter type or status
        switch (filterType) {
            case "all":
                if (acceptedEntrants != null && acceptedEntrants.contains(entrant)) {
                    // Entrant has accepted
                    statusLabel.setBackgroundColor(Color.GREEN);
                    entrantName.append(" - Accepted");
                } else if (declinedEntrants != null && declinedEntrants.contains(entrant)) {
                    // Entrant has declined
                    statusLabel.setBackgroundColor(Color.RED);
                    entrantName.append(" - Declined");
                } else {
                    // Notification sent, but no response yet
                    statusLabel.setBackgroundColor(Color.YELLOW);
                    entrantName.append(" - Sent");
                }
                break;
            case "accepted":
                statusLabel.setBackgroundColor(Color.GREEN);
                entrantName.append(" - Accepted");
                break;
            case "declined":
                statusLabel.setBackgroundColor(Color.RED);
                entrantName.append(" - Declined");
                break;
        }

        return convertView;
    }
}
