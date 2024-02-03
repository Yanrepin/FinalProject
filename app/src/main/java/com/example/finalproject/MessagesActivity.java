package com.example.finalproject;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MessagesActivity extends AppCompatActivity implements View.OnClickListener {
    ImageButton back;
    String cityLocation;
    TextView newsForLocation;
    private Object Firebase;
    ListView newsList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages);

        Intent intent=getIntent();
        cityLocation=intent.getStringExtra("cityLocation");
        newsForLocation=findViewById(R.id.newsForLocation);
        newsForLocation.setText("החדשות הם עבור עיר המיקום הנוכחי שלך: " + cityLocation);
        newsList=findViewById(R.id.newsList);
        getNewsFromDB();
        back=findViewById(R.id.backButton);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==back.getId())
        {
            finish();
        }
    }

    private void getNewsFromDB()
    {
        DocumentReference docRef = db.collection("messages").document(cityLocation);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> data = document.getData();
                        List<String> itemList = new ArrayList<>();
                        // Iterate through the map to access each field
                        for (Map.Entry<String, Object> entry : data.entrySet()) {
                            String fieldName = entry.getKey();
                            Object value = entry.getValue();
                            // Append data to the list as a string
                            String fieldValueString = fieldName + " : " + value.toString();
                            itemList.add(fieldValueString);
                        }

                        // Set up ListView with ArrayAdapter
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(MessagesActivity.this, android.R.layout.simple_list_item_1, itemList);
                        newsList.setAdapter(adapter);
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
