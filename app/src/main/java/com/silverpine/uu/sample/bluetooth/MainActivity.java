package com.silverpine.uu.sample.bluetooth;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.silverpine.uu.bluetooth.UUBluetooth;
import com.silverpine.uu.core.UUCore;

public class MainActivity extends AppCompatActivity
{
    private static final String LOG_TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(LOG_TAG, "UUCore.BUILD_VERSION: " + UUCore.BUILD_VERSION);
        Log.d(LOG_TAG, "UUCore.BUILD_BRANCH: " + UUCore.BUILD_BRANCH);
        Log.d(LOG_TAG, "UUCore.BUILD_COMMIT_HASH: " + UUCore.BUILD_COMMIT_HASH);
        Log.d(LOG_TAG, "UUCore.BUILD_DATE: " + UUCore.BUILD_DATE);

        Log.d(LOG_TAG, "UUBluetooth.BUILD_VERSION: " + UUBluetooth.BUILD_VERSION);
        Log.d(LOG_TAG, "UUBluetooth.BUILD_BRANCH: " + UUBluetooth.BUILD_BRANCH);
        Log.d(LOG_TAG, "UUBluetooth.BUILD_COMMIT_HASH: " + UUBluetooth.BUILD_COMMIT_HASH);
        Log.d(LOG_TAG, "UUBluetooth.BUILD_DATE: " + UUBluetooth.BUILD_DATE);
    }
}