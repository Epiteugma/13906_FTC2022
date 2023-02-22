package org.firstinspires.ftc.teamcode.autonomous.vision.sleeveRecognition;

import static android.os.SystemClock.sleep;

import android.util.Log;

import com.acmerobotics.dashboard.FtcDashboard;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.util.ArrayList;

public class Detection {
    OpenCvCamera camera;
    AprilTagDetectionPipeline pipeline;
    VuforiaLocalizer vuforiaHook;
    public CameraState cameraState = CameraState.CLOSED;

    public enum CameraState {
        CLOSED,
        OPENING,
        OPENED,
        FAILED;
    }

    public Detection(WebcamName webcam, int cameraMonitorViewId) {
        this.camera = OpenCvCameraFactory.getInstance().createWebcam(webcam, cameraMonitorViewId);
    }

    public void init(double tagsize, double fx, double fy, double cx, double cy, OpenCvCameraRotation camRot) {
        this.pipeline = new AprilTagDetectionPipeline(tagsize, fx, fy, cx, cy);
        this.cameraState = CameraState.OPENING;
        this.camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                camera.startStreaming(640, 480, camRot);
                camera.setPipeline(pipeline);
                FtcDashboard.getInstance().startCameraStream(camera, 0);
                cameraState = CameraState.OPENED;
            }

            @Override
            public void onError(int errorCode) {
                cameraState = CameraState.FAILED;
            }
        });
    }

    public ArrayList<AprilTagDetection> getRecognitions() {
        if(this.cameraState != CameraState.OPENED) return new ArrayList<>();
        return this.pipeline.getLatestDetections();
    }

    public void waitForCamera() {
        double startTime = System.currentTimeMillis();
        while((cameraState == CameraState.OPENING || cameraState == CameraState.CLOSED) && System.currentTimeMillis() < startTime + 3000) {}
    }

    public void stop() {
        this.camera.closeCameraDeviceAsync(() -> {});
    }
}