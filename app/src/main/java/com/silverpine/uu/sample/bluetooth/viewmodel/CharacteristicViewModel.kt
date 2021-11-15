package com.silverpine.uu.sample.bluetooth.viewmodel

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.silverpine.uu.bluetooth.UUBluetooth
import com.silverpine.uu.sample.bluetooth.R
import com.silverpine.uu.sample.bluetooth.ui.Strings
import com.silverpine.uu.sample.bluetooth.ui.uuTypeAsString

class CharacteristicViewModel(val model: BluetoothGattCharacteristic): ViewModel()
{
    private val _uuid = MutableLiveData<String?>(null)
    private val _name = MutableLiveData<String?>(null)
    private val _properties = MutableLiveData<String?>(null)
    private val _isNotifying = MutableLiveData<String?>(null)

    val uuid: LiveData<String?> = _uuid
    val name: LiveData<String?> = _name
    val properties: LiveData<String?> = _properties
    val isNotifying: LiveData<String?> = _isNotifying

    init
    {
        _uuid.value = "${model.uuid}"
        _name.value = UUBluetooth.bluetoothSpecName(model.uuid)
        _properties.value = UUBluetooth.characteristicPropertiesToString(model.properties)

        if (UUBluetooth.isNotifying(model))
        {
            _isNotifying.value = Strings.load(R.string.yes)
        }
        else
        {
            _isNotifying.value = Strings.load(R.string.no)
        }

    }
}