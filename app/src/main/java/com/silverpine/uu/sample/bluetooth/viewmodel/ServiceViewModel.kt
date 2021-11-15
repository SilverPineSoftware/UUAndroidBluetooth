package com.silverpine.uu.sample.bluetooth.viewmodel

import android.bluetooth.BluetoothGattService
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.silverpine.uu.bluetooth.UUBluetooth
import com.silverpine.uu.sample.bluetooth.ui.uuTypeAsString

class ServiceViewModel(val model: BluetoothGattService): ViewModel()
{
    private val _uuid = MutableLiveData<String?>(null)
    private val _name = MutableLiveData<String?>(null)
    private val _type = MutableLiveData<String?>(null)

    val uuid: LiveData<String?> = _uuid
    val name: LiveData<String?> = _name
    val type: LiveData<String?> = _type

    var onClick: ((BluetoothGattService)->Unit) = { }

    init
    {
        _uuid.value = "${model.uuid}"
        _name.value = UUBluetooth.bluetoothSpecName(model.uuid)
        _type.value = model.uuTypeAsString()
    }

    fun handleClick(view: View)
    {
        onClick.invoke(model)
    }

}