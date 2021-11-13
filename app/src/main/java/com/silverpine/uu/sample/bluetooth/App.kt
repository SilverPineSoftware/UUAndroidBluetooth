package com.silverpine.uu.sample.bluetooth

import android.app.Application
import com.silverpine.uu.bluetooth.UUBluetooth

class App: Application()
{
    override fun onCreate()
    {
        super.onCreate()
        UUBluetooth.init(applicationContext)
    }
}