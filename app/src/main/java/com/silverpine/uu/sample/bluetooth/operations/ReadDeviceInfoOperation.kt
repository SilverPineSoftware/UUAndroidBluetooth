package com.silverpine.uu.sample.bluetooth.operations

import com.silverpine.uu.bluetooth.UUBluetoothConstants
import com.silverpine.uu.bluetooth.UUPeripheral
import com.silverpine.uu.bluetooth.UUPeripheralOperation
import com.silverpine.uu.core.UUError
import com.silverpine.uu.core.UUObjectDelegate
import java.util.*

class ReadDeviceInfoOperation(peripheral: UUPeripheral): UUPeripheralOperation<UUPeripheral>(peripheral)
{
    var deviceName: String? = null
    var mfgName: String? = null

    override fun execute(completion: UUObjectDelegate<UUError>)
    {
        readUtf8String(UUBluetoothConstants.Characteristics.DEVICE_NAME_UUID)
        { deviceNameResult ->

            deviceName = deviceNameResult

            readUtf8String(UUBluetoothConstants.Characteristics.MANUFACTURER_NAME_STRING_UUID)
            { mfgNameResult ->
                mfgName = mfgNameResult

                completion.onCompleted(null)
            }
        }
    }

    private fun readUtf8String(characteristic: UUID, completion: (String?)->Unit)
    {
        read(characteristic)
        {
            var result: String? = null

            it?.let()
            {
                result = String(it, Charsets.UTF_8)
            }

            completion(result)
        }
    }
}