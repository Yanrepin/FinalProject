package com.example.finalproject;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class AddNewMessageToCity extends AppCompatActivity implements View.OnClickListener{
    ImageButton back;
    String userEmail;
    String cityOfUser;
    TextView message;
    Button addMessage;
    String serial;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_massage_to_city);
        back = findViewById(R.id.backButton);
        message=findViewById(R.id.newMessage);
        addMessage=findViewById(R.id.submitMessage);
        back.setOnClickListener(this);
        addMessage.setOnClickListener(this);

        Intent intent=getIntent();
        userEmail=intent.getStringExtra("user email");
        cityOfUser=intent.getStringExtra("user city");



    }
    @Override
    public void onClick(View v) {
        if (v.getId() == back.getId()) {
            finish();
        }
        if (v.getId() == addMessage.getId() && !message.getText().toString().equals("")) {
            addMessage(message.getText().toString());
        }

    }
    private void addMessage(String messageContext) {
        DocumentReference docRef = db.collection("messages").document(cityOfUser);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> data = document.getData();
                        if(data!=null)
                        {
                            String highestKey = null;
                            for (String key : data.keySet())
                            {
                                try {
                                    int keyInt = Integer.parseInt(key);
                                    if (highestKey == null || keyInt > Integer.parseInt(highestKey)) {
                                        highestKey = key;
                                    }
                                } catch (NumberFormatException e) {
                                    // Handle non-integer keys (if any)
                                }
                            }
                            if (highestKey == null) {
                                serial = "0";
                            }
                            else {
                                serial = String.valueOf(Integer.parseInt(highestKey) + 1);
                            }
                            // Create a Map to represent the data you want to add
                            Map<String, Object> messageMap = new HashMap<>();
                            messageMap.put(serial, messageContext);
                            // Use set with merge option to add the new field without overwriting existing data
                            docRef.set(messageMap, SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("Firestore", "Document successfully updated with new field!"+messageMap);
                                            Toast.makeText(AddNewMessageToCity.this, "הודעה חדשה נשלחה לתושבי העיר שלך!",
                                                    Toast.LENGTH_SHORT).show();
                                            showNotification(messageContext);
                                            message.setText("");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("Firestore", "Error updating document", e);
                                        }
                                    });
                        }
                        Log.d("Firestore", "DocumentSnapshot data: " + document.getData().toString());
                    } else {
                        Log.d("Firestore", "No such document");
                    }
                } else {
                    Log.d("Firestore", "get failed with ", task.getException());
                }
            }
        });
    }


    private void showNotification(String notificationMessage) {
        // Create a notification channel for Android 8.0 (API level 26) and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "channel_id",
                    "My Notification Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        // Create a notification using NotificationCompat.Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("New message to "+cityOfUser+ " was created")
                .setContentText(notificationMessage)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        // Show the notification
        NotificationManager manager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            manager = getSystemService(NotificationManager.class);
        }
        manager.notify(1337, builder.build());
    }
}
