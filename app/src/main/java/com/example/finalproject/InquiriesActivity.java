package com.example.finalproject;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class InquiriesActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_inquiries);
        back = findViewById(R.id.backButton);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == back.getId()) {
            finish();
        }

    }
}
