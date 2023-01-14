package org.firstinspires.ftc.teamcode.teleop;

import android.util.Log;

import org.firstinspires.ftc.teamcode.Common;
import org.firstinspires.ftc.teamcode.Enums;

public class TeleOpMode extends Common {

    @Override
    public void runOpMode() {
        if(flags != null) {
            if(flags.robotType() == Enums.RobotType.BOX_DRIVE) this.initBoxDrive();
            else if(flags.robotType() == Enums.RobotType.H_DRIVE) this.initHDrive();

            this.initCommon();
            waitForStart();
            this.run();
        }
        else throw new RuntimeException("This class is not annotated with @Flags!");
    }

    private void initCommon() {}

    private void run() {
        while (opModeIsActive()) {
            telemetry.addData("leftSlide", leftSlide.getCurrentPosition());
            telemetry.addData("rightSlide", rightSlide.getCurrentPosition());
            driveTrain.driveRobotCentric(gamepad1.left_stick_y, gamepad1.right_stick_x, gamepad1.left_stick_x);
            double slideMass = 5;
            slideMotors.setPower(gamepad2.left_stick_y > 0 ? gamepad2.left_stick_y - (9.84 * slideMass / 105) : gamepad2.left_stick_y);
            Log.i("gamepadY", String.valueOf(gamepad2.left_stick_y));
            if(Math.abs(gamepad2.right_stick_y) > Math.abs(gamepad2.right_stick_x)) {
                extension.setPower(gamepad2.right_stick_y);
                rotatingBase.setPower(0);
            } else {
                rotatingBase.setPower(gamepad2.right_stick_x);
                extension.setPower(0);
            }
            if(gamepad2.right_trigger > 0.4) {
                leftClaw.setPosition(0.4);
                rightClaw.setPosition(1);
            }
            else if(gamepad2.left_trigger > 0.4) {
                leftClaw.setPosition(1);
                rightClaw.setPosition(0.4);
            }
            telemetry.update();
        }
    }

}
