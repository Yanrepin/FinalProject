package com.example.finalproject.Activities;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NewHazardActivity extends AppCompatActivity implements View.OnClickListener {

    EditText descriptionInput;
     EditText nameInput;
     Button submit;
    ImageView uploadedImage;
    TextView date;
    ImageButton imageUploadButton;
    ProgressBar progressBar;
    ImageView checkImg;
    String cityLocation;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_hazard);

        Intent intent=getIntent();
        cityLocation=intent.getStringExtra("cityLocation");



        submit=findViewById(R.id.sendNewHazard);
        submit.setOnClickListener(this);

        imageUploadButton = findViewById(R.id.imageUploadButton);
        imageUploadButton.setOnClickListener(this);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        checkImg=findViewById(R.id.checkImg);
        checkImg.setVisibility(View.GONE);

        descriptionInput = findViewById(R.id.descriptionInput);
        nameInput = findViewById(R.id.nameInput);
        uploadedImage = findViewById(R.id.uploadedImage);
        date=findViewById(R.id.dateTextView);

        // Get the current date and time
        Date currentDate = new Date();
        // Format the date and time using a specific pattern (optional)
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDateTime = formatter.format(currentDate);
        date.setText("Date: "+formattedDateTime);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==submit.getId())
        {
            handleSubmitNewHazard();
        }
        else if(v.getId()==imageUploadButton.getId())
        {
            checkCameraPermission();
        }
    }

    private void handleSubmitNewHazard()
    {
        // Get the current date and time
        Date currentDate = new Date();
        // Format the date and time using a specific pattern (optional)
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = formatter.format(currentDate);

        //if input empty
        String name=nameInput.getText().toString();
        String description=descriptionInput.getText().toString();
        if(description.equals("") || name.equals(""))
        {
            Toast.makeText(NewHazardActivity.this, "Invalid input for name/description",Toast.LENGTH_SHORT).show();
            return;
        }
        String imgName=name+formattedDateTime+ ".jpg";
        uploadImageToStorage(uploadedImage,imgName,progressBar,checkImg);
        saveHazardDetailsInDB(cityLocation,description, name, imgName, formattedDateTime,"open");
    }
    private void saveHazardDetailsInDB(String cityOfUser, String details, String name, String imgName, String currentDate, String status) {
        // Reference to the document where the subcollection will be updated
        DocumentReference docRef = db.collection("hazards").document(cityOfUser);

        // Create a Map to represent the data for the new hazard
        Map<String, Object> hazardMap = new HashMap<>();
        hazardMap.put("details", details);
        hazardMap.put("name", name);
        hazardMap.put("imgName", imgName);
        hazardMap.put("status", status);
        hazardMap.put("managerInfo","");

        // Update the data in the document with currentDate as the key
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(currentDate, hazardMap);

        // Update the document with the new hazard detail
        docRef.set(updateMap, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Firestore", "Document updated with ID: " + currentDate);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Firestore", "Error updating document", e);
            }
        });
    }

    //uploding th img to storage cloud of firebase
    private void uploadImageToStorage(ImageView uploadedImage , String imgName, ProgressBar progressBar , ImageView checkImg)
    {
        if(progressBar!=null)
        {
            progressBar.setVisibility(View.VISIBLE);
        }

        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();
        StorageReference mountainsRef = storageRef.child(imgName);

        // Get the data from an ImageView as bytes
        uploadedImage.setDrawingCacheEnabled(true);
        uploadedImage.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) uploadedImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);

        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.d(TAG, "Upload is " + progress + "% done");
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "Upload is paused");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                Log.d(TAG, "Upload is done");
                if(progressBar!=null)
                {
                    progressBar.setVisibility(View.GONE);
                }
                if(checkImg!=null)
                {
                    checkImg.setVisibility(View.VISIBLE);
                    Toast.makeText(NewHazardActivity.this, "New Hazard is sent to the city management", Toast.LENGTH_SHORT).show();

                }
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(checkImg!=null)
                        {
                            checkImg.setVisibility(View.GONE);
                        }
                    }
                }, 2000);
            }
        });
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null) {
                // Handle image picked from gallery
                Uri selectedImageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                    uploadedImage.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                // Handle image captured from camera
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                uploadedImage.setImageBitmap(imageBitmap);
            }
        }
    }

    private static final int CAMERA_PERMISSION_REQUEST = 100;

    private void checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
            } else {
                dispatchTakePictureIntent();
            }
        } else {
            // On versions lower than Android 6.0, permissions are granted at install time
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("CameraPermission", "Permission granted");
                dispatchTakePictureIntent();
            } else {
                Log.d("CameraPermission", "Permission denied");
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
