package com.example.finalproject.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.Classes.JavaMailAPI;
import com.example.finalproject.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactUsActivity extends AppCompatActivity implements View.OnClickListener{
    String cityLocation;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Spinner spinner;
    TextInputEditText name,phone,details;
    Button submit;
    Map<String, String> emailMap;
    private List<String> documentNamesList;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_us);

        Intent intent=getIntent();
        cityLocation = intent.getStringExtra("cityLocation");


        spinner = findViewById(R.id.spinner);
        // Initialize an array to store document names
        documentNamesList = new ArrayList<>();
        documentNamesList.add("Select other city");
        emailMap = new HashMap<>();
        getAvialibleCitesNames();

        name=findViewById(R.id.name);
        phone=findViewById(R.id.phone);
        details=findViewById(R.id.details);
        submit=findViewById(R.id.submit);
        submit.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        if(v.getId()==submit.getId() && inputCheck(name.getText().toString(), phone.getText().toString(), details.getText().toString()))
        {
            sendMail();
        }
    }

    private void sendMail() {
        String mail=emailMap.get(spinner.getSelectedItem().toString());
        String message="Send by: "+name.getText().toString()+
                "\nPhone: "+phone.getText().toString()+
                "\nMessage context: "+details.getText().toString();
        JavaMailAPI javaMailAPI = new JavaMailAPI(this,mail,"New Contact Us",message);
        javaMailAPI.execute();
    }

    private boolean inputCheck(String name, String phone,String details)
    {
        if(name.length()==0)
        {
            Toast.makeText(ContactUsActivity.this, "Empty input for name",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(phone.length()!=10)
        {
            Toast.makeText(ContactUsActivity.this, "Invalid input for Phone, need to be 10 digits",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(details.length()==0)
        {
            Toast.makeText(ContactUsActivity.this, "Empty input for details",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(spinner.getSelectedItem().toString().equals("Select other city"))
        {
            Toast.makeText(ContactUsActivity.this, "City not selected",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private void getAvialibleCitesNames()
    {
        db.collection("requests")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Iterate through the documents
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            // Add each document ID to the list
                            documentNamesList.add(document.getId());
                            // Extract email field from each document
                            String email = document.getString("email");

                            // Add email to the map with document ID as key
                            if (email != null) {
                                emailMap.put(document.getId(), email);
                            }
                        }
                        // Create an ArrayAdapter using a string array and a default spinner layout
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ContactUsActivity.this, android.R.layout.simple_spinner_item, documentNamesList) {

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
}
