package com.silverpine.uu.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.ParcelUuid;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import com.silverpine.uu.core.UUError;
import com.silverpine.uu.core.UUString;
import com.silverpine.uu.core.UUThread;
import com.silverpine.uu.core.UUTimer;
import com.silverpine.uu.logging.UULog;

public class UUBluetoothSpp
{
    private static boolean LOGGING_ENABLED = UULog.LOGGING_ENABLED;

    private static final String PAIR_WATCHDOG_BUCKET = "UUBluetoothSession_PairWatchdogBucket";
    private static final String UNPAIR_WATCHDOG_BUCKET = "UUBluetoothSession_UnPairWatchdogBucket";
    private static final String SERVICE_DISCOVERY_WATCHDOG_BUCKET = "UUBluetoothSession_ServiceDiscoveryWatchdogBucket";
    private static final String CONNECT_SPP_WATCHDOG_BUCKET = "UUBluetoothSession_ConnectSppWatchdogBucket";
    private static final String DISCONNECT_SPP_WATCHDOG_BUCKET = "UUBluetoothSession_DisconnectSppWatchdogBucket";
    private static final String READ_SPP_DATA_WATCHDOG_BUCKET = "UUBluetoothSession_ReadSppDataWatchdogBucket";
    private static final String WRITE_SPP_DATA_WATCHDOG_BUCKET = "UUBluetoothSession_WriteSppDataWatchdogBucket";

    private Context context;

    private BluetoothDevice device;
    private String deviceAddress;
    private BroadcastReceiver broadcastReceiver;
    private BluetoothSocket bluetoothSocket;

    private UUBluetoothSppErrorDelegate pairDelegate;
    private UUBluetoothSppErrorDelegate unpairDelegate;
    private UUBluetoothSppErrorDelegate serviceDiscoveryDelegate;
    private UUBluetoothSppErrorDelegate connectSppDelegate;
    private UUBluetoothSppErrorDelegate disconnectSppDelegate;
    private UUBluetoothSppErrorDelegate writeSppDataDelegate;
    private UUBluetoothSppErrorDataDelegate readSppDataDelegate;

    public UUBluetoothSpp(Context context, BluetoothDevice device)
    {
        this.context = context;
        this.device = device;
        if (device != null)
        {
            this.deviceAddress = device.getAddress().toLowerCase();
        }

        initBroadcastReceiver();
        registerReceivers();
    }

    public BluetoothDevice getDevice()
    {
        return device;
    }


    private void registerReceivers()
    {
        registerReceiver(broadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
        registerReceiver(broadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_UUID));
    }

    /*private void unregisterReceivers()
    {
        try
        {
            if (context != null && broadcastReceiver != null)
            {
                debugLog("unregisterReceivers", "Unregistering broadcast receiver");
                context.unregisterReceiver(broadcastReceiver);
            }
        }
        catch (Exception ex)
        {
            logException("unregisterReceivers", ex);
        }
        finally
        {
            broadcastReceiver = null;
        }
    }*/

    private void registerReceiver(BroadcastReceiver receiver, IntentFilter intentFilter)
    {
        context.registerReceiver(receiver, intentFilter);
    }

    private void initBroadcastReceiver()
    {
        broadcastReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                debugLog("onReceive", intent);

                String action = intent.getAction();

                if (shouldHandleIntent(intent))
                {
                    if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equalsIgnoreCase(action))
                    {
                        handleBondStateChanged(intent);
                    }
                    else if (BluetoothDevice.ACTION_UUID.equalsIgnoreCase(action))
                    {
                        handleActionUuid(intent);
                    }
                }
            }
        };
    }

    private void handleBondStateChanged(final Intent intent)
    {
        int previousBondState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, -1);
        int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        debugLog("handleBondStateChanged", "Bond State for device " + device.getAddress() + " changed from " + UUBluetooth.bondStateToString(previousBondState) + " to " + UUBluetooth.bondStateToString(bondState));

        if ((previousBondState == BluetoothDevice.BOND_BONDING) &&
            (bondState == BluetoothDevice.BOND_BONDED || bondState == BluetoothDevice.BOND_NONE) &&
                pairDelegate != null)
        {
            UUError err = null;

            if (bondState == BluetoothDevice.BOND_NONE)
            {
                err = UUBluetoothError.operationFailedError("pairing");
            }

            notifyPairingComplete(err);
        }
        else if ((previousBondState == BluetoothDevice.BOND_BONDING || previousBondState == BluetoothDevice.BOND_BONDED) &&
                (bondState == BluetoothDevice.BOND_NONE) &&
                unpairDelegate != null)
        {
            notifyUnpairingComplete(null);
        }
    }

    private void handleActionUuid(final Intent intent)
    {
        Parcelable extraUuids[] = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);
        if (extraUuids != null)
        {

            boolean foundSpp = false;

            for (Parcelable p : extraUuids)
            {
                ParcelUuid parcelUuid = (ParcelUuid)p;
                UUID uuid = parcelUuid.getUuid();
                debugLog("handleActionUuid", "uuid: " + uuid.toString());

                if (uuid.compareTo(UUBluetoothConstants.Services.SERIAL_PORT_PROFILE_UUID) == 0)
                {
                    foundSpp = true;
                    debugLog("handleActionUuid", "Found SPP profile on device: " + deviceAddress);
                }
            }

            if (foundSpp && serviceDiscoveryDelegate != null)
            {
                notifyServiceDiscoveryComplete(null);
            }
        }
    }

    private boolean shouldHandleIntent(@Nullable final Intent intent)
    {
        try
        {
            if (intent == null)
            {
                return false;
            }

            String action = intent.getAction();
            if (UUString.isEmpty(action))
            {
                return false;
            }

            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (device == null)
            {
                return false;
            }

            String address = device.getAddress();
            if (UUString.isEmpty(address))
            {
                return false;
            }

            if (UUString.isEmpty(deviceAddress))
            {
                return false;
            }

            return address.equalsIgnoreCase(deviceAddress);
        }
        catch (Exception ex)
        {
            logException("shouldHandleIntent", ex);
            return false;
        }
    }

    public boolean isPaired()
    {
        return (device.getBondState() == BluetoothDevice.BOND_BONDED);
    }

    public void pair(final long timeout, @NonNull final UUBluetoothSppErrorDelegate delegate)
    {
        final String timerId = pairWatchdogTimerId();

        UUBluetoothSppErrorDelegate tmpDelegate = new UUBluetoothSppErrorDelegate()
        {
            @Override
            public void onComplete(@NonNull UUBluetoothSpp session, @Nullable UUError error)
            {
                debugLog("pair", "Pairing complete: " + device + ", error: " + error);
                UUTimer.cancelActiveTimer(timerId);
                delegate.onComplete(session, error);
            }
        };

        pairDelegate = tmpDelegate;

        UUTimer.startTimer(timerId, timeout, null, new UUTimer.TimerDelegate()
        {
            @Override
            public void onTimer(@NonNull UUTimer timer, @Nullable Object userInfo)
            {
                debugLog("pair", "Pair timeout: " + device);
                notifyPairingComplete(UUBluetoothError.timeoutError());
            }
        });

        UUThread.runOnBackgroundThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (device == null)
                {
                    debugLog("pair", "device is null!");
                    notifyPairingComplete(UUBluetoothError.operationFailedError("device is null"));
                    return;
                }

                int bondState = device.getBondState();
                if (bondState == BluetoothDevice.BOND_BONDED)
                {
                    debugLog("pair", "device is already bonded.");
                    notifyPairingComplete(null);
                    return;
                }

                debugLog("pair", "Attempting to pair with device " + deviceAddress);
                boolean success = device.createBond();
                debugLog("pair", "createBond returned: " + success);

                if (!success)
                {
                    notifyPairingComplete(UUBluetoothError.operationFailedError("createBond"));
                }
            }
        });
    }

    public void unpair(final long timeout, @NonNull final UUBluetoothSppErrorDelegate delegate)
    {
        final String timerId = unpairWatchdogTimerId();

        UUBluetoothSppErrorDelegate tmpDelegate = new UUBluetoothSppErrorDelegate()
        {
            @Override
            public void onComplete(@NonNull UUBluetoothSpp session, @Nullable UUError error)
            {
                debugLog("unpair", "Unpairing complete: " + device + ", error: " + error);
                UUTimer.cancelActiveTimer(timerId);
                delegate.onComplete(session, error);
            }
        };

        unpairDelegate = tmpDelegate;

        UUTimer.startTimer(timerId, timeout, null, new UUTimer.TimerDelegate()
        {
            @Override
            public void onTimer(@NonNull UUTimer timer, @Nullable Object userInfo)
            {
                debugLog("unpair", "Unpair timeout: " + device);
                notifyUnpairingComplete(UUBluetoothError.timeoutError());
            }
        });

        UUThread.runOnBackgroundThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (device == null)
                {
                    debugLog("unpair", "device is null!");
                    notifyUnpairingComplete(UUBluetoothError.operationFailedError("device is null"));
                    return;
                }

                int bondState = device.getBondState();
                if (bondState != BluetoothDevice.BOND_BONDED)
                {
                    debugLog("unpair", "device is not bonded.");
                    notifyUnpairingComplete(null);
                    return;
                }

                debugLog("unpair", "Attempting to unpair with device " + deviceAddress);
                boolean success = callRemoveBond(device);
                debugLog("unpair", "removeBond returned: " + success);

                if (!success)
                {
                    notifyUnpairingComplete(UUBluetoothError.operationFailedError("removeBond"));
                }
            }
        });
    }

    private boolean callRemoveBond(@NonNull final BluetoothDevice device)
    {
        try
        {
            Method m =  device.getClass().getMethod("removeBond", (Class[])null);
            if (m != null)
            {
                Boolean result = (Boolean) m.invoke(device, (Object[]) null);
                if (result != null)
                {
                    return result;
                }
            }
        }
        catch (Exception ex)
        {
            return false;
        }

        return false;
    }

    public boolean hasService(@NonNull final UUID uuid)
    {
        List<UUID> services = discoveredServiceUuids();
        return services.contains(uuid);
    }

    public boolean hasSppService()
    {
        return hasService(UUBluetoothConstants.Services.SERIAL_PORT_PROFILE_UUID);
    }

    public void discoverServices(final long timeout, @NonNull final UUBluetoothSppErrorDelegate delegate)
    {
        final String timerId = serviceDiscoveryWatchdogTimerId();

        UUBluetoothSppErrorDelegate tmpDelegate = new UUBluetoothSppErrorDelegate()
        {
            @Override
            public void onComplete(@NonNull UUBluetoothSpp session, @Nullable UUError error)
            {
                debugLog("discoverServices", "Service Discovery complete: " + device + ", error: " + error);
                UUTimer.cancelActiveTimer(timerId);
                delegate.onComplete(session, error);
            }
        };

        serviceDiscoveryDelegate = tmpDelegate;

        UUTimer.startTimer(timerId, timeout, null, new UUTimer.TimerDelegate()
        {
            @Override
            public void onTimer(@NonNull UUTimer timer, @Nullable Object userInfo)
            {
                debugLog("discoverServices", "Service Discovery timeout: " + device);
                // TODO: Is this really how to handle timeout on service discovery?
                notifyServiceDiscoveryComplete(UUBluetoothError.timeoutError());
            }
        });

        UUThread.runOnBackgroundThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (device == null)
                {
                    debugLog("discoverServices", "device is null!");
                    notifyServiceDiscoveryComplete(UUBluetoothError.preconditionFailedError( "device is null"));
                    return;
                }

                debugLog("discoverServices", "Calling fetchUuidsWithSdp");
                boolean success = device.fetchUuidsWithSdp();
                debugLog("discoverServices", "device.fetchUuidsWithSdp returned " + success);

                if (!success)
                {
                    notifyServiceDiscoveryComplete(UUBluetoothError.operationFailedError("fetchUuidsWithSdp"));
                }
            }
        });
    }

    @NonNull
    public List<UUID> discoveredServiceUuids()
    {
        ArrayList<UUID> list = new ArrayList<>();

        if (device != null)
        {
            ParcelUuid[] cached = device.getUuids();
            if (cached != null)
            {
                for (ParcelUuid parcelUuid : cached)
                {
                    list.add(parcelUuid.getUuid());
                }
            }
        }

        return list;
    }

    public boolean isSppConnected()
    {
        return (bluetoothSocket != null && bluetoothSocket.isConnected());
    }

    public void connectSpp(final long timeout, final boolean secureConnection, @NonNull final UUBluetoothSppErrorDelegate delegate)
    {
        final String timerId = connectSppWatchdogTimerId();

        UUBluetoothSppErrorDelegate tmpDelegate = new UUBluetoothSppErrorDelegate()
        {
            @Override
            public void onComplete(@NonNull UUBluetoothSpp session, @Nullable UUError error)
            {
                debugLog("connectSpp", "Connect SPP complete: " + device + ", error: " + error);
                UUTimer.cancelActiveTimer(timerId);
                delegate.onComplete(session, error);
            }
        };

        connectSppDelegate = tmpDelegate;

        UUTimer.startTimer(timerId, timeout, null, new UUTimer.TimerDelegate()
        {
            @Override
            public void onTimer(@NonNull UUTimer timer, @Nullable Object userInfo)
            {
                debugLog("connectSpp", "Connect SPP timeout: " + device);
                // TODO: Do we need to disconnect/cleanup on timeout?
                notifyConnectSppComplete(UUBluetoothError.timeoutError());
            }
        });

        UUThread.runOnBackgroundThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (device == null)
                {
                    debugLog("connectSpp", "device is null!");
                    notifyConnectSppComplete(UUBluetoothError.preconditionFailedError( "device is null"));
                    return;
                }

                try
                {
                    if (secureConnection)
                    {
                        debugLog("connectSpp", "Creating socket with createRfcommSocketToServiceRecord");
                        bluetoothSocket = device.createRfcommSocketToServiceRecord(UUBluetoothConstants.Services.SERIAL_PORT_PROFILE_UUID);
                        debugLog("connectSpp", "createRfcommSocketToServiceRecord returned: " + UUString.safeToString (bluetoothSocket));
                    }
                    else
                    {
                        debugLog("connectSpp", "Creating socket with createInsecureRfcommSocketToServiceRecord");
                        bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(UUBluetoothConstants.Services.SERIAL_PORT_PROFILE_UUID);
                        debugLog("connectSpp", "createInsecureRfcommSocketToServiceRecord returned: " + UUString.safeToString (bluetoothSocket));
                    }

                    if (bluetoothSocket == null)
                    {
                        debugLog("connectSpp", "bluetoothSocket is null!");
                        notifyWriteSppDataComplete(UUBluetoothError.preconditionFailedError("bluetoothSocket is null"));
                        return;
                    }

                    debugLog("connectSpp", "Opening Bluetooth Socket");
                    bluetoothSocket.connect();
                    debugLog("connectSpp", "Bluetooth Socket Open");

                    notifyConnectSppComplete(null);
                }
                catch (Exception ex)
                {
                    logException("connectSpp", ex);
                    notifyConnectSppComplete(UUBluetoothError.operationFailedError(ex));
                }
            }
        });
    }

    public void disconnectSpp(final long timeout, @NonNull final UUBluetoothSppErrorDelegate delegate)
    {
        final String timerId = disconnectSppWatchdogTimerId();

        UUBluetoothSppErrorDelegate tmpDelegate = new UUBluetoothSppErrorDelegate()
        {
            @Override
            public void onComplete(@NonNull UUBluetoothSpp session, @Nullable UUError error)
            {
                debugLog("disconnectSpp", "Disconnect SPP complete: " + device + ", error: " + error);
                UUTimer.cancelActiveTimer(timerId);
                delegate.onComplete(session, error);
            }
        };

        disconnectSppDelegate = tmpDelegate;

        UUTimer.startTimer(timerId, timeout, null, new UUTimer.TimerDelegate()
        {
            @Override
            public void onTimer(@NonNull UUTimer timer, @Nullable Object userInfo)
            {
                debugLog("disconnectSpp", "Connect SPP timeout: " + device);
                // TODO: Do we need to disconnect/cleanup on timeout?
                notifyDisconnectSppComplete(UUBluetoothError.timeoutError());
            }
        });

        UUThread.runOnBackgroundThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (device == null)
                {
                    debugLog("disconnectSpp", "device is null!");
                    return;
                }

                closeSocket();
                notifyDisconnectSppComplete(null);
            }
        });
    }


    public void writeSppData(@NonNull final byte[] data, final long timeout, @NonNull final UUBluetoothSppErrorDelegate delegate)
    {
        final String timerId = writeSppDataWatchdogTimerId();

        UUBluetoothSppErrorDelegate tmpDelegate = new UUBluetoothSppErrorDelegate()
        {
            @Override
            public void onComplete(@NonNull UUBluetoothSpp session, @Nullable UUError error)
            {
                debugLog("writeSppData", "Write SPP Data complete: " + device + ", error: " + error);
                UUTimer.cancelActiveTimer(timerId);
                delegate.onComplete(session, error);
            }
        };

        writeSppDataDelegate = tmpDelegate;

        UUTimer.startTimer(timerId, timeout, null, new UUTimer.TimerDelegate()
        {
            @Override
            public void onTimer(@NonNull UUTimer timer, @Nullable Object userInfo)
            {
                debugLog("writeSppData", "Write SPP timeout: " + device);
                // TODO: Do we need to disconnect/cleanup on timeout?
                notifyWriteSppDataComplete(UUBluetoothError.timeoutError());
            }
        });

        UUThread.runOnBackgroundThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (device == null)
                {
                    debugLog("writeSppData", "device is null!");
                    notifyWriteSppDataComplete(UUBluetoothError.preconditionFailedError( "device is null"));
                    return;
                }

                if (bluetoothSocket == null)
                {
                    debugLog("writeSppData", "bluetoothSocket is null!");
                    notifyWriteSppDataComplete(UUBluetoothError.preconditionFailedError("bluetoothSocket is null"));
                    return;
                }

                if (!bluetoothSocket.isConnected())
                {
                    debugLog("writeSppData", "bluetoothSocket is not connected!");
                    notifyWriteSppDataComplete(UUBluetoothError.preconditionFailedError("bluetoothSocket is not connected"));
                    return;
                }

                try
                {
                    debugLog("writeSppData", "TX: " + UUString.byteToHex(data));
                    OutputStream os = bluetoothSocket.getOutputStream();

                    debugLog("writeSppData", "Attempting to write " + data.length + " bytes.");
                    os.write(data, 0, data.length);

                    debugLog("writeSppData", "Flushing bytes");
                    os.flush();
                }
                catch (Exception ex)
                {
                    logException("writeSppData", ex);
                    notifyWriteSppDataComplete(UUBluetoothError.operationFailedError(ex));
                    return;
                }

                notifyWriteSppDataComplete(null);
            }
        });
    }

    public interface RxPeekDelegate
    {
        boolean shouldKeepReading(@NonNull final byte[] bytesReceived);
    }

    public void readSppData(final int count, final long timeout, @Nullable final RxPeekDelegate rxPeekDelegate, @NonNull final UUBluetoothSppErrorDataDelegate delegate)
    {
        final String timerId = readSppDataWatchdogTimerId();

        readSppDataDelegate = (session, data, error) ->
        {
            debugLog("readSppData", "Read SPP Data complete: " + device + ", error: " + error);
            UUTimer.cancelActiveTimer(timerId);
            delegate.onComplete(session, data, error);
        };

        UUTimer.startTimer(timerId, timeout, null, (timer, userInfo) ->
        {
            debugLog("readSppData", "Read SPP timeout: " + device);
            // TODO: Do we need to disconnect/cleanup on timeout?
            notifyReadSppDataComplete(UUBluetoothError.timeoutError(), null);
        });

        UUThread.runOnBackgroundThread(() ->
        {
            if (device == null)
            {
                debugLog("readSppData", "device is null!");
                notifyReadSppDataComplete(UUBluetoothError.preconditionFailedError( "device is null"), null);
                return;
            }

            if (bluetoothSocket == null)
            {
                debugLog("readSppData", "bluetoothSocket is null!");
                notifyReadSppDataComplete(UUBluetoothError.preconditionFailedError("bluetoothSocket is null"), null);
                return;
            }

            if (!bluetoothSocket.isConnected())
            {
                debugLog("readSppData", "bluetoothSocket is not connected!");
                notifyReadSppDataComplete(UUBluetoothError.preconditionFailedError("bluetoothSocket is not connected"), null);
                return;
            }

            byte[] response = null;
            ByteArrayOutputStream bos = null;

            try
            {
                InputStream is = bluetoothSocket.getInputStream();
                bos = new ByteArrayOutputStream();

                int bytesRead = 0;
                while (bytesRead < count)
                {
                    int bytesAvailable = is.available();
                    debugLog("readSppData", "There are " + bytesAvailable + " bytes available to read");
                    if (bytesAvailable <= 0 && bytesRead > 0 && count == Integer.MAX_VALUE)
                    {
                        debugLog("readSppData", "There are no bytes available to read, bailing out of read loop.");
                        break;
                    }

                    byte[] rxChunk = new byte[1024];
                    debugLog("readSppData", "Attempting to read " + rxChunk.length + " bytes.");
                    int read = is.read(rxChunk, 0, rxChunk.length);
                    debugLog("readSppData", "Read " + read + " bytes.");

                    if (read > 0)
                    {
                        debugLog("readSppData", "RXChunk: " + UUString.byteToHex(rxChunk, 0, read));
                        bos.write(rxChunk, 0, read);

                        bytesRead += read;

                        boolean keepReading = rxPeek(bos, rxPeekDelegate);
                        if (!keepReading)
                        {
                            debugLog("readSppData", "rxPeek returned false, bailing out of receive loop");
                            break;
                        }

                    }
                    else
                    {
                        debugLog("readSppData", "inputStream.read returned " + read + ", totalRead: " + bytesRead + ", expectedToRead: " + count);
                    }
                }

                response = bos.toByteArray();
            }
            catch (Exception ex)
            {
                logException("readSppData", ex);
                notifyReadSppDataComplete(UUBluetoothError.operationFailedError(ex), null);
                return;
            }
            finally
            {
                closeObject(bos);
            }

            debugLog("readSppData", "RX: " + UUString.byteToHex(response));

            notifyReadSppDataComplete(null, response);
        });
    }

    private boolean rxPeek(@NonNull final ByteArrayOutputStream bos, @Nullable final RxPeekDelegate delegate)
    {
        boolean keepReading = true;

        try
        {
            if (delegate != null)
            {
                keepReading = delegate.shouldKeepReading(bos.toByteArray());
            }
        }
        catch (Exception ex)
        {
            logException("rxPeek", ex);
            keepReading = true;
        }

        return keepReading;
    }

    private void debugLog(final String method, final String message)
    {
        if (LOGGING_ENABLED)
        {
            UULog.debug(getClass(), method, message);
        }
    }

    private void debugLog(final String method, final Intent intent)
    {
        if (LOGGING_ENABLED)
        {
            UULog.logIntent(getClass(), method, "", intent);
        }
    }

    private void logException(final String method, final Throwable exception)
    {
        if (LOGGING_ENABLED)
        {
            UULog.error(getClass(), method, exception);
        }
    }

    private void closeSocket()
    {
        try
        {
            if (bluetoothSocket != null)
            {
                bluetoothSocket.close();
            }
        }
        catch (Exception ex)
        {
            logException("closeSocket", ex);
        }
        finally
        {
            bluetoothSocket = null;
        }
    }

    private void closeObject(@Nullable final Closeable closeable)
    {
        try
        {
            if (closeable != null)
            {
                closeable.close();
            }
        }
        catch (Exception ex)
        {
            logException("closeStream", ex);
        }
    }

    private void notifyErrorCallback(final UUError error, final UUBluetoothSppErrorDelegate callback)
    {
        try
        {
            if (callback != null)
            {
                callback.onComplete(this, error);
            }
        }
        catch (Exception ex)
        {
            logException("notifyErrorCallback", ex);
        }
    }

    private void notifyErrorDataCallback(final UUError error, final byte[] data, final UUBluetoothSppErrorDataDelegate callback)
    {
        try
        {
            if (callback != null)
            {
                callback.onComplete(this, data, error);
            }
        }
        catch (Exception ex)
        {
            logException("notifyErrorDataCallback", ex);
        }
    }

    private void notifyPairingComplete(final UUError error)
    {
        UUBluetoothSppErrorDelegate delegate = pairDelegate;
        pairDelegate = null;
        notifyErrorCallback(error, delegate);
    }

    private void notifyServiceDiscoveryComplete(final UUError error)
    {
        UUBluetoothSppErrorDelegate delegate = serviceDiscoveryDelegate;
        serviceDiscoveryDelegate = null;
        notifyErrorCallback(error, delegate);
    }

    private void notifyUnpairingComplete(final UUError error)
    {
        UUBluetoothSppErrorDelegate delegate = unpairDelegate;
        unpairDelegate = null;
        notifyErrorCallback(error, delegate);
    }

    private void notifyConnectSppComplete(final UUError error)
    {
        UUBluetoothSppErrorDelegate delegate = connectSppDelegate;
        connectSppDelegate = null;
        notifyErrorCallback(error, delegate);
    }

    private void notifyDisconnectSppComplete(final UUError error)
    {
        UUBluetoothSppErrorDelegate delegate = disconnectSppDelegate;
        disconnectSppDelegate = null;
        notifyErrorCallback(error, delegate);
    }

    private void notifyWriteSppDataComplete(final UUError error)
    {
        UUBluetoothSppErrorDelegate delegate = writeSppDataDelegate;
        writeSppDataDelegate = null;
        notifyErrorCallback(error, delegate);
    }

    private void notifyReadSppDataComplete(final UUError error, final byte[] response)
    {
        UUBluetoothSppErrorDataDelegate delegate = readSppDataDelegate;
        readSppDataDelegate = null;
        notifyErrorDataCallback(error, response, delegate);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Timers
    ////////////////////////////////////////////////////////////////////////////////////////////////


    private @NonNull String timerPrefix()
    {
        return String.format(Locale.US, "Classic__%s", device.getAddress());
    }

    private @NonNull String formatDeviceTimerId(final @NonNull String bucket)
    {
        return String.format(Locale.US, "%s__%s", timerPrefix(), bucket);
    }

    private @NonNull String pairWatchdogTimerId()
    {
        return formatDeviceTimerId(PAIR_WATCHDOG_BUCKET);
    }

    private @NonNull String unpairWatchdogTimerId()
    {
        return formatDeviceTimerId(UNPAIR_WATCHDOG_BUCKET);
    }

    private @NonNull String serviceDiscoveryWatchdogTimerId()
    {
        return formatDeviceTimerId(SERVICE_DISCOVERY_WATCHDOG_BUCKET);
    }

    private @NonNull String connectSppWatchdogTimerId()
    {
        return formatDeviceTimerId(CONNECT_SPP_WATCHDOG_BUCKET);
    }

    private @NonNull String disconnectSppWatchdogTimerId()
    {
        return formatDeviceTimerId(DISCONNECT_SPP_WATCHDOG_BUCKET);
    }

    private @NonNull String readSppDataWatchdogTimerId()
    {
        return formatDeviceTimerId(READ_SPP_DATA_WATCHDOG_BUCKET);
    }

    private @NonNull String writeSppDataWatchdogTimerId()
    {
        return formatDeviceTimerId(WRITE_SPP_DATA_WATCHDOG_BUCKET);
    }

    public void cancelAllTimers()
    {
        try
        {
            if (device != null)
            {
                ArrayList<UUTimer> list = UUTimer.listActiveTimers();

                String prefix = timerPrefix();
                for (UUTimer t : list)
                {
                    if (t.getTimerId().startsWith(prefix))
                    {
                        debugLog("cancelAllTimers", "Cancelling device timer: " + t.getTimerId());
                        t.cancel();
                    }
                }
            }
        }
        catch (Exception ex)
        {
            logException("cancelAllTimers", ex);
        }
    }
}