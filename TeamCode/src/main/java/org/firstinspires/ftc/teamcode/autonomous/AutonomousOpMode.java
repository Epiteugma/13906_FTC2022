package org.firstinspires.ftc.teamcode.autonomous;

import android.util.Log;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.z3db0y.flagship.DriveTrain;
import com.z3db0y.flagship.Logger;
import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.teamcode.Common;
import org.firstinspires.ftc.teamcode.Enums;
import org.firstinspires.ftc.teamcode.Flags;
import org.firstinspires.ftc.teamcode.TicksToAngles;
import org.firstinspires.ftc.teamcode.autonomous.vision.sleeveRecognition.Detection;
import org.firstinspires.ftc.teamcode.autonomous.vision.sleeveRecognition.ParkingPosition;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.util.ArrayList;

public class AutonomousOpMode extends Common {

    public int easyOpenCvViewId;
    public Detection detector;
    public ParkingPosition defaultParkingPosition = ParkingPosition.CENTER;
    public ArrayList<AprilTagDetection> detections;
    public ParkingPosition parkingPosition;
    public TicksToAngles rotatingBaseHelper;

    public void runOpMode() {
        if (flags != null) {

            if (flags.robotType() == Enums.RobotType.H_DRIVE2) this.initHDrive2();
            else if (flags.robotType() == Enums.RobotType.H_DRIVE) this.initHDrive();

            this.initCommon();
            waitForStart();
            this.run();
        } else throw new RuntimeException("This class is not annotated with @Flags!");
    }

    void initSleeveDetector(String deviceName, OpenCvCameraRotation camRot) {
        WebcamName webcam = hardwareMap.get(WebcamName.class, deviceName);
        easyOpenCvViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        this.detector = new Detection(webcam, easyOpenCvViewId);
        this.detector.init(5.32, 1932, 1932, 648, 648, camRot);
        this.detector.waitForCamera();
    }

    private void initCommon() {
        frontRight.setHoldPosition(true);
        frontLeft.setHoldPosition(true);
        backRight.setHoldPosition(true);
        backLeft.setHoldPosition(true);

        rotatingBase.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rotatingBase.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        extension.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        extension.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        rotatingBaseHelper = new TicksToAngles(rotatingBase,  (int) (28 * 125 * 1.5));
        Logger.setTelemetry(new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry()));
        closeClaw(false);
        initSleeveDetector(this.getClass().getAnnotation(Flags.class).side() == Enums.Side.LEFT ? "leftWebcam" : "rightWebcam", this.getClass().getAnnotation(Flags.class).side() == Enums.Side.LEFT ? OpenCvCameraRotation.UPSIDE_DOWN : OpenCvCameraRotation.UPRIGHT);
    }

    public ParkingPosition sleeveDetection(double maxTime) {
        detections = detector.getRecognitions();
        ElapsedTime timer = new ElapsedTime();
        timer.reset();
        while (opModeIsActive()) {
            detections = detector.getRecognitions();
            Logger.addData("Status Recognizing...");
            if (detections.size() > 0) {
                Logger.addData("Status Recognized");
                parkingPosition = ParkingPosition.values()[detections.get(0).id];
                break;
            }
            if (timer.milliseconds() > maxTime) {
                Logger.addData("Status No tags detected, default");
                return defaultParkingPosition;
            }
            Logger.update();
        }
        Logger.addData("Parking Position: " + parkingPosition.name());
        Logger.update();
        if(parkingPosition == null) return defaultParkingPosition;
        return parkingPosition;
    }

    public void left() {
        driveTrain.driveCM(47, 1, DriveTrain.Direction.FORWARD);
    }

    public void right() {
        driveTrain.driveCM(47, 1, DriveTrain.Direction.BACKWARD);
    }

    public void run() {
        Logger.setTelemetry(telemetry);
        parkingPosition = sleeveDetection(1500);
        detector.stop();
//        DriveTrain.Direction strafeDir = this.getClass().getAnnotation(Flags.class).side() == Enums.Side.LEFT ? DriveTrain.Direction.LEFT : DriveTrain.Direction.RIGHT;
//        slideMotors.runToPositionAsync(TickUtils.cmToTicks(100, 28 * 18, slideGearRadius), 1);
//        driveTrain.strafeCM(160, 1, strafeDir);
//        sleep(450);
//        slideMotors.runToPosition(TickUtils.cmToTicks(-20, 28 * 18, slideGearRadius), 1);
//        slideMotors.runToPositionAsync(TickUtils.cmToTicks(-75, 28 * 18, slideGearRadius), 1);
//        encloseClaw();
//        driveTrain.driveCM(5, 1, DriveTrain.Direction.BACKWARD);
//        driveTrain.strafeCM(-40, 1, strafeDir);
//        switch (parkingPosition) {
//            case LEFT:
//                left();
//                break;
//            case RIGHT:
//                right();
//                break;
//        }

        int turnMult = this.getClass().getAnnotation(Flags.class).side() == Enums.Side.LEFT ? 1 : -1;
        driveTrain.turn(turnMult * 90, 1, imu);
        driveTrain.driveCM(55, 1, DriveTrain.Direction.FORWARD);
        driveTrain.turn(180 * (turnMult == 1 ? 1 : 0), 1, imu, false);
        switch (parkingPosition) {
            case LEFT:
                left();
                break;
            case RIGHT:
                right();
                break;
        }
    }
}