package org.firstinspires.ftc.teamcode.pidtest;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.z3db0y.flagship.pid.AngularPIDController;
import com.z3db0y.flagship.pid.VelocityPIDController;

@Config
@TeleOp(name = "FullPIDForward", group = "PID_TEST")
public class FullPIDForward extends BigRobotMode {

    public static double angular_kP = 1;
    public static double angular_kI = 0;
    public static double angular_kD = 0;
    
    public static double drive_kP = 1;
    public static double drive_kI = 0;
    public static double drive_kD = 0;
    
    public static double pow = .5;
    public static double angle = 0;

    @Override
    public void runOpMode() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        
        BNO055IMU imu = hardwareMap.get(BNO055IMU.class, "imu");
        BNO055IMU.Parameters params = new BNO055IMU.Parameters();
        params.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        imu.initialize(params);
        
        AngularPIDController pid = new AngularPIDController(angular_kP, angular_kI, angular_kD);
        pid.bind(imu);
        pid.setDebug(telemetry, "IMU");
        
        getMotors();
        waitForStart();

        VelocityPIDController pidFrontLeft = new VelocityPIDController(drive_kP, drive_kI, drive_kD).bind(frontLeft, 28 * 5, 6000);
        VelocityPIDController pidFrontRight = new VelocityPIDController(drive_kP, drive_kI, drive_kD).bind(frontRight, 28 * 5, 6000);
        VelocityPIDController pidBackLeft = new VelocityPIDController(drive_kP, drive_kI, drive_kD).bind(backLeft, 28 * 5, 6000);
        VelocityPIDController pidBackRight = new VelocityPIDController(drive_kP, drive_kI, drive_kD).bind(backRight, 28 * 5, 6000);

        pidFrontLeft.setDebug(telemetry, "frontLeft");
        pidFrontRight.setDebug(telemetry, "frontRight");
        pidBackLeft.setDebug(telemetry, "backLeft");
        pidBackRight.setDebug(telemetry, "backRight");
        
        while (opModeIsActive()) {
            pid.updateCoeffs(angular_kP, angular_kI, angular_kD);
            double out = pid.getOutput(angle, 1); // 0, firstAngle
            frontLeft.setPower(pidFrontLeft.updateCoeffs(drive_kP, drive_kI, drive_kD).getOutput(pow - out));
            frontRight.setPower(pidFrontRight.updateCoeffs(drive_kP, drive_kI, drive_kD).getOutput(pow + out));
            backLeft.setPower(pidBackLeft.updateCoeffs(drive_kP, drive_kI, drive_kD).getOutput(pow - out));
            backRight.setPower(pidBackRight.updateCoeffs(drive_kP, drive_kI, drive_kD).getOutput(pow + out));
            telemetry.update();
        }
    }
}