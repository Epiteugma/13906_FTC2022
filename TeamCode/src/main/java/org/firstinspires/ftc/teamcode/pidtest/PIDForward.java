package org.firstinspires.ftc.teamcode.pidtest;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.z3db0y.flagship.pid.PIDController;

@Config
@TeleOp(name = "PIDForward", group = "PID_TEST")
public class PIDForward extends MiniRobotMode {
    public static double kP = 1;
    public static double kI = 0;
    public static double kD = 0;
    public static double pow = .5;

    @Override
    public void runOpMode() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        getMotors();
        waitForStart();

        PIDController pidFrontLeft = new PIDController(kP, kI, kD).bind(frontLeft, 288, 125);
        PIDController pidFrontRight = new PIDController(kP, kI, kD).bind(frontRight, 288, 125);
        PIDController pidBackLeft = new PIDController(kP, kI, kD).bind(backLeft, 288, 125);
        PIDController pidBackRight = new PIDController(kP, kI, kD).bind(backRight, 288, 125);

        pidFrontLeft.setDebug(telemetry, "frontLeft");
        pidFrontRight.setDebug(telemetry, "frontRight");
        pidBackLeft.setDebug(telemetry, "backLeft");
        pidBackRight.setDebug(telemetry, "backRight");

        waitForStart();
        while (opModeIsActive()) {
            pidFrontLeft.updateCoeffs(kP, kI, kD).run(pow);
            pidFrontRight.updateCoeffs(kP, kI, kD).run(pow);
            pidBackLeft.updateCoeffs(kP, kI, kD).run(pow);
            pidBackRight.updateCoeffs(kP, kI, kD).run(pow);

            telemetry.update();
        }
    }
}
