package com.silverpine.uu.bluetooth;

import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.Nullable;

/**
 * UUBluetooth error codes.
 */
@SuppressWarnings("unused")
public enum UUBluetoothErrorCode
{
    /**
     * An operation was successful.
     */
    Success(0),

    /**
     * An operation attempt was manually timed out by UUCoreBluetooth
     */
    Timeout(1),

    /**
     * A method call was not attempted because the BluetoothDevice was not connected.
     */
    NotConnected(2),

    /**
     * A Bluetooth operation failed for some reason. Check caught exception and user info for
     * more information.  This can be returned from any Bluetooth method that throws exceptions
     */
    OperationFailed(3),

    /**
     * A connection attempt failed.
     */
    ConnectionFailed(4),

    /**
     * A peripheral device disconnected
     */
    Disconnected(5),

    // An operation could not be attempted because one or more preconditions failed.
    PreconditionFailed(6);

    /**
     * The raw enum value
     */
    private int rawValue;

    /**
     * Returns the integer raw value
     *
     * @return an integer representation of the enum
     */
    public int getRawValue()
    {
        return rawValue;
    }

    UUBluetoothErrorCode(final int val)
    {
        rawValue = val;
    }

    /**
     *
     * @return a developer friendly error description
     */
    @Nullable
    public String getErrorDescription()
    {
        try
        {
            Context ctx = UUBluetooth.requireApplicationContext();
            Resources rez = ctx.getResources();

            switch (this)
            {
                case Timeout:
                    return rez.getString(R.string.error_description_timeout);

                case NotConnected:
                    return rez.getString(R.string.error_description_not_connected);

                case OperationFailed:
                    return rez.getString(R.string.error_description_operation_failed);

                case ConnectionFailed:
                    return rez.getString(R.string.error_description_connection_failed);

                case Disconnected:
                    return rez.getString(R.string.error_description_disconnected);

                case PreconditionFailed:
                    return rez.getString(R.string.error_description_precondition_failed);

                default:
                    return null;
            }
        }
        catch (Exception ex)
        {
            return this.toString();
        }
    }
}
