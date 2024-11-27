package com.example.myapplication.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;

/**
 * Custom ArrayAdapter for displaying user or event photos.
 */
public class AdminImageAdapter extends ArrayAdapter<String> {

    private Context context;
    private List<String> documentIds; // List of user or event document IDs
    private String type;    // Flag to determine if we are handling user photos

    public AdminImageAdapter(@NonNull Context context, @NonNull List<String> documentIds,String type) {
        super(context, 0, documentIds);
        this.context = context;
        this.documentIds = documentIds;
        this.type = type;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_image, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.image_view);

        String documentId = documentIds.get(position);

        // Fetch the image URL and details dynamically
        FirebaseFirestore.getInstance()
                .collection((Objects.equals(type, "users")) ? "users" : "events")
                .document(documentId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Load the image using Glide
                        String imageUrl = documentSnapshot.getString((Objects.equals(type, "users")) ? "profilePicture" : "posterUrl");
                        Glide.with(context)
                                .load(imageUrl)
                                .transform(new CircleCrop())
                                .into(imageView);
                    }
                })
                .addOnFailureListener(e -> {
//                    textView.setText("Error loading details");
                });

        return convertView;
    }
}
