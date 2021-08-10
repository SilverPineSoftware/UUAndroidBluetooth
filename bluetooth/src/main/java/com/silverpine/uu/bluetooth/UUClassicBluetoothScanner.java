package com.silverpine.uu.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@SuppressWarnings("unused")
public class UUClassicBluetoothScanner extends UUBluetoothDeviceScanner
{
    private enum ScannerState
    {
        Idle,
        Discovery,
        StoppingDiscovery,
    }

    private BroadcastReceiver broadcastReceiver;
    private ScannerState currentState = ScannerState.Idle;

    public UUClassicBluetoothScanner(final Context context)
    {
        super(context);

        initBroadcastReceiver();
        context.registerReceiver(broadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        context.registerReceiver(broadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
    }

    private void initBroadcastReceiver()
    {
        broadcastReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                String action = intent.getAction();
                if (action != null)
                {
                    switch (action)
                    {
                        case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                            handleDiscoveryFinished(intent);
                            break;

                        case BluetoothDevice.ACTION_FOUND:
                            handleDeviceFound(intent);
                            break;
                    }
                }
            }
        };
    }

    private void handleDiscoveryFinished(final Intent intent)
    {
        switch (currentState)
        {
            case Discovery:
                internalStartScanning();
                break;

            case StoppingDiscovery:
                changeState(ScannerState.Idle);
                break;
        }
    }

    private void handleDeviceFound(final Intent intent)
    {
        BluetoothDevice device = safeGetDevice(intent);
        if (device != null)
        {
            // Throw out immediately any LE only device objects
            if (!isClassicDevice(device))
            {
                return;
            }

            debugLog("handleDeviceFound", intent);

            debugLog("handleDeviceFound",
                    "Device discovered: " + device.getName() +
                    ", " + device.getAddress() +
                    ", BondState: " + UUBluetooth.bondStateToString(device.getBondState()) +
                    ", Type:" + UUBluetooth.deviceTypeToString(device.getType()) +
                    ", BluetoothClass: " + device.getBluetoothClass() +
                    ", State: " + currentState);

            processScannedDevice(device);
        }
    }

    private boolean isClassicDevice(@NonNull final BluetoothDevice device)
    {
        switch (device.getType())
        {
            case BluetoothDevice.DEVICE_TYPE_CLASSIC:
            case BluetoothDevice.DEVICE_TYPE_DUAL:
            {
                return true;
            }

            default:
            {
                return false;
            }
        }
    }

    @Nullable
    private BluetoothDevice safeGetDevice(@Nullable final Intent intent)
    {
        BluetoothDevice device = null;

        if (intent != null)
        {
            if (intent.hasExtra(BluetoothDevice.EXTRA_DEVICE))
            {
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            }
        }

        return device;
    }

    @Override
    protected void internalStartScanning()
    {
        boolean discoveryStarted = false;

        try
        {
            discoveryStarted = bluetoothAdapter.startDiscovery();
            debugLog("internalStartScanning", "bluetoothAdapter.startDiscovery() returned " + discoveryStarted);
        }
        catch (Exception ex)
        {
            debugLog("internalStartScanning", ex);
            discoveryStarted = false;
        }
        finally
        {
            if (discoveryStarted)
            {
                changeState(ScannerState.Discovery);
            }
            else
            {
                changeState(ScannerState.Idle);
            }
        }
    }

    @Override
    protected void internalStopScanning()
    {
        try
        {
            changeState(ScannerState.StoppingDiscovery);
            boolean result = bluetoothAdapter.cancelDiscovery();
            debugLog("internalStopScanning", "bluetoothAdapter.cancelDiscovery() returned " + result);
        }
        catch (Exception ex)
        {
            debugLog("internalStopScanning", ex);
        }
    }

    private void changeState(ScannerState state)
    {
        if (currentState != state)
        {
            currentState = state;
            debugLog("changeState", "New State: " + state);
        }
    }
}