package com.silverpine.uu.bluetooth;

import android.bluetooth.BluetoothGatt;

import com.silverpine.uu.core.UUString;

import java.util.HashMap;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Container class for UUBluetooth errors
 */
@SuppressWarnings("unused")
public class UUBluetoothError
{
    /**
     * Lookup key for errorDetails for the failing underlying bluetooth method name.
     */
    public static final String DETAIL_KEY_METHOD_NAME = "methodName";
    public static final String DETAIL_KEY_MESSAGE = "message";
    public static final String DETAIL_KEY_GATT_STATUS = "gattStatus";

    private Exception caughtException = null;
    private UUBluetoothErrorCode errorCode;
    private final HashMap<String, String> errorDetails = new HashMap<>();

    /**
     * Creates a UUBluetoothError from an error code
     *
     * @param errorCode the error code
     */
    public UUBluetoothError(final @NonNull UUBluetoothErrorCode errorCode)
    {
        this.errorCode = errorCode;
    }

    /**
     * To string override
     *
     * @return string
     */
    @Override
    public String toString()
    {
        try
        {
            return String.format(Locale.US, "code: %s, details: %s, ex: %s",
                    UUString.safeToString(errorCode),
                    UUString.safeToString(errorDetails),
                    UUString.safeToString(caughtException));
        }
        catch (Exception ex)
        {
            return super.toString();
        }
    }

    /**
     * Returns the captured exception
     *
     * @return an Exception object
     */
    public @Nullable Exception getCaughtException()
    {
        return caughtException;
    }

    /**
     * Returns the error code
     *
     * @return an error code
     */
    public @NonNull UUBluetoothErrorCode getErrorCode()
    {
        return errorCode;
    }

    /**
     * Gets error details
     *
     * @return error details dictionary
     */
    public @NonNull HashMap<String, String> getErrorDetails()
    {
        return errorDetails;
    }

    /**
     * Wrapper method to return a not connected error
     *
     * @return a UUBluetoothError object
     */
    public static @NonNull UUBluetoothError notConnectedError()
    {
        return new UUBluetoothError(UUBluetoothErrorCode.NotConnected);
    }

    /**
     * Wrapper method to return a timeout error
     *
     * @return a UUBluetoothError object
     */
    public static @NonNull UUBluetoothError timeoutError()
    {
        return new UUBluetoothError(UUBluetoothErrorCode.Timeout);
    }

    /**
     * Wrapper method to return a disconnected error
     *
     * @return a UUBluetoothError object
     */
    public static @NonNull UUBluetoothError disconnectedError()
    {
        return new UUBluetoothError(UUBluetoothErrorCode.Disconnected);
    }

    /**
     * Wrapper method to return an underlying Bluetooth method failure.  This is returned when
     * a method returns false or null or othe error condition.
     *
     *  @param method the method name
     *
     * @return a UUBluetoothError object
     */
    public static @NonNull UUBluetoothError operationFailedError(@NonNull final String method)
    {
        UUBluetoothError err = new UUBluetoothError(UUBluetoothErrorCode.OperationFailed);
        err.errorDetails.put(DETAIL_KEY_METHOD_NAME, method);
        return err;
    }

    /**
     * Wrapper method to return an error on a pre-condition check.
     *
     *  @param message a developer friendly message about the precondition that failed.
     *
     * @return a UUBluetoothError object
     */
    public static @NonNull UUBluetoothError preconditionFailedError(@NonNull final String message)
    {
        UUBluetoothError err = new UUBluetoothError(UUBluetoothErrorCode.PreconditionFailed);
        err.errorDetails.put(DETAIL_KEY_MESSAGE, message);
        return err;
    }

    /**
     * Wrapper method to return an underlying Bluetooth method failure.  This is returned when
     * a method returns false or null or othe error condition.
     *
     *  @param caughtException the exception that caused this error
     *
     * @return a UUBluetoothError object
     */
    public static @NonNull UUBluetoothError operationFailedError(@NonNull final Exception caughtException)
    {
        UUBluetoothError err = new UUBluetoothError(UUBluetoothErrorCode.OperationFailed);
        err.caughtException = caughtException;
        return err;
    }

    /**
     * Wrapper method to return an underlying Bluetooth method failure.  This is returned when
     * a method returns false or null or othe error condition.
     *
     *  @param method the method name
     *  @param gattStatus the gatt status at time of failure
     *
     * @return a UUBluetoothError object
     */
    public static @Nullable UUBluetoothError gattStatusError(@NonNull final String method, final int gattStatus)
    {
        if (gattStatus != BluetoothGatt.GATT_SUCCESS)
        {
            UUBluetoothError err = new UUBluetoothError(UUBluetoothErrorCode.OperationFailed);
            err.errorDetails.put(DETAIL_KEY_METHOD_NAME, method);
            err.errorDetails.put(DETAIL_KEY_GATT_STATUS, String.valueOf(gattStatus));
            return err;
        }
        else
        {
            return null;
        }
    }
}
