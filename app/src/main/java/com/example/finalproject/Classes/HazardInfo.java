package com.example.finalproject.Classes;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class HazardInfo {
    private String fieldName;
    private String name;
    private String details;
    private String status;
    private String imgName;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference imgRef;

    public HazardInfo(String fieldName,String name, String details, String status, String imgName) {
        this.fieldName=fieldName;
        this.name = name;
        this.details = details;
        this.status = status;
        this.imgName = imgName;
        this.storage = FirebaseStorage.getInstance();
        this.storageRef = storage.getReference();
        this.imgRef = storageRef.child(imgName);
    }

    public String getName() {
        return this.name;
    }
    public String getDetails() {
        return this.details;
    }
    public String getStatus() {
        return this.status;
    }
    public String getImageName() {
        return this.imgName;
    }
    public StorageReference getImageRef() {
        return this.imgRef;
    }

    public String getFieldName() {
        return this.fieldName;
    }

}
