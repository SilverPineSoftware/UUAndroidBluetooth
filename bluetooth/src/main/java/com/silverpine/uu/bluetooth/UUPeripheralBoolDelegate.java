package com.silverpine.uu.bluetooth;

import androidx.annotation.NonNull;

/**
 * Interface for delivering async results from a UUPeripheral action that returns a boolean
 */
public interface UUPeripheralBoolDelegate
{
    /**
     * Callback invoked when a peripheral action is completed.
     *
     * @param peripheral the peripheral being interacted with
     * @param result result of the operation
     */
    void onComplete(final @NonNull UUPeripheral peripheral, final boolean result);
}
