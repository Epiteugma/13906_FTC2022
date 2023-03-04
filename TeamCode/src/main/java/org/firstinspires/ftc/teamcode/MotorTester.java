package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorImplEx;
import com.z3db0y.flagship.Logger;
import com.z3db0y.flagship.Motor;

@TeleOp(name = "Motor tester", group = "FTC22")
//@Disabled
public class MotorTester extends LinearOpMode {
    Motor frontLeft;
    Motor frontRight;
    Motor backLeft;
    Motor backRight;

    @Override
    public void runOpMode() {
        frontLeft = new Motor(hardwareMap.get(DcMotorImplEx.class, "frontLeft"));
        frontRight = new Motor(hardwareMap.get(DcMotorImplEx.class, "frontRight"));
        backLeft = new Motor(hardwareMap.get(DcMotorImplEx.class, "backLeft"));
        backRight = new Motor(hardwareMap.get(DcMotorImplEx.class, "backRight"));
        Logger.setTelemetry(new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry()));
        waitForStart();
        while (opModeIsActive()) {
            frontRight.setPower(0);
            frontLeft.setPower(0);
            backLeft.setPower(0);
            backRight.setPower(0);
            if(gamepad1.triangle) {
                Logger.addData("Running frontLeft: " + frontLeft.getCurrentPosition());
                frontLeft.setPower(1);
            } else if(gamepad1.square) {
                Logger.addData("Running frontRight: " + frontRight.getCurrentPosition());
                frontRight.setPower(1);
            } else if(gamepad1.circle) {
                Logger.addData("Running backLeft: " + backLeft.getCurrentPosition());
                backLeft.setPower(1);
            } else if(gamepad1.cross) {
                Logger.addData("Running backRight: " + backRight.getCurrentPosition());
                backRight.setPower(1);
            }
            Logger.update();
        }
    }

}
