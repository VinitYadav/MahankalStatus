package com.mahankalstatus.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

import com.mahankalstatus.android.R;
import com.mahankalstatus.android.util.PreferenceConnector;
import com.mahankalstatus.android.util.Utility;

import java.io.File;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        getDeviceInfo();
        saveVersionCode();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                Intent intent = new Intent(SplashActivity.this, AddStatusActivity.class);
                startActivity(intent);
            }
        }, 4000);
    }

    /**
     * Get device info
     */
    private void getDeviceInfo() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        PreferenceConnector.writeInteger(this, PreferenceConnector.DEVICE_WIDTH, width);
        PreferenceConnector.writeInteger(this, PreferenceConnector.DEVICE_HEIGHT, height);
    }

    /**
     * Save version code in shared preference
     */
    private void saveVersionCode() {
        int code = PreferenceConnector.readInteger(SplashActivity.this, PreferenceConnector.APP_VERSION_CODE, 0);
        if (code == 0) {
            PreferenceConnector.writeInteger(SplashActivity.this,
                    PreferenceConnector.APP_VERSION_CODE, Utility.getVersionCode(SplashActivity.this));
        } else {
            int oldCode = Utility.getSaveVersionCode(SplashActivity.this);
            int newCode = Utility.getVersionCode(SplashActivity.this);
            if (newCode > oldCode) {
                try {
                    clearApplicationData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void clearApplicationData() {
        File cacheDirectory = getCacheDir();
        File applicationDirectory = new File(cacheDirectory.getParent());
        if (applicationDirectory.exists()) {
            String[] fileNames = applicationDirectory.list();
            for (String fileName : fileNames) {
                if (!fileName.equals("lib")) {
                    deleteFile(new File(applicationDirectory, fileName));
                }
            }
            PreferenceConnector.clear(SplashActivity.this);
        }
        PreferenceConnector.writeInteger(SplashActivity.this,
                PreferenceConnector.APP_VERSION_CODE, Utility.getVersionCode(SplashActivity.this));
    }

    private boolean deleteFile(File file) {
        boolean deletedAll = true;
        if (file != null) {
            if (file.isDirectory()) {
                String[] children = file.list();
                for (int i = 0; i < children.length; i++) {
                    deletedAll = deleteFile(new File(file, children[i])) && deletedAll;
                }
            } else {
                deletedAll = file.delete();
            }
        }

        return deletedAll;
    }
}
