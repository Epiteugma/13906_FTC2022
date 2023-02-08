package org.firstinspires.ftc.teamcode.teleop;

import android.util.Log;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.z3db0y.flagship.Logger;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Common;
import org.firstinspires.ftc.teamcode.Enums;

public class TeleOpMode extends Common {

    @Override
    public void runOpMode() {
        if (flags != null) {
            if (flags.robotType() == Enums.RobotType.H_DRIVE2) this.initHDrive2();
            else if (flags.robotType() == Enums.RobotType.H_DRIVE) this.initHDrive();

            this.initCommon();
            waitForStart();
            this.run();
        } else throw new RuntimeException("This class is not annotated with @Flags!");
    }

    private void initCommon() {
    }

    private void logAll(){
        Logger.addData("Info: ");
        Logger.addData("Powers: ");
        Logger.addData("rotatingBase: " + rotatingBase.getPower());
        Logger.addData("extension: " + extension.getPower());
        Logger.addData("slideMotors: " + leftSlide.getPower());
        Logger.addData("Ticks: ");
        Logger.addData("leftSlide: " + leftSlide.getCurrentPosition());
        Logger.addData("rightSlide: " + rightSlide.getCurrentPosition());
        Logger.addData("extension: " + extension.getCurrentPosition());
        Logger.addData("rotatingBase: " + rotatingBase.getCurrentPosition());
        Logger.update();
    }

    private void run() {
        Logger.setTelemetry(telemetry);
        double forwardMultiplier = 1.0;
        double slideDownMultiplier = 0.59;
        if (flags.robotType() == Enums.RobotType.H_DRIVE) rotatingBaseServo.setPosition(1);
        boolean fieldCentric = true;
        double fieldCentricAngle = 0;
        boolean lastModeSwitchHeld = false;
        while (opModeIsActive()) {
            if (gamepad1.left_bumper) {
                forwardMultiplier = 0.45;
            }
            else if (gamepad1.right_bumper) {
                forwardMultiplier = 1.0;
            }
            double turnPower = gamepad1.right_stick_x;
            if(gamepad1.right_trigger < 0.5) {
                slideMotors.setPower(0);
            }
            else {
                turnPower = 0;
                if (gamepad1.right_stick_y < 0) {
                    // UP
                    slideMotors.setPower(-gamepad1.right_stick_y);
                }
                else if (gamepad1.right_stick_y > 0) {
                    // DOWN
                    slideMotors.setPower(-gamepad1.right_stick_y * slideDownMultiplier);
                }
                else {
                    slideMotors.setPower(0);
                }
            }
            if(fieldCentric) driveTrain.driveFieldCentric(gamepad1.left_stick_y * forwardMultiplier, turnPower, gamepad1.left_stick_x, imu.getAngularOrientation().firstAngle, fieldCentricAngle);
            else driveTrain.driveRobotCentric(gamepad1.left_stick_y * forwardMultiplier, turnPower, gamepad1.left_stick_x);

            if(gamepad1.cross && !lastModeSwitchHeld) {
                fieldCentric = !fieldCentric;
                Log.i("fieldCentric", String.valueOf(fieldCentric));
                fieldCentricAngle = imu.getAngularOrientation().firstAngle;
            }
            lastModeSwitchHeld = gamepad1.cross;
//            if (Math.abs(gamepad2.right_stick_y) > Math.abs(gamepad2.right_stick_x)) {
//                extension.setPower(gamepad2.right_stick_y * 0.7);
//                rotatingBase.setPower(0);
//            }
//            else {
//                rotatingBase.setPower(gamepad2.right_stick_x * 0.6);
//                extension.setPower(0);
//            }
            if (flags.robotType() == Enums.RobotType.H_DRIVE) extension.setPower(gamepad2.right_stick_y * 0.5);
            if (flags.robotType() == Enums.RobotType.H_DRIVE) {
//                if(gamepad1.left_trigger > gamepad1.right_trigger) {
//                    if(gamepad1.left_trigger < 0.65) rotatingBase.setPower(-0.5 * gamepad1.left_trigger);
//                    else rotatingBase.setPower(-1 * gamepad1.left_trigger * rotatingBaseMultiplier * rotatingBaseMultiplier);
//                }
//                else {
//                    if(gamepad1.right_trigger < 0.65) rotatingBase.setPower(0.5 * gamepad1.right_trigger * rotatingBaseMultiplier);
//                    else rotatingBase.setPower(gamepad1.right_trigger * rotatingBaseMultiplier);
//                }
                rotatingBase.setPower(gamepad2.left_stick_x * 0.5);
            }
            if (gamepad2.right_trigger > 0.3) {
                closeClaw();
            }
            else if (gamepad2.left_trigger > 0.3) {
                openClaw();
            }
            // logAll();
        }
    }
}