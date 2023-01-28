package org.firstinspires.ftc.teamcode.autonomous;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.z3db0y.flagship.DriveTrain;
import com.z3db0y.flagship.Logger;
import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.teamcode.Common;
import org.firstinspires.ftc.teamcode.Enums;
import org.firstinspires.ftc.teamcode.TicksToAngles;
import org.firstinspires.ftc.teamcode.autonomous.vision.sleeveRecognition.Detection;
import org.firstinspires.ftc.teamcode.autonomous.vision.sleeveRecognition.ParkingPosition;
import org.firstinspires.ftc.teamcode.autonomous.vision.vuforia.VuforiaTracker;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.openftc.apriltag.AprilTagDetection;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
    public ParkingPosition parkingPosition = defaultParkingPosition;
    public TicksToAngles rotatingBaseHelper;

    public void runOpMode() {
        if (flags != null) {

            if (flags.robotType() == Enums.RobotType.X_DRIVE) this.initBoxDrive();
            else if (flags.robotType() == Enums.RobotType.H_DRIVE) this.initHDrive();

            this.initCommon();
            waitForStart();
            this.run();
        } else throw new RuntimeException("This class is not annotated with @Flags!");
    }

    void initSleeveDetector() {
        WebcamName webcam = hardwareMap.get(WebcamName.class, "Webcam 1");
        easyOpenCvViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        this.detector = new Detection(webcam, easyOpenCvViewId);
        this.detector.init(5.32, 1932, 1932, 648, 648);
        this.detector.waitForCamera();
    }

    void initVuforia() {
        // maybe it could rotate all the time like a lidar scanner???
        WebcamName vuforiaWebcam = hardwareMap.get(WebcamName.class, "vuforiaWebcam");
        vuforiaViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(vuforiaViewId);
        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = vuforiaWebcam;
        parameters.useExtendedTracking = false;
        vuforia = ClassFactory.getInstance().createVuforia(parameters);
        vuforiaTracker = new VuforiaTracker(vuforia, vuforiaWebcam, vuforiaViewId);
        vuforiaTracker.init();
        Logger.addData("Status Initialized vuforia tracker");
        Logger.update();
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
        initSleeveDetector();
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
                break;
            }
            Logger.update();
        }
        detector.stop();
        Logger.addData("Parking Position: " + parkingPosition.name());
        Logger.update();
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

    public void run() {
        parkingPosition = sleeveDetection(1500);
    }
}