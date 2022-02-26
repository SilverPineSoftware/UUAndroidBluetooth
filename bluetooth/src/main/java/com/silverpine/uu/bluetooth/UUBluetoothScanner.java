package com.silverpine.uu.bluetooth;

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

import com.silverpine.uu.core.UUListDelegate;
import com.silverpine.uu.core.UUThread;
import com.silverpine.uu.core.UUTimer;
import com.silverpine.uu.core.UUWorkerThread;
import com.silverpine.uu.logging.UULog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

//@SuppressWarnings("unused")
public class UUBluetoothScanner<T extends UUPeripheral>
{
    private static boolean LOGGING_ENABLED = UULog.LOGGING_ENABLED;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private ScanCallback scanCallback;
    private UUWorkerThread scanThread;
    private boolean isScanning = false;
    private ArrayList<UUPeripheralFilter<T>> scanFilters;
    private final UUPeripheralFactory<T> peripheralFactory;
    private final HashMap<String, Boolean> ignoredDevices = new HashMap<>();

    private final HashMap<String, T> nearbyPeripherals = new HashMap<>();
    private UUListDelegate<T> nearbyPeripheralCallback = null;

    public UUBluetoothScanner(@NonNull final Context context, @NonNull final UUPeripheralFactory<T> factory)
    {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        scanThread = new UUWorkerThread("UUBluetoothScanner");
        peripheralFactory = factory;
    }

    public void startScanning(
        final @Nullable UUID[] serviceUuidList,
        final @Nullable ArrayList<UUPeripheralFilter<T>> filters,
        final @NonNull UUListDelegate<T> callback)
    {
        UUThread.runOnMainThread(new Runnable()
        {
            @Override
            public void run()
            {
                scanFilters = filters;
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

        cancelNoResponseTimer();

        UUThread.runOnMainThread(new Runnable()
        {
            @Override
            public void run()
            {
                stopScan();
            }
        });
    }

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
            kickNoResponseTimer();
        }
        catch (Exception ex)
        {
            debugLog("startScan", ex);
        }
    }

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

            scanThread.post(() ->
            {
                T peripheral = createPeripheral(scanResult);
                if (shouldDiscoverPeripheral(peripheral))
                {
                    handlePeripheralFound(peripheral);
                }
            });
        }
        catch (Exception ex)
        {
            debugLog("handleScanResult", ex);
        }
    }

    @Nullable
    private T createPeripheral(@NonNull final ScanResult scanResult)
    {
        try
        {
            return peripheralFactory.createPeripheral(scanResult.getDevice(), scanResult.getRssi(), safeGetScanRecord(scanResult));
        }
        catch (Exception ex)
        {
            debugLog("createPeripheral", ex);
            return null;
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

    private boolean shouldDiscoverPeripheral(final @Nullable T peripheral)
    {
        if (peripheral == null)
        {
            return false;
        }

        if (scanFilters != null)
        {
            for (UUPeripheralFilter<T> filter : scanFilters)
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
        }

        return true;
    }

    private long noResponseTimeout = 500; // milliseconds
    private long noResponseTimerFrequency = 250; // milliseconds

    private static final String noResponseTimerId = "UUBluetoothScanner_NoResponseTimerId";

    public long getNoResponseTimeout()
    {
        return noResponseTimeout;
    }

    public void setNoResponseTimeout(long noResponseTimeout)
    {
        this.noResponseTimeout = noResponseTimeout;
    }

    public long getNoResponseTimerFrequency()
    {
        return noResponseTimerFrequency;
    }

    public void setNoResponseTimerFrequency(long noResponseTimerFrequency)
    {
        this.noResponseTimerFrequency = noResponseTimerFrequency;
    }

    private void kickNoResponseTimer()
    {
        UUTimer.cancelActiveTimer(noResponseTimerId);

        UUTimer t = new UUTimer(noResponseTimerId, noResponseTimerFrequency, true, null, (timer, userInfo) ->
        {
            synchronized (nearbyPeripherals)
            {
                boolean didChange = false;

                ArrayList<T> keep = new ArrayList<>();
                for (T peripheral : nearbyPeripherals.values())
                {
                    if (peripheral.getTimeSinceLastUpdate() < noResponseTimeout)
                    {
                        keep.add(peripheral);
                    }
                    else
                    {
                        didChange = true;
                    }
                }

                nearbyPeripherals.clear();

                for (T peripheral: keep)
                {
                    nearbyPeripherals.put(peripheral.getAddress(), peripheral);
                }

                if (didChange)
                {
                    ArrayList<T> sorted = sortedPeripherals();
                    UUListDelegate.safeInvoke(nearbyPeripheralCallback, sorted);
                }
            }
        });

        t.start();
    }

    private void cancelNoResponseTimer()
    {
        UUTimer.cancelActiveTimer(noResponseTimerId);
    }

    private static void debugLog(final String method, final String message)
    {
        if (LOGGING_ENABLED)
        {
            UULog.debug(UUBluetoothScanner.class, method, message);
        }
    }

    private synchronized static void debugLog(final String method, final Throwable exception)
    {
        if (LOGGING_ENABLED)
        {
            UULog.debug(UUBluetoothScanner.class, method, exception);
        }
    }
}