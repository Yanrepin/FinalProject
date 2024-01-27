package com.example.finalproject;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;


public class AddNewUserActivity extends AppCompatActivity implements View.OnClickListener{
    ImageButton back;

    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_user);

        back = findViewById(R.id.backButton);
        back.setOnClickListener(this);
        Intent intent=getIntent();
        userEmail=intent.getStringExtra("user email");




    }
    @Override
    public void onClick(View v) {
        if (v.getId() == back.getId()) {
            finish();
        }
    }
}
