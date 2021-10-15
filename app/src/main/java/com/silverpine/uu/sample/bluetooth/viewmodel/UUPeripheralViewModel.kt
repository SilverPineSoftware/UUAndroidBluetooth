package com.silverpine.uu.sample.bluetooth.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.silverpine.uu.bluetooth.UUPeripheral

class UUPeripheralViewModel(model: UUPeripheral, context: Context): ViewModel()
{
    private val _friendlyName = MutableLiveData<String?>(null)
    private val _macAddress = MutableLiveData<String?>(null)
    private val _connectionState = MutableLiveData<String?>(null)
    private val _rssi = MutableLiveData<String?>(null)
    private val _timeSinceLastUpdate = MutableLiveData<String?>(null)

    val friendlyName: LiveData<String?> = _friendlyName
    val macAddress: LiveData<String?> = _macAddress
    val connectionState: LiveData<String?> = _connectionState
    val rssi: LiveData<String?> = _rssi
    val timeSinceLastUpdate: LiveData<String?> = _timeSinceLastUpdate

    init
    {
        _friendlyName.value = "${model.name}"
        _macAddress.value = "${model.address}"
        _connectionState.value = "${model.getConnectionState(context)}"
        _rssi.value = "${model.rssi}"
        _timeSinceLastUpdate.value = "${model.timeSinceLastUpdate}"
    }
}