package com.silverpine.uu.sample.bluetooth.ui

import android.bluetooth.BluetoothGattService
import android.content.Context
import androidx.annotation.StringRes
import com.silverpine.uu.sample.bluetooth.R

class Strings
{
    companion object
    {
        lateinit var applicationContext: Context

        fun init(ctx: Context)
        {
            applicationContext = ctx
        }

        fun load(@StringRes resourceId: Int): String
        {
            return applicationContext.resources.getString(resourceId)
        }
    }
}

fun BluetoothGattService.uuTypeAsString(): String
{
    return when (type)
    {
        BluetoothGattService.SERVICE_TYPE_PRIMARY -> Strings.load(R.string.primary)
        BluetoothGattService.SERVICE_TYPE_SECONDARY -> Strings.load(R.string.secondary)
        else -> "$type"
    }
}