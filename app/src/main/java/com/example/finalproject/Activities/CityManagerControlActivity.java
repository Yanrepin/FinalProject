package com.example.finalproject.Activities;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class CityManagerControlActivity extends AppCompatActivity implements View.OnClickListener{

    TextView welcome;
    String userEmail;
    String userCity;
    String userRule;
    Button appManager;
    Button addNewMassage;
    Button inquiriesHandler;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_manager_control_page);

        addNewMassage=findViewById(R.id.addNewMassage);
        addNewMassage.setOnClickListener(this);
        welcome=findViewById(R.id.welcome);
        appManager=findViewById(R.id.appManager);
        appManager.setOnClickListener(this);
        inquiriesHandler=findViewById(R.id.inquiriesHandler);
        inquiriesHandler.setOnClickListener(this);

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
        getUserNameFromData();


    }
    @Override
    public void onClick(View v) {

        if (v.getId() == appManager.getId()) {
            Intent i=new Intent(this, AddOrRemoveUsersActivity.class);
            i.putExtra("user email",userEmail);
            startActivity(i);
        }
        if (v.getId() == addNewMassage.getId()) {
            Intent i=new Intent(this, AddNewMessageToCityActivity.class);
            i.putExtra("user email",userEmail);
            i.putExtra("user city",userCity);
            i.putExtra("user rule",userRule);
            startActivity(i);
        }
        if(v.getId()==inquiriesHandler.getId())
        {
            Intent i=new Intent(this, ControlPanelForInquiriesActivity.class);
            i.putExtra("user email",userEmail);
            i.putExtra("user city",userCity);
            i.putExtra("user rule",userRule);
            startActivity(i);
        }
    }

    private void getUserNameFromData()
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
                            userCity= (String) data.get("city");
                            userRule=(String) data.get("rule");
                            welcome.setText("Hi  "+data.get("name") + "\nIn rule "+data.get("rule")+" of city "+data.get("city"));
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
