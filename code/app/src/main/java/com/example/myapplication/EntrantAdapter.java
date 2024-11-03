package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class EntrantAdapter extends ArrayAdapter<User> {
    private Context context;
    private List<User> entrants; // List of entrants

    // Constructor
    public EntrantAdapter(@NonNull Context context, List<User> entrants) {
        super(context, 0, entrants);
        this.context = context;
        this.entrants = entrants;
    }

    // Create the view for each item in the ListView
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            // Inflate the custom layout for each list item
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_waitlist_entrant, parent, false);
        }

        // Get the User (entrant) object for this position
        User entrant = getItem(position);

        // Find the TextViews in the custom layout
        TextView nameTextView = convertView.findViewById(R.id.entrantName);

        // Populate the TextViews with the User data
        if (entrant != null) {
            nameTextView.setText(entrant.getName());;
        }

        // Return the completed view to be displayed in the ListView
        return convertView;
    }
}
