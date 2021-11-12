package com.silverpine.uu.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.Context;
import android.os.Build;

import com.silverpine.uu.core.UUString;
import com.silverpine.uu.core.UUThread;
import com.silverpine.uu.core.UUTimer;
import com.silverpine.uu.logging.UULog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * A helpful set of wrapper methods around BluetoothGatt
 */
class UUBluetoothGatt
{
    private static boolean LOGGING_ENABLED = UULog.LOGGING_ENABLED;

    // Internal Constants
    private static final String CONNECT_WATCHDOG_BUCKET = "UUBluetoothConnectWatchdogBucket";
    private static final String SERVICE_DISCOVERY_WATCHDOG_BUCKET = "UUBluetoothServiceDiscoveryWatchdogBucket";
    private static final String CHARACTERISTIC_NOTIFY_STATE_WATCHDOG_BUCKET = "UUBluetoothCharacteristicNotifyStateWatchdogBucket";
    private static final String READ_CHARACTERISTIC_WATCHDOG_BUCKET = "UUBluetoothReadCharacteristicValueWatchdogBucket";
    private static final String WRITE_CHARACTERISTIC_WATCHDOG_BUCKET = "UUBluetoothWriteCharacteristicValueWatchdogBucket";
    private static final String READ_DESCRIPTOR_WATCHDOG_BUCKET = "UUBluetoothReadDescriptorValueWatchdogBucket";
    private static final String WRITE_DESCRIPTOR_WATCHDOG_BUCKET = "UUBluetoothWriteDescriptorValueWatchdogBucket";
    private static final String READ_RSSI_WATCHDOG_BUCKET = "UUBluetoothReadRssiWatchdogBucket";
    private static final String POLL_RSSI_BUCKET = "UUBluetoothPollRssiBucket";
    private static final String DISCONNECT_WATCHDOG_BUCKET = "UUBluetoothDisconnectWatchdogBucket";

    private static final int TIMEOUT_DISABLED = -1;

    private final Context context;
    private final UUPeripheral peripheral;
    private BluetoothGatt bluetoothGatt;
    private final BluetoothGattCallback bluetoothGattCallback;

    private UUConnectionDelegate connectionDelegate;
    private UUPeripheralErrorDelegate serviceDiscoveryDelegate;
    private UUPeripheralErrorDelegate readRssiDelegate;
    private UUPeripheralDelegate pollRssiDelegate;

    private UUBluetoothError disconnectError;

    private final HashMap<String, UUCharacteristicDelegate> readCharacteristicDelegates = new HashMap<>();
    private final HashMap<String, UUCharacteristicDelegate> writeCharacteristicDelegates = new HashMap<>();
    private final HashMap<String, UUCharacteristicDelegate> characteristicChangedDelegates = new HashMap<>();
    private final HashMap<String, UUCharacteristicDelegate> setNotifyDelegates = new HashMap<>();
    private final HashMap<String, UUDescriptorDelegate> readDescriptorDelegates = new HashMap<>();
    private final HashMap<String, UUDescriptorDelegate> writeDescriptorDelegates = new HashMap<>();

    private long disconnectTimeout = 0;

    UUBluetoothGatt(@NonNull final Context context, @NonNull final UUPeripheral peripheral)
    {
        this.context = context;
        this.peripheral = peripheral;
        bluetoothGattCallback = new UUBluetoothGattCallback();
    }

    boolean isConnecting()
    {
        return (bluetoothGatt != null && isConnectWatchdogActive());
    }

    private boolean isConnectWatchdogActive()
    {
        UUTimer t = UUTimer.findActiveTimer(connectWatchdogTimerId());
        return (t != null);
    }

    void connect(
        final boolean connectGattAutoFlag,
        final long timeout,
        final long disconnectTimeout,
        final @NonNull UUConnectionDelegate delegate)
    {
        final String timerId = connectWatchdogTimerId();

        connectionDelegate = new UUConnectionDelegate()
        {
            @Override
            public void onConnected(@NonNull UUPeripheral peripheral)
            {
                debugLog("connect", "Connected to: " + peripheral);
                UUTimer.cancelActiveTimer(timerId);
                delegate.onConnected(peripheral);
            }

            @Override
            public void onDisconnected(@NonNull UUPeripheral peripheral, @Nullable UUBluetoothError error)
            {
                debugLog("connect", "Disconnected from: " + peripheral + ", error: " + error);
                cancelAllTimers();
                delegate.onDisconnected(peripheral, error);
            }
        };

        UUTimer.startTimer(timerId, timeout, peripheral, new UUTimer.TimerDelegate()
        {
            @Override
            public void onTimer(@NonNull UUTimer timer, @Nullable Object userInfo)
            {
                debugLog("connect", "Connect timeout: " + peripheral);

                disconnect(UUBluetoothError.timeoutError());
            }
        });

        this.disconnectTimeout = disconnectTimeout;
        UUThread.runOnMainThread(new Runnable()
        {
            @Override
            public void run()
            {
                debugLog("connect", "Connecting to: " + peripheral + ", gattAuto: " + connectGattAutoFlag);

                disconnectError = null;
                bluetoothGatt = peripheral.getBluetoothDevice().connectGatt(context, connectGattAutoFlag, bluetoothGattCallback, BluetoothDevice.TRANSPORT_LE);
            }
        });
    }

    void disconnect(@Nullable final UUBluetoothError error)
    {
        disconnectError = error;

        String timerId = disconnectWatchdogTimerId();

        final long timeout = disconnectTimeout;

        UUTimer.startTimer(timerId, timeout, peripheral, new UUTimer.TimerDelegate()
        {
            @Override
            public void onTimer(@NonNull UUTimer timer, @Nullable Object userInfo)
            {
                debugLog("disconnect", "Disconnect timeout: " + peripheral);
                notifyDisconnected(error);

                // Just in case the timeout fires and a real disconnect is needed, this is the last
                // ditch effort to close the connection
                disconnectGattOnMainThread();
            }
        });

        disconnectGattOnMainThread();
    }

    private boolean requestHighPriority()
    {
        try
        {
            if (bluetoothGatt != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                int connectionPriority = BluetoothGatt.CONNECTION_PRIORITY_HIGH;
                debugLog("requestHighPriority", "Requesting connection priority " + connectionPriority);
                boolean result = bluetoothGatt.requestConnectionPriority(connectionPriority);
                debugLog("requestHighPriority", "requestConnectionPriority returned " + result);
                return result;
            }
        }
        catch (Exception ex)
        {
            logException("requestHighPriority", ex);
        }

        return false;
    }

    void requestHighPriority(@NonNull final UUPeripheralBoolDelegate delegate)
    {
        UUThread.runOnMainThread(new Runnable()
        {
            @Override
            public void run()
            {
                boolean result = requestHighPriority();
                notifyBoolResult(delegate, result);
            }
        });
    }

    void discoverServices(
            final long timeout,
            final @NonNull UUPeripheralErrorDelegate delegate)
    {
        final String timerId = serviceDiscoveryWatchdogTimerId();

        serviceDiscoveryDelegate = new UUPeripheralErrorDelegate()
        {
            @Override
            public void onComplete(@NonNull UUPeripheral peripheral, @Nullable UUBluetoothError error)
            {
                debugLog("discoverServices", "Service Discovery complete: " + peripheral + ", error: " + error);
                UUTimer.cancelActiveTimer(timerId);
                delegate.onComplete(peripheral, error);
            }
        };

        UUTimer.startTimer(timerId, timeout, peripheral, new UUTimer.TimerDelegate()
        {
            @Override
            public void onTimer(@NonNull UUTimer timer, @Nullable Object userInfo)
            {
                debugLog("discoverServices", "Service Discovery timeout: " + peripheral);

                disconnect(UUBluetoothError.timeoutError());
            }
        });

        UUThread.runOnMainThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (bluetoothGatt == null)
                {
                    debugLog("discoverServices", "bluetoothGatt is null!");
                    notifyServicesDiscovered(UUBluetoothError.notConnectedError());
                    return;
                }

                debugLog("discoverServices", "Discovering services for: " + peripheral);
                boolean ok = bluetoothGatt.discoverServices();
                debugLog("discoverServices", "returnCode: " + ok);

                if (!ok)
                {
                    notifyServicesDiscovered(UUBluetoothError.operationFailedError("discoverServices"));
                }
                // else
                //
                // wait for delegate or timeout
            }
        });
    }

    void readCharacteristic(
            final @NonNull BluetoothGattCharacteristic characteristic,
            final long timeout,
            final @NonNull UUCharacteristicDelegate delegate)
    {
        final String timerId = readCharacteristicWatchdogTimerId(characteristic);

        UUCharacteristicDelegate readCharacteristicDelegate = new UUCharacteristicDelegate()
        {
            @Override
            public void onComplete(@NonNull UUPeripheral peripheral, @NonNull BluetoothGattCharacteristic characteristic, @Nullable UUBluetoothError error)
            {
                debugLog("readCharacteristic", "Read characteristic complete: " + peripheral + ", error: " + error + ", data: " + UUString.byteToHex(characteristic.getValue()));
                UUTimer.cancelActiveTimer(timerId);
                removeReadCharacteristicDelegate(characteristic);
                delegate.onComplete(peripheral, characteristic, error);
            }
        };

        registerReadCharacteristicDelegate(characteristic, readCharacteristicDelegate);

        UUTimer.startTimer(timerId, timeout, peripheral, new UUTimer.TimerDelegate()
        {
            @Override
            public void onTimer(@NonNull UUTimer timer, @Nullable Object userInfo)
            {
                debugLog("readCharacteristic", "Read characteristic timeout: " + peripheral);

                disconnect(UUBluetoothError.timeoutError());
            }
        });

        UUThread.runOnMainThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (bluetoothGatt == null)
                {
                    debugLog("readCharacteristic", "bluetoothGatt is null!");
                    notifyCharacteristicRead(characteristic, UUBluetoothError.notConnectedError());
                    return;
                }

                debugLog("readCharacteristic", "characteristic: " + characteristic.getUuid());

                boolean success = bluetoothGatt.readCharacteristic(characteristic);

                debugLog("readCharacteristic", "readCharacteristic returned " + success);

                if (!success)
                {
                    notifyCharacteristicRead(characteristic, UUBluetoothError.operationFailedError("readCharacteristic"));
                }
            }
        });
    }

    void readDescriptor(
            final @NonNull BluetoothGattDescriptor descriptor,
            final long timeout,
            final @NonNull UUDescriptorDelegate delegate)
    {
        final String timerId = readDescritporWatchdogTimerId(descriptor);

        UUDescriptorDelegate readDescriptorDelegate = new UUDescriptorDelegate()
        {
            @Override
            public void onComplete(@NonNull UUPeripheral peripheral, @NonNull BluetoothGattDescriptor descriptor, @Nullable UUBluetoothError error)
            {
                debugLog("readDescriptor", "Read descriptor complete: " + peripheral + ", error: " + error + ", data: " + UUString.byteToHex(descriptor.getValue()));
                removeReadDescriptorDelegate(descriptor);
                UUTimer.cancelActiveTimer(timerId);
                delegate.onComplete(peripheral, descriptor, error);
            }
        };

        registerReadDescriptorDelegate(descriptor, readDescriptorDelegate);

        UUTimer.startTimer(timerId, timeout, peripheral, new UUTimer.TimerDelegate()
        {
            @Override
            public void onTimer(@NonNull UUTimer timer, @Nullable Object userInfo)
            {
                debugLog("readDescriptor", "Read descriptor timeout: " + peripheral);

                disconnect(UUBluetoothError.timeoutError());
            }
        });

        UUThread.runOnMainThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (bluetoothGatt == null)
                {
                    debugLog("readDescriptor", "bluetoothGatt is null!");
                    notifyDescriptorRead(descriptor, UUBluetoothError.notConnectedError());
                    return;
                }

                debugLog("readDescriptor", "descriptor: " + descriptor.getUuid());

                boolean success = bluetoothGatt.readDescriptor(descriptor);

                debugLog("readDescriptor", "readDescriptor returned " + success);

                if (!success)
                {
                    notifyDescriptorRead(descriptor, UUBluetoothError.operationFailedError("readDescriptor"));
                }
            }
        });
    }

    void writeDescriptor(
            final @NonNull BluetoothGattDescriptor descriptor,
            final byte[] data,
            final long timeout,
            final @NonNull UUDescriptorDelegate delegate)
    {
        final String timerId = writeDescriptorWatchdogTimerId(descriptor);

        UUDescriptorDelegate writeDescriptorDelegate = new UUDescriptorDelegate()
        {
            @Override
            public void onComplete(@NonNull UUPeripheral peripheral, @NonNull BluetoothGattDescriptor descriptor, @Nullable UUBluetoothError error)
            {
                debugLog("readDescriptor", "Write descriptor complete: " + peripheral + ", error: " + error + ", data: " + UUString.byteToHex(descriptor.getValue()));
                removeWriteDescriptorDelegate(descriptor);
                UUTimer.cancelActiveTimer(timerId);
                delegate.onComplete(peripheral, descriptor, error);
            }
        };

        registerWriteDescriptorDelegate(descriptor, writeDescriptorDelegate);

        UUTimer.startTimer(timerId, timeout, peripheral, new UUTimer.TimerDelegate()
        {
            @Override
            public void onTimer(@NonNull UUTimer timer, @Nullable Object userInfo)
            {
                debugLog("writeDescriptor", "Write descriptor timeout: " + peripheral);

                disconnect(UUBluetoothError.timeoutError());
            }
        });

        UUThread.runOnMainThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (bluetoothGatt == null)
                {
                    debugLog("writeDescriptor", "bluetoothGatt is null!");
                    notifyDescriptorWritten(descriptor, UUBluetoothError.notConnectedError());
                    return;
                }

                descriptor.setValue(data);

                boolean success = bluetoothGatt.writeDescriptor(descriptor);
                debugLog("writeDescriptor", "writeDescriptor returned " + success);

                if (!success)
                {
                    notifyDescriptorWritten(descriptor, UUBluetoothError.operationFailedError("writeDescriptor"));
                }
                // else
                //
                // wait for delegate or timeout

            }
        });
    }

    void setNotifyState(
            final @NonNull BluetoothGattCharacteristic characteristic,
            final boolean enabled,
            final long timeout,
            final @Nullable UUCharacteristicDelegate notifyDelegate,
            final @NonNull UUCharacteristicDelegate delegate)
    {
        final String timerId = setNotifyStateWatchdogTimerId(characteristic);

        UUCharacteristicDelegate setNotifyDelegate = new UUCharacteristicDelegate()
        {
            @Override
            public void onComplete(@NonNull UUPeripheral peripheral, @NonNull BluetoothGattCharacteristic characteristic, @Nullable UUBluetoothError error)
            {
                debugLog("setNotifyState", "Set characteristic notify complete: " + peripheral + ", error: " + error + ", data: " + UUString.byteToHex(characteristic.getValue()));
                removeSetNotifyDelegate(characteristic);
                UUTimer.cancelActiveTimer(timerId);
                delegate.onComplete(peripheral, characteristic, error);
            }
        };

        registerSetNotifyDelegate(characteristic, setNotifyDelegate);

        UUTimer.startTimer(timerId, timeout, peripheral, new UUTimer.TimerDelegate()
        {
            @Override
            public void onTimer(@NonNull UUTimer timer, @Nullable Object userInfo)
            {
                debugLog("setNotifyState", "Set notify state timeout: " + peripheral);

                disconnect(UUBluetoothError.timeoutError());
            }
        });

        final long start = System.currentTimeMillis();

        UUThread.runOnMainThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (bluetoothGatt == null)
                {
                    debugLog("toggleNotifyState", "bluetoothGatt is null!");
                    notifyCharacteristicNotifyStateChanged(characteristic, UUBluetoothError.notConnectedError());
                    return;
                }

                if (enabled && notifyDelegate != null)
                {
                    registerCharacteristicChangedDelegate(characteristic, notifyDelegate);
                }
                else
                {
                    removeCharacteristicChangedDelegate(characteristic);
                }

                debugLog("toggleNotifyState", "Setting characteristic notify for " + characteristic.getUuid().toString());
                boolean success = bluetoothGatt.setCharacteristicNotification(characteristic, enabled);
                debugLog("toggleNotifyState", "setCharacteristicNotification returned " + success);

                if (!success)
                {
                    notifyCharacteristicNotifyStateChanged(characteristic, UUBluetoothError.operationFailedError("setCharacteristicNotification"));
                    return;
                }

                BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUBluetoothConstants.Descriptors.CLIENT_CHARACTERISTIC_CONFIGURATION_UUID);
                if (descriptor == null)
                {
                    notifyCharacteristicNotifyStateChanged(characteristic, UUBluetoothError.operationFailedError("getDescriptor"));
                    return;
                }

                byte[] data = enabled ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
                long timeoutLeft = timeout - (System.currentTimeMillis() - start);

                writeDescriptor(descriptor, data, timeoutLeft, new UUDescriptorDelegate()
                {
                    @Override
                    public void onComplete(@NonNull UUPeripheral peripheral, @NonNull BluetoothGattDescriptor descriptor, @Nullable UUBluetoothError error)
                    {
                        notifyCharacteristicNotifyStateChanged(characteristic, error);
                    }
                });
            }
        });
    }

    void writeCharacteristic(
            final @NonNull BluetoothGattCharacteristic characteristic,
            final @NonNull byte[] data,
            final long timeout,
            final @NonNull UUCharacteristicDelegate delegate)
    {
        writeCharacteristic(characteristic, data, timeout, BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT, delegate);
    }

    void writeCharacteristicWithoutResponse(
            final @NonNull BluetoothGattCharacteristic characteristic,
            final @NonNull byte[] data,
            final long timeout,
            final @NonNull UUCharacteristicDelegate delegate)
    {
        writeCharacteristic(characteristic, data, timeout, BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE, delegate);
    }

    private void writeCharacteristic(
            final @NonNull BluetoothGattCharacteristic characteristic,
            final @NonNull byte[] data,
            final long timeout,
            final int writeType,
            final @NonNull UUCharacteristicDelegate delegate)
    {
        final String timerId = writeCharacteristicWatchdogTimerId(characteristic);

        UUCharacteristicDelegate writeCharacteristicDelegate = new UUCharacteristicDelegate()
        {
            @Override
            public void onComplete(@NonNull UUPeripheral peripheral, @NonNull BluetoothGattCharacteristic characteristic, @Nullable UUBluetoothError error)
            {
                debugLog("writeCharacteristic", "Write characteristic complete: " + peripheral + ", error: " + error + ", data: " + UUString.byteToHex(characteristic.getValue()));
                removeWriteCharacteristicDelegate(characteristic);
                UUTimer.cancelActiveTimer(timerId);
                delegate.onComplete(peripheral, characteristic, error);
            }
        };

        registerWriteCharacteristicDelegate(characteristic, writeCharacteristicDelegate);

        UUTimer.startTimer(timerId, timeout, peripheral, new UUTimer.TimerDelegate()
        {
            @Override
            public void onTimer(@NonNull UUTimer timer, @Nullable Object userInfo)
            {
                debugLog("writeCharacteristic", "Write characteristic timeout: " + peripheral);

                disconnect(UUBluetoothError.timeoutError());
            }
        });

        UUThread.runOnMainThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (bluetoothGatt == null)
                {
                    debugLog("writeCharacteristic", "bluetoothGatt is null!");
                    notifyCharacteristicWritten(characteristic, UUBluetoothError.notConnectedError());
                    return;
                }

                debugLog("writeCharacteristic", "characteristic: " + characteristic.getUuid() + ", data: " + UUString.byteToHex(data));
                debugLog("writeCharacteristic", "props: " + UUBluetooth.characteristicPropertiesToString(characteristic.getProperties()) + ", (" + characteristic.getProperties() + ")");
                debugLog("writeCharacteristic", "permissions: " + UUBluetooth.characteristicPermissionsToString(characteristic.getPermissions()) + ", (" + characteristic.getPermissions() + ")");

                characteristic.setValue(data);
                characteristic.setWriteType(writeType);
                boolean success = bluetoothGatt.writeCharacteristic(characteristic);

                debugLog("writeCharacteristic", "writeCharacteristic returned " + success);

                if (!success)
                {
                    notifyCharacteristicWritten(characteristic, UUBluetoothError.operationFailedError("writeCharacteristic"));
                }
            }
        });
    }

    void readRssi(
        final long timeout,
        final @NonNull UUPeripheralErrorDelegate delegate)
    {
        final String timerId = readRssiWatchdogTimerId();

        readRssiDelegate = new UUPeripheralErrorDelegate()
        {
            @Override
            public void onComplete(@NonNull UUPeripheral peripheral, @Nullable UUBluetoothError error)
            {
                debugLog("readRssi", "Read RSSI complete: " + peripheral + ", error: " + error);
                UUTimer.cancelActiveTimer(timerId);
                delegate.onComplete(peripheral, error);
            }
        };

        UUTimer.startTimer(timerId, timeout, peripheral, new UUTimer.TimerDelegate()
        {
            @Override
            public void onTimer(@NonNull UUTimer timer, @Nullable Object userInfo)
            {
                debugLog("readRssi", "Read RSSI timeout: " + peripheral);
                notifyReadRssiComplete(UUBluetoothError.timeoutError());
            }
        });

        UUThread.runOnMainThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (bluetoothGatt == null)
                {
                    debugLog("readRssi", "bluetoothGatt is null!");
                    notifyReadRssiComplete(UUBluetoothError.notConnectedError());
                    return;
                }

                debugLog("readRssi", "Reading RSSI for: " + peripheral);
                boolean ok = bluetoothGatt.readRemoteRssi();
                debugLog("readRssi", "returnCode: " + ok);

                if (!ok)
                {
                    notifyReadRssiComplete(UUBluetoothError.operationFailedError("readRemoteRssi"));
                }
                // else
                //
                // wait for delegate or timeout
            }
        });
    }


    // Begins polling RSSI for a peripheral.  When the RSSI is successfully
    // retrieved, the peripheralFoundBlock is called.  This method is useful to
    // perform a crude 'ranging' logic when already connected to a peripheral
    void startRssiPolling(@NonNull final Context context, final long interval, @NonNull final UUPeripheralDelegate delegate)
    {
        pollRssiDelegate = delegate;

        final String timerId = pollRssiTimerId();
        UUTimer.cancelActiveTimer(timerId);

        readRssi(TIMEOUT_DISABLED, new UUPeripheralErrorDelegate()
        {
            @Override
            public void onComplete(@NonNull final UUPeripheral peripheral, @Nullable UUBluetoothError error)
            {
                debugLog("rssiPoll",
                    String.format(Locale.US, "RSSI (%d) Updated for %s-%s, error: %s",
                            peripheral.getRssi(), peripheral.getAddress(), peripheral.getName(), error));

                UUPeripheralDelegate pollDelegate = pollRssiDelegate;

                if (error == null)
                {
                    notifyPeripheralDelegate(pollDelegate);
                }
                else
                {
                    debugLog("startRssiPolling.onComplete", "Error while reading RSSI: " + error);
                }

                if (pollDelegate != null)
                {
                    UUTimer.startTimer(timerId, interval, peripheral, new UUTimer.TimerDelegate()
                    {
                        @Override
                        public void onTimer(@NonNull UUTimer timer, @Nullable Object userInfo)
                        {
                            debugLog("rssiPolling.timer", String.format(Locale.US, "RSSI Polling timer %s - %s", peripheral.getAddress(), peripheral.getName()));

                            UUPeripheralDelegate pollingDelegate = pollRssiDelegate;
                            if (pollingDelegate == null)
                            {
                                debugLog("rssiPolling.timer", String.format(Locale.US, "Peripheral %s-%s not polling anymore", peripheral.getAddress(), peripheral.getAddress()));
                            }
                            else if (peripheral.getConnectionState(context) == UUPeripheral.ConnectionState.Connected)
                            {
                                startRssiPolling(context, interval, delegate);
                            }
                            else
                            {
                                debugLog("rssiPolling.timer", String.format(Locale.US, "Peripheral %s-%s is not connected anymore, cannot poll for RSSI", peripheral.getAddress(), peripheral.getName()));
                            }
                        }
                    });
                }
            }
        });
    }

    void stopRssiPolling()
    {
        pollRssiDelegate = null;
        UUTimer.cancelActiveTimer(pollRssiTimerId());
    }

    boolean isPollingForRssi()
    {
        return (pollRssiDelegate != null);
    }

    private void notifyConnectDelegate(final @Nullable UUConnectionDelegate delegate)
    {
        try
        {
            if (delegate != null)
            {
                delegate.onConnected(peripheral);
            }
        }
        catch (Exception ex)
        {
            logException("notifyConnectDelegate", ex);
        }
    }

    private void notifyDisconnectDelegate(final @Nullable UUConnectionDelegate delegate, final @Nullable UUBluetoothError error)
    {
        try
        {
            if (delegate != null)
            {
                delegate.onDisconnected(peripheral, error);
            }
        }
        catch (Exception ex)
        {
            logException("notifyDisconnectDelegate", ex);
        }
    }

    private void notifyPeripheralErrorDelegate(final @Nullable UUPeripheralErrorDelegate delegate, final @Nullable UUBluetoothError error)
    {
        try
        {
            if (delegate != null)
            {
                delegate.onComplete(peripheral, error);
            }
        }
        catch (Exception ex)
        {
            logException("notifyPeripheralErrorDelegate", ex);
        }
    }

    private void notifyPeripheralDelegate(final @Nullable UUPeripheralDelegate delegate)
    {
        try
        {
            if (delegate != null)
            {
                delegate.onComplete(peripheral);
            }
        }
        catch (Exception ex)
        {
            logException("notifyPeripheralDelegate", ex);
        }
    }

    private void notifyCharacteristicDelegate(final @Nullable UUCharacteristicDelegate delegate, final @NonNull BluetoothGattCharacteristic characteristic, final @Nullable UUBluetoothError error)
    {
        try
        {
            if (delegate != null)
            {
                delegate.onComplete(peripheral, characteristic, error);
            }
        }
        catch (Exception ex)
        {
            logException("notifyCharacteristicDelegate", ex);
        }
    }

    private void notifyDescriptorDelegate(final @Nullable UUDescriptorDelegate delegate, final @NonNull BluetoothGattDescriptor descriptor, final @Nullable UUBluetoothError error)
    {
        try
        {
            if (delegate != null)
            {
                delegate.onComplete(peripheral, descriptor, error);
            }
        }
        catch (Exception ex)
        {
            logException("notifyDescriptorDelegate", ex);
        }
    }

    private void notifyConnected(@NonNull final String fromWhere)
    {
        try
        {
            debugLog("notifyConnected", "Notifying connected from: " + fromWhere);
            peripheral.setBluetoothGatt(bluetoothGatt);

            UUConnectionDelegate delegate = connectionDelegate;
            notifyConnectDelegate(delegate);
        }
        catch (Exception ex)
        {
            logException("notifyConnected", ex);
        }
    }

    BluetoothGatt getBluetoothGatt()
    {
        return bluetoothGatt;
    }

    private void notifyDisconnected(final @Nullable UUBluetoothError error)
    {
        closeGatt();
        peripheral.setBluetoothGatt(null);

        UUConnectionDelegate delegate = connectionDelegate;
        connectionDelegate = null;
        notifyDisconnectDelegate(delegate, error);
    }

    private void notifyServicesDiscovered(final @Nullable UUBluetoothError error)
    {
        UUPeripheralErrorDelegate delegate = serviceDiscoveryDelegate;
        serviceDiscoveryDelegate = null;
        notifyPeripheralErrorDelegate(delegate, error);
    }

    private void notifyDescriptorWritten(final @NonNull BluetoothGattDescriptor descriptor, final @Nullable UUBluetoothError error)
    {
        UUDescriptorDelegate delegate = getWriteDescriptorDelegate(descriptor);
        removeWriteDescriptorDelegate(descriptor);
        notifyDescriptorDelegate(delegate, descriptor, error);
    }

    private void notifyCharacteristicNotifyStateChanged(final @NonNull BluetoothGattCharacteristic characteristic, final @Nullable UUBluetoothError error)
    {
        UUCharacteristicDelegate delegate = getSetNotifyDelegate(characteristic);
        removeSetNotifyDelegate(characteristic);
        notifyCharacteristicDelegate(delegate, characteristic, error);
    }

    private void notifyCharacteristicWritten(final @NonNull BluetoothGattCharacteristic characteristic, final @Nullable UUBluetoothError error)
    {
        UUCharacteristicDelegate delegate = getWriteCharacteristicDelegate(characteristic);
        removeWriteCharacteristicDelegate(characteristic);
        notifyCharacteristicDelegate(delegate, characteristic, error);
    }

    private void notifyCharacteristicRead(final @NonNull BluetoothGattCharacteristic characteristic, final @Nullable UUBluetoothError error)
    {
        UUCharacteristicDelegate delegate = getReadCharacteristicDelegate(characteristic);
        removeReadCharacteristicDelegate(characteristic);
        notifyCharacteristicDelegate(delegate, characteristic, error);
    }

    private void notifyCharacteristicChanged(final @NonNull BluetoothGattCharacteristic characteristic)
    {
        UUCharacteristicDelegate delegate = getCharacteristicChangedDelegate(characteristic);
        notifyCharacteristicDelegate(delegate, characteristic, null);
    }

    private void notifyDescriptorRead(final @NonNull BluetoothGattDescriptor descriptor, final @Nullable UUBluetoothError error)
    {
        UUDescriptorDelegate delegate = getReadDescriptorDelegate(descriptor);
        removeReadDescriptorDelegate(descriptor);
        notifyDescriptorDelegate(delegate, descriptor, error);
    }

    private void notifyReadRssiComplete(final @Nullable UUBluetoothError error)
    {
        UUPeripheralErrorDelegate delegate = readRssiDelegate;
        readRssiDelegate = null;
        notifyPeripheralErrorDelegate(delegate, error);
    }

    private void notifyBoolResult(@Nullable final UUPeripheralBoolDelegate delegate, final boolean result)
    {
        try
        {
            if (delegate != null)
            {
                delegate.onComplete(peripheral, result);
            }
        }
        catch (Exception ex)
        {
            logException("notifyBoolResult", ex);
        }
    }

    private void registerCharacteristicChangedDelegate(final @NonNull BluetoothGattCharacteristic characteristic, final @NonNull UUCharacteristicDelegate delegate)
    {
        characteristicChangedDelegates.put(safeUuidString(characteristic), delegate);
    }

    private void removeCharacteristicChangedDelegate(final @NonNull BluetoothGattCharacteristic characteristic)
    {
        characteristicChangedDelegates.remove(safeUuidString(characteristic));
    }

    private @Nullable UUCharacteristicDelegate getCharacteristicChangedDelegate(final @NonNull BluetoothGattCharacteristic characteristic)
    {
        return characteristicChangedDelegates.get(safeUuidString(characteristic));
    }

    private void registerSetNotifyDelegate(final @NonNull BluetoothGattCharacteristic characteristic, final @NonNull UUCharacteristicDelegate delegate)
    {
        setNotifyDelegates.put(safeUuidString(characteristic), delegate);
    }

    private void removeSetNotifyDelegate(final @NonNull BluetoothGattCharacteristic characteristic)
    {
        setNotifyDelegates.remove(safeUuidString(characteristic));
    }

    private @Nullable UUCharacteristicDelegate getSetNotifyDelegate(final @NonNull BluetoothGattCharacteristic characteristic)
    {
        return setNotifyDelegates.get(safeUuidString(characteristic));
    }

    private void registerReadCharacteristicDelegate(final @NonNull BluetoothGattCharacteristic characteristic, final @NonNull UUCharacteristicDelegate delegate)
    {
        readCharacteristicDelegates.put(safeUuidString(characteristic), delegate);
    }

    private void removeReadCharacteristicDelegate(final @NonNull BluetoothGattCharacteristic characteristic)
    {
        readCharacteristicDelegates.remove(safeUuidString(characteristic));
    }

    private @Nullable UUCharacteristicDelegate getReadCharacteristicDelegate(final @NonNull BluetoothGattCharacteristic characteristic)
    {
        return readCharacteristicDelegates.get(safeUuidString(characteristic));
    }

    private void registerWriteCharacteristicDelegate(final @NonNull BluetoothGattCharacteristic characteristic, final @NonNull UUCharacteristicDelegate delegate)
    {
        writeCharacteristicDelegates.put(safeUuidString(characteristic), delegate);
    }

    private void removeWriteCharacteristicDelegate(final @NonNull BluetoothGattCharacteristic characteristic)
    {
        writeCharacteristicDelegates.remove(safeUuidString(characteristic));
    }

    private @Nullable UUCharacteristicDelegate getWriteCharacteristicDelegate(final @NonNull BluetoothGattCharacteristic characteristic)
    {
        return writeCharacteristicDelegates.get(safeUuidString(characteristic));
    }

    private void registerReadDescriptorDelegate(final @NonNull BluetoothGattDescriptor descriptor, final @NonNull UUDescriptorDelegate delegate)
    {
        readDescriptorDelegates.put(safeUuidString(descriptor), delegate);
    }

    private void removeReadDescriptorDelegate(final @NonNull BluetoothGattDescriptor descriptor)
    {
        readDescriptorDelegates.remove(safeUuidString(descriptor));
    }

    private @Nullable UUDescriptorDelegate getReadDescriptorDelegate(final @NonNull BluetoothGattDescriptor descriptor)
    {
        return readDescriptorDelegates.get(safeUuidString(descriptor));
    }

    private void registerWriteDescriptorDelegate(final @NonNull BluetoothGattDescriptor descriptor, final @NonNull UUDescriptorDelegate delegate)
    {
        writeDescriptorDelegates.put(safeUuidString(descriptor), delegate);
    }

    private void removeWriteDescriptorDelegate(final @NonNull BluetoothGattDescriptor descriptor)
    {
        writeDescriptorDelegates.remove(safeUuidString(descriptor));
    }

    private @Nullable UUDescriptorDelegate getWriteDescriptorDelegate(final @NonNull BluetoothGattDescriptor descriptor)
    {
        return writeDescriptorDelegates.get(safeUuidString(descriptor));
    }

    private @NonNull String safeUuidString(final @Nullable BluetoothGattCharacteristic characteristic)
    {
        if (characteristic != null)
        {
            return safeUuidString(characteristic.getUuid());
        }
        else
        {
            return "";
        }
    }

    private @NonNull String safeUuidString(final @Nullable BluetoothGattDescriptor descriptor)
    {
        if (descriptor != null)
        {
            return safeUuidString(descriptor.getUuid());
        }
        else
        {
            return "";
        }
    }

    private @NonNull String safeUuidString(final @Nullable UUID uuid)
    {
        String result = null;

        if (uuid != null)
        {
            result = uuid.toString();
        }

        if (result == null)
        {
            result = "";
        }

        result = result.toLowerCase();
        return result;
    }

    private void disconnectGattOnMainThread()
    {
        UUThread.runOnMainThread(new Runnable()
        {
            @Override
            public void run()
            {
                disconnectGatt();
            }
        });
    }

    private void disconnectGatt()
    {
        try
        {
            debugLog("disconnectGatt", "Disconnecting from: " + peripheral);

            if (bluetoothGatt != null)
            {
                bluetoothGatt.disconnect();
            }
            else
            {
                debugLog("disconnectGatt", "Bluetooth Gatt is null. Unable to disconnect");
            }
        }
        catch (Exception ex)
        {
            logException("disconnectGatt", ex);
        }
    }

    private void closeGatt()
    {
        try
        {
            if (bluetoothGatt != null)
            {
                bluetoothGatt.close();
            }
        }
        catch (Exception ex)
        {
            logException("closeGatt", ex);
        }
        finally
        {
            bluetoothGatt = null;
        }
    }

    private void reconnectGatt()
    {
        try
        {
            boolean success = bluetoothGatt.connect();
            debugLog("reconnectGatt", "connect() returned " + success);
        }
        catch (Exception ex)
        {
            logException("reconnectGatt", ex);
        }
    }

    private void debugLog(final String method, final String message)
    {
        if (LOGGING_ENABLED)
        {
            UULog.debug(getClass(), method, message);
        }
    }

    private static void logException(final String method, final Throwable exception)
    {
        if (LOGGING_ENABLED)
        {
            UULog.error(UUBluetoothGatt.class, method, exception);
        }
    }

    private @NonNull String statusLog(final int status)
    {
        return String.format(Locale.US, "%s (%d)", UUBluetooth.gattStatusToString(status), status);
    }

    private @NonNull String formatPeripheralTimerId(final @NonNull String bucket)
    {
        return String.format(Locale.US, "%s__%s", peripheral.getAddress(), bucket);
    }

    private @NonNull String formatCharacteristicTimerId(final @NonNull BluetoothGattCharacteristic characteristic, final @NonNull String bucket)
    {
        return String.format(Locale.US, "%s__ch_%s__%s", peripheral.getAddress(), safeUuidString(characteristic), bucket);
    }

    private @NonNull String formatDescriptorTimerId(final @NonNull BluetoothGattDescriptor descriptor, final @NonNull String bucket)
    {
        return String.format(Locale.US, "%s__de_%s__%s", peripheral.getAddress(), safeUuidString(descriptor), bucket);
    }

    private @NonNull String connectWatchdogTimerId()
    {
        return formatPeripheralTimerId(CONNECT_WATCHDOG_BUCKET);
    }

    private @NonNull String disconnectWatchdogTimerId()
    {
        return formatPeripheralTimerId(DISCONNECT_WATCHDOG_BUCKET);
    }

    private @NonNull String serviceDiscoveryWatchdogTimerId()
    {
        return formatPeripheralTimerId(SERVICE_DISCOVERY_WATCHDOG_BUCKET);
    }

    private @NonNull String setNotifyStateWatchdogTimerId(final @NonNull BluetoothGattCharacteristic characteristic)
    {
        return formatCharacteristicTimerId(characteristic, CHARACTERISTIC_NOTIFY_STATE_WATCHDOG_BUCKET);
    }

    private @NonNull String readCharacteristicWatchdogTimerId(final @NonNull BluetoothGattCharacteristic characteristic)
    {
        return formatCharacteristicTimerId(characteristic, READ_CHARACTERISTIC_WATCHDOG_BUCKET);
    }

    private @NonNull String readDescritporWatchdogTimerId(final @NonNull BluetoothGattDescriptor descriptor)
    {
        return formatDescriptorTimerId(descriptor, READ_DESCRIPTOR_WATCHDOG_BUCKET);
    }

    private @NonNull String writeCharacteristicWatchdogTimerId(final @NonNull BluetoothGattCharacteristic characteristic)
    {
        return formatCharacteristicTimerId(characteristic, WRITE_CHARACTERISTIC_WATCHDOG_BUCKET);
    }

    private @NonNull String writeDescriptorWatchdogTimerId(final @NonNull BluetoothGattDescriptor descriptor)
    {
        return formatDescriptorTimerId(descriptor, WRITE_DESCRIPTOR_WATCHDOG_BUCKET);
    }

    private @NonNull String readRssiWatchdogTimerId()
    {
        return formatPeripheralTimerId(READ_RSSI_WATCHDOG_BUCKET);
    }

    private @NonNull String pollRssiTimerId()
    {
        return formatPeripheralTimerId(POLL_RSSI_BUCKET);
    }

    public void cancelAllTimers()
    {
        try
        {
            if (peripheral != null)
            {
                ArrayList<UUTimer> list = UUTimer.listActiveTimers();

                String prefix = peripheral.getAddress();
                if (prefix != null)
                {
                    for (UUTimer t : list)
                    {
                        if (t.getTimerId().startsWith(prefix))
                        {
                            debugLog("cancelAllTimers", "Cancelling peripheral timer: " + t.getTimerId());
                            t.cancel();
                        }
                    }
                }
            }
        }
        catch (Exception ex)
        {
            logException("cancelAllTimers", ex);
        }
    }

    private class UUBluetoothGattCallback extends BluetoothGattCallback
    {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
        {
            debugLog("onConnectionStateChanged",
                    String.format(Locale.US, "status: %s, newState: %s (%d)",
                            statusLog(status), UUBluetooth.connectionStateToString(newState), newState));

            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothGatt.STATE_CONNECTED)
            {
                notifyConnected("onConnectionStateChange");
            }
            else if (newState == BluetoothGatt.STATE_DISCONNECTED)
            {
                UUBluetoothError err = disconnectError;

                if (err == null)
                {
                    err = UUBluetoothError.gattStatusError("onConnectionStateChanged", status);
                }

                if (err == null)
                {
                    err = UUBluetoothError.disconnectedError();
                }

                notifyDisconnected(err);
            }
            else if (status == UUBluetoothConstants.GATT_ERROR)
            {
                // Sometimes when attempting a connection, the operation fails with status 133 and state
                // other than connected.  Through trial and error, calling BluetoothGatt.connect() after
                // this happens will make the connection happen.
                reconnectGatt();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status)
        {
            debugLog("onServicesDiscovered",
                    String.format(Locale.US, "status: %s", statusLog(status)));

            notifyServicesDiscovered(UUBluetoothError.gattStatusError("onServicesDiscovered", status));
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
        {
            debugLog("onCharacteristicRead",
                    "characteristic: " + safeUuidString(characteristic) +
                            ", status: " + statusLog(status) +
                            ", char.data: " + UUString.byteToHex(characteristic.getValue()));

            notifyCharacteristicRead(characteristic, UUBluetoothError.gattStatusError("onCharacteristicRead", status));
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
        {
            debugLog("onCharacteristicWrite",
                    "characteristic: " + safeUuidString(characteristic) +
                            ", status: " + statusLog(status) +
                            ", char.data: " + UUString.byteToHex(characteristic.getValue()));

            notifyCharacteristicWritten(characteristic, UUBluetoothError.gattStatusError("onCharacteristicWrite", status));
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic)
        {
            debugLog("onCharacteristicChanged",
                    "characteristic: " + safeUuidString(characteristic) +
                            ", char.data: " + UUString.byteToHex(characteristic.getValue()));

            notifyCharacteristicChanged(characteristic);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status)
        {
            debugLog("onDescriptorRead",
                    "descriptor: " + safeUuidString(descriptor) +
                            ", status: " + statusLog(status) +
                            ", char.data: " + UUString.byteToHex(descriptor.getValue()));

            notifyDescriptorRead(descriptor, UUBluetoothError.gattStatusError("onDescriptorRead", status));
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status)
        {
            debugLog("onDescriptorWrite",
                    "descriptor: " + safeUuidString(descriptor) +
                            ", status: " + statusLog(status) +
                            ", char.data: " + UUString.byteToHex(descriptor.getValue()));

            notifyDescriptorWritten(descriptor, UUBluetoothError.gattStatusError("onDescriptorWrite", status));
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status)
        {
            debugLog("onReliableWriteCompleted", ", status: " + status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status)
        {
            debugLog("onReadRemoteRssi", "device: " + peripheral.getAddress() + ", rssi: " + rssi + ", status: " + status);

            if (status == BluetoothGatt.GATT_SUCCESS)
            {
                peripheral.updateRssi(rssi);
            }

            notifyReadRssiComplete(UUBluetoothError.gattStatusError("onReadRemoteRssi", status));
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Static Gatt management
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static final HashMap<String, UUBluetoothGatt> gattHashMap = new HashMap<>();

    @Nullable
    static UUBluetoothGatt gattForPeripheral(final @NonNull UUPeripheral peripheral)
    {
        Context ctx = UUBluetooth.requireApplicationContext();

        UUBluetoothGatt gatt = null;

        String address = peripheral.getAddress();
        if (UUString.isNotEmpty(address))
        {
            if (gattHashMap.containsKey(address))
            {
                gatt = gattHashMap.get(address);
            }

            if (gatt == null)
            {
                gatt = new UUBluetoothGatt(ctx, peripheral);
                gattHashMap.put(address, gatt);
            }
        }

        return gatt;
    }
}
