package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorImplEx;
import com.z3db0y.flagship.DriveTrain;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;

public class Common extends LinearOpMode {
    public WebcamName webcamName;
    public DriveTrain.MotorWithLocation frontLeft;
    public DriveTrain.MotorWithLocation frontRight;
    public DriveTrain.MotorWithLocation backLeft;
    public DriveTrain.MotorWithLocation backRight;
    public Rev2mDistanceSensor leftDistance;
    public Rev2mDistanceSensor rightDistance;
    public Rev2mDistanceSensor backDistance;
    public Rev2mDistanceSensor frontDistance;
    public DriveTrain driveTrain;
    public BNO055IMU imu;

    @Override public void runOpMode() {}

    public void initXDrive() {
        double gearRatio = 15.0;
        double wheelDiameter = 9;
        backLeft = new DriveTrain.MotorWithLocation(hardwareMap.get(DcMotorImplEx.class, "backLeft"), DriveTrain.MotorWithLocation.Location.FRONT);
        backRight = new DriveTrain.MotorWithLocation(hardwareMap.get(DcMotorImplEx.class, "backRight"), DriveTrain.MotorWithLocation.Location.RIGHT);
        frontLeft = new DriveTrain.MotorWithLocation(hardwareMap.get(DcMotorImplEx.class, "frontLeft"), DriveTrain.MotorWithLocation.Location.LEFT);
        frontRight = new DriveTrain.MotorWithLocation(hardwareMap.get(DcMotorImplEx.class, "frontRight"), DriveTrain.MotorWithLocation.Location.BACK);

        leftDistance = hardwareMap.get(Rev2mDistanceSensor.class, "leftDistance");
        rightDistance = hardwareMap.get(Rev2mDistanceSensor.class, "rightDistance");
        backDistance = hardwareMap.get(Rev2mDistanceSensor.class, "backDistance");
        frontDistance = hardwareMap.get(Rev2mDistanceSensor.class, "frontDistance");

        frontRight.setDirection(DcMotorImplEx.Direction.REVERSE);
        backRight.setDirection(DcMotorImplEx.Direction.REVERSE);

        backLeft.setHoldPosition(true);
        backRight.setHoldPosition(true);
        frontLeft.setHoldPosition(true);
        frontRight.setHoldPosition(true);

        driveTrain = new DriveTrain(DriveTrain.Type.X_DRIVE, new DriveTrain.MotorWithLocation[]{backLeft, backRight, frontLeft, frontRight}, wheelDiameter, gearRatio);

        imu = hardwareMap.get(BNO055IMU.class, "imu");
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        imu.initialize(parameters);
    }

    public void initHDrive() {
        double gearRatio = 15.0;
        double wheelDiameter = 7.5;

        backLeft = new DriveTrain.MotorWithLocation(hardwareMap.get(DcMotorImplEx.class, "backLeft"), DriveTrain.MotorWithLocation.Location.BACK_LEFT);
        backRight = new DriveTrain.MotorWithLocation(hardwareMap.get(DcMotorImplEx.class, "backRight"), DriveTrain.MotorWithLocation.Location.BACK_RIGHT);
        frontLeft = new DriveTrain.MotorWithLocation(hardwareMap.get(DcMotorImplEx.class, "frontLeft"), DriveTrain.MotorWithLocation.Location.FRONT_LEFT);
        frontRight = new DriveTrain.MotorWithLocation(hardwareMap.get(DcMotorImplEx.class, "frontRight"), DriveTrain.MotorWithLocation.Location.FRONT_RIGHT);

        frontLeft.setDirection(DcMotorImplEx.Direction.REVERSE);
        backRight.setDirection(DcMotorImplEx.Direction.REVERSE);

        for(DriveTrain.MotorWithLocation motor : new DriveTrain.MotorWithLocation[]{backLeft, backRight, frontLeft, frontRight}) {
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor.getMotorType().setTicksPerRev(280 * 15);
            motor.setHoldPosition(true);
        }

        driveTrain = new DriveTrain(DriveTrain.Type.MECANUM, new DriveTrain.MotorWithLocation[]{backLeft, backRight, frontLeft, frontRight}, wheelDiameter, gearRatio);

        imu = hardwareMap.get(BNO055IMU.class, "imu");
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        imu.initialize(parameters);
    }
}
