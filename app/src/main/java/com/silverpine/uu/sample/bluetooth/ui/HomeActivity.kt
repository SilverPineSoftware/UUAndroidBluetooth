package com.silverpine.uu.sample.bluetooth.ui

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.util.Pair
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.silverpine.uu.bluetooth.UUBluetoothScanner
import com.silverpine.uu.bluetooth.UUPeripheral
import com.silverpine.uu.bluetooth.UUPeripheralFilter
import com.silverpine.uu.core.UUPermissions
import com.silverpine.uu.core.UUThread
import com.silverpine.uu.sample.bluetooth.BR
import com.silverpine.uu.sample.bluetooth.R
import com.silverpine.uu.sample.bluetooth.operations.ReadDeviceInfoOperation
import com.silverpine.uu.sample.bluetooth.viewmodel.UUPeripheralViewModel
import com.silverpine.uu.ux.UUMenuHandler
import com.silverpine.uu.ux.UURecyclerActivity
import com.silverpine.uu.ux.uuOpenSystemSettings
import com.silverpine.uu.ux.uuPrompt

class HomeActivity: UURecyclerActivity()
{
    private val TAG = HomeActivity::javaClass.name

    private lateinit var scanner: UUBluetoothScanner<UUPeripheral>

    private var lastUpdate: Long = 0

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        scanner = UUBluetoothScanner(applicationContext, UUPeripheral::class.java)
    }

    override fun setupAdapter(recyclerView: RecyclerView)
    {
        adapter.registerClass(UUPeripheralViewModel::class.java, R.layout.peripheral_row, BR.vm)
    }

    override fun handleRowTapped(viewModel: ViewModel)
    {
        stopScanning()

        if (viewModel is UUPeripheralViewModel)
        {
            val peripheral = viewModel.model
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Choose an action for ${peripheral.name} - ${peripheral.address}")

            val actions = ArrayList<Pair<String, Runnable>>()
            actions.add(Pair("View Services", Runnable { gotoPeripheralServices(peripheral) }))
            actions.add(Pair("Read Info", Runnable { readDeviceInfo(peripheral) }))

            val items = arrayOfNulls<String>(actions.size)
            for (i in items.indices)
            {
                items[i] = actions[i].first
            }
            builder.setCancelable(true)

            builder.setItems(items) { dialog, which ->
                if (which >= 0 && which < actions.size) {
                    actions[which].second.run()
                }
            }

            builder.create().show()
        }
    }

    private fun gotoPeripheralServices(peripheral: UUPeripheral)
    {
        val intent = Intent(applicationContext, PeripheralDetailActivity::class.java)
        intent.putExtra("peripheral", peripheral)
        startActivity(intent)
    }

    private var readDeviceInfoOperation: ReadDeviceInfoOperation? = null
    private fun readDeviceInfo(peripheral: UUPeripheral)
    {
        val op = ReadDeviceInfoOperation(peripheral)
        readDeviceInfoOperation = op
        op.start()
        { err ->

            UUThread.runOnMainThread()
            {
                if (err != null)
                {
                    uuPrompt("Read Devie Info",
                        "Error: ${err.toString()}",
                        "OK",
                        null,
                        true,
                        { },
                        {})
                }
                else
                {
                    uuPrompt("Read Device Info",
                        "Name: ${op.deviceName}\nMfg: ${op.mfgName}",
                        "OK",
                        null,
                        true,
                        { },
                        { })
                }
            }
        }
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
            menuHandler.addAction(R.string.scan, this::startScanning)
        }
    }

    private fun startScanning()
    {
        Log.d(TAG, "startScanning")

        scanner.startScanning(null, arrayListOf(PeripheralFilter()))
        { list ->

            val timeSinceLastUpdate = System.currentTimeMillis() - this.lastUpdate
            if (timeSinceLastUpdate > 300)
            {
                UUThread.runOnMainThread()
                {
                    Log.d(TAG, "Updating devices, ${list.size} nearby")
                    val tmp = ArrayList<ViewModel>()
                    tmp.addAll(list.map { UUPeripheralViewModel(it, applicationContext) })
                    adapter.update(tmp)

                    lastUpdate = System.currentTimeMillis()
                }
            }
        }

        invalidateOptionsMenu()
    }

    private fun stopScanning()
    {
        Log.d(TAG, "stopScanning")

        scanner.stopScanning()
        invalidateOptionsMenu()
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
        if (!hasPermissions)
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
        }
    }
}
