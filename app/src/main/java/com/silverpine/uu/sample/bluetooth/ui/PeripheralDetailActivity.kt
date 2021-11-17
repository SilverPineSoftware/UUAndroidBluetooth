package com.silverpine.uu.sample.bluetooth.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.silverpine.uu.bluetooth.UUPeripheral
import com.silverpine.uu.core.UUThread
import com.silverpine.uu.sample.bluetooth.BR
import com.silverpine.uu.sample.bluetooth.R
import com.silverpine.uu.sample.bluetooth.viewmodel.SectionHeaderViewModel
import com.silverpine.uu.sample.bluetooth.viewmodel.ServiceViewModel
import com.silverpine.uu.sample.bluetooth.viewmodel.UUPeripheralViewModel
import com.silverpine.uu.ux.UUMenuHandler
import com.silverpine.uu.ux.uuRequireParcelable
import com.silverpine.uu.ux.uuShowToast

class PeripheralDetailActivity: RecyclerActivity()
{
    private lateinit var peripheral: UUPeripheral

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        peripheral = intent.uuRequireParcelable("peripheral")

        title = peripheral.name
    }

    override fun setupAdapter(recyclerView: RecyclerView)
    {
        adapter.registerClass(UUPeripheralViewModel::class.java, R.layout.peripheral_header, BR.vm)
        adapter.registerClass(ServiceViewModel::class.java, R.layout.service_row, BR.vm)
        adapter.registerClass(SectionHeaderViewModel::class.java, R.layout.section_header, BR.vm)
    }

    override fun handleRowTapped(viewModel: ViewModel)
    {
        if (viewModel is ServiceViewModel)
        {
            val intent = Intent(applicationContext, ServiceDetailActivity::class.java)
            intent.putExtra("peripheral", peripheral)
            intent.putExtra("service", viewModel.model)
            intent.putExtra("serviceUuid", viewModel.model.uuid.toString())
            startActivity(intent)
        }

    }

    override fun onResume()
    {
        super.onResume()
        refreshUi()
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
            refreshUi()

        },
        { disconnectError ->

            Log.d("LOG", "Peripheral disconnected")
            uuShowToast("Disconnected")
            refreshUi()
        })
    }

    private fun handleDiscoverServices()
    {
        peripheral.discoverServices(60000)
        { services, error ->
            uuShowToast("Found ${services?.size ?: 0} services")

            refreshUi()
        }
    }

    private fun handleDisconnect()
    {
        peripheral.disconnect(null)
    }

    private fun refreshUi()
    {
        UUThread.runOnMainThread()
        {
            val tmp = ArrayList<ViewModel>()
            tmp.add(SectionHeaderViewModel(R.string.info))
            tmp.add(UUPeripheralViewModel(peripheral, applicationContext))
            tmp.add(SectionHeaderViewModel(R.string.services))
            tmp.addAll(peripheral.discoveredServices().map { ServiceViewModel(it) })
            adapter.update(tmp)
        }
    }

}


