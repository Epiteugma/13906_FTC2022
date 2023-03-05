package org.firstinspires.ftc.teamcode.pidtest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "NoPIDForward", group = "PID_TEST")
@Disabled
public class NoPIDForward extends MiniRobotMode {

    @Override
    public void runOpMode() {
        getMotors();
        waitForStart();
        while (opModeIsActive()) {
            frontLeft.setPower(1);
            frontRight.setPower(1);
            backLeft.setPower(1);
            backRight.setPower(1);
        }
    }
}
