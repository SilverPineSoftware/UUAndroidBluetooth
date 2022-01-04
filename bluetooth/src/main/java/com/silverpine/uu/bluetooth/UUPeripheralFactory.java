package com.silverpine.uu.bluetooth;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface UUPeripheralFactory<T extends UUPeripheral>
{
    @NonNull T createPeripheral(final @NonNull BluetoothDevice device, final int rssi, final @Nullable byte[] scanRecord);
}
