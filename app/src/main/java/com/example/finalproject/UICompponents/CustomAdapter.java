package com.example.finalproject.UICompponents;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.finalproject.Classes.HazardInfo;
import com.example.finalproject.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomAdapter extends ArrayAdapter<HazardInfo> {
    private Context mContext;
    private List<HazardInfo> mItemList;
    private String cityOfUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    public CustomAdapter(Context context, List<HazardInfo> itemList, String cityOfUser) {
        super(context, R.layout.custom_list_item, itemList);
        mContext = context;
        mItemList = itemList;
        this.cityOfUser=cityOfUser;
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
        TextView managerInfo=listItemView.findViewById(R.id.managerInfo);

        // Get the HazardInfo object for this position
        HazardInfo hazardInfo = mItemList.get(position);

        // Set data to views
        textView.setText("Hazard:  "+hazardInfo.getDetails() +"\nopenned by "+hazardInfo.getName() + "\n"+hazardInfo.getFieldName()+"\nManager Notes: "+hazardInfo.getManagerInfo()); // Assuming getName() returns the name field of HazardInfo

        managerInfo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used in this example
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not used in this example
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Save the input after text has changed
                String input = s.toString();
                saveHazardDetailsInDB(cityOfUser, hazardInfo.getDetails(), hazardInfo.getName(), hazardInfo.getImageName(), hazardInfo.getFieldName(), hazardInfo.getStatus(), input);
            }
        });


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
        for (int i = 0; i < statuses.length; i++) {
            RadioButton radioButton = new RadioButton(mContext);
            radioButton.setText(statuses[i]);
            radioButton.setTextSize(30);
            radioButton.setTag(statuses[i]); // Set the status as the tag
            radioGroup.addView(radioButton);
            // Add OnCheckedChangeListener to each radio button
            radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        // Retrieve the selected status from the button's tag
                        String selectedStatus = (String) buttonView.getTag();
                        Log.d("Status when selected", selectedStatus);
                        saveHazardDetailsInDB(cityOfUser, hazardInfo.getDetails(), hazardInfo.getName(), hazardInfo.getImageName(), hazardInfo.getFieldName(), selectedStatus,hazardInfo.getManagerInfo());
                    }
                }
            });
        }

        // Set the default selected radio button based on hazardInfo.getStatus()
        String currentStatus = hazardInfo.getStatus();
        for (int i = 0; i < statuses.length; i++) {
            if (statuses[i].equals(currentStatus)) {
                radioGroup.check(radioGroup.getChildAt(i).getId());
                Log.d("Status", currentStatus);
                break;
            }
        }
        return listItemView;
    }
    private void saveHazardDetailsInDB(String cityOfUser, String details, String name, String imgName, String currentDate, String status,String managerInfo) {
        // Reference to the document where the subcollection will be updated
        DocumentReference docRef = db.collection("hazards").document(cityOfUser);

        // Create a Map to represent the data for the new hazard
        Map<String, Object> hazardMap = new HashMap<>();
        hazardMap.put("details", details);
        hazardMap.put("name", name);
        hazardMap.put("imgName", imgName);
        hazardMap.put("status", status);
        hazardMap.put("managerInfo",managerInfo);

        // Update the data in the document with currentDate as the key
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(currentDate, hazardMap);

        // Update the document with the new hazard detail
        docRef.set(updateMap, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Firestore", "Document updated with ID: " + currentDate);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Firestore", "Error updating document", e);
            }
        });
    }
}
