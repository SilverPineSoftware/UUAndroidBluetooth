package com.silverpine.uu.sample.bluetooth.ui

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.silverpine.uu.bluetooth.UUPeripheral
import com.silverpine.uu.core.UUThread
import com.silverpine.uu.sample.bluetooth.BR
import com.silverpine.uu.sample.bluetooth.R
import com.silverpine.uu.sample.bluetooth.viewmodel.ServiceViewModel
import com.silverpine.uu.sample.bluetooth.viewmodel.UUPeripheralViewModel
import com.silverpine.uu.ux.UUMenuHandler
import com.silverpine.uu.ux.UUViewModelRecyclerAdapter
import com.silverpine.uu.ux.uuRequireParcelable
import com.silverpine.uu.ux.uuShowToast

class PeripheralDetailActivity: RecyclerActivity()
{
    private lateinit var adapter: UUViewModelRecyclerAdapter

    private lateinit var peripheral: UUPeripheral

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        peripheral = intent.uuRequireParcelable("peripheral")

        title = peripheral?.name
    }

    override fun setupAdapter(recyclerView: RecyclerView)
    {
        adapter = UUViewModelRecyclerAdapter(this::handleRowTapped)
        adapter.registerClass(UUPeripheralViewModel::class.java, R.layout.peripheral_row, BR.vm)
        adapter.registerClass(ServiceViewModel::class.java, R.layout.service_row, BR.vm)
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

    private fun handleRowTapped(viewModel: ViewModel)
    {

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
            { services ->

                UUThread.runOnMainThread()
                {
                    val tmp = ArrayList<ViewModel>()
                    tmp.add(UUPeripheralViewModel(peripheral, applicationContext))
                    tmp.addAll(services.map { ServiceViewModel(it, applicationContext) })
                    adapter.update(tmp)
                }
            }
        }

    }

    private fun handleDisconnect()
    {
        peripheral.disconnect(null)

    }
}


