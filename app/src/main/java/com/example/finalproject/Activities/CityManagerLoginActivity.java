package com.example.finalproject.Activities;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CityManagerLoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText emailCityManager;
    EditText passCityManager;
    Button submitCityManager;
    FirebaseAuth mAuth;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_manager_login);

        emailCityManager=findViewById(R.id.emailCityManager);
        passCityManager=findViewById(R.id.passwordCityManager);
        submitCityManager=findViewById(R.id.submitCityManager);
        submitCityManager.setOnClickListener(this);


        mAuth = FirebaseAuth.getInstance();
        String email=new String("yanrepinyan@gmail.con");
        String pass=new String("yan1997");

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //if user logged
        }
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

    @Override
    public void onClick(View v) {

        if(v.getId()==submitCityManager.getId())
        {
            String validResult=inputCheck(emailCityManager.getText().toString(),passCityManager.getText().toString());
            if(!validResult.equals(""))
            {
                Toast.makeText(CityManagerLoginActivity.this, validResult,
                        Toast.LENGTH_SHORT).show();
            }
            else
            {
                //checking if user exists
                mAuth.signInWithEmailAndPassword(emailCityManager.getText().toString(), passCityManager.getText().toString())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.w(TAG, "signinUserWithEmail:success");
                                    Intent i=new Intent(CityManagerLoginActivity.this, CityManagerControlActivity.class);
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    i.putExtra("user email",user.getEmail());
                                    startActivity(i);
                                } else
                                {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signinUserWithEmail:failure", task.getException());
                                    Toast.makeText(CityManagerLoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

        }
    }
}
