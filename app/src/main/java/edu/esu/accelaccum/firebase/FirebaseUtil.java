package edu.esu.accelaccum.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by hanke.kimm on 3/24/17.
 */

public class FirebaseUtil {

    public static DatabaseReference getFirebaseDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference();
    }

}
