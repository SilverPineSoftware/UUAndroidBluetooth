package com.silverpine.uu.sample.bluetooth.ui

import android.bluetooth.BluetoothGattService
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.silverpine.uu.bluetooth.UUPeripheral
import com.silverpine.uu.core.UUThread
import com.silverpine.uu.sample.bluetooth.BR
import com.silverpine.uu.sample.bluetooth.R
import com.silverpine.uu.sample.bluetooth.viewmodel.CharacteristicViewModel
import com.silverpine.uu.sample.bluetooth.viewmodel.SectionHeaderViewModel
import com.silverpine.uu.sample.bluetooth.viewmodel.ServiceViewModel
import com.silverpine.uu.ux.UUMenuHandler
import com.silverpine.uu.ux.UURecyclerActivity
import com.silverpine.uu.ux.uuRequireParcelable
import com.silverpine.uu.ux.uuShowToast

class ServiceDetailActivity: UURecyclerActivity()
{
    private lateinit var peripheral: UUPeripheral
    private lateinit var service: BluetoothGattService

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        peripheral = intent.uuRequireParcelable("peripheral")
        service = intent.uuRequireService(peripheral,"serviceUuid")
        title = peripheral.name
    }

    override fun setupAdapter(recyclerView: RecyclerView)
    {
        adapter.registerClass(ServiceViewModel::class.java, R.layout.service_row, BR.vm)
        adapter.registerClass(SectionHeaderViewModel::class.java, R.layout.section_header, BR.vm)
        adapter.registerClass(CharacteristicViewModel::class.java, R.layout.characteristic_row, BR.vm)
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
        }
        else
        {
            menuHandler.add(R.string.connect, this::handleConnect)
        }
    }

    override fun handleRowTapped(viewModel: ViewModel)
    {

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

    /*
    private fun handleDiscoverServices()
    {
        peripheral.discoverServices(60000)
        { services, error ->
            uuShowToast("Found ${services?.size ?: 0} services")

            refreshUi()
        }
    }*/

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
            tmp.add(ServiceViewModel(service))
            tmp.add(SectionHeaderViewModel(R.string.characteristics))
            tmp.addAll(service.characteristics.map { CharacteristicViewModel(peripheral, it) })
            adapter.update(tmp)
        }
    }
}


