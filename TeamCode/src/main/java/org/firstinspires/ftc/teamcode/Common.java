package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorImplEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.z3db0y.flagship.DriveTrain;
import com.z3db0y.flagship.Motor;
import com.z3db0y.flagship.MotorGroup;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;

public class Common extends LinearOpMode {
    public Flags flags = this.getClass().isAnnotationPresent(Flags.class) ? this.getClass().getAnnotation(Flags.class) : null;
    public WebcamName webcamName;
    public DriveTrain.MotorWithLocation frontLeft;
    public DriveTrain.MotorWithLocation frontRight;
    public DriveTrain.MotorWithLocation backLeft;
    public DriveTrain.MotorWithLocation backRight;
    public Motor leftSlide;
    public Motor rightSlide;
    public MotorGroup slideMotors;
    public Motor rotatingBase;
    public Motor extension;
    public DriveTrain driveTrain;
    public BNO055IMU imu;
    public Servo cameraBase;
    public Servo leftClaw;
    public Servo rightClaw;
    public Rev2mDistanceSensor clawLimitSwitch;

    @Override public void runOpMode() {}

    public void initBoxDrive() {
        double gearRatio = 15.0;
        double wheelDiameter = 9;

        backLeft = new DriveTrain.MotorWithLocation(hardwareMap.get(DcMotorImplEx.class, "backLeft"), DriveTrain.MotorWithLocation.Location.BACK_LEFT);
        backRight = new DriveTrain.MotorWithLocation(hardwareMap.get(DcMotorImplEx.class, "backRight"), DriveTrain.MotorWithLocation.Location.BACK_RIGHT);
        frontLeft = new DriveTrain.MotorWithLocation(hardwareMap.get(DcMotorImplEx.class, "frontLeft"), DriveTrain.MotorWithLocation.Location.FRONT_LEFT);
        frontRight = new DriveTrain.MotorWithLocation(hardwareMap.get(DcMotorImplEx.class, "frontRight"), DriveTrain.MotorWithLocation.Location.FRONT_RIGHT);

//        leftDistance = hardwareMap.get(Rev2mDistanceSensor.class, "leftDistance");
//        rightDistance = hardwareMap.get(Rev2mDistanceSensor.class, "rightDistance");
//        backDistance = hardwareMap.get(Rev2mDistanceSensor.class, "backDistance");
//        frontDistance = hardwareMap.get(Rev2mDistanceSensor.class, "frontDistance");

        frontRight.setDirection(DcMotorImplEx.Direction.REVERSE);
        backLeft.setDirection(DcMotorImplEx.Direction.REVERSE);
//        left.setDirection(DcMotorImplEx.Direction.REVERSE);
//        front.setDirection(DcMotorImplEx.Direction.REVERSE);

        frontRight.setHoldPosition(true);
        frontLeft.setHoldPosition(true);
        backRight.setHoldPosition(true);
        backLeft.setHoldPosition(true);

        driveTrain = new DriveTrain(DriveTrain.Type.MECANUM, new DriveTrain.MotorWithLocation[]{frontLeft, backRight, frontRight, backLeft}, wheelDiameter, gearRatio);

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

        extension = new Motor(hardwareMap.get(DcMotorImplEx.class, "extension"));
        rotatingBase = new Motor(hardwareMap.get(DcMotorImplEx.class, "rotatingBase"));
        rotatingBase.setDirection(DcMotorSimple.Direction.REVERSE);
        leftSlide = new Motor(hardwareMap.get(DcMotorImplEx.class, "leftSlide"));
        rightSlide = new Motor(hardwareMap.get(DcMotorImplEx.class, "rightSlide"));
        slideMotors = new MotorGroup(leftSlide, rightSlide);
        slideMotors.setHoldPosition(true);
        slideMotors.setDirection(DcMotorImplEx.Direction.REVERSE);
        leftSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rotatingBase.setHoldPosition(true);
        extension.setHoldPosition(true);

        clawLimitSwitch = hardwareMap.get(Rev2mDistanceSensor.class, "clawLimitSwitch");

        imu = hardwareMap.get(BNO055IMU.class, "imu");
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        imu.initialize(parameters);

        cameraBase = hardwareMap.get(Servo.class, "cameraBase");
        leftClaw = hardwareMap.get(Servo.class, "leftClaw");
        rightClaw = hardwareMap.get(Servo.class, "rightClaw");
    }
}
