package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorImplEx;
import com.z3db0y.flagship.DriveTrain;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.teamcode.autonomous.vision.sleeveRecognition.Detection;
import org.firstinspires.ftc.teamcode.autonomous.vision.sleeveRecognition.ParkingPosition;
import org.firstinspires.ftc.teamcode.autonomous.vision.vuforia.VuforiaTracker;
import org.openftc.apriltag.AprilTagDetection;

import java.util.ArrayList;

@Autonomous(name = "RedLeft", group="FTC22")
public class RedLeft extends LinearOpMode{
    private static final String VUFORIA_KEY = "ASELAJn/////AAABmZUiJdqKE0EHuf5Spf2stsFNBGiBj0GE9nhWS2s8BiqaK/BfRvMTtnuNt3v1/Wyp4UXWQ8c0s7zWCMs4wDVdq/zyocEw7Fl420YioboCqFG4bewm0HzqsTJmK0NefkvTrKvIz/EiBj9oPmuOdmANngjPrtT0+B2cdByiuit8i8COR1j3W1pd6npai0LmGdS6d8HWIPQ9lpm1I4M7YKsDhQpuFVTMgw1idN/eFvPvkOlI64JVaQotkTs4JV5vUwZeytAclQtzCm6rdx3Qfu1fqWtQRR21Oiq2S13omyaProgVT394sXfESMlEwHcr9Ruur90emvtqvJH3RzUZR7Tl2QVzZDEKKdJcc94hJdTiOPnt";
    DriveTrain.MotorWithLocation backLeft;
    DriveTrain.MotorWithLocation backRight;
    DriveTrain.MotorWithLocation frontLeft;
    DriveTrain.MotorWithLocation frontRight;
    WebcamName webcamName;
    int cameraMonitorViewId;
    DriveTrain driveTrain;
    double gearRatio = 15.0;

    double wheelDiameter = 9.0;
    VuforiaLocalizer vuforia;
    VuforiaTracker vuforiaTracker;
    VuforiaTrackable trackable;
    float[] robotLocation;
    OpenGLMatrix trackableLocation;
    Detection detector;
    ParkingPosition defaultParkingPosition = ParkingPosition.CENTER;


    void initCamera(){
        webcamName = hardwareMap.get(WebcamName.class, "Webcam 1");
        cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
    }

    void initDetector() {
        this.detector = new Detection(webcamName, cameraMonitorViewId);
        this.detector.init(5.32, 1932, 1932, 648, 648);
    }

    void initMotors() {
        backLeft = new DriveTrain.MotorWithLocation(hardwareMap.get(DcMotorImplEx.class, "backLeft"), DriveTrain.MotorWithLocation.Location.BACK_LEFT);
        backRight = new DriveTrain.MotorWithLocation(hardwareMap.get(DcMotorImplEx.class, "backRight"), DriveTrain.MotorWithLocation.Location.BACK_RIGHT);
        frontLeft = new DriveTrain.MotorWithLocation(hardwareMap.get(DcMotorImplEx.class, "frontLeft"), DriveTrain.MotorWithLocation.Location.FRONT_LEFT);
        frontRight = new DriveTrain.MotorWithLocation(hardwareMap.get(DcMotorImplEx.class, "frontRight"), DriveTrain.MotorWithLocation.Location.FRONT_RIGHT);

//        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
//        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setDirection(DcMotorImplEx.Direction.REVERSE);
        backRight.setDirection(DcMotorImplEx.Direction.REVERSE);

        for(DriveTrain.MotorWithLocation motor : new DriveTrain.MotorWithLocation[]{backLeft, backRight, frontLeft, frontRight}) {
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor.getMotorType().setTicksPerRev(280 * 15);
            motor.setHoldPosition(true);
        }

        driveTrain = new DriveTrain(DriveTrain.Type.MECANUM, new DriveTrain.MotorWithLocation[]{backLeft, backRight, frontLeft, frontRight}, wheelDiameter, gearRatio);
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = webcamName;
        parameters.useExtendedTracking = false;
        vuforia = ClassFactory.getInstance().createVuforia(parameters);
        vuforiaTracker = new VuforiaTracker(vuforia, webcamName, cameraMonitorViewId);
        vuforiaTracker.init();
    }

    @Override
    public void runOpMode() {
        initMotors();
        initCamera();
        initDetector();
        BNO055IMU imu = hardwareMap.get(BNO055IMU.class, "imu");
        BNO055IMU.Parameters params = new BNO055IMU.Parameters();
        params.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        params.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        imu.write8(BNO055IMU.Register.OPR_MODE, BNO055IMU.SensorMode.CONFIG.bVal);
        sleep(100);
        imu.write8(BNO055IMU.Register.AXIS_MAP_CONFIG, 0b00001001);
        imu.write8(BNO055IMU.Register.OPR_MODE, BNO055IMU.SensorMode.IMU.bVal);
        sleep(100);
        imu.initialize(params);

        waitForStart();
        detector.waitForCamera();
        ArrayList<AprilTagDetection> detections = detector.getRecognitions();
        ParkingPosition parkingPosition = defaultParkingPosition;
        if(detections.size() > 0) {
            parkingPosition = ParkingPosition.values()[detections.get(0).id];
        }
        detector.stop();
        telemetry.addData("Parking Position", parkingPosition.name());
        /// START OF AUTONOMOUS ///
        driveTrain.driveCM(70.42153540919753, 1, DriveTrain.Direction.FORWARD);
        driveTrain.turn(90, 1, 3, imu);
        driveTrain.driveCM(310.5175208803499, 1, DriveTrain.Direction.FORWARD);
        driveTrain.turn(0, 1, 3, imu);
        driveTrain.driveCM(244.6378317854858, 1, DriveTrain.Direction.FORWARD);
        vuforiaTracker.update();
        trackable = vuforiaTracker.recognizeTarget();
        if(trackable != null) {
            trackableLocation = trackable.getLocation();
            telemetry.addData(trackable.getName() + ": ", "x: " + trackableLocation.getTranslation().get(0) / 10 + " y: " + trackableLocation.getTranslation().get(1) / 10 + " z: " + trackableLocation.getTranslation().get(2) / 10);
            robotLocation = vuforiaTracker.getLocation();
            if (robotLocation != null) {
                telemetry.addData("Robot: ", "x: " + robotLocation[0] + " y: " + robotLocation[1] + " z: " + robotLocation[2]);
            }
        }
        else{
            telemetry.addData("Target", "None");
        }
        telemetry.update();
        /// END OF AUTONOMOUS ///
    }
}