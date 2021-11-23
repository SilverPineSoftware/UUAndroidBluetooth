package com.silverpine.uu.bluetooth;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import com.silverpine.uu.core.UUNonNullObjectDelegate;
import com.silverpine.uu.core.UUObjectDelegate;
import com.silverpine.uu.core.UURunnable;
import com.silverpine.uu.core.UUString;
import com.silverpine.uu.logging.UULog;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class UUPeripheralOperation<T extends UUPeripheral>
{
    private final @NonNull T peripheral;
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
                //let err = NSError(domain: "Err", code: -1, userInfo: [NSLocalizedDescriptionKey: "No services were discovered"])
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

