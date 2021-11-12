package com.silverpine.uu.sample.bluetooth.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.silverpine.uu.bluetooth.UUBluetoothScanner
import com.silverpine.uu.bluetooth.UUPeripheral
import com.silverpine.uu.bluetooth.UUPeripheralFilter
import com.silverpine.uu.core.UUPermissions
import com.silverpine.uu.core.UUThread
import com.silverpine.uu.sample.bluetooth.R
import com.silverpine.uu.sample.bluetooth.adapter.PeripheralRowAdapter
import com.silverpine.uu.ux.UUMenuHandler
import com.silverpine.uu.ux.uuOpenSystemSettings
import com.silverpine.uu.ux.uuPrompt
import com.silverpine.uu.ux.uuSetAsActionAlways

class ScanActivity: RecyclerActivity()
{
    private lateinit var adapter: PeripheralRowAdapter
    private lateinit var scanner: UUBluetoothScanner

    private var lastUpdate: Long = 0

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        scanner = UUBluetoothScanner(applicationContext, null)
    }

    override fun setupAdapter(recyclerView: RecyclerView)
    {
        adapter = PeripheralRowAdapter(applicationContext, this::handlePeripheralClicked)
        recyclerView.adapter = adapter
    }

    override fun onResume()
    {
        super.onResume()

        refreshPermissions()
    }

    override fun populateMenu(menuHandler: UUMenuHandler)
    {
        if (scanner.isScanning)
        {
            menuHandler.addAction(R.string.stop, this::stopScanning)
        }
        else
        {
            menuHandler.addAction(R.string.start, this::startScanning)
        }
    }

    private fun startScanning()
    {
        scanner.startScanning<UUPeripheral>(null, arrayListOf(PeripheralFilter()))
        { list ->

            val timeSinceLastUpdate = System.currentTimeMillis() - this.lastUpdate
            if (timeSinceLastUpdate > 300)
            {
                UUThread.runOnMainThread {
                    adapter.update(list)
                }

                lastUpdate = System.currentTimeMillis()
            }
        }

        invalidateOptionsMenu()
    }

    private fun stopScanning()
    {
        scanner.stopScanning()
        invalidateOptionsMenu()
    }

    private fun handlePeripheralClicked(peripheral: UUPeripheral)
    {
        val intent = Intent(applicationContext, PeripheralDetailActivity::class.java)
        intent.putExtra("peripheral", peripheral)
        startActivity(intent)

    }






    companion object
    {
        const val LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
    }

    private val hasLocationPermission: Boolean
        get()
        {
            return UUPermissions.hasPermission(applicationContext, LOCATION_PERMISSION)
        }

    private val canRequestLocationPermission: Boolean
        get()
        {
            return UUPermissions.canRequestPermission(this, LOCATION_PERMISSION)
        }

    private fun refreshPermissions()
    {
        refreshPermissions(hasLocationPermission)
    }

    private fun refreshPermissions(hasPermissions: Boolean)
    {
        if (hasPermissions)
        {
            //homeViewModel?.scanForNearbyDevices(bleService)
        }
        else
        {
            val canRequest = canRequestLocationPermission
            var msgId = R.string.location_permission_denied_message
            var buttonId = R.string.app_settings

            if (canRequest)
            {
                msgId = R.string.location_permission_request_message
                buttonId = R.string.request_permission
            }

            uuPrompt(
                title = R.string.permissions,
                message = msgId,
                positiveButtonTextId = buttonId,
                cancelable = false,
                positiveAction =
                {
                    if (canRequest)
                    {
                        UUPermissions.requestPermissions(this, LOCATION_PERMISSION, 12276)
                        { _, granted ->

                            refreshPermissions(granted)
                        }
                    }
                    else
                    {
                        uuOpenSystemSettings()
                    }
                })
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    )
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        UUPermissions.handleRequestPermissionsResult(this, requestCode, permissions, grantResults)
    }



    inner class PeripheralFilter: UUPeripheralFilter
    {
        override fun shouldDiscoverPeripheral(peripheral: UUPeripheral): UUPeripheralFilter.Result
        {
            if (peripheral.name == null)
            {
                return UUPeripheralFilter.Result.IgnoreForever
            }

            return UUPeripheralFilter.Result.Discover;

        /*
            if (peripheral.name == null)
            {
                return UUPeripheralFilter.Result.IgnoreForever
            }
            else if (peripheral.name!!.startsWith("OTA_"))
            {
                return UUPeripheralFilter.Result.Discover
            }
            else
            {
                return UUPeripheralFilter.Result.IgnoreForever
            }*/
        }
    }
}
