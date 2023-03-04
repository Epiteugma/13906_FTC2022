package org.firstinspires.ftc.teamcode.pidtest;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.z3db0y.flagship.pid.VelocityPIDController;

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

        VelocityPIDController pidFrontLeft = new VelocityPIDController(kP, kI, kD).bind(frontLeft, 288, 125);
        VelocityPIDController pidFrontRight = new VelocityPIDController(kP, kI, kD).bind(frontRight, 288, 125);
        VelocityPIDController pidBackLeft = new VelocityPIDController(kP, kI, kD).bind(backLeft, 288, 125);
        VelocityPIDController pidBackRight = new VelocityPIDController(kP, kI, kD).bind(backRight, 288, 125);

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
