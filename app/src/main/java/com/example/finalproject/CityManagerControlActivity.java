package com.example.finalproject;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class CityManagerControlActivity extends AppCompatActivity implements View.OnClickListener{
    ImageButton back;
    TextView welcome;
    String userEmail;
    ImageView appManager;
    ImageView addNewMassage;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_manager_control_page);
        back = findViewById(R.id.backButton);
        back.setOnClickListener(this);
        addNewMassage=findViewById(R.id.addNewMassage);
        addNewMassage.setOnClickListener(this);
        welcome=findViewById(R.id.welcome);
        appManager=findViewById(R.id.appManager);
        appManager.setOnClickListener(this);

        Intent intent=getIntent();
        userEmail=intent.getStringExtra("user email");
        if(!userEmail.equals("yanrepinyan@gmail.com"))
        {
            appManager.setVisibility(View.INVISIBLE);
        }
        else
        {
            appManager.setVisibility(View.VISIBLE);
        }
        setUserNameFromData();


    }
    @Override
    public void onClick(View v) {
        if (v.getId() == back.getId()) {
            finish();
        }
        if (v.getId() == appManager.getId()) {
            Intent i=new Intent(this, AddOrRemoveUsersActivity.class);
            i.putExtra("user email",userEmail);
            startActivity(i);
        }
        if (v.getId() == addNewMassage.getId()) {
            Intent i=new Intent(this, AddNewMessageToCity.class);
            i.putExtra("user email",userEmail);
            startActivity(i);
        }
    }

    private void setUserNameFromData()
    {
        DocumentReference docRef = db.collection("users").document(userEmail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> data = document.getData();
                        if (data.containsKey("name") && data.containsKey("rule") && data.containsKey("city")) {
                            welcome.setText("שלום "+data.get("name") + "\nבתפקיד "+data.get("rule")+" בעיר "+data.get("city"));
                        }
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
