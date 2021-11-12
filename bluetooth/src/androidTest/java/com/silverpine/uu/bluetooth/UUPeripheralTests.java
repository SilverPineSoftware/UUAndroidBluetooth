package com.silverpine.uu.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.os.Parcelable;

import com.silverpine.uu.core.UUParcel;
import com.silverpine.uu.core.UUString;
import com.silverpine.uu.test.core.UUAssert;
import com.silverpine.uu.test.core.UUParcelableBaseTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public class UUPeripheralTests extends UUParcelableBaseTest<UUPeripheral>
{
    @Test
    public void testDefaultObject()
    {
        UUPeripheral obj = new UUPeripheral();
        doTest(obj);
    }

    @Test
    public void testRandomeObject()
    {
        UUPeripheral obj = new UUPeripheral();

        byte[] deviceBytes = UUString.hexToByte("11000000370039003A00370032003A00300042003A00350043003A00440044003A00320046000000");
        BluetoothDevice device = UUParcel.deserializeParcelable(BluetoothDevice.CREATOR, deviceBytes);
        Assert.assertNotNull(device);

        byte[] scanRecord = UUString.hexToByte("020106");
        obj.updateAdvertisement(device, -57, scanRecord);
        doTest(obj);
    }

    @Override
    protected Parcelable.Creator<UUPeripheral> getParcelableCreator()
    {
        return UUPeripheral.CREATOR;
    }

    @Override
    protected void assertEquals(UUPeripheral lhs, UUPeripheral rhs)
    {
        Assert.assertNotNull(lhs);
        Assert.assertNotNull(rhs);
        UUAssert.assertSameNullness(lhs, rhs);
        Assert.assertEquals(lhs, rhs);
    }
}
