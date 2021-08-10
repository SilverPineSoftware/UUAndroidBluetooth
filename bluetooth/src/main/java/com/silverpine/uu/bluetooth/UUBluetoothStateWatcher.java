package com.silverpine.uu.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.silverpine.uu.logging.UULog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class UUBluetoothStateWatcher extends BroadcastReceiver
{
    private Context context;
    private Listener listener;

    public interface Listener
    {
        void onBluetoothStateChanged(@Nullable Integer state);
    }

    public UUBluetoothStateWatcher(@NonNull final Context appContext)
    {
        context = appContext.getApplicationContext();
        registerForEvent(BluetoothAdapter.ACTION_STATE_CHANGED);
    }

    public void setListener(@Nullable final Listener listener)
    {
        this.listener = listener;
    }

    private void registerForEvent(@NonNull final String event)
    {
        try
        {
            context.registerReceiver(this, new IntentFilter(event));
        }
        catch (Exception ex)
        {
            UULog.error(getClass(), "registerForEvent", ex);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (BluetoothAdapter.ACTION_STATE_CHANGED.equalsIgnoreCase(intent.getAction()))
        {
            int oldState = -1;
            int newState = -1;

            if (intent.hasExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE))
            {
                oldState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, -1);
            }

            if (intent.hasExtra(BluetoothAdapter.EXTRA_STATE))
            {
                newState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
            }

            UULog.debug(getClass(), "onReceive", "Bluetooth state changed from " + UUBluetooth.powerStateToString(oldState) + " to " + UUBluetooth.powerStateToString(newState));

            Integer state = null;

            if (newState != -1)
            {
                state = newState;
            }

            notifyListener(state);
        }
    }

    private void notifyListener(@Nullable final Integer state)
    {
        try
        {
            if (listener != null)
            {
                listener.onBluetoothStateChanged(state);
            }
        }
        catch (Exception ex)
        {
            UULog.error(getClass(), "notifyListener", ex);
        }
    }
}
