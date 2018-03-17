package com.mahankalstatus.android.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.mahankalstatus.android.R;
import com.mahankalstatus.android.bean.AddStatus;
import com.mahankalstatus.android.listener.MyCallback;

import java.util.Map;

public class Utility {

    /**
     * Change icons color
     */
    public static void changeColor(Activity activity, ImageView imageView, int color) {
        DrawableCompat.setTint(imageView.getDrawable(),
                ContextCompat.getColor(activity, color));
    }

    /**
     * Show toast
     *
     * @param context Context object
     * @param msg     Display message
     */
    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Check network availability
     *
     * @param context Context object
     * @return True or False
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get application version code
     */
    public static int getVersionCode(Activity activity) {
        try {
            PackageInfo pInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
            return pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Get save version code
     */
    public static int getSaveVersionCode(Activity activity) {
        return PreferenceConnector.readInteger(activity, PreferenceConnector.APP_VERSION_CODE, 0);
    }

    /**
     * Get all news
     */
    public static void getNewsData(String serverKey, int limit, final MyCallback myCallback) {

        try {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Constants.TABLE_ALL_STATUS);
            int next = 10;
            //Query query = ref.orderByChild("id").startAt(startId).endAt(endId);
            Query query = ref.orderByKey().startAt(serverKey).limitToFirst(limit + 1);
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    myCallback.onSuccess(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    myCallback.onFailed(databaseError);
                }
            };
            query.addValueEventListener(listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get all news
     */
    public static void getAllNewsData(final MyCallback myCallback) {

        try {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Constants.TABLE_ALL_STATUS);
            Query query = ref.orderByKey();
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    myCallback.onSuccess(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    myCallback.onFailed(databaseError);
                }
            };
            query.addValueEventListener(listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get total item
     *
     * @param myCallback MyCallback object
     */
    public static void getTotalItem(final MyCallback myCallback) {
        try {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Constants.TABLE_TOTAL_ITEM);
            Query query = ref.orderByKey();
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    myCallback.onSuccess(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    myCallback.onFailed(databaseError);
                }
            };
            query.addValueEventListener(listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get version code
     *
     * @param myCallback MyCallback object
     */
    public static void getVersionCode(final MyCallback myCallback) {
        try {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Constants.TABLE_VERSION_CODE);
            Query query = ref.orderByKey();
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    myCallback.onSuccess(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    myCallback.onFailed(databaseError);
                }
            };
            query.addValueEventListener(listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Add news in other database with section type
     *
     * @param context   Context
     * @param tableName Table name
     * @param bean      Bean object
     */
    public static void sendNewsInOtherDatabase(FirebaseApp secondApp, final Context context, String tableName, AddStatus bean) {

        FirebaseDatabase secondDatabase = FirebaseDatabase.getInstance(secondApp);
        Map<String, String> time = ServerValue.TIMESTAMP;
        secondDatabase.getReference().setValue(time);

        DatabaseReference mDatabase = secondDatabase.getReference(tableName);
        String newsId = mDatabase.push().getKey();
        mDatabase.child(newsId).setValue(bean);

        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Utility.showToast(context, "Other news added successfully");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Utility.showToast(context, "Other news changed");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Utility.showToast(context, "Other news removed");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Utility.showToast(context, "Other news moved");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Utility.showToast(context, "Other news cancelled");
            }
        });
    }

    /**
     * Show application update pop up
     */
    public static void showApplicationUpdatePopUp(final Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_application_update);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView imageViewGooglePlay = (ImageView) dialog.findViewById(R.id.imageViewGooglePlay);
        ImageView imageViewClose = (ImageView) dialog.findViewById(R.id.imageViewClose);

        imageViewGooglePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String appPackageName = activity.getPackageName();
                try {
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });

        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.setCancelable(false);
        dialog.show();
    }
}
