package org.firstinspires.ftc.teamcode;

import com.outoftheboxrobotics.photoncore.PhotonCore;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
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
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.autonomous.AutonomousOpMode;

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
    public Servo leftClaw;
    public Servo rightClaw;
    public Servo rotatingBaseServo;
    public CommonConfig config;

    public static double slideGearRadius = 4.75/2;
    public static double extensionGearRadius = 4.75/2;

    @Override public void runOpMode() {}

    public void initHDrive2() {
        config = new HDRIVE2_Config();

        double gearRatio = 5 * 3;
        double wheelDiameter = 9;

        backLeft = new DriveTrain.MotorWithLocation(hardwareMap.get(DcMotorImplEx.class, "backLeft"), DriveTrain.MotorWithLocation.Location.BACK_LEFT);
        backRight = new DriveTrain.MotorWithLocation(hardwareMap.get(DcMotorImplEx.class, "backRight"), DriveTrain.MotorWithLocation.Location.BACK_RIGHT);
        frontLeft = new DriveTrain.MotorWithLocation(hardwareMap.get(DcMotorImplEx.class, "frontLeft"), DriveTrain.MotorWithLocation.Location.FRONT_LEFT);
        frontRight = new DriveTrain.MotorWithLocation(hardwareMap.get(DcMotorImplEx.class, "frontRight"), DriveTrain.MotorWithLocation.Location.FRONT_RIGHT);

        backLeft.initPID((int) gearRatio * 28, 6000);
        backRight.initPID((int) gearRatio * 28, 6000);
        frontLeft.initPID((int) gearRatio * 28, 6000);
        frontRight.initPID((int) gearRatio * 28, 6000);

        frontLeft.setDirection(DcMotorImplEx.Direction.REVERSE);
        backLeft.setDirection(DcMotorImplEx.Direction.REVERSE);

        driveTrain = new DriveTrain(DriveTrain.Type.MECANUM, new DriveTrain.MotorWithLocation[]{frontLeft, backRight, frontRight, backLeft}, wheelDiameter, gearRatio);

        leftSlide = new Motor(hardwareMap.get(DcMotorImplEx.class, "leftSlide"));
        rightSlide = new Motor(hardwareMap.get(DcMotorImplEx.class, "rightSlide"));
        slideMotors = new MotorGroup(leftSlide, rightSlide);
        slideMotors.setHoldPosition(true);
        leftSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightSlide.setDirection(DcMotorSimple.Direction.REVERSE);

        imu = hardwareMap.get(BNO055IMU.class, "imu");
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        imu.initialize(parameters);

        leftClaw = hardwareMap.get(Servo.class, "leftClaw");
        rightClaw = hardwareMap.get(Servo.class, "rightClaw");
        leftClaw.setDirection(Servo.Direction.REVERSE);
    }

    public void openClaw(boolean async){
        leftClaw.setPosition(0.65);
        rightClaw.setPosition(0.65);
        if(!async) sleep(1200);
    }

    public void openClaw() {
        if(this.getClass().isAnnotationPresent(Autonomous.class)) openClaw(false);
        else openClaw(true);
    }
//
//    public void openClawPartially(boolean async) {
//        leftClaw.setPosition(0.6);
//        rightClaw.setPosition(0.6);
//        if(!async) sleep(1200);
//    }
//
//    public void openClawPartially() {
//        if(this.getClass().isAnnotationPresent(Autonomous.class)) openClawPartially(false);
//        else openClawPartially(true);
//    }

    public void closeClaw(boolean async){
        leftClaw.setPosition(0.94);
        rightClaw.setPosition(0.94);
        if(!async) sleep(1200);
    }

    public void closeClaw() {
        if(this.getClass().isAnnotationPresent(Autonomous.class)) closeClaw(false);
        else closeClaw(true);
    }

    public void releaseRotatingBaseServo(boolean async) {
        rotatingBaseServo.setPosition(1);
        if(!async) sleep(1000);
    }

    public void releaseRotatingBaseServo() {
        if(this.getClass().isAnnotationPresent(Autonomous.class)) releaseRotatingBaseServo(false);
        else releaseRotatingBaseServo(true);
    }

    public void encloseClaw(){
        leftClaw.setPosition(0.5);
        rightClaw.setPosition(0.5);
    }

    public void initHDrive() {
        config = new HDRIVE_Config();

        PhotonCore.enable();
        double gearRatio = 5;
        double wheelDiameter = 7.5;

        backLeft = new DriveTrain.MotorWithLocation(hardwareMap.get(DcMotorImplEx.class, "backLeft"), DriveTrain.MotorWithLocation.Location.BACK_LEFT);
        backRight = new DriveTrain.MotorWithLocation(hardwareMap.get(DcMotorImplEx.class, "backRight"), DriveTrain.MotorWithLocation.Location.BACK_RIGHT);
        frontLeft = new DriveTrain.MotorWithLocation(hardwareMap.get(DcMotorImplEx.class, "frontLeft"), DriveTrain.MotorWithLocation.Location.FRONT_LEFT);
        frontRight = new DriveTrain.MotorWithLocation(hardwareMap.get(DcMotorImplEx.class, "frontRight"), DriveTrain.MotorWithLocation.Location.FRONT_RIGHT);

        backLeft.initPID((int) gearRatio * 28, 6000);
        backRight.initPID((int) gearRatio * 28, 6000);
        frontLeft.initPID((int) gearRatio * 28, 6000);
        frontRight.initPID((int) gearRatio * 28, 6000);

//        frontLeft.setDirection(DcMotorImplEx.Direction.REVERSE);
//        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorImplEx.Direction.REVERSE);
//        backRight.setDirection(DcMotorImplEx.Direction.REVERSE);

        for(DriveTrain.MotorWithLocation motor : new DriveTrain.MotorWithLocation[]{backLeft, backRight, frontLeft, frontRight}) {
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }

        driveTrain = new DriveTrain(DriveTrain.Type.MECANUM, new DriveTrain.MotorWithLocation[]{backLeft, backRight, frontLeft, frontRight}, wheelDiameter, gearRatio);

        extension = new Motor(hardwareMap.get(DcMotorImplEx.class, "extension"));
        rotatingBase = new Motor(hardwareMap.get(DcMotorImplEx.class, "rotatingBase"));
        rotatingBase.setDirection(DcMotorSimple.Direction.REVERSE);
        leftSlide = new Motor(hardwareMap.get(DcMotorImplEx.class, "leftSlide"));
        rightSlide = new Motor(hardwareMap.get(DcMotorImplEx.class, "rightSlide"));
        slideMotors = new MotorGroup(leftSlide, rightSlide);
        slideMotors.setHoldPosition(true);
        leftSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        rightSlide.setDirection(DcMotorSimple.Direction.REVERSE);
        leftSlide.setDirection(DcMotorSimple.Direction.REVERSE);
        leftSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rotatingBase.setHoldPosition(true);
        extension.setHoldPosition(true);
        extension.setHoldPower(0.4);
        rotatingBase.setHoldPower(0.5);
        slideMotors.setHoldPower(0.8);
//        extension.getMotorType().setTicksPerRev(28 * 5 * 4);
//        extension.setStallDetect(true);
//        leftSlide.getMotorType().setTicksPerRev(28 * 20);
//        rightSlide.getMotorType().setTicksPerRev(28 * 20);

        imu = hardwareMap.get(BNO055IMU.class, "imu");
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        imu.initialize(parameters);

        leftClaw = hardwareMap.get(Servo.class, "leftClaw");
        rightClaw = hardwareMap.get(Servo.class, "rightClaw");
        leftClaw.setDirection(Servo.Direction.REVERSE);
        rotatingBaseServo = hardwareMap.get(Servo.class, "rotatingBaseServo");
    }
}
