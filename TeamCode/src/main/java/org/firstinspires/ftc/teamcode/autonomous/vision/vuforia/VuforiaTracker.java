package org.firstinspires.ftc.teamcode.autonomous.vision.vuforia;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XZY;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;

import android.util.Log;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import java.util.ArrayList;
import java.util.List;


public class VuforiaTracker {
    private VuforiaLocalizer vuforia;
    private VuforiaTrackables trackables;
    private OpenGLMatrix lastLocation = null;
    private WebcamName webcamName;
    private final int cameraMonitorViewId;
    private VuforiaTrackables targets;
    private float targetHeight = 0.47625f * 1000;
    private float halfField = 1.83f * 1000;
    private float halfTile = 0.305f * 1000;
    private VuforiaTrackable trackable;

    public VuforiaTracker(VuforiaLocalizer vuforia, WebcamName webcamName, int cameraMonitorViewId) {
        this.vuforia = vuforia;
        this.webcamName = webcamName;
        this.cameraMonitorViewId = cameraMonitorViewId;
    }

    public void init() {

        targets = this.vuforia.loadTrackablesFromAsset("PowerPlay");

        List<VuforiaTrackable> allTrackables = new ArrayList<VuforiaTrackable>();
        allTrackables.addAll(targets);

        final float CAMERA_FORWARD_DISPLACEMENT = 0.33f * 1000;
        final float CAMERA_VERTICAL_DISPLACEMENT = 0.22f * 1000;
        final float CAMERA_LEFT_DISPLACEMENT = 0.15f * 1000;

        OpenGLMatrix cameraLocationOnRobot = OpenGLMatrix
                .translation(CAMERA_FORWARD_DISPLACEMENT, CAMERA_LEFT_DISPLACEMENT, CAMERA_VERTICAL_DISPLACEMENT)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XZY, DEGREES, 90, 90, 0));

        /**  Let all the trackable listeners know where the camera is.  */
        for (VuforiaTrackable trackable : allTrackables) {
            ((VuforiaTrackableDefaultListener) trackable.getListener()).setCameraLocationOnRobot(webcamName, cameraLocationOnRobot);
        }

        // Define the locations of the powerplay targets.
        identifyTarget(0, "Red Audience Wall", -halfField, -3 * halfTile, targetHeight, 90, 0,  90);
        identifyTarget(1, "Red Rear Wall", halfField, -3 * halfTile, targetHeight, 90, 0, -90);
        identifyTarget(2, "Blue Audience Wall", -halfField, 3 * halfTile, targetHeight, 90, 0,  90);
        identifyTarget(3, "Blue Rear Wall", halfField, 3 * halfTile, targetHeight, 90, 0, -90);

        targets.activate();

    }

    public VuforiaTrackable recognizeTarget() {
        for (VuforiaTrackable trackable : targets) {
            if (((VuforiaTrackableDefaultListener) trackable.getListener()).isVisible()) {
                return trackable;
            }
        }
        return null;
    }

    public void update() {
        trackable = recognizeTarget();
        if (trackable != null) {
            Log.i("Vuforia", "Visible Target: " + trackable.getName());
            OpenGLMatrix robotLocationTransform = ((VuforiaTrackableDefaultListener) trackable.getListener()).getUpdatedRobotLocation();
            if (robotLocationTransform != null) {
                lastLocation = robotLocationTransform;
            }
        }
        else{
            Log.i("Vuforia", "No Target Visible");
        }
    }

    public OpenGLMatrix getVuforiaLocation() {
        return lastLocation;
    }

    void identifyTarget(int targetIndex, String targetName, float dx, float dy, float dz, float rx, float ry, float rz) {
        VuforiaTrackable aTarget = targets.get(targetIndex);
        aTarget.setName(targetName);
        aTarget.setLocation(OpenGLMatrix.translation(dx, dy, dz)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, rx, ry, rz)));
    }

    public float[] getLocation(){
        // shift the position 90 degrees clockwise to make it relative to the audience wall
        float[] location = lastLocation.getTranslation().getData();
        float x = location[0];
        float y = location[1];
        float z = location[2];
        float[] newLocation = new float[3];
        newLocation[0] = y;
        newLocation[1] = x;
        newLocation[2] = z;
        Log.i("Vuforia", "Location: " + newLocation[0] + ", " + newLocation[1] + ", " + newLocation[2]);
        return newLocation;
    }

    public String formatMatrix(OpenGLMatrix matrix){
        return matrix.formatAsTransform();
    }

    public OpenGLMatrix getLastLocation() {
        return lastLocation;
    }
}
