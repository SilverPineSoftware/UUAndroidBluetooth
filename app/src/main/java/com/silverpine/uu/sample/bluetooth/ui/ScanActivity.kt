package com.silverpine.uu.sample.bluetooth.ui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.silverpine.uu.bluetooth.UUBluetoothScanner
import com.silverpine.uu.bluetooth.UUPeripheral
import com.silverpine.uu.bluetooth.UUPeripheralFilter
import com.silverpine.uu.core.UUPermissions
import com.silverpine.uu.core.UUString
import com.silverpine.uu.core.UUThread
import com.silverpine.uu.sample.bluetooth.R
import com.silverpine.uu.sample.bluetooth.adapter.PeripheralRowAdapter

class ScanActivity : AppCompatActivity()
{
    private var adapter: PeripheralRowAdapter? = null
    private var scanner: UUBluetoothScanner? = null

    private var lastUpdate: Long = 0

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        adapter = PeripheralRowAdapter(applicationContext)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        scanner = UUBluetoothScanner(applicationContext, null)
    }

    override fun onResume()
    {
        super.onResume()

        refreshPermissions()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        menuInflater.inflate(R.menu.scan_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        if (item.itemId == R.id.action_start_scan)
        {
            startScanning()
            return true
        }

        if (item.itemId == R.id.action_stop_scan)
        {
            stopScanning()
            return true
        }

        return super.onOptionsItemSelected(item)
    }



    private fun startScanning()
    {
        scanner?.startScanning<UUPeripheral>(null, arrayListOf(PeripheralFilter()))
        { list ->

            val timeSinceLastUpdate = System.currentTimeMillis() - this.lastUpdate
            if (timeSinceLastUpdate > 300)
            {
                UUThread.runOnMainThread {
                    adapter?.update(list)
                }

                lastUpdate = System.currentTimeMillis()
            }


        }
    }

    private fun stopScanning()
    {
        scanner?.stopScanning()
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
            if (UUString.isEmpty(peripheral.name))
            {
                return UUPeripheralFilter.Result.IgnoreOnce
            }

            return UUPeripheralFilter.Result.Discover
        }
    }
}



// TODO: Move this into a core UI/UI package
fun Context.uuPrompt(
    @StringRes title: Int = -1,
    @StringRes message: Int,
    @StringRes positiveButtonTextId: Int,
    @StringRes negativeButtonTextId: Int = -1,
    cancelable: Boolean = true,
    positiveAction: () -> Unit = { },
    negativeAction: () -> Unit = { })
{
    val builder = AlertDialog.Builder(this)
    builder.setCancelable(cancelable)

    if (title != -1)
    {
        builder.setTitle(title)
    }

    if (message != -1)
    {
        builder.setMessage(message)
    }

    if (positiveButtonTextId != -1)
    {
        builder.setPositiveButton(positiveButtonTextId)
        { dialog, _ ->
            dialog.cancel()
            positiveAction()
        }
    }

    if (negativeButtonTextId != -1)
    {
        builder.setNegativeButton(negativeButtonTextId)
        { dialog, _ ->
            dialog.cancel()
            negativeAction()
        }
    }

    val dialog = builder.create()
    dialog.show()
}

// TODO: Move this into a core UI/UI package
fun Activity.uuOpenSystemSettings()
{
    val intent = Intent()
    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    val uri = Uri.fromParts("package", packageName, null)
    intent.data = uri
    startActivity(intent)
}