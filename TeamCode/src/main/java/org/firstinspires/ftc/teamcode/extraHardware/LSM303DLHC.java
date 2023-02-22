package org.firstinspires.ftc.teamcode.extraHardware;

import android.util.Log;

import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchDevice;
import com.qualcomm.robotcore.hardware.configuration.annotations.DeviceProperties;
import com.qualcomm.robotcore.hardware.configuration.annotations.I2cDeviceType;

import org.firstinspires.ftc.robotcore.external.navigation.Axis;

@I2cDeviceType
@DeviceProperties(name = "LSM303DLHC Accelerometer", xmlTag = "LSM303DLHC")
public class LSM303DLHC extends I2cDeviceSynchDevice<I2cDeviceSynch> {

    public LSM303DLHC(I2cDeviceSynch deviceClient, boolean deviceClientOwned) {
        super(deviceClient, deviceClientOwned);

        this.deviceClient.setI2cAddress(I2cAddr.create7bit(0x19));
        this.deviceClient.engage();
    }

    private enum Register {
        CTRL_REG1(0x20),
        ACCEL_X_L(0x28),
        ACCEL_X_H(0x29),
        ACCEL_Y_L(0x2A),
        ACCEL_Y_H(0x2B),
        ACCEL_Z_L(0x2C),
        ACCEL_Z_H(0x2D);

        public int bVal;
        Register(int bVal) {
            this.bVal = bVal;
        }
    }

    @Override
    protected boolean doInitialize() {
        write(Register.CTRL_REG1, 0x97);
        return true;
    }

    @Override
    public Manufacturer getManufacturer() {
        return Manufacturer.Other;
    }

    @Override
    public String getDeviceName() {
        return "LSM303DLHC Accelerometer";
    }

    private void write(Register reg, int value) {
        this.deviceClient.write(reg.bVal, new byte[]{(byte)value});
    }

    private int read(Register reg) {
        return (int) this.deviceClient.read(reg.bVal, 1)[0];
    }

    private int read16(Register reg) {
        byte[] bytes = this.deviceClient.read(reg.bVal, 2);
        return (int) (bytes[1] << 8) | bytes[0];
    }

    // Public methods
    public int getAccel(Axis axis) {
        switch(axis) {
            case X:
                return this.read16(Register.ACCEL_X_L);
            case Y:
                return this.read16(Register.ACCEL_Y_L);
            case Z:
                return this.read16(Register.ACCEL_Z_L);
            default:
                return 0;
        }
    }

    public int[] getAccel() {
        return new int[]{
                getAccel(Axis.X),
                getAccel(Axis.Y),
                getAccel(Axis.Z)
        };
    }
}
