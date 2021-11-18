package com.silverpine.uu.bluetooth;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;

import com.silverpine.uu.core.UUListDelegate;
import com.silverpine.uu.core.UUThread;
import com.silverpine.uu.core.UUWorkerThread;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

//@SuppressWarnings("unused")
public class UUBluetoothScanner<T extends UUPeripheral>
{
    private static boolean LOGGING_ENABLED = true;//UULog.LOGGING_ENABLED;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private ScanCallback scanCallback;
    private UUWorkerThread scanThread;
    private boolean isScanning = false;
    private ArrayList<UUPeripheralFilter> scanFilters;
    private Class<T> peripheralClass;
    private final HashMap<String, Boolean> ignoredDevices = new HashMap<>();

    private final HashMap<String, T> nearbyPeripherals = new HashMap<>();
    private UUListDelegate<T> nearbyPeripheralCallback = null;

    public UUBluetoothScanner(@NonNull final Context context, @NonNull final Class<T> clazz)
    {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        scanThread = new UUWorkerThread("UUBluetoothScanner");
        peripheralClass = clazz;
    }

    public void startScanning(
        final @Nullable UUID[] serviceUuidList,
        final @Nullable ArrayList<UUPeripheralFilter> filters,
        final @NonNull UUListDelegate<T> callback)
    {
        UUThread.runOnMainThread(new Runnable()
        {
            @Override
            public void run()
            {
                scanFilters = filters;
                //peripheralFactory = factory;
                isScanning = true;
                clearIgnoredDevices();
                nearbyPeripheralCallback = callback;

                startScan(serviceUuidList);
            }
        });
    }

    private synchronized void clearIgnoredDevices()
    {
        ignoredDevices.clear();
    }

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
                stopScan();
            }
        });
    }

    /*
    public static boolean canUseLollipopScanning()
    {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }

    public boolean useLollipopScanning()
    {
        return useLollipopScanning;
    }

    public void setUseLollipopScanning(final boolean useLollipopScanning)
    {
        this.useLollipopScanning = useLollipopScanning;
    }

    public boolean shouldUseLollipopScanning()
    {
        return canUseLollipopScanning() && useLollipopScanning();
    }*/

    /*
    @SuppressWarnings("deprecation")
    private void startLegacyScan(final @Nullable UUID[] serviceUuidList)
    {
        stopLegacyScan();

        try
        {
            bluetoothAdapter.startLeScan(serviceUuidList);
        }
        catch (Exception ex)
        {
            debugLog("startLegacyScan", ex);
        }
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord)
    {
        handleLegacyScanResult(device, rssi, scanRecord, listener);
    }*/

    private void startScan(final @Nullable UUID[] serviceUuidList)
    {
        stopScan();

        try
        {
            ArrayList<ScanFilter> filters = new ArrayList<>();

            if (serviceUuidList != null)
            {
                for (UUID uuid : serviceUuidList)
                {
                    ScanFilter.Builder fb = new ScanFilter.Builder();
                    fb.setServiceUuid(new ParcelUuid(uuid));
                    filters.add(fb.build());
                }
            }

            ScanSettings.Builder builder = new ScanSettings.Builder();
            builder.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES);
            builder.setMatchMode(ScanSettings.MATCH_MODE_STICKY);
            builder.setNumOfMatches(ScanSettings.MATCH_NUM_MAX_ADVERTISEMENT);
            builder.setReportDelay(0);
            builder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);

            ScanSettings settings = builder.build();

            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

            if (scanCallback == null)
            {
                scanCallback = new ScanCallback()
                {
                    @Override
                    public void onScanResult(int callbackType, ScanResult result)
                    {
                        //debugLog("startScan.onScanResult", "callbackType: " + callbackType + ", result: " + result.toString());
                        handleScanResult(result);
                    }

                    /**
                     * Callback when batch results are delivered.
                     *
                     * @param results List of scan results that are previously scanned.
                     */
                    public void onBatchScanResults(List<ScanResult> results)
                    {
                        debugLog("startScan.onBatchScanResults", "There are " + results.size() + " batched results");

                        for (ScanResult sr : results)
                        {
                            debugLog("startScan.onBatchScanResults", results.toString());
                            handleScanResult(sr);
                        }
                    }

                    /**
                     * Callback when scan could not be started.
                     *
                     * @param errorCode Error code (one of SCAN_FAILED_*) for scan failure.
                     */
                    public void onScanFailed(int errorCode)
                    {
                        debugLog("startScan.onScanFailed", "errorCode: " + errorCode);
                    }
                };
            }

            bluetoothLeScanner.startScan(filters, settings, scanCallback);
        }
        catch (Exception ex)
        {
            debugLog("startScan", ex);
        }
    }

    /*
    private void handleLegacyScanResult(final BluetoothDevice device, final int rssi, final byte[] scanRecord, final Listener delegate)
    {
        try
        {
            if (!isScanning)
            {
                debugLog("handleLegacyScanResult", "Not scanning, ignoring advertisement from " + device.getAddress());
                return;
            }

            if (isIgnored(device))
            {
                //debugLog("handleLegacyScanResult", "Ignoring advertisement from " + device.getAddress());
                return;
            }

            scanThread.post(new Runnable()
            {
                @Override
                public void run()
                {
                    UUPeripheral peripheral = peripheralFactory.fromScanResult(device, rssi, scanRecord);
                    if (shouldDiscoverPeripheral(peripheral))
                    {
                        handlePeripheralFound(peripheral, delegate);
                    }
                }
            });
        }
        catch (Exception ex)
        {
            debugLog("handleLegacyScanResult", ex);
        }
    }*/

    //@TargetApi(21)
    private void handleScanResult(final ScanResult scanResult)
    {
        try
        {
            if (!isScanning)
            {
                //debugLog("handleScanResult", "Not scanning, ignoring advertisement from " + scanResult.getDevice().getAddress());
                return;
            }

            if (isIgnored(scanResult))
            {
                //debugLog("handleScanResult", "Ignoring advertisement from " + scanResult.getDevice().getAddress());
                return;
            }

            scanThread.post(new Runnable()
            {
                @Override
                public void run()
                {
                    T peripheral = createPeripheral(scanResult);
                    if (shouldDiscoverPeripheral(peripheral))
                    {
                        handlePeripheralFound(peripheral);
                    }
                }
            });
        }
        catch (Exception ex)
        {
            debugLog("handleScanResult", ex);
        }
    }

    private T createPeripheral(@NonNull final ScanResult scanResult)
    {
        try
        {
            Constructor<T> ctor = peripheralClass.getConstructor(BluetoothDevice.class, int.class, byte[].class);
            return ctor.newInstance(scanResult.getDevice(), scanResult.getRssi(), safeGetScanRecord(scanResult));
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Failed to construct peripheral", ex);
        }
    }

    private byte[] safeGetScanRecord(final ScanResult result)
    {
        if (result != null)
        {
            ScanRecord sr = result.getScanRecord();
            if (sr != null)
            {
                return sr.getBytes();
            }
        }

        return null;
    }

    private void handlePeripheralFound(@NonNull final T peripheral)
    {
        if (!isScanning)
        {
            debugLog("handlePeripheralFound", "Not scanning anymore, throwing away scan result from: " + peripheral);
            safeEndAllScanning();
            return;
        }

        String address = peripheral.getAddress();
        if (address == null)
        {
            debugLog("handlePeripheralFound", "Peripheral has a null address, throwing it out.");
            return;
        }

        debugLog("handlePeripheralFound", "Peripheral Found: " + peripheral);
        nearbyPeripherals.put(address, peripheral);

        ArrayList<T> sorted = sortedPeripherals();
        UUListDelegate.safeInvoke(nearbyPeripheralCallback, sorted);
    }

    private ArrayList<T> sortedPeripherals()
    {
        ArrayList<T> list = new ArrayList<>(nearbyPeripherals.values());
        list.sort((lhs, rhs) ->
        {
            if (lhs.getRssi() > rhs.getRssi())
            {
                return -1;
            }
            else if (lhs.getRssi() < rhs.getRssi())
            {
                return 1;
            }

            return 0;

        });

        return list;

    }

    /*
    private void notifyPeripheralFound(final UUPeripheral peripheral, final Listener delegate)
    {
        try
        {
            if (delegate != null && peripheral != null)
            {
                delegate.onPeripheralFound(this, peripheral);
            }
        }
        catch (Exception ex)
        {
            debugLog("notifyPeripheralFound", ex);
        }
    }*/

    /*
    @SuppressWarnings("deprecation")
    private void stopLegacyScan()
    {
        try
        {
            if (bluetoothAdapter != null)
            {
                bluetoothAdapter.stopLeScan(this);
            }
            else
            {
                debugLog("stopLegacyScan", "Bluetooth adapter is null, nothing to do.");
            }
        }
        catch (Exception ex)
        {
            debugLog("stopLegacyScan", ex);
        }
    }*/

    private void stopScan()
    {
        try
        {
            if (bluetoothLeScanner != null && scanCallback != null)
            {
                bluetoothLeScanner.stopScan(scanCallback);
            }
        }
        catch (Exception ex)
        {
            debugLog("stopScan", ex);
        }
    }

    private void safeEndAllScanning()
    {
        //stopLegacyScan();
        stopScan();
    }

    private synchronized boolean isIgnored(@Nullable final BluetoothDevice device)
    {
        return (device == null || ignoredDevices.containsKey(device.getAddress()));
    }

    private boolean isIgnored(@Nullable final ScanResult scanResult)
    {
        return (scanResult == null || isIgnored(scanResult.getDevice()));
    }

    public synchronized void ignoreDevice(@NonNull final BluetoothDevice device)
    {
        ignoredDevices.put(device.getAddress(), Boolean.TRUE);
    }

    public synchronized void clearIgnoreList()
    {
        ignoredDevices.clear();
    }

    private boolean shouldDiscoverPeripheral(final @NonNull UUPeripheral peripheral)
    {
        if (scanFilters != null)
        {
            for (UUPeripheralFilter filter : scanFilters)
            {
                UUPeripheralFilter.Result result = filter.shouldDiscoverPeripheral(peripheral);
                if (result == UUPeripheralFilter.Result.IgnoreForever)
                {
                    ignoreDevice(peripheral.getBluetoothDevice());
                    return false;
                }

                if (result == UUPeripheralFilter.Result.IgnoreOnce)
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

    /*
    private static class DefaultPeripheralFactory implements UUPeripheralFactory<UUPeripheral>
    {
        @NonNull
        @Override
        public UUPeripheral fromScanResult(@NonNull BluetoothDevice device, int rssi, @NonNull byte[] scanRecord)
        {
            return new UUPeripheral(device, rssi, scanRecord);
        }
    }*/

    private static void debugLog(final String method, final String message)
    {
        if (LOGGING_ENABLED)
        {
            Log.d("UUBluetoothScanner", method + ": " + message);
        }
    }

    private synchronized static void debugLog(final String method, final Throwable exception)
    {
        if (LOGGING_ENABLED)
        {
            Log.d("UUBluetoothScanner", method + ": " + exception.toString());
        }
    }
}