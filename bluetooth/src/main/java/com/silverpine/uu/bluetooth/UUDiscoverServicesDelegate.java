package com.silverpine.uu.bluetooth;

import android.bluetooth.BluetoothGattService;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public interface UUDiscoverServicesDelegate
{
    void onCompleted(@Nullable final ArrayList<BluetoothGattService> services, @Nullable final UUBluetoothError error);
}
