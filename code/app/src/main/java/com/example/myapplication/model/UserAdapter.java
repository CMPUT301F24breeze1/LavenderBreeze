package com.example.myapplication.model;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import com.example.myapplication.R;

import java.util.List;

public class UserAdapter extends ArrayAdapter<User> {
    private String status;

    public UserAdapter(Context context, List<User> users, String status) {
        super(context, 0,users);
        this.status = status; // Pass the status to determine the action on click
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_list_item, parent, false);
        }

        User user = getItem(position);
        TextView eventName = convertView.findViewById(R.id.eventNameTextView);
        TextView organizerName = convertView.findViewById(R.id.organizerNameTextView);
        TextView statusTag = convertView.findViewById(R.id.statusTagTextView);
        ImageView arrowImage = convertView.findViewById(R.id.arrowImageView);

        eventName.setText(user.getName());
        organizerName.setText("Organized by: " + user.getEmail());
        statusTag.setText(status);

        // Set arrow click listener based on status
        arrowImage.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("user", user);
            switch (status) {
                case "all":
                    Navigation.findNavController(v).navigate(R.id.action_entrantEventsList_to_entrantSelectedPage,bundle);
                    break;
                case "accepted":
                    Navigation.findNavController(v).navigate(R.id.action_entrantEventsList_to_entrantLeavePage2,bundle);
                    break;
                case "canceled":
                    Navigation.findNavController(v).navigate(R.id.action_entrantEventsList_to_entranteventdescription,bundle);
                    break;
                default:
                    Navigation.findNavController(v).navigate(R.id.action_entrantEventsList_to_entrantEventPage);
                    break;
            }
            //Navigation.findNavController(v).navigate(actionId);
        });

        return convertView;
    }
}
