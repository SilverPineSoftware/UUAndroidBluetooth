package com.silverpine.uu.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.silverpine.uu.core.UUData;
import com.silverpine.uu.core.UUString;
import com.silverpine.uu.logging.UULog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Wrapper class around a BTLE scanning result.
 */
@SuppressWarnings("unused")
public class UUPeripheral implements Parcelable
{
    private static boolean LOGGING_ENABLED = UULog.LOGGING_ENABLED;

    private static final byte DATA_TYPE_INCOMPLETE_LIST_OF_16_BIT_SERVICE_CLASS_UUIDS   = 0x02;
    private static final byte DATA_TYPE_COMPLETE_LIST_OF_16_BIT_SERVICE_CLASS_UUIDS     = 0x03;
    private static final byte DATA_TYPE_INCOMPLETE_LIST_OF_128_BIT_SERVICE_CLASS_UUIDS  = 0x06;
    private static final byte DATA_TYPE_COMPLETE_LIST_OF_128_BIT_SERVICE_CLASS_UUIDS    = 0x07;
    private static final byte DATA_TYPE_COMPLETE_LOCAL_NAME = 0x09;
    private static final byte DATA_TYPE_MANUFACTURING_DATA = (byte) 0xFF;

    public enum ConnectionState
    {
        Connecting,
        Connected,
        Disconnecting,
        Disconnected;

        public static @NonNull ConnectionState fromString(final @Nullable String string)
        {
            for (ConnectionState s : ConnectionState.values())
            {
                if (s.toString().equalsIgnoreCase(string))
                {
                    return s;
                }
            }

            return Disconnected;
        }

        static @NonNull ConnectionState fromProfileConnectionState(final int state)
        {
            switch (state)
            {
                case BluetoothProfile.STATE_CONNECTED:
                    return Connected;

                case BluetoothProfile.STATE_CONNECTING:
                    return Connecting;

                case BluetoothProfile.STATE_DISCONNECTING:
                    return Disconnecting;

                case BluetoothProfile.STATE_DISCONNECTED:
                    return Disconnected;
            }

            return Disconnected;
        }
    }

    private BluetoothDevice device;
    private byte[] scanRecord;
    private int rssi;
    private long lastRssiUpdateTime;
    private byte[] manufacturingData;
    private String localName;
    private final ArrayList<String> serviceUuids = new ArrayList<>();
    private long firstAdvertisementTime;
    private long lastAdvertisementTime;
    private long totalBeaconCount;

    private BluetoothGatt bluetoothGatt;

    public UUPeripheral()
    {
    }

    public UUPeripheral(final @NonNull BluetoothDevice device, final int rssi, final @Nullable byte[] scanRecord)
    {
        firstAdvertisementTime = 0;
        totalBeaconCount = 0;
        updateAdvertisement(device, rssi, scanRecord);
    }

    public @Nullable byte[] getScanRecord()
    {
        return scanRecord;
    }

    public @Nullable byte[] getManufacturingData()
    {
        return manufacturingData;
    }

    public @NonNull BluetoothDevice getBluetoothDevice()
    {
        return device;
    }

    public @Nullable String getAddress()
    {
        return device.getAddress();
    }

    public @Nullable String getName()
    {
        if (UUString.isNotEmpty(localName))
        {
            return localName;
        }
        else
        {
            return device.getName();
        }
    }

    public @NonNull String[] getServiceUuids()
    {
        String[] list = new String[serviceUuids.size()];
        serviceUuids.toArray(list);
        return list;
    }


    public boolean hasServiceUuid(final @Nullable String uuidToCheck)
    {
        if (serviceUuids != null && uuidToCheck != null)
        {
            for (String uuid : serviceUuids)
            {
                if (uuid.equalsIgnoreCase(uuidToCheck))
                {
                    return true;
                }
            }
        }

        return false;
    }

    public int getRssi()
    {
        return rssi;
    }

    public long getLastRssiUpdateTime()
    {
        return lastRssiUpdateTime;
    }

    public void updateRssi(final int updatedRssi)
    {
        rssi = updatedRssi;
        lastRssiUpdateTime = System.currentTimeMillis();

    }

    public void updateAdvertisement(final @NonNull BluetoothDevice device, final int rssi, final @Nullable byte[] scanRecord)
    {
        this.device = device;
        this.scanRecord = scanRecord;

        if (firstAdvertisementTime == 0)
        {
            firstAdvertisementTime = System.currentTimeMillis();
        }

        debugLog("updateAdvertisement", totalBeaconCount + ", timeSinceLastAdvertisement: " + getTimeSinceLastUpdate());

        lastAdvertisementTime = System.currentTimeMillis();
        ++totalBeaconCount;


        updateRssi(rssi);
        parseScanRecord();
    }

    public long getFirstAdvertisementTime()
    {
        return firstAdvertisementTime;
    }

    public long getLastAdvertisementTime()
    {
        return lastAdvertisementTime;
    }

    public long totalBeaconCount()
    {
        return totalBeaconCount;
    }

    public double averageBeaconRate()
    {
        double avg = 0.0f;

        long timeSinceFirstBeacon = lastAdvertisementTime - firstAdvertisementTime;
        return (double)totalBeaconCount / (double)timeSinceFirstBeacon * 1000.0f;
    }


    public @NonNull ConnectionState getConnectionState(final @NonNull Context context)
    {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        int state = bluetoothManager.getConnectionState(device, BluetoothProfile.GATT);
        debugLog("getConnectionState", "Actual connection state is: " + state + " (" + ConnectionState.fromProfileConnectionState(state) + ")");

        UUBluetoothGatt gatt = UUBluetooth.gattForPeripheral(this);

        if (gatt != null)
        {
            if (state != BluetoothProfile.STATE_CONNECTING && gatt.isConnecting())
            {
                debugLog("getConnectionState", "Forcing state to connecting");
                state = BluetoothProfile.STATE_CONNECTING;
            }
            else if (state != BluetoothProfile.STATE_DISCONNECTED && gatt.getBluetoothGatt() == null)
            {
                debugLog("getConnectionState", "Forcing state to disconnected");
                state = BluetoothProfile.STATE_DISCONNECTED;
            }
        }

        return ConnectionState.fromProfileConnectionState(state);
    }

    void setBluetoothGatt(final @Nullable BluetoothGatt gatt)
    {
        bluetoothGatt = gatt;
    }

    public void requestHighPriority(@NonNull final UUPeripheralBoolDelegate delegate)
    {
        UUBluetoothGatt gatt = UUBluetooth.gattForPeripheral(this);
        if (gatt != null)
        {
            gatt.requestHighPriority(delegate);
        }
    }

    public void discoverServices(
            final long timeout,
            final @NonNull UUPeripheralErrorDelegate delegate)
    {
        UUBluetoothGatt gatt = UUBluetooth.gattForPeripheral(this);
        if (gatt != null)
        {
            gatt.discoverServices(timeout, delegate);
        }
    }

    public @NonNull List<BluetoothGattService> discoveredServices()
    {
        acquireExistingGatt();

        List<BluetoothGattService> list = new ArrayList<>();

        if (bluetoothGatt != null)
        {
            list.addAll(bluetoothGatt.getServices());
        }

        return list;
    }

    public void setNotifyState(
            final @NonNull BluetoothGattCharacteristic characteristic,
            final boolean notifyState,
            final long timeout,
            final @Nullable UUCharacteristicDelegate notifyDelegate,
            final @NonNull UUCharacteristicDelegate delegate)
    {
        UUBluetoothGatt gatt = UUBluetooth.gattForPeripheral(this);
        if (gatt != null)
        {
            gatt.setNotifyState(characteristic, notifyState, timeout, notifyDelegate, delegate);
        }
    }

    public void readCharacteristic(
            final @NonNull BluetoothGattCharacteristic characteristic,
            final long timeout,
            final @NonNull UUCharacteristicDelegate delegate)
    {
        UUBluetoothGatt gatt = UUBluetooth.gattForPeripheral(this);
        if (gatt != null)
        {
            gatt.readCharacteristic(characteristic, timeout, delegate);
        }
    }

    public void readDescriptor(
            final @NonNull BluetoothGattDescriptor descriptor,
            final long timeout,
            final @NonNull UUDescriptorDelegate delegate)
    {
        UUBluetoothGatt gatt = UUBluetooth.gattForPeripheral(this);
        if (gatt != null)
        {
            gatt.readDescriptor(descriptor, timeout, delegate);
        }
    }

    public void writeDescriptor(
            final @NonNull BluetoothGattDescriptor descriptor,
            final @NonNull byte[] data,
            final long timeout,
            final @NonNull UUDescriptorDelegate delegate)
    {
        UUBluetoothGatt gatt = UUBluetooth.gattForPeripheral(this);
        if (gatt != null)
        {
            gatt.writeDescriptor(descriptor, data, timeout, delegate);
        }
    }

    public void writeCharacteristic(
            final @NonNull BluetoothGattCharacteristic characteristic,
            final @NonNull byte[] data,
            final long timeout,
            final @NonNull UUCharacteristicDelegate delegate)
    {
        UUBluetoothGatt gatt = UUBluetooth.gattForPeripheral(this);
        if (gatt != null)
        {
            gatt.writeCharacteristic(characteristic, data, timeout, delegate);
        }
    }

    public void writeCharacteristicWithoutResponse(
            final @NonNull BluetoothGattCharacteristic characteristic,
            final @NonNull byte[] data,
            final long timeout,
            final @NonNull UUCharacteristicDelegate delegate)
    {
        UUBluetoothGatt gatt = UUBluetooth.gattForPeripheral(this);
        if (gatt != null)
        {
            gatt.writeCharacteristicWithoutResponse(characteristic, data, timeout, delegate);
        }
    }

    public void readRssi(
            final long timeout,
            final @NonNull UUPeripheralErrorDelegate delegate)
    {
        UUBluetoothGatt gatt = UUBluetooth.gattForPeripheral(this);
        if (gatt != null)
        {
            gatt.readRssi(timeout, delegate);
        }
    }

    public void startRssiPolling(@NonNull final Context context, final long interval, @NonNull final UUPeripheralDelegate delegate)
    {
        UUBluetoothGatt gatt = UUBluetooth.gattForPeripheral(this);
        if (gatt != null)
        {
            gatt.startRssiPolling(context, interval, delegate);
        }
    }

    public void stopRssiPolling()
    {
        UUBluetoothGatt gatt = UUBluetooth.gattForPeripheral(this);
        if (gatt != null)
        {
            gatt.stopRssiPolling();
        }
    }

    public boolean isPollingForRssi()
    {
        boolean isPolling = false;

        UUBluetoothGatt gatt = UUBluetooth.gattForPeripheral(this);
        if (gatt != null)
        {
            isPolling = gatt.isPollingForRssi();
        }

        return isPolling;
    }

    public long getTimeSinceLastUpdate()
    {
        return System.currentTimeMillis() - lastAdvertisementTime;
    }

    public void cancelAllTimers()
    {
        UUBluetoothGatt gatt = UUBluetooth.gattForPeripheral(this);
        if (gatt != null)
        {
            gatt.cancelAllTimers();
        }
    }

    private void parseScanRecord()
    {
        if (scanRecord != null)
        {
            int index = 0;

            while (index < scanRecord.length)
            {
                byte length = scanRecord[index];
                if (length == 0)
                    break;

                byte dataType = scanRecord[index + 1];

                byte[] data = new byte[length - 1];
                System.arraycopy(scanRecord, index + 2, data, 0, data.length);

                switch (dataType)
                {
                    case DATA_TYPE_MANUFACTURING_DATA:
                    {
                        manufacturingData = data;
                        break;
                    }

                    case DATA_TYPE_COMPLETE_LOCAL_NAME:
                    {
                        localName = UUString.byteToUtf8String(data);
                        break;
                    }

                    case DATA_TYPE_INCOMPLETE_LIST_OF_16_BIT_SERVICE_CLASS_UUIDS:
                    case DATA_TYPE_COMPLETE_LIST_OF_16_BIT_SERVICE_CLASS_UUIDS:
                    {
                        parseServiceUuid(data, 2);
                        break;
                    }

                    case DATA_TYPE_COMPLETE_LIST_OF_128_BIT_SERVICE_CLASS_UUIDS:
                    case DATA_TYPE_INCOMPLETE_LIST_OF_128_BIT_SERVICE_CLASS_UUIDS:
                    {
                        parseServiceUuid(data, 16);
                        break;
                    }
                }

                index += (1 + length);
            }

            if (UUData.isNotEmpty(manufacturingData))
            {
                parseManufacturingData(manufacturingData);
            }
        }
    }

    private void parseServiceUuid(final byte[] data, final int length)
    {
        int index = 0;

        while (index < data.length)
        {
            String hexChunk = UUString.byteToHex(data, index, length);
            if (UUString.isNotEmpty(hexChunk))
            {
                serviceUuids.add(hexChunk);
            }

            index += length;
        }
    }

    protected void parseManufacturingData(final @NonNull byte[] manufacturingData)
    {
        // Default does nothing
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // System.Object overrides
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @NonNull
    @Override
    public String toString()
    {
        try
        {
            return String.format(Locale.US,
                    "%s, %s, %d, %s", getAddress(), getName(), this.rssi, UUString.byteToHex(manufacturingData));
        }
        catch (Exception ex)
        {
            return super.toString();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // JSON
    ////////////////////////////////////////////////////////////////////////////////////////////////
/*
    private static final String JSON_DEVICE_KEY = "device";
    private static final String JSON_RSSI_KEY = "rssi";
    private static final String JSON_RSSI_LAST_UPDATE_KEY = "rssi_last_updated";
    private static final String JSON_SCAN_RECORD_KEY = "scanRecord";
    private static final String JSON_FIRST_ADVERTISEMENT_KEY = "first_advertisement";
    private static final String JSON_LAST_ADVERTISEMENT_KEY = "last_advertisement";
    private static final String JSON_TOTAL_BEACON_COUNT_KEY = "total_beacon_count";

    @NonNull
    @Override
    public JSONObject toJsonObject()
    {
        JSONObject o = new JSONObject();

        UUJson.safePut(o, JSON_DEVICE_KEY, UUString.byteToHex(UUParcel.serializeParcel(device)));
        UUJson.safePut(o, JSON_SCAN_RECORD_KEY, UUString.byteToHex(scanRecord));
        UUJson.safePut(o, JSON_RSSI_KEY, rssi);
        UUJson.safePut(o, JSON_RSSI_LAST_UPDATE_KEY, lastRssiUpdateTime);
        UUJson.safePut(o, JSON_FIRST_ADVERTISEMENT_KEY, firstAdvertisementTime);
        UUJson.safePut(o, JSON_LAST_ADVERTISEMENT_KEY, lastAdvertisementTime);
        UUJson.safePut(o, JSON_TOTAL_BEACON_COUNT_KEY, totalBeaconCount);

        return o;
    }

    @Override
    public void fillFromJson(@NonNull final JSONObject json)
    {
        device = UUParcel.deserializeParcelable(BluetoothDevice.CREATOR, UUString.hexToByte(UUJson.safeGetString(json, JSON_DEVICE_KEY)));
        scanRecord = UUString.hexToByte(UUJson.safeGetString(json, JSON_SCAN_RECORD_KEY));
        rssi = UUJson.safeGetInt(json, JSON_RSSI_KEY);
        lastRssiUpdateTime = UUJson.safeGetLong(json, JSON_RSSI_LAST_UPDATE_KEY);
        firstAdvertisementTime = UUJson.safeGetLong(json, JSON_FIRST_ADVERTISEMENT_KEY);
        lastAdvertisementTime = UUJson.safeGetLong(json, JSON_LAST_ADVERTISEMENT_KEY);
        totalBeaconCount = UUJson.safeGetLong(json, JSON_TOTAL_BEACON_COUNT_KEY);

        // Fill in derived data from scan record
        parseScanRecord();
    }*/

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Parcelable
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
//        try
//        {
//            dest.writeString(toJsonObject().toString());
//        }
//        catch (Exception ex)
//        {
//            debugLog("writeToParcel", ex);
//        }
    }

    public static final Parcelable.Creator<UUPeripheral> CREATOR
            = new Parcelable.Creator<UUPeripheral>()
    {
        public UUPeripheral createFromParcel(Parcel in)
        {
            return new UUPeripheral(in);
        }

        public UUPeripheral[] newArray(int size)
        {
            return new UUPeripheral[size];
        }
    };

    protected UUPeripheral(final Parcel in)
    {
        //String jsonStr = in.readString();
        //JSONObject json = UUJson.toJsonObject(jsonStr);
        //fillFromJson(json);
    }

    private void acquireExistingGatt()
    {
        UUBluetoothGatt gatt = UUBluetooth.gattForPeripheral(this);
        if (gatt != null)
        {
            setBluetoothGatt(gatt.getBluetoothGatt());
        }
    }

    private static void debugLog(final String method, final String message)
    {
        if (LOGGING_ENABLED)
        {
            UULog.debug(UUPeripheral.class, method, message);
        }
    }

    private synchronized static void debugLog(final String method, final Throwable exception)
    {
        if (LOGGING_ENABLED)
        {
            UULog.debug(UUPeripheral.class, method, exception);
        }
    }
}