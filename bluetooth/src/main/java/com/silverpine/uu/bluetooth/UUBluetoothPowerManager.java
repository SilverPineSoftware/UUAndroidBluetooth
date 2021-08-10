package com.silverpine.uu.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;

import com.silverpine.uu.logging.UULog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * UUBluetoothPowerManager provides a simple interface to toggle the bluetooth radio on and off.
 */
public class UUBluetoothPowerManager implements UUBluetoothStateWatcher.Listener
{
    private enum State
    {
        Idle,
        WaitingForPowerOn,
        WaitingForPowerOff,
    };

    public interface PowerOnDelegate
    {
        void onBluetoothPoweredOn(boolean success);
    }

    public interface PowerOffDelegate
    {
        void onBluetoothPoweredOff(boolean success);
    }

    public interface PowerCycleDelegate
    {
        void onBluetoothPoweredCycled(boolean success);
    }

    private Context appContext;
    private BluetoothAdapter adapter;
    private UUBluetoothStateWatcher stateWatcher;
    private State state;
    private PowerOnDelegate powerOnDelegate;
    private PowerOffDelegate powerOffDelegate;
    private PowerCycleDelegate powerCycleDelegate;

    public UUBluetoothPowerManager(@NonNull final Context context)
    {
        appContext = context.getApplicationContext();
        adapter = BluetoothAdapter.getDefaultAdapter();
        stateWatcher = new UUBluetoothStateWatcher(appContext);
        stateWatcher.setListener(this);
        state = State.Idle;
    }

    public boolean isBluetoothOn()
    {
        return checkBluetoothState(BluetoothAdapter.STATE_ON);
    }

    public boolean isBluetoothOff()
    {
        return checkBluetoothState(BluetoothAdapter.STATE_OFF);
    }

    private boolean checkBluetoothState(final int bluetoothState)
    {
        try
        {
            Integer state = UUBluetooth.getBluetoothState(appContext);
            return (state != null && state == bluetoothState);
        }
        catch (Exception ex)
        {
            UULog.debug(getClass(), "checkBluetoothState", ex);
            return false;
        }
    }

    public void turnBluetoothOn(@NonNull final PowerOnDelegate delegate)
    {
        try
        {
            powerOnDelegate = delegate;

            if (!isBluetoothOn())
            {
                boolean result = adapter.enable();
                UULog.debug(getClass(), "turnBluetoothOn", "adapter.enable() returned " + result);

                if (!result)
                {
                    notifyPowerOn(false);
                }
                else
                {
                    state = State.WaitingForPowerOn;
                }
            }
            else
            {
                UULog.debug(getClass(), "turnBluetoothOn", "Bluetooth is already on");
                notifyPowerOn(true);
            }
        }
        catch (Exception ex)
        {
            UULog.debug(getClass(), "turnBluetoothOn", ex);
            notifyPowerOn(false);
        }
    }

    public void powerCycleBluetooth(@NonNull final PowerCycleDelegate delegate)
    {
        powerCycleDelegate = delegate;

        turnBluetoothOff(new PowerOffDelegate()
        {
            @Override
            public void onBluetoothPoweredOff(boolean success)
            {
                if (success)
                {
                    turnBluetoothOn(new PowerOnDelegate()
                    {
                        @Override
                        public void onBluetoothPoweredOn(boolean success)
                        {
                            notifyPowerCycled(success);
                        }
                    });
                }
                else
                {
                    notifyPowerCycled(false);
                }
            }
        });
    }

    public void turnBluetoothOff(@NonNull final PowerOffDelegate delegate)
    {
        try
        {
            powerOffDelegate = delegate;

            if (!isBluetoothOff())
            {
                boolean result = adapter.disable();
                UULog.debug(getClass(), "turnBluetoothOff", "adapter.disable() returned " + result);

                if (!result)
                {
                    notifyPowerOff(false);
                }
                else
                {
                    state = State.WaitingForPowerOff;
                }
            }
            else
            {
                UULog.debug(getClass(), "turnBluetoothOff", "Bluetooth is already off");
                notifyPowerOff(true);
            }
        }
        catch (Exception ex)
        {
            UULog.debug(getClass(), "turnBluetoothOff", ex);
            notifyPowerOff(false);
        }
    }

    @Override
    public void onBluetoothStateChanged(@Nullable Integer bluetoothState)
    {
        if (bluetoothState == null)
        {
            // Shouldn't ever happen
            return;
        }

        if (BluetoothAdapter.STATE_OFF == bluetoothState && state == State.WaitingForPowerOff)
        {
            notifyPowerOff(true);
        }
        else if (BluetoothAdapter.STATE_ON == bluetoothState && state == State.WaitingForPowerOn)
        {
            notifyPowerOn(true);
        }
    }

    private void notifyPowerOff(final boolean success)
    {
        try
        {
            if (powerOffDelegate != null)
            {
                powerOffDelegate.onBluetoothPoweredOff(success);
            }
        }
        catch (Exception ex)
        {
            UULog.debug(getClass(), "notifyPowerOff", ex);
        }
    }

    private void notifyPowerOn(final boolean success)
    {
        try
        {
            if (powerOnDelegate != null)
            {
                powerOnDelegate.onBluetoothPoweredOn(success);
            }
        }
        catch (Exception ex)
        {
            UULog.debug(getClass(), "notifyPowerOn", ex);
        }
    }

    private void notifyPowerCycled(final boolean success)
    {
        try
        {
            if (powerCycleDelegate != null)
            {
                powerCycleDelegate.onBluetoothPoweredCycled(success);
            }
        }
        catch (Exception ex)
        {
            UULog.debug(getClass(), "notifyPowerCycled", ex);
        }
    }
}
