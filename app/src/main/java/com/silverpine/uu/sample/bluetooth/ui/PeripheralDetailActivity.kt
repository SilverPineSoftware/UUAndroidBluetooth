package com.silverpine.uu.sample.bluetooth.ui

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.silverpine.uu.bluetooth.UUBluetoothScanner
import com.silverpine.uu.bluetooth.UUPeripheral
import com.silverpine.uu.bluetooth.UUPeripheralFilter
import com.silverpine.uu.core.UUPermissions
import com.silverpine.uu.core.UUThread
import com.silverpine.uu.sample.bluetooth.R
import com.silverpine.uu.sample.bluetooth.adapter.PeripheralRowAdapter
import com.silverpine.uu.sample.bluetooth.adapter.ServiceRowAdapter
import com.silverpine.uu.ux.uuOpenSystemSettings
import com.silverpine.uu.ux.uuPrompt

class PeripheralDetailActivity : AppCompatActivity()
{
    private var adapter: ServiceRowAdapter? = null
    private var peripheral: UUPeripheral? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_peripheral_detail)

        adapter = ServiceRowAdapter(applicationContext)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        peripheral = intent.extras?.getParcelable("peripheral")

        title = "Peripheral Detail"

        peripheral?.let {
            title = it.name
        } ?: run {
            title = "Unknown"
        }


        //title = peripheral?.name ?: (peripheral?.address ?: "Unknown" )

    }

    override fun onResume()
    {
        super.onResume()

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
            //startScanning()
            return true
        }

        if (item.itemId == R.id.action_stop_scan)
        {
            //stopScanning()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
