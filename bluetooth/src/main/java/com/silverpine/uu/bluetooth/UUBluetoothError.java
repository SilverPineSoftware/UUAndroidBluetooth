package com.silverpine.uu.bluetooth;

import android.bluetooth.BluetoothGatt;

import com.silverpine.uu.core.UUError;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Container class for UUBluetooth errors
 */
@SuppressWarnings("unused")
public class UUBluetoothError extends UUError
{
    /**
     * Lookup key for errorDetails for the failing underlying bluetooth method name.
     */
    public static final String USER_INFO_KEY_METHOD_NAME = "methodName";
    public static final String USER_INFO_KEY_MESSAGE = "message";
    public static final String USER_INFO_KEY_GATT_STATUS = "gattStatus";
    public static final String DOMAIN = "UUBluetoothError";

    private final UUBluetoothErrorCode errorCode;

    /**
     * Creates a UUBluetoothError from an error code
     *
     * @param errorCode the error code
     */
    UUBluetoothError(final @NonNull UUBluetoothErrorCode errorCode)
    {
        this(errorCode, null);
    }

    /**
     * Creates a UUBluetoothError
     *
     * @param errorCode error code
     * @param caughtException caught exception
     */
    UUBluetoothError(final @NonNull UUBluetoothErrorCode errorCode, @Nullable final Exception caughtException)
    {
        super(DOMAIN, errorCode.getRawValue(), caughtException);
        this.errorCode = errorCode;
        setErrorDescription(errorCode.getErrorDescription());
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
     * Wrapper method to return a success error object
     *
     * @return a UUBluetoothError object
     */
    public static @NonNull UUBluetoothError success()
    {
        return new UUBluetoothError(UUBluetoothErrorCode.Success);
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
     * Wrapper method to return a connection failed error
     *
     * @return a UUBluetoothError object
     */
    public static @NonNull UUBluetoothError connectionFailedError()
    {
        return new UUBluetoothError(UUBluetoothErrorCode.ConnectionFailed);
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
    public static @NonNull UUBluetoothError preconditionFailedError(@NonNull final String message)
    {
        UUBluetoothError err = new UUBluetoothError(UUBluetoothErrorCode.PreconditionFailed);
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
    public static @NonNull UUBluetoothError operationFailedError(@NonNull final Exception caughtException)
    {
        return new UUBluetoothError(UUBluetoothErrorCode.OperationFailed, caughtException);
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
