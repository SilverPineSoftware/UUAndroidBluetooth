package com.silverpine.uu.sample.bluetooth

import android.app.Application
import com.silverpine.uu.bluetooth.UUBluetooth

class BluetoothSampleApp: Application()
{
    override fun onCreate()
    {
        super.onCreate()
        UUBluetooth.init(applicationContext)
    }
}