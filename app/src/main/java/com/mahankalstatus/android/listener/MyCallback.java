package com.mahankalstatus.android.listener;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public interface MyCallback {
    void onSuccess(DataSnapshot dataSnapshot);
    void onFailed(DatabaseError databaseError);
}
