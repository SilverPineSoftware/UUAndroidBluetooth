package com.silverpine.uu.sample.bluetooth.viewmodel

import android.bluetooth.BluetoothGattCharacteristic
import android.util.Log
import android.view.View
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.silverpine.uu.bluetooth.UUBluetooth
import com.silverpine.uu.bluetooth.UUBluetoothError
import com.silverpine.uu.bluetooth.UUCharacteristicDelegate
import com.silverpine.uu.bluetooth.UUPeripheral
import com.silverpine.uu.core.UUString
import com.silverpine.uu.core.UUThread
import com.silverpine.uu.logging.UULog
import com.silverpine.uu.sample.bluetooth.R
import com.silverpine.uu.sample.bluetooth.ui.Strings

class CharacteristicViewModel(private val peripheral: UUPeripheral, val model: BluetoothGattCharacteristic): ViewModel()
{
    private val _uuid = MutableLiveData<String?>(null)
    private val _name = MutableLiveData<String?>(null)
    private val _properties = MutableLiveData<String?>(null)
    private val _isNotifying = MutableLiveData<String?>(null)
    private val _dataEditable = MutableLiveData(false)
    private val _hexSelected = MutableLiveData(true)

    val uuid: LiveData<String?> = _uuid
    val name: LiveData<String?> = _name
    val properties: LiveData<String?> = _properties
    val isNotifying: LiveData<String?> = _isNotifying
    val dataEditable: LiveData<Boolean> = _dataEditable
    val hexSelected: LiveData<Boolean> = _hexSelected

    var data = MutableLiveData<String?>(null)

    init
    {
        _uuid.value = "${model.uuid}"
        _name.value = UUBluetooth.bluetoothSpecName(model.uuid)
        _properties.value = UUBluetooth.characteristicPropertiesToString(model.properties)
        _dataEditable.value = (UUBluetooth.canWriteData(model) || UUBluetooth.canWriteWithoutResponse(model))

        refreshNotifyLabel()
        refreshData()
    }

    private fun formatData(): String?
    {
        if (model.value == null)
        {
            return null
        }

        if (hexSelected.value == true)
        {
            return UUString.byteToHex(model.value)
        }

        return String(model.value, Charsets.UTF_8)
    }

    fun toggleHex(hex: Boolean)
    {
        Log.d("LOG", "Hex: $hex")
        _hexSelected.value = hex
        refreshData()
    }

    fun readData()
    {
        peripheral.readCharacteristic(model, 60000)
        { p, updatedCharacteristic, error ->

            UUThread.runOnMainThread()
            {
                refreshData()
            }
        }
    }

    fun toggleNotify()
    {
        val isNotifying = UUBluetooth.isNotifying(model)

        peripheral.setNotifyState(model,
            !isNotifying,
            30000,
            object : UUCharacteristicDelegate {
                override fun onComplete(
                    peripheral: UUPeripheral,
                    characteristic: BluetoothGattCharacteristic,
                    error: UUBluetoothError?,
                ) {
                    //UULog.debug(javaClass, "setNotify.characteristicChanged",
                      //  "Characteristic changed, characteristic: " + characteristic.uuid +
                        //        ", data: " + UUString.byteToHex(characteristic.value) +
                          //      ", error: " + error)

                    UUThread.runOnMainThread()
                    {
                        refreshData()
                    }
                }
            },
            object : UUCharacteristicDelegate {
                override fun onComplete(
                    peripheral: UUPeripheral,
                    characteristic: BluetoothGattCharacteristic,
                    error: UUBluetoothError?,
                ) {
                    //UULog.debug(javaClass, "setNotify.onComplete",
                      //  ("Set Notify complete, characteristic: " + characteristic.uuid +
                        //        ", error: " + error))
                    //UUListView.reloadRow(listView, position)

                    UUThread.runOnMainThread()
                    {
                        refreshData()
                    }
                }
            })
    }

    fun writeData()
    {

    }

    fun wworWriteData()
    {

    }

    private fun refreshNotifyLabel()
    {
        if (UUBluetooth.isNotifying(model))
        {
            _isNotifying.value = Strings.load(R.string.yes)
        }
        else
        {
            _isNotifying.value = Strings.load(R.string.no)
        }
    }

    private fun refreshData()
    {
        data.value = formatData()
    }
}

@BindingAdapter("selected")
fun setSelected(view: View, value: Boolean)
{
    view.isSelected = value
    view.invalidate()
}