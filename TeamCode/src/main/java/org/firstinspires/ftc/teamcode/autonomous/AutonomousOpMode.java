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
import org.firstinspires.ftc.teamcode.TickUtils;
import org.firstinspires.ftc.teamcode.TicksToAngles;
import org.firstinspires.ftc.teamcode.autonomous.vision.sleeveRecognition.Detection;
import org.firstinspires.ftc.teamcode.autonomous.vision.sleeveRecognition.ParkingPosition;
import org.firstinspires.ftc.teamcode.autonomous.vision.vuforia.VuforiaTracker;
import org.json.JSONException;
import org.json.JSONObject;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.util.ArrayList;

public class AutonomousOpMode extends Common {

    public int vuforiaViewId;
    public int easyOpenCvViewId;
    public Detection detector;
    private static final String VUFORIA_KEY = "ASELAJn/////AAABmZUiJdqKE0EHuf5Spf2stsFNBGiBj0GE9nhWS2s8BiqaK/BfRvMTtnuNt3v1/Wyp4UXWQ8c0s7zWCMs4wDVdq/zyocEw7Fl420YioboCqFG4bewm0HzqsTJmK0NefkvTrKvIz/EiBj9oPmuOdmANngjPrtT0+B2cdByiuit8i8COR1j3W1pd6npai0LmGdS6d8HWIPQ9lpm1I4M7YKsDhQpuFVTMgw1idN/eFvPvkOlI64JVaQotkTs4JV5vUwZeytAclQtzCm6rdx3Qfu1fqWtQRR21Oiq2S13omyaProgVT394sXfESMlEwHcr9Ruur90emvtqvJH3RzUZR7Tl2QVzZDEKKdJcc94hJdTiOPnt";
    public VuforiaLocalizer vuforia;
    public VuforiaTracker vuforiaTracker;
    public VuforiaTrackable trackable;
    public float[] actualLocation = {0, 0, 0};
    public OpenGLMatrix trackableLocation;
    public ParkingPosition defaultParkingPosition = ParkingPosition.CENTER;
    public JSONObject map;
    public float[] coneStackLocation;
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
        // Force stopper
        Thread main = Thread.currentThread();
        Thread terminator = new Thread(() -> {
            while(opModeInInit()) if(isStopRequested()) main.interrupt();
            while (!isStopRequested()) {
            }
            try {
                main.interrupt();
            } catch (Exception ignored) {
            }
        });
        terminator.setName("THE TERMINATOR");
        terminator.start();
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

    private void park(ParkingPosition parkingPosition) {
        driveTrain.strafeCM(50, 1, DriveTrain.Direction.RIGHT);
        switch (parkingPosition) {
            case LEFT:
                driveTrain.driveCM(-75, 1, DriveTrain.Direction.BACKWARD);
                break;
            case CENTER:
                break;
            case RIGHT:
                driveTrain.driveCM(75, 1, DriveTrain.Direction.FORWARD);
                break;
        }
    }

    public float[] getConeStackLocation(String name) {
        JSONObject coneStackLocation = null;
        try {
            coneStackLocation = map.getJSONObject("objects").getJSONObject("coneStacks").getJSONObject(name);
            telemetry.addData("coneStackLocation", map.getJSONObject("map").getJSONObject("coneStacks"));
            telemetry.update();
            Log.i("coneStackLocation", map.getJSONObject("coneStacks").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            return new float[]{(float) coneStackLocation.getDouble("x"), (float) coneStackLocation.getDouble("y"), (float) coneStackLocation.getDouble("z")};
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void pickUpCone() {
    }

    public void left() {
        driveTrain.driveCM(50, 1, this.getClass().getAnnotation(Flags.class).side() == Enums.Side.LEFT ? DriveTrain.Direction.BACKWARD : DriveTrain.Direction.FORWARD);
    }

    public void right() {
        driveTrain.driveCM(50, 1, this.getClass().getAnnotation(Flags.class).side() == Enums.Side.LEFT ? DriveTrain.Direction.FORWARD : DriveTrain.Direction.BACKWARD);
    }

    public void run() {
        Logger.setTelemetry(telemetry);
        parkingPosition = sleeveDetection(1500);
        detector.stop();
        DriveTrain.Direction strafeDir = this.getClass().getAnnotation(Flags.class).side() == Enums.Side.LEFT ? DriveTrain.Direction.LEFT : DriveTrain.Direction.RIGHT;
        slideMotors.runToPositionAsync(TickUtils.cmToTicks(100, 28 * 18, slideGearRadius), 1);
        driveTrain.strafeCM(160, 1, strafeDir);
        sleep(450);
        slideMotors.runToPosition(TickUtils.cmToTicks(-20, 28 * 18, slideGearRadius), 1);
        slideMotors.runToPositionAsync(TickUtils.cmToTicks(-75, 28 * 18, slideGearRadius), 1);
        encloseClaw();
        driveTrain.driveCM(5, 1, DriveTrain.Direction.BACKWARD);
        driveTrain.strafeCM(-40, 1, strafeDir);
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