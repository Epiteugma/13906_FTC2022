package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.Axis;
import org.firstinspires.ftc.teamcode.extraHardware.LSM303DLHC;
import org.firstinspires.ftc.teamcode.extraHardware.MPU6050;

import java.util.Arrays;

@TeleOp(name = "Gyro I2C Test", group = "Test")
@Disabled
public class GyroProofOfConcept extends LinearOpMode {

    @Override
    public void runOpMode() {
        waitForStart();
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        LSM303DLHC gyro = hardwareMap.get(LSM303DLHC.class, "gyro");
        while (opModeIsActive()) {
            telemetry.addData("Gyro accel X", gyro.getAccel(Axis.X));
            telemetry.update();
        }
    }

}
