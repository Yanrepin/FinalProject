package com.example.finalproject.Classes;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class HazardInfo {
    private String fieldName;
    private String name;
    private String details;
    private String status;
    private String imgName;
    private String managerInfo;

    public HazardInfo(String fieldName,String name, String details, String status, String imgName,String managerInfo) {
        this.fieldName=fieldName;
        this.name = name;
        this.details = details;
        this.status = status;
        this.imgName = imgName;
        this.managerInfo=managerInfo;
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
    public String getFieldName() {
        return this.fieldName;
    }
    public String getManagerInfo(){return this.managerInfo;}

}
