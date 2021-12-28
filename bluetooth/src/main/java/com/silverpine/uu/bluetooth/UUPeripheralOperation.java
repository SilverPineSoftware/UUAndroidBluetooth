package com.silverpine.uu.bluetooth;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import com.silverpine.uu.core.UUData;
import com.silverpine.uu.core.UUNonNullObjectDelegate;
import com.silverpine.uu.core.UUObjectDelegate;
import com.silverpine.uu.core.UURunnable;
import com.silverpine.uu.core.UUString;
import com.silverpine.uu.logging.UULog;

import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import kotlin.text.Charsets;

public class UUPeripheralOperation<T extends UUPeripheral>
{
    protected final @NonNull T peripheral;
    private @Nullable UUObjectDelegate<UUBluetoothError> operationCallback;
    private final @NonNull ArrayList<BluetoothGattService> discoveredServices = new ArrayList<>();
    private final @NonNull ArrayList<BluetoothGattCharacteristic> discoveredCharacteristics = new ArrayList<>();
    private final @NonNull ArrayList<BluetoothGattService> servicesNeedingCharacteristicDiscovery = new ArrayList<>();
    private long connectTimeout = UUPeripheral.Defaults.ConnectTimeout;
    private long disconnectTimeout = UUPeripheral.Defaults.DisconnectTimeout;
    private long serviceDiscoveryTimeout = UUPeripheral.Defaults.ServiceDiscoveryTimeout;
    private long readTimeout = UUPeripheral.Defaults.OperationTimeout;
    private long writeTimeout = UUPeripheral.Defaults.OperationTimeout;


    public UUPeripheralOperation(@NonNull final T peripheral)
    {
        this.peripheral = peripheral;
    }

    public long getConnectTimeout()
    {
        return connectTimeout;
    }

    public void setConnectTimeout(long connectTimeout)
    {
        this.connectTimeout = connectTimeout;
    }

    public long getDisconnectTimeout()
    {
        return disconnectTimeout;
    }

    public void setDisconnectTimeout(long disconnectTimeout)
    {
        this.disconnectTimeout = disconnectTimeout;
    }

    public long getServiceDiscoveryTimeout()
    {
        return serviceDiscoveryTimeout;
    }

    public void setServiceDiscoveryTimeout(long serviceDiscoveryTimeout)
    {
        this.serviceDiscoveryTimeout = serviceDiscoveryTimeout;
    }

    @Nullable
    public BluetoothGattService findDiscoveredService(@NonNull final UUID uuid)
    {
        for (BluetoothGattService service: discoveredServices)
        {
            if (service.getUuid().equals(uuid))
            {
                return service;
            }
        }

        return null;
    }

    @Nullable
    public BluetoothGattCharacteristic findDiscoveredCharacteristic(@NonNull final UUID uuid)
    {
        for (BluetoothGattCharacteristic characteristic: discoveredCharacteristics)
        {
            if (characteristic.getUuid().equals(uuid))
            {
                return characteristic;
            }
        }

        return null;
    }

    public void requireDiscoveredService(@NonNull final UUID uuid, @NonNull UUNonNullObjectDelegate<BluetoothGattService> completion)
    {
        BluetoothGattService discovered = findDiscoveredService(uuid);
        if (discovered == null)
        {
            UUBluetoothError err = new UUBluetoothError(UUBluetoothErrorCode.OperationFailed);
            end(err);
            return;
        }

        UUNonNullObjectDelegate.safeInvoke(completion, discovered);
    }

    public void requireDiscoveredCharacteristic(@NonNull final UUID uuid, @NonNull UUNonNullObjectDelegate<BluetoothGattCharacteristic> completion)
    {
        BluetoothGattCharacteristic discovered = findDiscoveredCharacteristic(uuid);
        if (discovered == null)
        {
            UUBluetoothError err = new UUBluetoothError(UUBluetoothErrorCode.OperationFailed);
            end(err);
            return;
        }

        UUNonNullObjectDelegate.safeInvoke(completion, discovered);
    }

    public void write(@NonNull final byte[] data, @NonNull final UUID toCharacteristic, @NonNull final Runnable completion)
    {
        requireDiscoveredCharacteristic(toCharacteristic,
            characteristic -> peripheral.writeCharacteristic(characteristic, data, writeTimeout, (peripheral1, characteristic1, error) ->
            {
                if (error != null)
                {
                    end(error);
                    return;
                }

                UURunnable.safeInvoke(completion);
            }));
    }

    public void wwor(@NonNull final byte[] data, @NonNull final UUID toCharacteristic, @NonNull final Runnable completion)
    {
        requireDiscoveredCharacteristic(toCharacteristic,
            characteristic -> peripheral.writeCharacteristicWithoutResponse(characteristic, data, writeTimeout, (peripheral1, characteristic1, error) ->
            {
                if (error != null)
                {
                    end(error);
                    return;
                }

                UURunnable.safeInvoke(completion);
            }));
    }

    public void read(@NonNull final UUID fromCharacteristic, @NonNull final UUObjectDelegate<byte[]> completion)
    {
        requireDiscoveredCharacteristic(fromCharacteristic, characteristic ->
            peripheral.readCharacteristic(characteristic, readTimeout, (peripheral1, characteristic1, error) ->
            {
                if (error != null)
                {
                    end(error);
                    return;
                }

                UUObjectDelegate.safeInvoke(completion, characteristic.getValue());
            }));
    }

    public void readString(@NonNull final UUID fromCharacteristic, @NonNull final Charset charset, @NonNull final UUObjectDelegate<String> completion)
    {
        read(fromCharacteristic, data ->
        {
            String result = null;

            if (data != null)
            {
                result = new String(data, charset);
            }

            UUObjectDelegate.safeInvoke(completion, result);
        });
    }

    public void readUtf8(@NonNull final UUID fromCharacteristic, @NonNull final UUObjectDelegate<String> completion)
    {
        readString(fromCharacteristic, Charsets.UTF_8, completion);
    }

    public void readUInt8(@NonNull final UUID fromCharacteristic, @NonNull final UUObjectDelegate<Integer> completion)
    {
        read(fromCharacteristic, data ->
        {
            Integer result = null;

            if (data != null && data.length >= Byte.BYTES)
            {
                result = UUData.readUInt8(data, 0);
            }

            UUObjectDelegate.safeInvoke(completion, result);
        });
    }

    public void readUInt16(@NonNull final UUID fromCharacteristic, @NonNull final ByteOrder byteOrder, @NonNull final UUObjectDelegate<Integer> completion)
    {
        read(fromCharacteristic, data ->
        {
            Integer result = null;

            if (data != null && data.length >= Short.BYTES)
            {
                result = UUData.readUInt16(byteOrder, data, 0);
            }

            UUObjectDelegate.safeInvoke(completion, result);
        });
    }

    public void readUInt32(@NonNull final UUID fromCharacteristic, @NonNull final ByteOrder byteOrder, @NonNull final UUObjectDelegate<Long> completion)
    {
        read(fromCharacteristic, data ->
        {
            Long result = null;

            if (data != null && data.length >= Integer.BYTES)
            {
                result = UUData.readUInt32(byteOrder, data, 0);
            }

            UUObjectDelegate.safeInvoke(completion, result);
        });
    }

    public void readUInt64(@NonNull final UUID fromCharacteristic, @NonNull final ByteOrder byteOrder, @NonNull final UUObjectDelegate<Long> completion)
    {
        read(fromCharacteristic, data ->
        {
            Long result = null;

            if (data != null && data.length >= Long.BYTES)
            {
                result = UUData.readUInt64(byteOrder, data, 0);
            }

            UUObjectDelegate.safeInvoke(completion, result);
        });
    }

    public void readInt8(@NonNull final UUID fromCharacteristic, @NonNull final UUObjectDelegate<Byte> completion)
    {
        read(fromCharacteristic, data ->
        {
            Byte result = null;

            if (data != null && data.length >= Byte.BYTES)
            {
                result = UUData.readInt8(data, 0);
            }

            UUObjectDelegate.safeInvoke(completion, result);
        });
    }

    public void readInt16(@NonNull final UUID fromCharacteristic, @NonNull final ByteOrder byteOrder, @NonNull final UUObjectDelegate<Short> completion)
    {
        read(fromCharacteristic, data ->
        {
            Short result = null;

            if (data != null && data.length >= Short.BYTES)
            {
                result = UUData.readInt16(byteOrder, data, 0);
            }

            UUObjectDelegate.safeInvoke(completion, result);
        });
    }

    public void readInt32(@NonNull final UUID fromCharacteristic, @NonNull final ByteOrder byteOrder, @NonNull final UUObjectDelegate<Integer> completion)
    {
        read(fromCharacteristic, data ->
        {
            Integer result = null;

            if (data != null && data.length >= Integer.BYTES)
            {
                result = UUData.readInt32(byteOrder, data, 0);
            }

            UUObjectDelegate.safeInvoke(completion, result);
        });
    }

    public void readInt64(@NonNull final UUID fromCharacteristic, @NonNull final ByteOrder byteOrder, @NonNull final UUObjectDelegate<Long> completion)
    {
        read(fromCharacteristic, data ->
        {
            Long result = null;

            if (data != null && data.length >= Long.BYTES)
            {
                result = UUData.readInt64(byteOrder, data, 0);
            }

            UUObjectDelegate.safeInvoke(completion, result);
        });
    }

    public void write(@NonNull final String value, @NonNull final Charset charset, @NonNull final UUID toCharacteristic, @NonNull final Runnable completion)
    {
        byte[] buffer = value.getBytes(charset);
        write(buffer, toCharacteristic, completion);
    }

    public void writeUtf8(@NonNull final String value, @NonNull final UUID toCharacteristic, @NonNull final Runnable completion)
    {
        write(value, Charsets.UTF_8, toCharacteristic, completion);
    }

    public void writeUInt8(int value, @NonNull final UUID toCharacteristic, @NonNull final Runnable completion)
    {
        byte[] buffer = new byte[Byte.BYTES];
        UUData.writeUInt8(buffer, 0, value);
        write(buffer, toCharacteristic, completion);
    }

    public void writeUInt16(int value, @NonNull final ByteOrder byteOrder, @NonNull final UUID toCharacteristic, @NonNull final Runnable completion)
    {
        byte[] buffer = new byte[Short.BYTES];
        UUData.writeUInt16(byteOrder, buffer, 0, value);
        write(buffer, toCharacteristic, completion);
    }

    public void writeUInt32(long value, @NonNull final ByteOrder byteOrder, @NonNull final UUID toCharacteristic, @NonNull final Runnable completion)
    {
        byte[] buffer = new byte[Integer.BYTES];
        UUData.writeUInt32(byteOrder, buffer, 0, value);
        write(buffer, toCharacteristic, completion);
    }

    public void writeUInt64(long value, @NonNull final ByteOrder byteOrder, @NonNull final UUID toCharacteristic, @NonNull final Runnable completion)
    {
        byte[] buffer = new byte[Long.BYTES];
        UUData.writeUInt64(byteOrder, buffer, 0, value);
        write(buffer, toCharacteristic, completion);
    }

    public void writeInt8(byte value, @NonNull final UUID toCharacteristic, @NonNull final Runnable completion)
    {
        byte[] buffer = new byte[Byte.BYTES];
        UUData.writeInt8(buffer, 0, value);
        write(buffer, toCharacteristic, completion);
    }

    public void writeInt16(short value, @NonNull final ByteOrder byteOrder, @NonNull final UUID toCharacteristic, @NonNull final Runnable completion)
    {
        byte[] buffer = new byte[Short.BYTES];
        UUData.writeInt16(byteOrder, buffer, 0, value);
        write(buffer, toCharacteristic, completion);
    }

    public void writeInt32(int value, @NonNull final ByteOrder byteOrder, @NonNull final UUID toCharacteristic, @NonNull final Runnable completion)
    {
        byte[] buffer = new byte[Integer.BYTES];
        UUData.writeInt32(byteOrder, buffer, 0, value);
        write(buffer, toCharacteristic, completion);
    }

    public void writeInt64(long value, @NonNull final ByteOrder byteOrder, @NonNull final UUID toCharacteristic, @NonNull final Runnable completion)
    {
        byte[] buffer = new byte[Long.BYTES];
        UUData.writeInt64(byteOrder, buffer, 0, value);
        write(buffer, toCharacteristic, completion);
    }

    public final void start(UUObjectDelegate<UUBluetoothError> completion)
    {
        operationCallback = completion;

        peripheral.connect(connectTimeout, disconnectTimeout, this::handleConnected, this::handleDisconnection);
    }

    public void end(@Nullable final UUBluetoothError error)
    {
        UULog.debug(getClass(), "end", "**** Ending Operation with error: " + UUString.safeToString(error));
        peripheral.disconnect(error);
    }

    protected void execute(@NonNull final UUObjectDelegate<UUBluetoothError> completion)
    {
        UUObjectDelegate.safeInvoke(completion, null);
    }

    private void handleConnected()
    {
        peripheral.discoverServices(serviceDiscoveryTimeout, (peripheral1, error) ->
        {
            if (error != null)
            {
                end(error);
                return;
            }

            discoveredServices.clear();
            discoveredCharacteristics.clear();
            servicesNeedingCharacteristicDiscovery.clear();

            List<BluetoothGattService> services = peripheral.discoveredServices();

            if (services.isEmpty())
            {
                UUBluetoothError err = new UUBluetoothError(UUBluetoothErrorCode.OperationFailed);
                end(err);
                return;
            }

            discoveredServices.addAll(services);
            servicesNeedingCharacteristicDiscovery.addAll(services);
            discoverNextCharacteristics();
        });
    }

    private void handleDisconnection(@Nullable final UUBluetoothError disconnectError)
    {
        UUObjectDelegate<UUBluetoothError> callback = operationCallback;
        operationCallback = null;
        UUObjectDelegate.safeInvoke(callback, disconnectError);
    }

    private void discoverNextCharacteristics()
    {
        if (servicesNeedingCharacteristicDiscovery.isEmpty())
        {
            handleCharacteristicDiscoveryFinished();
            return;
        }

        BluetoothGattService service = servicesNeedingCharacteristicDiscovery.remove(0);

        discoverCharacteristics(service, this::discoverNextCharacteristics);
    }

    private void discoverCharacteristics(@NonNull final BluetoothGattService service, @NonNull final Runnable completion)
    {
        discoveredCharacteristics.addAll(service.getCharacteristics());
        completion.run();
    }

    private void handleCharacteristicDiscoveryFinished()
    {
        execute(this::end);
    }

}

