package org.firstinspires.ftc.teamcode.autonomous;

import android.util.Log;

import com.qualcomm.robotcore.hardware.Servo;
import com.z3db0y.flagship.DriveTrain;

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
        // Read the JSON file
        try {
            map = (JSONObject) new JSONTokener(readFile("map.json")).nextValue();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        if(flags != null) {

            if(flags.robotType() == Enums.RobotType.X_DRIVE) this.initXDrive();
            else if(flags.robotType() == Enums.RobotType.H_DRIVE) this.initHDrive();

            this.initCommon();
            waitForStart();
            this.run();
        }
        else throw new RuntimeException("This class is not annotated with @Flags!");
    }

    int cameraMonitorViewId;
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


    void initCameraModule(){
        cameraBase = hardwareMap.get(Servo.class, "cameraBase");
        webcamName = hardwareMap.get(WebcamName.class, "Webcam 1");
        cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
    }

    void initDetector() {
        this.detector = new Detection(webcamName, cameraMonitorViewId);
        this.detector.init(5.32, 1932, 1932, 648, 648);
    }

    void initVuforia(){
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = webcamName;
        parameters.useExtendedTracking = false;
        vuforia = ClassFactory.getInstance().createVuforia(parameters);
        vuforiaTracker = new VuforiaTracker(vuforia, webcamName, cameraMonitorViewId);
        vuforiaTracker.init();
    }

    private void initCommon() {
        initCameraModule();
        initDetector();
        initVuforia();
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

    public boolean targetVisible() {
        return vuforiaTracker.recognizeTarget() != null;
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
        vuforiaTracker.update();
        while(!targetVisible()) {
            vuforiaTracker.update();
            Log.i("Vuforia", "No target visible");
        }
        robotLocation = vuforiaTracker.getLocation();
        // triangulate the path to the cone stack strafeCM turn to face the stack and drive to the stack
        telemetry.addData("Robot Location", robotLocation[0] + ", " + robotLocation[1] + ", " + robotLocation[2]);
        telemetry.addData("Cone Stack Location", coneStackLocation[0] + ", " + coneStackLocation[1] + ", " + coneStackLocation[2]);
        telemetry.update();
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

    public void run() {
        detector.waitForCamera();
        ArrayList<AprilTagDetection> detections = detector.getRecognitions();
        ParkingPosition parkingPosition = defaultParkingPosition;
        if(detections.size() > 0) {
            parkingPosition = ParkingPosition.values()[detections.get(0).id];
        }
        detector.stop();
        telemetry.addData("Parking Position", parkingPosition.name());
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