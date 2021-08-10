package com.silverpine.uu.bluetooth;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;

/**
 * Factory to build UUPeripheral objects or their derived classes from scan results
 */
public interface UUPeripheralFactory<T extends UUPeripheral>
{
    /**
     * Return a new UUPeripheral derived object
     * @param device the bluetooth device
     * @param rssi current RSSI value
     * @param scanRecord advertisement
     *
     * @return an instance of UUPeripheral
     */
    @NonNull T fromScanResult(final @NonNull BluetoothDevice device, final int rssi, final @NonNull byte[] scanRecord);
}
