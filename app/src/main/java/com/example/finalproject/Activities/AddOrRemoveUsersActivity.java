package com.example.finalproject.Activities;


import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AddOrRemoveUsersActivity extends AppCompatActivity implements View.OnClickListener{
    ImageButton back;
    String userEmail;
    FirebaseAuth mAuth;
    TextView emailInput;
    TextView passInput;
    TextView ruleInput;
    TextView cityInput;
    TextView nameInput;
    Button add;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_remove_user);

        back = findViewById(R.id.backButton);
        back.setOnClickListener(this);
        Intent intent=getIntent();
        userEmail=intent.getStringExtra("user email");
        emailInput=findViewById(R.id.addNewUserEmailInput);
        passInput=findViewById(R.id.addNewUserPasswordInput);
        ruleInput=findViewById(R.id.addNewUserRuleInput);
        cityInput=findViewById(R.id.addNewUserCityInput);
        nameInput=findViewById(R.id.addNewUserNameInput);
        add=findViewById(R.id.addNewUserButton);
        add.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        if (v.getId() == back.getId()) {
            finish();
        }
        if (v.getId() == add.getId()) {
            String validResult=inputCheck(emailInput.getText().toString(),passInput.getText().toString());
            if(!inputCheck(emailInput.getText().toString(),passInput.getText().toString()).equals(""))
            {
                Toast.makeText(AddOrRemoveUsersActivity.this, validResult,Toast.LENGTH_SHORT).show();
            }
            else if(ruleInput.getText().toString().equals("") || cityInput.getText().toString().equals("") || nameInput.getText().toString().equals(""))
            {
                Toast.makeText(AddOrRemoveUsersActivity.this, "Invalid input for name/rule/city",Toast.LENGTH_SHORT).show();
            }
            else{
                addHandler(emailInput.getText().toString(),passInput.getText().toString(),cityInput.getText().toString(),nameInput.getText().toString(),ruleInput.getText().toString());
                emailInput.setText("");
                emailInput.setHint("Email");
                passInput.setText("");
                passInput.setHint("Password");
                cityInput.setText("");
                cityInput.setHint("City");
                nameInput.setText("");
                nameInput.setHint("Name");
                ruleInput.setText("");
                ruleInput.setHint("Rule");
            }
        }
    }



    private void addHandler(String email,String pass,String city,String name,String rule)
    {
        DocumentReference docRef = db.collection("users").document(email);
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, pass)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    // Sign in success, update UI with the signed-in user's information
                    Log.w(TAG, "createUserWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    Map<String, Object> emptyData = new HashMap<>();
                    // Use Firestore's get() method to check if the document already exists
                    DocumentReference hazardDocumentRef = db.collection("hazards").document(city);
                    DocumentReference messagesDocumentRef = db.collection("messages").document(city);

                    hazardDocumentRef.get().addOnCompleteListener(hazardTask -> {
                        if (hazardTask.isSuccessful()) {
                            DocumentSnapshot hazardSnapshot = hazardTask.getResult();
                            // Check if the document already exists
                            if (!hazardSnapshot.exists()) {
                                // Document does not exist, proceed with creating it
                                hazardDocumentRef.set(emptyData, SetOptions.merge());
                            }
                        } else {
                            // Handle the error
                            Exception e = hazardTask.getException();
                            if (e != null) {
                                e.printStackTrace();
                            }
                        }
                    });

                    // Repeat the process for the "messages" collection
                    messagesDocumentRef.get().addOnCompleteListener(messagesTask -> {
                        if (messagesTask.isSuccessful()) {
                            DocumentSnapshot messagesSnapshot = messagesTask.getResult();
                            // Check if the document already exists
                            if (!messagesSnapshot.exists()) {
                                // Document does not exist, proceed with creating it
                                messagesDocumentRef.set(emptyData, SetOptions.merge());
                            }
                        } else {
                            // Handle the error
                            Exception e = messagesTask.getException();
                            if (e != null) {
                                e.printStackTrace();
                            }
                        }
                    });

                    // Create a Map to represent the data you want to add
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("city", city);
                    userMap.put("name", name);
                    userMap.put("rule", rule);
                    // Use set with merge option to add the new field without overwriting existing data
                    docRef.set(userMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Firestore", "Document successfully updated with new field!"+userMap);
                                Toast.makeText(AddOrRemoveUsersActivity.this, "נוצר יוזר חדש בהצלחה!",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Firestore", "Error updating document", e);
                            }
                        });
                }
                else
                {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(AddOrRemoveUsersActivity.this, "יוזר זה כבר קיים!",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
    private static final String EMAIL_REGEX =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z]).{6,}$";

    private String inputCheck(String email,String pass)
    {
        Pattern patternEmail = Pattern.compile(EMAIL_REGEX);
        // Create a matcher object
        Matcher matcherEmail = patternEmail.matcher(email);
        if (!matcherEmail.matches())
        {
            return "Email not valid";
        }
        Pattern patternPass = Pattern.compile(PASSWORD_REGEX);
        // Create a matcher object
        Matcher matcherPass = patternPass.matcher(pass);
        if (!matcherPass.matches())
        {
            return "Password not valid. must contain 6 chars, at least one number , at least one small letter";
        }
        return "";
    }
}
