package com.raywenderlich.camelot;

import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by williamxia on 2/3/18.
 */

public class FirebaseAdaptor {
    //public static String timeStamp;
    public static int count = 0;
    public static void uploadTimeStamp(){
        //initial date
        Date d = new Date();
        String s = null;
        DateFormat na = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        s = na.format(d);



        //link firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("timeStamp");
        count++;
        String stringCount = String.valueOf(count);
        //timeStamp=timeStamp+stringCount;
        myRef.child(stringCount).setValue(s);

    }
}
