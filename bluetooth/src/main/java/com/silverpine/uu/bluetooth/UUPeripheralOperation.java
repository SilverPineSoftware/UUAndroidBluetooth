package com.silverpine.uu.bluetooth;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import com.silverpine.uu.core.UUNonNullObjectDelegate;
import com.silverpine.uu.core.UUObjectDelegate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class UUPeripheralOperation<T extends UUPeripheral>
{
    private final @NonNull Context applicationContext;
    private final @NonNull T peripheral;
    private @Nullable UUBluetoothError operationError;
    private @Nullable UUObjectDelegate<UUBluetoothError> operationCallback;
    private final @NonNull ArrayList<BluetoothGattService> discoveredServices = new ArrayList<>();
    private final @NonNull ArrayList<BluetoothGattCharacteristic> discoveredCharacteristics = new ArrayList<>();
    private final @NonNull ArrayList<BluetoothGattService> servicesNeedingCharacteristicDiscovery = new ArrayList<>();
    private long connectTimeout = UUPeripheral.Defaults.ConnectTimeout;
    private long disconnectTimeout = UUPeripheral.Defaults.DisconnectTimeout;
    private long serviceDiscoveryTimeout = UUPeripheral.Defaults.ServiceDiscoveryTimeout;
    private long readTimeout = UUPeripheral.Defaults.OperationTimeout;
    private long writeTimeout = UUPeripheral.Defaults.OperationTimeout;
    private final @NonNull ArrayList<UUID> servicesToDiscover = new ArrayList<>();


    UUPeripheralOperation(@NonNull final Context applicationContext, @NonNull final T peripheral)
    {
        this.applicationContext = applicationContext;
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

    public void write(@NonNull final byte[] data, @NonNull final UUID toCharacteristic, @NonNull final UUObjectDelegate<UUBluetoothError> completion)
    {
        requireDiscoveredCharacteristic(toCharacteristic,
            characteristic -> peripheral.writeCharacteristic(characteristic, data, writeTimeout, (peripheral1, characteristic1, error) ->
                UUObjectDelegate.safeInvoke(completion, error)));
    }

    public void wwor(@NonNull final byte[] data, @NonNull final UUID toCharacteristic, @NonNull final UUObjectDelegate<UUBluetoothError> completion)
    {
        requireDiscoveredCharacteristic(toCharacteristic,
            characteristic -> peripheral.writeCharacteristicWithoutResponse(characteristic, data, writeTimeout, (peripheral1, characteristic1, error) ->
                UUObjectDelegate.safeInvoke(completion, error)));
    }

    public void read(@NonNull final UUID fromCharacteristic, @NonNull final UUObjectDelegate<byte[]> completion)
    {
        requireDiscoveredCharacteristic(fromCharacteristic, characteristic ->
            peripheral.readCharacteristic(characteristic, readTimeout, (peripheral1, characteristic1, error) ->
                UUObjectDelegate.safeInvoke(completion, characteristic.getValue())));
    }

    public final void start(UUObjectDelegate<UUBluetoothError> completion)
    {
        operationError = null;
        operationCallback = completion;

        UUBluetooth.connectPeripheral(applicationContext, peripheral, false, connectTimeout, disconnectTimeout, new UUConnectionDelegate()
        {
            @Override
            public void onConnected(@NonNull UUPeripheral peripheral)
            {
                handleConnected();
            }

            @Override
            public void onDisconnected(@NonNull UUPeripheral peripheral, @Nullable UUBluetoothError error)
            {
                handleDisconnection(error);
            }
        });
    }

    public void end(@Nullable final UUBluetoothError error)
    {
        //NSLog("**** Ending Operation with error: \(error?.localizedDescription ?? "nil")")
        operationError = error;
        UUBluetooth.disconnectPeripheral(peripheral);
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
        if (operationError == null)
        {
            operationError = disconnectError;
        }

        UUObjectDelegate<UUBluetoothError> callback = operationCallback;
        UUBluetoothError err = operationError;
        operationCallback = null;
        operationError = null;
        UUObjectDelegate.safeInvoke(callback, err);
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

