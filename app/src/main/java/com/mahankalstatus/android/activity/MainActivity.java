package com.mahankalstatus.android.activity;

import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.mahankalstatus.android.R;
import com.mahankalstatus.android.adapter.DisplayStatusAdapter;
import com.mahankalstatus.android.bean.AddStatus;
import com.mahankalstatus.android.database.DatabaseHandler;
import com.mahankalstatus.android.databinding.ActivityMainBinding;
import com.mahankalstatus.android.listener.MyCallback;
import com.mahankalstatus.android.util.PreferenceConnector;
import com.mahankalstatus.android.util.Utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding activityBinding;
    private DatabaseHandler database;
    private boolean serviceFlag = false;
    private ArrayList<AddStatus> statusList = new ArrayList<>();
    private InterstitialAd mInterstitialAd;
    private int adCount = 0;
    private String versionCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        activityBinding.setActivity(this);
        setToolBar();
        init();
        getStatus();
        loadAdCount();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemShare: // Share application url
                String appPackageName = getPackageName();
                String url = "http://play.google.com/store/apps/details?id=" + appPackageName;
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, url);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                return true;
            case R.id.itemDisclaimer:
                showDisclaimerPopUp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Set tool bar
     */
    private void setToolBar() {
        activityBinding.toolbar.setTitle("Jai Shree Mahankal");
        activityBinding.toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(activityBinding.toolbar);
    }

    private void init() {
        try {
            adCount = PreferenceConnector.readInteger(MainActivity.this, PreferenceConnector.AD_SWIPE_COUNT, 0);
            database = new DatabaseHandler(this);
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int height = displaymetrics.heightPixels;
            int width = displaymetrics.widthPixels;
            PreferenceConnector.writeInteger(this, PreferenceConnector.DEVICE_WIDTH, width);
            PreferenceConnector.writeInteger(this, PreferenceConnector.DEVICE_HEIGHT, height);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * load ad count
     */
    private void loadAdCount() {
        if (Utility.isNetworkAvailable(MainActivity.this)) {
            adCount++;
            PreferenceConnector.writeInteger(MainActivity.this, PreferenceConnector.AD_SWIPE_COUNT, adCount);

            if (adCount >= 5) {
                adCount = 0;
                PreferenceConnector.writeInteger(MainActivity.this, PreferenceConnector.AD_SWIPE_COUNT, adCount);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadInterstitialAd();
                    }
                }, 4000);
            }
        }
    }

    /**
     * Interstitial video ad
     */
    private void loadInterstitialAd() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_ad_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
            }

            @Override
            public void onAdOpened() {
            }

            @Override
            public void onAdLeftApplication() {
            }

            @Override
            public void onAdClosed() {
            }
        });
    }

    /**
     * Get status
     */
    private void getStatus() {
        if (Utility.isNetworkAvailable(MainActivity.this)) {
            activityBinding.progressBar.setVisibility(View.VISIBLE);
            getVersionCodeFromServer();
        } else {
            getNewsFromLocalDataBase();
        }
    }

    /**
     * Get version code
     */
    private void getVersionCodeFromServer() {
        try {
            Utility.getVersionCode(new MyCallback() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    parseGetVersionCodeResponse(dataSnapshot);
                }

                @Override
                public void onFailed(DatabaseError databaseError) {
                    parseGetVersionCodeResponse(null);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Parse get version code response
     */
    private void parseGetVersionCodeResponse(final DataSnapshot dataSnapshot) {
        if (!isDestroyed()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {

                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            versionCode = child.getValue().toString();
                        }
                        if (!TextUtils.isEmpty(versionCode)) {
                            getTotalItemFromServer();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * Get total item
     */
    private void getTotalItemFromServer() {
        try {
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
     */
    private void parseTotalItemResponse(final DataSnapshot dataSnapshot) {
        if (!isDestroyed()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String totalTemp = "";
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            totalTemp = child.getValue().toString();
                        }
                        if (!TextUtils.isEmpty(totalTemp)) {
                            getDataFromLocalAndServer(totalTemp);
                        } else {
                            activityBinding.progressBar.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        activityBinding.progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * Get data from local and server
     */
    private void getDataFromLocalAndServer(String total) {
        int serverTotal = parseInt(total);
        int localTotal = database.getNewsCount();
        int difference = serverTotal - localTotal;
        int count = PreferenceConnector.readInteger(MainActivity.this, PreferenceConnector.AD_SWIPE_COUNT, 0);
        if (localTotal == 0 && count == 1) {
            getNewsFromServer();
        } else {
            if (difference > 0) {
                if (localTotal > 0) {
                    ArrayList<AddStatus> tempList = database.getAllNews();
                    String serverKey = tempList.get(localTotal - 1).getServerKey();
                    getMoreNewsFromServer(serverKey, difference);
                }
            } else {
                getNewsFromLocalDataBase();
            }
        }
    }

    /**
     * Get more news
     */
    private void getMoreNewsFromServer(final String serverKey, final int limit) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    serviceFlag = true;
                    Utility.getNewsData(serverKey, limit, new MyCallback() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            parseNewsResponse(dataSnapshot, true);
                        }

                        @Override
                        public void onFailed(DatabaseError databaseError) {
                            parseNewsResponse(null, true);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 1500);
    }

    /**
     * Parse news response
     *
     * @param dataSnapshot DataSnapshot object
     */
    private void parseNewsResponse(final DataSnapshot dataSnapshot, final boolean flag) {
        if (!isDestroyed() && serviceFlag) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        serviceFlag = false;
                        activityBinding.progressBar.setVisibility(View.GONE);
                        statusList.clear();
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            Map<String, Object> news = (Map<String, Object>) child.getValue();
                            String key = child.getKey();
                            if (news != null) {
                                String status = news.get("status").toString();
                                String id = news.get("id").toString();
                                String isApproved = news.get("isApproved").toString();
                                statusList.add(new AddStatus(status, id, key, isApproved));
                            } else {
                                Utility.showToast(MainActivity.this, "Unable to load status try again");
                            }
                        }
                        if (statusList.size() > 0) {
                            if (flag) {
                                statusList.remove(0);
                            }
                            addNewsInLocalDatabase(statusList);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * Get all status
     */
    private void getNewsFromServer() {
        try {
            serviceFlag = true;
            Utility.getAllNewsData(new MyCallback() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    parseStatusResponse(dataSnapshot, false);
                }

                @Override
                public void onFailed(DatabaseError databaseError) {
                    parseStatusResponse(null, false);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Parse news response
     */
    private void parseStatusResponse(final DataSnapshot dataSnapshot, final boolean flag) {
        if (!isDestroyed() && serviceFlag) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        serviceFlag = false;
                        activityBinding.progressBar.setVisibility(View.GONE);
                        statusList.clear();
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            Map<String, Object> news = (Map<String, Object>) child.getValue();
                            String key = child.getKey();
                            if (news != null) {
                                String status = news.get("status").toString();
                                String id = news.get("id").toString();
                                String isApproved = news.get("isApproved").toString();
                                statusList.add(new AddStatus(status, id, key, isApproved));
                            } else {
                                Utility.showToast(MainActivity.this, "Unable to load more status try again");
                            }
                        }
                        if (statusList.size() > 0) {
                            if (flag) {
                                statusList.remove(0);
                            }
                            addNewsInLocalDatabase(statusList);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * Add news in local database
     */
    private void addNewsInLocalDatabase(ArrayList<AddStatus> list) {
        try {
            for (AddStatus bean : list) {
                String isApproved = bean.getIsApproved();
                if (isApproved.equals("1")) {
                    database.addContact(bean);
                }
            }
            getNewsFromLocalDataBase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get news list from local database
     */
    private void getNewsFromLocalDataBase() {
        statusList.clear();
        activityBinding.progressBar.setVisibility(View.GONE);

        statusList = database.getAllNews();

        if (statusList == null || statusList.size() == 0) {
            if (!Utility.isNetworkAvailable(MainActivity.this)) {
                activityBinding.linearLayoutNetwork.setVisibility(View.VISIBLE);
            }
            return;
        }
        setAdapter();
    }

    /**
     * Set adapter
     */
    private void setAdapter() {
        if (statusList == null || statusList.size() == 0) {
            return;
        }
        if (Utility.isNetworkAvailable(MainActivity.this)) {
            int version = Utility.getVersionCode(MainActivity.this);
            int versionTemp = parseInt(versionCode);
            if (version != versionTemp) {
                Utility.showApplicationUpdatePopUp(MainActivity.this);
            }
        }
        Collections.reverse(statusList);
        activityBinding.recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        activityBinding.recyclerView.setAdapter(new DisplayStatusAdapter(MainActivity.this, statusList));
    }

    /**
     * Show disclaimer pop up
     */
    private void showDisclaimerPopUp() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_dis);

        dialog.setCancelable(false);
        dialog.show();

        TextView textViewOk = (TextView) dialog.findViewById(R.id.textViewOk);

        textViewOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }
}
