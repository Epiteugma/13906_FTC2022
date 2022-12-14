package org.firstinspires.ftc.teamcode.autonomous;

import android.util.Log;

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
import org.firstinspires.ftc.teamcode.autonomous.vision.sleeveRecognition.Detection;
import org.firstinspires.ftc.teamcode.autonomous.vision.sleeveRecognition.ParkingPosition;
import org.firstinspires.ftc.teamcode.autonomous.vision.vuforia.VuforiaTracker;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.openftc.apriltag.AprilTagDetection;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class AutonomousOpMode extends Common {

    int vuforiaViewId;
    int easyOpenCvViewId;
    Detection detector;
    private static final String VUFORIA_KEY = "ASELAJn/////AAABmZUiJdqKE0EHuf5Spf2stsFNBGiBj0GE9nhWS2s8BiqaK/BfRvMTtnuNt3v1/Wyp4UXWQ8c0s7zWCMs4wDVdq/zyocEw7Fl420YioboCqFG4bewm0HzqsTJmK0NefkvTrKvIz/EiBj9oPmuOdmANngjPrtT0+B2cdByiuit8i8COR1j3W1pd6npai0LmGdS6d8HWIPQ9lpm1I4M7YKsDhQpuFVTMgw1idN/eFvPvkOlI64JVaQotkTs4JV5vUwZeytAclQtzCm6rdx3Qfu1fqWtQRR21Oiq2S13omyaProgVT394sXfESMlEwHcr9Ruur90emvtqvJH3RzUZR7Tl2QVzZDEKKdJcc94hJdTiOPnt";
    VuforiaLocalizer vuforia;
    VuforiaTracker vuforiaTracker;
    VuforiaTrackable trackable;
    float[] robotLocation;
    OpenGLMatrix trackableLocation;
    ParkingPosition defaultParkingPosition = ParkingPosition.CENTER;
    JSONObject map;
    float[] coneStackLocation;
    ArrayList<AprilTagDetection> detections;
    ParkingPosition parkingPosition = defaultParkingPosition;

    private String readFile(String path) {
        try {
            InputStream is = hardwareMap.appContext.getAssets().open(path);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void runOpMode() {
        try {
            map = (JSONObject) new JSONTokener(readFile("map.json")).nextValue();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        if(flags != null) {

            if(flags.robotType() == Enums.RobotType.X_DRIVE) this.initBoxDrive();
            else if(flags.robotType() == Enums.RobotType.H_DRIVE) this.initHDrive();

            this.initCommon();
            waitForStart();
            this.run();
        }
        else throw new RuntimeException("This class is not annotated with @Flags!");
    }

    void initSleeveDetector() {
        WebcamName easyOpenCvWebcam = hardwareMap.get(WebcamName.class, "easyOpenCvWebcam");
        this.detector = new Detection(easyOpenCvWebcam, easyOpenCvViewId);
        this.detector.init(5.32, 1932, 1932, 648, 648);
        this.detector.waitForCamera();
    }

    void initVuforia(){
        cameraBase = hardwareMap.get(Servo.class, "cameraBase");
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
        Logger.addData("Status", "Initialized vuforia tracker");
        Logger.update();
    }

    private void initCommon() {
        Logger.setTelemetry(new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry()));
        initSleeveDetector();
        initVuforia();
    }

    public ParkingPosition sleeveDetection(double maxTime) {
        detections = detector.getRecognitions();
        ElapsedTime timer = new ElapsedTime();
        timer.reset();
        while(detections.size() < 1){
            detections = detector.getRecognitions();
            Logger.addData("Status", "Recognizing...");
            if(detections.size() > 0) {
                Logger.addData("Status", "Recognized");
                parkingPosition = ParkingPosition.values()[detections.get(0).id];
                break;
            }
            if(timer.milliseconds() > maxTime) {
                Logger.addData("Status", "No tags detected, default");
                break;
            }
            Logger.update();
        }
        detector.stop();
        Logger.addData("Parking Position: ", parkingPosition.name());
        Logger.update();
        return parkingPosition;
    }

    private void park(ParkingPosition parkingPosition) {
        driveTrain.strafeCM(50, 1, DriveTrain.Direction.RIGHT);
        switch (parkingPosition) {
            case LEFT:
                driveTrain.driveCM(-75 , 1, DriveTrain.Direction.BACKWARD);
                break;
            case CENTER:
                break;
            case RIGHT:
                driveTrain.driveCM(75, 1, DriveTrain.Direction.FORWARD);
                break;
        }
    }

    public float[] getConeStackLocation(String name) {
        JSONArray coneStackLocation = null;
        try {
            coneStackLocation = map.getJSONObject("coneStacks").getJSONArray(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            return new float[]{(float) coneStackLocation.getDouble(0), (float) coneStackLocation.getDouble(1), (float) coneStackLocation.getDouble(2)};
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void pickUpCone(){}

    public void driveToConeStack(){
        while(!vuforiaTracker.targetVisible()) {
            // no need to update the location because it is done in the vuforia thread
            Log.i("Vuforia", "No target visible");
        }
        robotLocation = vuforiaTracker.getLocation();
        // triangulate the path to the cone stack strafeCM turn to face the stack and drive to the stack
        Logger.addData("Robot Location", robotLocation[0] + ", " + robotLocation[1] + ", " + robotLocation[2]);
        Logger.addData("Cone Stack Location", coneStackLocation[0] + ", " + coneStackLocation[1] + ", " + coneStackLocation[2]);
        Logger.update();
        double yDistance = coneStackLocation[1] - robotLocation[1];
        double xDistance = (coneStackLocation[0] - robotLocation[0]) - 5; // we want to go just in front of the stack
        if(flags.side() == Enums.Side.RIGHT){
            driveTrain.turn(-90, 1, imu);
            driveTrain.strafeCM(yDistance, 1, DriveTrain.Direction.LEFT);
        }
        else if(flags.side() == Enums.Side.LEFT){
            driveTrain.turn(90, 1, imu);
            driveTrain.strafeCM(yDistance, 1, DriveTrain.Direction.RIGHT);
        }
        driveTrain.driveCM(xDistance, 1, DriveTrain.Direction.FORWARD);
    }

//    Thread vuforiaThread = new Thread(new Runnable() {
//        while (opModeIsActive()) {
//            vuforiaTracker.update();
//            robotLocation = vuforiaTracker.getLocation();
//        }
//    });

    public void run() {
        parkingPosition = sleeveDetection(3000);
        new Thread(new Runnable() {
            while (opModeIsActive()) {
                vuforiaTracker.update();
                robotLocation = vuforiaTracker.getLocation();
            }
        }).start();
        if(flags.side() == Enums.Side.LEFT){
            cameraBase.setPosition(-90);
            if(flags.alliance() == Enums.Alliance.BLUE) {
                coneStackLocation = getConeStackLocation("blueUp");
            }
            else if(flags.alliance() == Enums.Alliance.RED) {
                coneStackLocation = getConeStackLocation("redDown");
            }
        }
        else if(flags.side() == Enums.Side.RIGHT){
            cameraBase.setPosition(90);
            if(flags.alliance() == Enums.Alliance.BLUE) {
                coneStackLocation = getConeStackLocation("blueDown");
            }
            else if(flags.alliance() == Enums.Alliance.RED) {
                coneStackLocation = getConeStackLocation("redUp");
            }
        }
        driveToConeStack();
//        driveTrain.driveCM(50, 1, DriveTrain.Direction.FORWARD);
//        driveTrain.turn(90, 1, imu);
//        driveTrain.driveCM(50, 1, DriveTrain.Direction.FORWARD);
//        driveTrain.turn(0, 1, imu);
//        driveTrain.driveCM(50, 1, DriveTrain.Direction.FORWARD);
//        driveTrain.turn(-90, 1, imu);
        park(parkingPosition);
    }
}