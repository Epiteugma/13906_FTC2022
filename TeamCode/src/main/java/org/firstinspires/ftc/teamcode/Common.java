package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorImplEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.z3db0y.flagship.DriveTrain;
import com.z3db0y.flagship.Motor;
import com.z3db0y.flagship.MotorGroup;

public class Common extends LinearOpMode {
    public Flags flags = this.getClass().isAnnotationPresent(Flags.class) ? this.getClass().getAnnotation(Flags.class) : null;
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

    public CommonConfig.ServoConfig leftServoConfig;
    public CommonConfig.ServoConfig rightServoConfig;

    public interface CheckCallback {
        boolean run();
    }

    @Override public void runOpMode() {}

    public void initHDrive2() {
        config = new REVVED_UP_JR_Config();

        int gearRatio = config.getDrivetrain().gearRatio;
        int ticksPerRev = config.getDrivetrain().ticksPerRev;
        double wheelDiameter = config.getDrivetrain().wheelDiameterCM;

        backLeft = new DriveTrain.MotorWithLocation(hardwareMap.get(DcMotorImplEx.class, "backLeft"), DriveTrain.MotorWithLocation.Location.BACK_LEFT);
        backRight = new DriveTrain.MotorWithLocation(hardwareMap.get(DcMotorImplEx.class, "backRight"), DriveTrain.MotorWithLocation.Location.BACK_RIGHT);
        frontLeft = new DriveTrain.MotorWithLocation(hardwareMap.get(DcMotorImplEx.class, "frontLeft"), DriveTrain.MotorWithLocation.Location.FRONT_LEFT);
        frontRight = new DriveTrain.MotorWithLocation(hardwareMap.get(DcMotorImplEx.class, "frontRight"), DriveTrain.MotorWithLocation.Location.FRONT_RIGHT);

        backLeft.initPID(gearRatio * 28, 6000);
        backRight.initPID(gearRatio * 28, 6000);
        frontLeft.initPID(gearRatio * 28, 6000);
        frontRight.initPID(gearRatio * 28, 6000);

        frontRight.setDirection(DcMotorImplEx.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        driveTrain = new DriveTrain(DriveTrain.Type.MECANUM, new DriveTrain.MotorWithLocation[]{frontLeft, backRight, frontRight, backLeft}, wheelDiameter, ticksPerRev, gearRatio);

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
        leftClaw.setPosition(config.getLeftClawConfig().openPosition);
        rightClaw.setPosition(config.getRightClawConfig().openPosition);
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
        leftClaw.setPosition(config.getLeftClawConfig().closedPosition);
        rightClaw.setPosition(config.getRightClawConfig().closedPosition);
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
        leftClaw.setPosition(config.getLeftClawConfig().openPosition * 0.77);
        rightClaw.setPosition(config.getRightClawConfig().openPosition * 0.77);
    }

    public void initHDrive() {
        config = new REVVED_UP_Config();

//        PhotonCore.enable();
        int gearRatio = config.getDrivetrain().gearRatio;
        int ticksPerRev = config.getDrivetrain().ticksPerRev;
        double wheelDiameter = config.getDrivetrain().wheelDiameterCM;

        backLeft = new DriveTrain.MotorWithLocation(hardwareMap.get(DcMotorImplEx.class, "backLeft"), DriveTrain.MotorWithLocation.Location.BACK_LEFT);
        backRight = new DriveTrain.MotorWithLocation(hardwareMap.get(DcMotorImplEx.class, "backRight"), DriveTrain.MotorWithLocation.Location.BACK_RIGHT);
        frontLeft = new DriveTrain.MotorWithLocation(hardwareMap.get(DcMotorImplEx.class, "frontLeft"), DriveTrain.MotorWithLocation.Location.FRONT_LEFT);
        frontRight = new DriveTrain.MotorWithLocation(hardwareMap.get(DcMotorImplEx.class, "frontRight"), DriveTrain.MotorWithLocation.Location.FRONT_RIGHT);

        backLeft.initPID(gearRatio * 28, 6000);
        backRight.initPID(gearRatio * 28, 6000);
        frontLeft.initPID(gearRatio * 28, 6000);
        frontRight.initPID(gearRatio * 28, 6000);

        backRight.setDirection(DcMotorImplEx.Direction.REVERSE);

        for(DriveTrain.MotorWithLocation motor : new DriveTrain.MotorWithLocation[]{backLeft, backRight, frontLeft, frontRight}) {
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        }

        driveTrain = new DriveTrain(DriveTrain.Type.MECANUM, new DriveTrain.MotorWithLocation[]{backLeft, backRight, frontLeft, frontRight}, wheelDiameter, ticksPerRev, gearRatio);

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
        slideMotors.setTargetPositionTolerance(20);
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
