package com.silverpine.uu.bluetooth;

import com.silverpine.uu.core.UUError;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Interface for delivering async results from a UUBluetoothSession action
 */
public interface UUBluetoothSppErrorDelegate
{
    /**
     * Callback invoked when a session action is completed.
     *
     * @param session the session being interacted with
     * @param error an error if one occurs
     */
    void onComplete(final @NonNull UUBluetoothSpp session, final @Nullable UUError error);
}
