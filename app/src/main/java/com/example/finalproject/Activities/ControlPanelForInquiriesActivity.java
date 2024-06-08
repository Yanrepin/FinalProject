package com.example.finalproject.Activities;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.Classes.HazardInfo;
import com.example.finalproject.R;
import com.example.finalproject.UICompponents.CustomAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ControlPanelForInquiriesActivity extends AppCompatActivity implements View.OnClickListener{
    String cityOfUser;
    String userEmail;
    TextView inquiriesForControl;
    ListView inquiriesList;
    MaterialButton statusOpen;
    boolean statusOpenFlag=false;
    MaterialButton statusPending;
    boolean statusPendingFlag=false;
    MaterialButton statusClosed;
    boolean statusClosedFlag=false;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private List<String> documentNamesList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.control_panel_inquiries);

        Intent intent=getIntent();
        userEmail=intent.getStringExtra("user email");
        cityOfUser=intent.getStringExtra("user city");

        inquiriesForControl=findViewById(R.id.inquiriesForControl);
        inquiriesForControl.setText("Your city inquiries: " + cityOfUser);

        inquiriesList=findViewById(R.id.inquiriesList);


        statusOpen=findViewById(R.id.statusOpen);
        statusOpen.setOnClickListener(this);
        statusPending=findViewById(R.id.statusPending);
        statusPending.setOnClickListener(this);
        statusClosed=findViewById(R.id.statusClosed);
        statusClosed.setOnClickListener(this);
        statusOpen.performClick();


    }
    @Override
    public void onClick(View v) {

        if (v.getId() == statusOpen.getId()) {
            statusOpenFlag=!statusOpenFlag;
            getInquiriessFromDB( cityOfUser , statusOpenFlag, statusPendingFlag, statusClosedFlag);
        }
        else if (v.getId() == statusPending.getId()) {
            statusPendingFlag=!statusPendingFlag;
            getInquiriessFromDB( cityOfUser , statusOpenFlag, statusPendingFlag, statusClosedFlag);
        }
        else if (v.getId() == statusClosed.getId()) {
            statusClosedFlag=!statusClosedFlag;
            getInquiriessFromDB( cityOfUser , statusOpenFlag, statusPendingFlag, statusClosedFlag);
        }
    }

    private void getInquiriessFromDB(String cityOfUser, boolean statusOpenFlag, boolean statusPendingFlag, boolean statusClosedFlag) {
        DocumentReference docRef = db.collection("hazards").document(cityOfUser);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> data = document.getData();
                        List<HazardInfo> itemList = new ArrayList<>();
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
                                Object imgNameValue = nestedMap.get("imgName");
                                Object managerInfo=nestedMap.get("managerInfo");
                                if (nameValue != null && detailsValue != null && statusValue != null && imgNameValue != null && managerInfo!=null) {
                                    // Create HazardInfo object
                                    HazardInfo hazardInfo = new HazardInfo(fieldName,nameValue.toString(), detailsValue.toString(),
                                            statusValue.toString(), imgNameValue.toString(),managerInfo.toString());
                                    // Append data to the list as a string
                                    if ((statusValue.toString().equals("open") && statusOpenFlag) ||
                                            (statusValue.toString().equals("pending") && statusPendingFlag) ||
                                            (statusValue.toString().equals("closed") && statusClosedFlag)) {
                                        itemList.add(hazardInfo);
                                    }
                                }
                            }
                        }

                        // Set up ListView with ArrayAdapter
                        CustomAdapter adapter = new CustomAdapter(ControlPanelForInquiriesActivity.this, itemList,cityOfUser);
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
