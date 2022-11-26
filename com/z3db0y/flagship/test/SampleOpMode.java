package com.z3db0y.flagship.test;

import android.util.Log;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorImplEx;
import com.z3db0y.flagship.DriveTrain;
import com.z3db0y.flagship.Navigator;

import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;

@TeleOp(name = "SampleOpMode", group = "test")
public class SampleOpMode extends LinearOpMode {
    Navigator navigator;

    @Override
    public void runOpMode() {
        navigator = new Navigator(hardwareMap.get(BNO055IMU.class, "imu"), AxesOrder.ZYX);

        waitForStart();
        navigator.startTracking();

        while (opModeIsActive()) {
            telemetry.addData("Acceleration", navigator.getAcceleration());
            telemetry.addData("Velocity", navigator.getVelocity());
            telemetry.addData("Position", navigator.getPosition());
            telemetry.update();
        }
    }

}
