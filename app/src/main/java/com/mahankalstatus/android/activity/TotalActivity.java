package com.mahankalstatus.android.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mahankalstatus.android.R;
import com.mahankalstatus.android.databinding.ActivityTotalBinding;
import com.mahankalstatus.android.listener.MyCallback;
import com.mahankalstatus.android.util.Constants;
import com.mahankalstatus.android.util.ProgressHelper;
import com.mahankalstatus.android.util.Utility;

public class TotalActivity extends AppCompatActivity {

    private ActivityTotalBinding activityBinding;
    private ProgressHelper progressHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_total);
        activityBinding.setActivity(this);

        progressHelper = new ProgressHelper(TotalActivity.this);
        getTotalItemFromServer();
    }

    public void onClickSend() {
        String totalItem = activityBinding.editText.getText().toString();
        if (!TextUtils.isEmpty(totalItem)) {
            addTotalItem(totalItem);
        } else {
            Utility.showToast(TotalActivity.this, "Enter value");
        }
    }

    public void onClickSendVersion() {
        String version = activityBinding.editTextVersion.getText().toString();
        if (!TextUtils.isEmpty(version)) {
            addVersionCode(version);
        } else {
            Utility.showToast(TotalActivity.this, "Enter version");
        }
    }

    /**
     * Add total item of news
     *
     * @param total Total item of news
     */
    public void addTotalItem(String total) {
        progressHelper.show();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.TABLE_TOTAL_ITEM);
        mDatabase.child("item").setValue(total);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressHelper.dismiss();
                Utility.showToast(TotalActivity.this, "Total count added successfully");
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressHelper.dismiss();
                Utility.showToast(TotalActivity.this, "Count not added");
            }
        };
        mDatabase.addValueEventListener(postListener);
    }

    /**
     * Get total item
     */
    private void getTotalItemFromServer() {
        try {
            //progressHelper.show();
            Utility.getTotalItem(new MyCallback() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    parseTotalItemResponse(dataSnapshot);
                }

                @Override
                public void onFailed(DatabaseError databaseError) {
                    parseTotalItemResponse(null);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Parse total item response
     *
     * @param dataSnapshot DataSnapshot object
     */
    private void parseTotalItemResponse(final DataSnapshot dataSnapshot) {
        if (!isDestroyed()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //progressHelper.dismiss();
                    try {
                        String totalTemp = "";
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            totalTemp = child.getValue().toString();
                        }
                        activityBinding.editText.setText(totalTemp);
                    } catch (Exception e) {
                        Utility.showToast(TotalActivity.this, "Something wrong");
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * Add version code
     *
     * @param total Total item of news
     */
    public void addVersionCode(String total) {
        progressHelper.show();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.TABLE_VERSION_CODE);
        mDatabase.child("version_code").setValue(total);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressHelper.dismiss();
                Utility.showToast(TotalActivity.this, "Version code added successfully");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressHelper.dismiss();
                Utility.showToast(TotalActivity.this, "Count not added");
            }
        };
        mDatabase.addValueEventListener(postListener);
    }
}
