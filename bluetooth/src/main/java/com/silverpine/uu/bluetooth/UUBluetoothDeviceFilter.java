package com.silverpine.uu.bluetooth;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;

/**
 * Interface used by UUClassicBluetoothScanner to allow custom filtering of scanned
 * Bluetooth devices
 */
public interface UUBluetoothDeviceFilter
{
    enum Result
    {
        IgnoreOnce,
        IgnoreForever,
        Discover
    };

    /**
     * Return enum indicating whether the device should be discovered or not.
     *
     * @param device the device to check
     *
     * @return value indicating whether the device should be ignored for this one scan result or forever, or discovered
     */
    @NonNull
    Result shouldDiscoverDevice(@NonNull BluetoothDevice device);
}
