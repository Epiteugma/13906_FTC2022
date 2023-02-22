package org.firstinspires.ftc.teamcode.extraHardware;

import static android.os.SystemClock.sleep;

import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchDevice;
import com.qualcomm.robotcore.hardware.configuration.annotations.DeviceProperties;
import com.qualcomm.robotcore.hardware.configuration.annotations.I2cDeviceType;

import org.firstinspires.ftc.robotcore.external.navigation.Axis;

@I2cDeviceType
@DeviceProperties(name = "MPU6050 Gyro Sensor", xmlTag = "MPU6050")
// https://invensense.tdk.com/wp-content/uploads/2015/02/MPU-6000-Register-Map1.pdf
public class MPU6050 extends I2cDeviceSynchDevice<I2cDeviceSynch> {

    private enum Register {
        CONFIG(0x1A),
        GYRO_CONFIG(0x1B),
        ACCEL_CONFIG(0x1C),
        ACCEL_XOUT_H(0x3B),
        ACCEL_XOUT_L(0x3C),
        ACCEL_YOUT_H(0x3D),
        ACCEL_YOUT_L(0x3E),
        ACCEL_ZOUT_H(0x3F),
        ACCEL_ZOUT_L(0x40),
        TEMP_OUT_H(0x41),
        TEMP_OUT_L(0x42),
        GYRO_XOUT_H(0x43),
        GYRO_XOUT_L(0x44),
        GYRO_YOUT_H(0x45),
        GYRO_YOUT_L(0x46),
        GYRO_ZOUT_H(0x47),
        GYRO_ZOUT_L(0x48),
        POWER_MANAGEMENT_1(0x6B),
        POWER_MANAGEMENT_2(0x6C);

        public int bVal;
        Register(int bVal) {
            this.bVal = bVal;
        }
    }

    private enum FrameSync {
        DISABLED(0x0),
        TEMP_OUT_L(0x1),
        GYRO_XOUT_L(0x2),
        GYRO_YOUT_L(0x3),
        GYRO_ZOUT_L(0x4),
        ACCEL_XOUT_L(0x5),
        ACCEL_YOUT_L(0x6),
        ACCEL_ZOUT_L(0x7);

        public int bVal;
        FrameSync(int bVal) {
            this.bVal = bVal;
        }
    }

    private enum DigitalLowPassFilter {
        MODE_0(0x0),
        MODE_1(0x1),
        MODE_2(0x2),
        MODE_3(0x3),
        MODE_4(0x4),
        MODE_5(0x5),
        MODE_6(0x6);

        public int bVal;
        DigitalLowPassFilter(int bVal) {
            this.bVal = bVal;
        }
    }

    public MPU6050(I2cDeviceSynch deviceClient, boolean deviceClientOwned) {
        super(deviceClient, deviceClientOwned);
        this.deviceClient.setI2cAddress(I2cAddr.create7bit(0x68));
        this.deviceClient.engage();
    }

    @Override
    protected boolean doInitialize() {
        write(Register.CONFIG, FrameSync.DISABLED.bVal << 3 | DigitalLowPassFilter.MODE_0.bVal);
        write(Register.POWER_MANAGEMENT_1, 0x0);
        write(Register.POWER_MANAGEMENT_2, 0x0);
        this.deviceClient.disengage();
        sleep(50);
        this.deviceClient.engage();

        write(Register.GYRO_CONFIG, 0x0);
        write(Register.ACCEL_CONFIG, 0x0);
        return true;
    }

    @Override
    public Manufacturer getManufacturer() {
        return Manufacturer.Other;
    }

    @Override
    public String getDeviceName() {
        return "MPU6050 Gyro Sensor";
    }

    private void write(Register reg, int val) {
        this.deviceClient.write(reg.bVal, new byte[]{(byte)val});
    }

    private int read(Register reg) {
        return this.deviceClient.read(reg.bVal, 1)[0];
    }

    private int read16(Register reg) {
        byte[] bytes = this.deviceClient.read(reg.bVal, 2);
        return (bytes[0] << 8) | bytes[1];
    }

    // Public methods
    public double getAngle(Axis axis) {
        if(axis == Axis.X) {
            return this.read16(Register.GYRO_XOUT_H) / 131.0;
        } else if(axis == Axis.Y) {
            return this.read16(Register.GYRO_YOUT_H) / 131.0;
        } else if(axis == Axis.Z) {
            return this.read16(Register.GYRO_ZOUT_H) / 131.0;
        } else return 0;
    }

    public double getAccel(Axis axis) {
        if(axis == Axis.X) {
            return this.read16(Register.ACCEL_XOUT_H) / 16384.0 * 9.80665;
        } else if(axis == Axis.Y) {
            return this.read16(Register.ACCEL_YOUT_H) / 16384.0 * 9.80665;
        } else if(axis == Axis.Z) {
            return this.read16(Register.ACCEL_ZOUT_H) / 16384.0 * 9.80665;
        } else return 0;
    }

    public double getTemp() {
        return this.read16(Register.TEMP_OUT_H) / 340.0 + 36.53;
    }
}
