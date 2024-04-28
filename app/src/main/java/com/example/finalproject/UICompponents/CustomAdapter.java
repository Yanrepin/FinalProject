package com.example.finalproject.UICompponents;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.finalproject.Classes.HazardInfo;
import com.example.finalproject.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<HazardInfo> {
    private Context mContext;
    private List<HazardInfo> mItemList;


    public CustomAdapter(Context context, List<HazardInfo> itemList) {
        super(context, R.layout.custom_list_item, itemList);
        mContext = context;
        mItemList = itemList;


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = inflater.inflate(R.layout.custom_list_item, parent, false);
        }

        // Find views in the custom layout
        TextView textView = listItemView.findViewById(R.id.textView);
        ImageView imageView = listItemView.findViewById(R.id.imageView);
        RadioGroup radioGroup = listItemView.findViewById(R.id.radioGroup);

        // Get the HazardInfo object for this position
        HazardInfo hazardInfo = mItemList.get(position);

        // Set data to views
        textView.setText(hazardInfo.getDetails() +"\n openned by "+hazardInfo.getName() + "\n"+hazardInfo.getFieldName()); // Assuming getName() returns the name field of HazardInfo

         FirebaseStorage storage=FirebaseStorage.getInstance();
         StorageReference storageRef=storage.getReference();
         StorageReference imgRef=storageRef.child(hazardInfo.getImageName());
        // Load image using Glide or Picasso into the ImageView
        final long ONE_MEGABYTE = 1024 * 1024;
        imgRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Convert bytes to Bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                // Set the bitmap to the ImageView
                imageView.setImageBitmap(bitmap);
                Log.d("Firestore", "seccessded to get img");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.d("Firestore", exception.toString());
            }
        });


        // Populate radio buttons in the radio group
        String[] statuses = {"open", "pending", "closed"};
        for (String status : statuses) {
            RadioButton radioButton = new RadioButton(mContext);
            radioButton.setText(status);
            radioGroup.addView(radioButton);
        }

        // Set the default selected radio button based on hazardInfo.getStatus()
        String currentStatus = hazardInfo.getStatus();
        switch (currentStatus) {
            case "open":
                radioGroup.check(radioGroup.getChildAt(0).getId());
                break;
            case "pending":
                radioGroup.check(radioGroup.getChildAt(1).getId());
                break;
            case "closed":
                radioGroup.check(radioGroup.getChildAt(2).getId());
                break;
            default:
                // Do something if the status is not one of the expected values
        }
        return listItemView;
    }
}
