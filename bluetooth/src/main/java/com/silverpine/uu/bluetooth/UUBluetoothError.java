package com.silverpine.uu.bluetooth;

import android.bluetooth.BluetoothGatt;

import com.silverpine.uu.core.UUError;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Container class for UUBluetooth errors
 */
@SuppressWarnings("unused")
class UUBluetoothError
{
    /**
     * Lookup key for errorDetails for the failing underlying bluetooth method name.
     */
    public static final String USER_INFO_KEY_METHOD_NAME = "methodName";
    public static final String USER_INFO_KEY_MESSAGE = "message";
    public static final String USER_INFO_KEY_GATT_STATUS = "gattStatus";
    public static final String DOMAIN = "UUBluetoothError";

    /**
     * Creates a UUError
     *
     * @param errorCode error code
     */
    @NonNull
    private static UUError makeError(final @NonNull UUBluetoothErrorCode errorCode)
    {
        return makeError(errorCode, null);
    }

    /**
     * Creates a UUBluetoothError
     *
     * @param errorCode error code
     * @param caughtException caught exception
     */
    @NonNull
    private static UUError makeError(final @NonNull UUBluetoothErrorCode errorCode, @Nullable final Exception caughtException)
    {
        UUError err = new UUError(DOMAIN, errorCode.getRawValue(), caughtException);
        //this.errorCode = errorCode;
        err.setErrorDescription(errorCode.getErrorDescription());
        return err;
    }

    /**
     * Wrapper method to return a success error object
     *
     * @return a UUBluetoothError object
     */
    public static @NonNull UUError success()
    {
        return makeError(UUBluetoothErrorCode.Success);
    }

    /**
     * Wrapper method to return a not connected error
     *
     * @return a UUBluetoothError object
     */
    public static @NonNull UUError notConnectedError()
    {
        return makeError(UUBluetoothErrorCode.NotConnected);
    }

    /**
     * Wrapper method to return a connection failed error
     *
     * @return a UUBluetoothError object
     */
    public static @NonNull UUError connectionFailedError()
    {
        return makeError(UUBluetoothErrorCode.ConnectionFailed);
    }

    /**
     * Wrapper method to return a timeout error
     *
     * @return a UUBluetoothError object
     */
    public static @NonNull UUError timeoutError()
    {
        return makeError(UUBluetoothErrorCode.Timeout);
    }

    /**
     * Wrapper method to return a disconnected error
     *
     * @return a UUBluetoothError object
     */
    public static @NonNull UUError disconnectedError()
    {
        return makeError(UUBluetoothErrorCode.Disconnected);
    }

    /**
     * Wrapper method to return an underlying Bluetooth method failure.  This is returned when
     * a method returns false or null or othe error condition.
     *
     *  @param method the method name
     *
     * @return a UUBluetoothError object
     */
    public static @NonNull UUError operationFailedError(@NonNull final String method)
    {
        UUError err = makeError(UUBluetoothErrorCode.OperationFailed);
        err.addUserInfo(USER_INFO_KEY_METHOD_NAME, method);
        return err;
    }

    /**
     * Wrapper method to return an error on a pre-condition check.
     *
     *  @param message a developer friendly message about the precondition that failed.
     *
     * @return a UUBluetoothError object
     */
    public static @NonNull UUError preconditionFailedError(@NonNull final String message)
    {
        UUError err = makeError(UUBluetoothErrorCode.PreconditionFailed);
        err.addUserInfo(USER_INFO_KEY_MESSAGE, message);
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
    public static @NonNull UUError operationFailedError(@NonNull final Exception caughtException)
    {
        return makeError(UUBluetoothErrorCode.OperationFailed, caughtException);
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
    public static @Nullable UUError gattStatusError(@NonNull final String method, final int gattStatus)
    {
        if (gattStatus != BluetoothGatt.GATT_SUCCESS)
        {
            UUError err = makeError(UUBluetoothErrorCode.OperationFailed);
            err.addUserInfo(USER_INFO_KEY_METHOD_NAME, method);
            err.addUserInfo(USER_INFO_KEY_GATT_STATUS, String.valueOf(gattStatus));
            return err;
        }
        else
        {
            return null;
        }
    }
}
