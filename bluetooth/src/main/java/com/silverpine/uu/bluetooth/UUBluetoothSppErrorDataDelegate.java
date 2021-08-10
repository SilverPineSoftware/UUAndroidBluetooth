package com.silverpine.uu.bluetooth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Interface for delivering async results from a UUBluetoothSession action
 */
public interface UUBluetoothSppErrorDataDelegate
{
    /**
     * Callback invoked when a session action is completed.
     *
     * @param session the session being interacted with
     * @param data returned data
     * @param error an error if one occurs
     */
    void onComplete(final @NonNull UUBluetoothSpp session, @Nullable final byte[] data, final @Nullable UUBluetoothError error);
}
