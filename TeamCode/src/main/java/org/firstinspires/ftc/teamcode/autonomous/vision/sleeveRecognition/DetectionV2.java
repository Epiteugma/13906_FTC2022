package org.firstinspires.ftc.teamcode.autonomous.vision.sleeveRecognition;

import static com.vuforia.PIXEL_FORMAT.RGB565;
import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XZY;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;

import android.graphics.Bitmap;
import android.util.Log;

import com.vuforia.Frame;
import com.vuforia.Image;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.Consumer;
import org.firstinspires.ftc.robotcore.external.function.Continuation;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.teamcode.autonomous.vision.vuforia.VuforiaTracker;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.openftc.apriltag.AprilTagDetection;

import java.util.ArrayList;
import java.util.List;

public class DetectionV2 {
    AprilTagDetectionPipeline pipeline;
    WebcamName webcamName;
    int cameraMonitorViewId;
    VuforiaLocalizer vuforia;
    VuforiaTrackables targets;
    private float targetHeight = 0.47625f * 1000;
    private float halfField = 1.83f * 1000;
    private float halfTile = 0.305f * 1000;
    private OpenGLMatrix lastLocation = null;
    private VuforiaTrackable trackable;
    private static final String VUFORIA_KEY = "ASELAJn/////AAABmZUiJdqKE0EHuf5Spf2stsFNBGiBj0GE9nhWS2s8BiqaK/BfRvMTtnuNt3v1/Wyp4UXWQ8c0s7zWCMs4wDVdq/zyocEw7Fl420YioboCqFG4bewm0HzqsTJmK0NefkvTrKvIz/EiBj9oPmuOdmANngjPrtT0+B2cdByiuit8i8COR1j3W1pd6npai0LmGdS6d8HWIPQ9lpm1I4M7YKsDhQpuFVTMgw1idN/eFvPvkOlI64JVaQotkTs4JV5vUwZeytAclQtzCm6rdx3Qfu1fqWtQRR21Oiq2S13omyaProgVT394sXfESMlEwHcr9Ruur90emvtqvJH3RzUZR7Tl2QVzZDEKKdJcc94hJdTiOPnt";


    public DetectionV2(WebcamName webcamName, int cameraMonitorViewId) {
        this.webcamName = webcamName;
        this.cameraMonitorViewId = cameraMonitorViewId;
    }

    public void init(double tagsize, double fx, double fy, double cx, double cy) {
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        parameters.cameraName = webcamName;
        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.useExtendedTracking = false;
        vuforia = ClassFactory.getInstance().createVuforia(parameters);
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
        pipeline = new AprilTagDetectionPipeline(tagsize, fx, fy, cx, cy);

//        vuforia
    }

    public ArrayList<AprilTagDetection> getAprilTags() {
        return pipeline.getLatestDetections();
    }

    public void getVuforiaTargets() {}

    void identifyTarget(int targetIndex, String targetName, float dx, float dy, float dz, float rx, float ry, float rz) {
        VuforiaTrackable aTarget = targets.get(targetIndex);
        aTarget.setName(targetName);
        aTarget.setLocation(OpenGLMatrix.translation(dx, dy, dz)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, rx, ry, rz)));
    }
}
