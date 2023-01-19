package org.firstinspires.ftc.teamcode.teleop;

import android.util.Log;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Common;
import org.firstinspires.ftc.teamcode.Enums;

public class TeleOpMode extends Common {

    @Override
    public void runOpMode() {
        if (flags != null) {
            if (flags.robotType() == Enums.RobotType.BOX_DRIVE) this.initBoxDrive();
            else if (flags.robotType() == Enums.RobotType.H_DRIVE) this.initHDrive();

            this.initCommon();
            waitForStart();
            this.run();
        } else throw new RuntimeException("This class is not annotated with @Flags!");
    }

    private void initCommon() {
    }

    private void openClaw() {
        leftClaw.setPosition(1);
        rightClaw.setPosition(0.4);
    }

    private void closeClaw() {
        leftClaw.setPosition(0.4);
        rightClaw.setPosition(1);
    }

    private void run() {
        double forwardMultiplier = 1;
        while (opModeIsActive()) {
            telemetry.addData("leftSlide", leftSlide.getCurrentPosition());
            telemetry.addData("rightSlide", rightSlide.getCurrentPosition());
            if (gamepad1.left_bumper) forwardMultiplier = 0.45;
            if (gamepad1.right_bumper) forwardMultiplier = 1;
            driveTrain.driveRobotCentric(gamepad1.left_stick_y * forwardMultiplier, gamepad1.right_stick_x, gamepad1.left_stick_x);
            double downMultiplier = 0.25;
            if (gamepad2.left_stick_y < 0) {
                // UP
                slideMotors.setPower(-gamepad2.left_stick_y);
            }
            else if (gamepad2.left_stick_y > 0) {
                // DOWN
                slideMotors.setPower(-gamepad2.left_stick_y * downMultiplier);
            }
            else {
                slideMotors.setPower(0);
            }
            Log.i("gamepadY", String.valueOf(gamepad2.left_stick_y));
            Log.i("slideMotors", String.valueOf(leftSlide.getPower()));
            boolean extensionAllowed = clawLimitSwitch.getDistance(DistanceUnit.CM) > 15;

            if (Math.abs(gamepad2.right_stick_y) > Math.abs(gamepad2.right_stick_x)) {
                if (extensionAllowed || gamepad2.right_stick_y < 0) {
                    extension.setPower(gamepad2.right_stick_y * 0.7);
                }
                rotatingBase.setPower(0);
            }
            else {
                rotatingBase.setPower(gamepad2.right_stick_x * 0.5);
                if (extensionAllowed) extension.setPower(0);
            }
            if (gamepad2.right_trigger > 0.3) {
                closeClaw();
            }
            else if (gamepad2.left_trigger > 0.3) {
                openClaw();
            }

            if (!extensionAllowed) {
                extension.setPower(0.15);
            }
            telemetry.addData("clawLimitSwitch", clawLimitSwitch.getDistance(DistanceUnit.CM));
            telemetry.update();
        }
    }
}