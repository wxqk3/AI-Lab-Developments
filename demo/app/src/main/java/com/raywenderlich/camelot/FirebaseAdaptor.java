package com.raywenderlich.camelot;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by williamxia on 2/3/18.
 */

public class FirebaseAdaptor {
    //public static String timeStamp;

    private StorageReference mStorageRef;


    public FirebaseAdaptor(){

        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    public File getFile(){

        File localFile = null;
        try {
            localFile = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        StorageReference riversRef = mStorageRef.child("images/profile.jpg");

        riversRef.getFile(localFile);

        return localFile;
    }

    public void upload(String filePath){

        Uri file = Uri.fromFile(new File(filePath));

        String date = new Date().toString();
        String fileName = "recordings/test/".concat(date).concat(".3gp");

        System.out.println(fileName);
        StorageReference riversRef = mStorageRef.child(fileName);

        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });
    }




}
