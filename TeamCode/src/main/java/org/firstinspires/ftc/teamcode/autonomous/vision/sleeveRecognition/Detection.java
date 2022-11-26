package org.firstinspires.ftc.teamcode.autonomous.vision.sleeveRecognition;

import static android.os.SystemClock.sleep;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.util.ArrayList;

public class Detection {
    OpenCvCamera camera;
    AprilTagDetectionPipeline pipeline;
    public CameraState cameraState = CameraState.CLOSED;

    enum CameraState {
        CLOSED,
        OPENING,
        OPENED,
        FAILED;
    }

    public Detection(WebcamName webcam, int cameraMonitorViewId) {
        this.camera = OpenCvCameraFactory.getInstance().createWebcam(webcam, cameraMonitorViewId);
    }

    public void init(double tagsize, double fx, double fy, double cx, double cy) {
        this.pipeline = new AprilTagDetectionPipeline(tagsize, fx, fy, cx, cy);
        this.cameraState = CameraState.OPENING;
        this.camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                camera.startStreaming(640, 480, OpenCvCameraRotation.UPRIGHT);
                camera.setPipeline(pipeline);
                cameraState = CameraState.OPENED;
            }

            @Override
            public void onError(int errorCode) {
                cameraState = CameraState.FAILED;
            }
        });
    }

    public ArrayList<AprilTagDetection> getRecognitions() {
        return this.pipeline.getLatestDetections();
    }

    public void waitForCamera() {
        while(cameraState == CameraState.OPENING || cameraState == CameraState.CLOSED) {
            sleep(10);
        }
    }

    public void stop() {
        this.camera.closeCameraDevice();
    }
}