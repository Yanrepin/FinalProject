package com.example.finalproject;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InquiriesActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton back;
    String cityLocation;
    TextView inquiriesForLocation;
    ListView inquiriesList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Spinner spinner;
    private List<String> documentNamesList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_inquiries);

        Intent intent=getIntent();
        cityLocation=intent.getStringExtra("cityLocation");

        back = findViewById(R.id.backButton);
        back.setOnClickListener(this);
        cityLocation=intent.getStringExtra("cityLocation");
        inquiriesForLocation=findViewById(R.id.inquiriesForLocation);
        inquiriesForLocation.setText("המפגעים הם עבור עיר המיקום הנוכחי שלך: " + cityLocation);

        inquiriesList=findViewById(R.id.inquiriesList);
        getInquiriessFromDB();

        back=findViewById(R.id.backButton);
        back.setOnClickListener(this);

        spinner = findViewById(R.id.spinner);
        // Initialize an array to store document names
        documentNamesList = new ArrayList<>();
        documentNamesList.add("Select other city");
        getAvialibleCitesNames();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == back.getId()) {
            finish();
        }

    }
    private void getAvialibleCitesNames()
    {
        db.collection("hazards")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Iterate through the documents
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            // Add each document ID to the list
                            documentNamesList.add(document.getId());
                        }
                        // Create an ArrayAdapter using a string array and a default spinner layout
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(InquiriesActivity.this, android.R.layout.simple_spinner_item, documentNamesList) {

                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);

                                // Set the hint text color to gray
                                if (position == 0) {
                                    ((TextView) view.findViewById(android.R.id.text1)).setTextColor(Color.GRAY);
                                } else {
                                    ((TextView) view.findViewById(android.R.id.text1)).setTextColor(Color.BLACK);
                                }

                                return view;
                            }

                            @Override
                            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                                View view = super.getDropDownView(position, convertView, parent);

                                // Set the hint text color to gray
                                if (position == 0) {
                                    ((TextView) view.findViewById(android.R.id.text1)).setTextColor(Color.GRAY);
                                } else {
                                    ((TextView) view.findViewById(android.R.id.text1)).setTextColor(Color.BLACK);
                                }

                                return view;
                            }
                        };

                        // Specify the layout to use when the list of choices appears
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        // Apply the adapter to the spinner
                        spinner.setAdapter(adapter);
                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                // Code to be executed when an item is selected
                                String selectedDocument = documentNamesList.get(position);

                                // You can perform actions based on the selected item
                                if (!selectedDocument.equals("Select other city")) {
                                    cityLocation=selectedDocument;
                                    inquiriesForLocation.setText("המפגעים הם עבור עיר המיקום הנוכחי שלך: " + cityLocation);
                                    getInquiriessFromDB();
                                }
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parentView) {
                                // Code to be executed when nothing is selected
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // Handle failures
                        System.out.println("Error getting documents: " + e.getMessage());
                    }
                });
    }

    private void getInquiriessFromDB() {
        DocumentReference docRef = db.collection("hazards").document(cityLocation);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    if (document.exists()) {
                        Map<String, Object> data = document.getData();
                        List<String> itemList = new ArrayList<>();
                        // Iterate through the map to access each field
                        for (Map.Entry<String, Object> entry : data.entrySet()) {
                            String fieldName = entry.getKey();
                            Object value = entry.getValue();
                            if (value instanceof Map) {
                                // Cast the value to a Map
                                Map<String, Object> nestedMap = (Map<String, Object>) value;
                                // Get the value of the "name" key
                                Object nameValue = nestedMap.get("name");
                                Object detailsValue = nestedMap.get("details");
                                Object statusValue = nestedMap.get("status");
                                if (nameValue != null && detailsValue!= null && statusValue!= null) {
                                    // Append data to the list as a string
                                    String fieldValueString = fieldName + " : " + nameValue.toString() + " opened a new hazard.\n" +
                                            "Details : "+detailsValue + "\nUpdated Status : "+statusValue;
                                    itemList.add(fieldValueString);
                                }
                            }
                        }

                        // Set up ListView with ArrayAdapter
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(InquiriesActivity.this, android.R.layout.simple_list_item_1, itemList);
                        inquiriesList.setAdapter(adapter);
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData().toString());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}
