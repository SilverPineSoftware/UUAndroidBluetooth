package com.silverpine.uu.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;

import com.silverpine.uu.core.UUThread;
import com.silverpine.uu.core.UUWorkerThread;
import com.silverpine.uu.logging.UULog;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@SuppressWarnings("unused")
abstract public class UUBluetoothDeviceScanner
{
    private static boolean LOGGING_ENABLED = UULog.LOGGING_ENABLED;

    public interface Listener
    {
        void onDeviceFound(@NonNull final UUBluetoothDeviceScanner scanner, @NonNull final BluetoothDevice device);
    }

    protected BluetoothAdapter bluetoothAdapter;
    protected UUWorkerThread scanThread;
    protected boolean isScanning = false;
    protected ArrayList<UUBluetoothDeviceFilter> scanFilters;
    protected final HashMap<String, Boolean> ignoredDevices = new HashMap<>();
    protected Listener listener;

    public UUBluetoothDeviceScanner(final Context context)
    {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null)
        {
            bluetoothAdapter = bluetoothManager.getAdapter();
        }

        scanThread = new UUWorkerThread("UUBluetoothDeviceScanner");
    }

    public void startScanning(
            final @Nullable ArrayList<UUBluetoothDeviceFilter> filters,
            final @NonNull Listener delegate)
    {
        UUThread.runOnMainThread(new Runnable()
        {
            @Override
            public void run()
            {
                scanFilters = filters;
                isScanning = true;
                clearIgnoredDevices();
                listener = delegate;

                internalStartScanning();
            }
        });
    }

    private synchronized void clearIgnoredDevices()
    {
        ignoredDevices.clear();
    }

    protected abstract void internalStartScanning();

    public boolean isScanning()
    {
        return isScanning;
    }

    public void stopScanning()
    {
        isScanning = false;

        UUThread.runOnMainThread(new Runnable()
        {
            @Override
            public void run()
            {
                internalStopScanning();
            }
        });
    }

    protected abstract void internalStopScanning();


    protected void processScannedDevice(@NonNull final BluetoothDevice device)
    {
        try
        {
            // Quick checks to throw out this result
            if (!isScanning)
            {
                debugLog("processScannedDevice", "Not scanning, ignoring advertisement from " + device.getAddress());
                return;
            }

            if (isIgnored(device))
            {
                debugLog("processScannedDevice", "Ignoring advertisement from " + device.getAddress());
                return;
            }

            scanThread.post(new Runnable()
            {
                @Override
                public void run()
                {
                    if (shouldDiscoverDevice(device))
                    {
                        handleDeviceFound(device);
                    }
                }
            });
        }
        catch (Exception ex)
        {
            debugLog("processScannedDevice", ex);
        }
    }

    private void handleDeviceFound(final BluetoothDevice device)
    {
        if (isScanning)
        {
            debugLog("handleDeviceFound", "Device Found: " + device);
            notifyDeviceFound(device);
        }
        else
        {
            debugLog("handleDeviceFound", "Not scanning anymore, throwing away scan result from: " + device);
            stopScanning();
        }
    }

    private void notifyDeviceFound(final BluetoothDevice device)
    {
        try
        {
            if (listener != null && device != null)
            {
                listener.onDeviceFound(this, device);
            }
        }
        catch (Exception ex)
        {
            debugLog("notifyDeviceFound", ex);
        }
    }

    private synchronized boolean isIgnored(@Nullable final BluetoothDevice device)
    {
        return (device == null || ignoredDevices.containsKey(device.getAddress()));
    }

    public synchronized void ignoreDevice(@NonNull final BluetoothDevice device)
    {
        ignoredDevices.put(device.getAddress(), Boolean.TRUE);
    }

    public synchronized void clearIgnoreList()
    {
        ignoredDevices.clear();
    }

    private boolean shouldDiscoverDevice(@NonNull final BluetoothDevice device)
    {
        if (scanFilters != null)
        {
            for (UUBluetoothDeviceFilter filter : scanFilters)
            {
                UUBluetoothDeviceFilter.Result result = filter.shouldDiscoverDevice(device);
                if (result == UUBluetoothDeviceFilter.Result.IgnoreForever)
                {
                    ignoreDevice(device);
                    return false;
                }

                if (result == UUBluetoothDeviceFilter.Result.IgnoreOnce)
                {
                    return false;
                }
            }

            return true;
        }
        else
        {
            return true;
        }
    }

    protected void debugLog(final String method, final String message)
    {
        if (LOGGING_ENABLED)
        {
            UULog.debug(getClass(), method, message);
        }
    }

    protected void debugLog(final String method, final Throwable exception)
    {
        if (LOGGING_ENABLED)
        {
            UULog.debug(getClass(), method, exception);
        }
    }

    protected void debugLog(final String method, final Intent intent)
    {
        if (LOGGING_ENABLED)
        {
            UULog.logIntent(getClass(), method, "", intent);
        }
    }
}