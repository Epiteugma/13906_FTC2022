package org.firstinspires.ftc.teamcode.pidtest;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.z3db0y.flagship.pid.AngularPIDController;

@Config
@TeleOp(name = "AngularPIDForward", group = "PID_TEST")
@Disabled
public class AngularPIDForward extends MiniRobotMode {

    public static double kP = 1;
    public static double kI = 0;
    public static double kD = 0;

    public static double angle = 0;

    @Override
    public void runOpMode() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        BNO055IMU imu = hardwareMap.get(BNO055IMU.class, "imu");
        BNO055IMU.Parameters params = new BNO055IMU.Parameters();
        params.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        imu.initialize(params);

        AngularPIDController pid = new AngularPIDController(kP, kI, kD);
        pid.bind(imu);
        pid.setDebug(telemetry, "IMU");

        getMotors();
        waitForStart();
        while (opModeIsActive()) {
            pid.updateCoeffs(kP, kI, kD);
            double out = pid.getOutput(angle, 1); // 0, firstAngle
            frontLeft.setPower(1 - out);
            frontRight.setPower(1 + out);
            backLeft.setPower(1 - out);
            backRight.setPower(1 + out);
            telemetry.update();
        }
    }
}