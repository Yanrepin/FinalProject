package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class OpenningActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opening_activity);
        // opening screen
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(2000);
                        Intent intent = new Intent (OpenningActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
