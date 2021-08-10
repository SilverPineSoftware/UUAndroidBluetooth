package com.silverpine.uu.bluetooth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Interface for delivering BTLE connection events to callers
 */
public interface UUConnectionDelegate
{
    /**
     * Invoked when a peripheral is successfully connected.
     *
     * @param peripheral the peripheral that was connected
     */
    void onConnected(final @NonNull UUPeripheral peripheral);

    /**
     * Invoked when a peripheral was disconnected
     *
     * @param peripheral the peripheral that was disconnect
     * @param error the error (if any) that caused the disconnect to occur
     */
    void onDisconnected(final @NonNull UUPeripheral peripheral, final @Nullable UUBluetoothError error);
}
