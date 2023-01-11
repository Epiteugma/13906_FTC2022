package org.firstinspires.ftc.teamcode.teleop;

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
            leftSlide.setPower(gamepad2.left_stick_y);
            rightSlide.setPower(gamepad2.left_stick_y);
            rotatingBase.setPower(gamepad2.right_stick_y);
            telemetry.addData("leftSlide", leftSlide.getCurrentPosition());
            telemetry.addData("rightSlide", rightSlide.getCurrentPosition());
            driveTrain.driveRobotCentric(gamepad1.left_stick_y, gamepad1.right_stick_x, gamepad1.left_stick_x);
            slideMotors.setVelocity(gamepad2.left_stick_y);
            extension.setPower(gamepad2.right_stick_y);
            if(gamepad2.right_trigger > 0.4) {
                leftClaw.setPosition(0.4);
                rightClaw.setPosition(1);
            }
            else if(gamepad2.left_trigger > 0.4) {
                leftClaw.setPosition(1);
                rightClaw.setPosition(0.4);
            }
            if(gamepad2.dpad_right){
                rotatingBase.setPower(1);
            }
            else if(gamepad2.dpad_left){
                rotatingBase.setPower(-1);
            }
            else{
                rotatingBase.setPower(0);
            }
            telemetry.update();
        }
    }

}
