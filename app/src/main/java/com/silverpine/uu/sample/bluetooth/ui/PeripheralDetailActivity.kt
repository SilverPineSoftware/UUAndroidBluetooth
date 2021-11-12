package com.silverpine.uu.sample.bluetooth.ui

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.silverpine.uu.bluetooth.UUDiscoverServicesDelegate
import com.silverpine.uu.bluetooth.UUPeripheral
import com.silverpine.uu.core.UUObjectDelegate
import com.silverpine.uu.sample.bluetooth.R
import com.silverpine.uu.sample.bluetooth.adapter.ServiceRowAdapter
import com.silverpine.uu.ux.UUMenuHandler

class PeripheralDetailActivity : UUAppCompatActivity(layoutResourceId = R.layout.activity_peripheral_detail)
{
    private var adapter: ServiceRowAdapter? = null

    private var peripheral: UUPeripheral? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        adapter = ServiceRowAdapter(applicationContext)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        peripheral = intent.uuRequireParcelable("peripheral")

        title = peripheral?.name
    }

    override fun onResume()
    {
        super.onResume()

    }

    override fun populateMenu(menuHandler: UUMenuHandler)
    {
        if (peripheral!!.getConnectionState(applicationContext) == UUPeripheral.ConnectionState.Connected)
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
        peripheral!!.connect(60000, 10000, {

            Log.d("LOG", "Peripheral connected")
            uuShowToast("Connected")

        }, UUObjectDelegate
        { disconnectError ->

            uuShowToast("Disconnected")

            Log.d("LOG", "Peripheral disconnected")
        })
        /*
        UUBluetooth.connectPeripheral(applicationContext, peripheral!!, true, 10000, 10000, object: UUConnectionDelegate
        {
            override fun onConnected(peripheral: UUPeripheral)
            {
                Log.d("LOG", "Peripheral connected")
                updatePeripheral(peripheral)
            }

            override fun onDisconnected(peripheral: UUPeripheral, error: UUBluetoothError?)
            {
                Log.d("LOG", "Peripheral disconnected")
                updatePeripheral(peripheral)
            }
        })*/
    }

    private fun handleDiscoverServices()
    {
        peripheral!!.discoverServices(60000)
        { services, error ->
            uuShowToast("Found ${services?.size ?: 0} services")
        }

    }

    private fun handleDisconnect()
    {
        peripheral!!.disconnect(null)
        //UUBluetooth.disconnectPeripheral(peripheral!!)
        //invalidateOptionsMenu()

    }
}

fun <T: Parcelable> Intent.uuRequireParcelable(key: String): T
{
    val obj = extras?.getParcelable<T>(key)
    if (obj == null)
    {
        throw RuntimeException("Expected extra with key $key to be non-nil.")
    }
    else
    {
        return obj
    }
}
