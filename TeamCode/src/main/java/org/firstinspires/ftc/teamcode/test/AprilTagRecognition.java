package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.autonomous.vision.sleeveRecognition.Detection;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.apriltag.AprilTagPose;

import java.util.ArrayList;

@TeleOp(name = "AprilTagRecognition", group = "test")
public class AprilTagRecognition extends LinearOpMode {

    public String tagPoseToString(AprilTagPose pose) {
        return "AprilTagPose{" + pose.x + ", " + pose.y + ", " + pose.z + ", " + pose.yaw + ", " + pose.pitch + ", " + pose.roll + "}";
    }

    @Override
    public void runOpMode() {
        WebcamName camera = hardwareMap.get(WebcamName.class, "Webcam 1");
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        Detection detector = new Detection(camera, cameraMonitorViewId);
        detector.init(5.32, 1932, 1932, 648, 648);

        waitForStart();

        while (opModeIsActive()) {
            ArrayList<AprilTagDetection> recognitions = detector.getRecognitions();
            telemetry.addData("Recognitions", recognitions.size());
            if(recognitions.size() > 0) {
                telemetry.addData("ID", recognitions.get(0).id);
                telemetry.addData("Pose", tagPoseToString(recognitions.get(0).pose));
            }
            telemetry.update();
        }
    }
}
