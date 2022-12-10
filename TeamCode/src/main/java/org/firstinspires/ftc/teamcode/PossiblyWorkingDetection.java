package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.autonomous.vision.sleeveRecognition.DetectionV2;

@TeleOp(name = "PossiblyWorkingDetection", group = "test")
public class PossiblyWorkingDetection extends LinearOpMode {

        @Override
        public void runOpMode() {
            WebcamName camera = hardwareMap.get(WebcamName.class, "Webcam 1");
            DetectionV2 detector = new DetectionV2(camera, hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName()));
            detector.init(5.32, 1932, 1932, 648, 648);
            waitForStart();

            while (opModeIsActive()) {
                telemetry.addData("Status", "Running");
                telemetry.addData("Recognitions", detector.getAprilTags().size());
                telemetry.update();
            }
        }
}
