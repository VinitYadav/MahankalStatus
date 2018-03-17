package com.mahankalstatus.android.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mahankalstatus.android.R;
import com.mahankalstatus.android.bean.AddStatus;
import com.mahankalstatus.android.databinding.ActivityAddStatusBinding;
import com.mahankalstatus.android.util.Constants;
import com.mahankalstatus.android.util.ProgressHelper;
import com.mahankalstatus.android.util.Utility;

public class AddStatusActivity extends AppCompatActivity {

    private ActivityAddStatusBinding activityBinding;
    private boolean serviceFlag = false;
    private ProgressHelper progressHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_status);
        activityBinding.setActivity(this);
        activityBinding.setBean(new AddStatus());
        init();
    }

    public void onClickSend() {
        if (!TextUtils.isEmpty(activityBinding.getBean().getStatus())) {
            if (Utility.isNetworkAvailable(AddStatusActivity.this)) {
                serviceFlag = true;
                sendStatus(activityBinding.getBean());
            } else {
                Utility.showToast(AddStatusActivity.this, "Network error");
            }

        } else {
            Utility.showToast(this, "Enter status");
        }
    }

    /**
     * Click on total button
     */
    public void onClickTotal() {
        Intent intent = new Intent(AddStatusActivity.this, TotalActivity.class);
        startActivity(intent);
    }

    /**
     * Click on all status button
     */
    public void onClickStatus() {
        Intent intent = new Intent(AddStatusActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void init() {
        progressHelper = new ProgressHelper(AddStatusActivity.this);
    }

    /**
     * Add new status in database
     */
    public void sendStatus(final AddStatus bean) {
        progressHelper.show();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.TABLE_ALL_STATUS);
        final String newsId = mDatabase.push().getKey();
        mDatabase.child(newsId).setValue(bean);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                if (serviceFlag) {
                    serviceFlag = false;
                    progressHelper.dismiss();
                    Utility.showToast(AddStatusActivity.this, "Status added successfully");
                    Intent intent = new Intent(AddStatusActivity.this, TotalActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                serviceFlag = false;
                progressHelper.dismiss();
                Utility.showToast(AddStatusActivity.this, "Status not added");
            }
        };
        mDatabase.addValueEventListener(postListener);
    }
}
