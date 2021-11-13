package com.silverpine.uu.sample.bluetooth.ui

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.silverpine.uu.bluetooth.UUPeripheral
import com.silverpine.uu.core.UUThread
import com.silverpine.uu.sample.bluetooth.R
import com.silverpine.uu.sample.bluetooth.adapter.ServiceRowAdapter
import com.silverpine.uu.ux.UUMenuHandler
import com.silverpine.uu.ux.uuRequireParcelable
import com.silverpine.uu.ux.uuShowToast

class PeripheralDetailActivity: RecyclerActivity()
{
    private var adapter: ServiceRowAdapter? = null

    private lateinit var peripheral: UUPeripheral

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        peripheral = intent.uuRequireParcelable("peripheral")

        title = peripheral?.name
    }

    override fun setupAdapter(recyclerView: RecyclerView)
    {
        adapter = ServiceRowAdapter(applicationContext)
        recyclerView.adapter = adapter
    }

    override fun onResume()
    {
        super.onResume()

    }

    override fun populateMenu(menuHandler: UUMenuHandler)
    {
        if (peripheral.getConnectionState(applicationContext) == UUPeripheral.ConnectionState.Connected)
        {
            menuHandler.add(R.string.disconnect, this::handleDisconnect)
            menuHandler.add(R.string.discover_services, this::handleDiscoverServices)
        }
        else
        {
            menuHandler.add(R.string.connect, this::handleConnect)
        }
    }

    private fun handleConnect()
    {
        peripheral.connect(60000, 10000, {

            Log.d("LOG", "Peripheral connected")
            uuShowToast("Connected")

        },
        { disconnectError ->

            uuShowToast("Disconnected")

            Log.d("LOG", "Peripheral disconnected")
        })
    }

    private fun handleDiscoverServices()
    {
        peripheral.discoverServices(60000)
        { services, error ->
            uuShowToast("Found ${services?.size ?: 0} services")

            services?.let()
            {
                UUThread.runOnMainThread()
                {
                    adapter?.update(it)
                }
            }
        }

    }

    private fun handleDisconnect()
    {
        peripheral.disconnect(null)

    }
}


