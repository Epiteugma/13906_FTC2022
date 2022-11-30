package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorImplEx;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.z3db0y.flagship.DriveTrain;
import com.z3db0y.flagship.DriveTrain.MotorWithLocation;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.Common;
import org.firstinspires.ftc.teamcode.Enums;
import org.firstinspires.ftc.teamcode.Flags;
import org.firstinspires.ftc.teamcode.autonomous.vision.sleeveRecognition.Detection;

public class AutonomousOpMode extends Common {

    public void runOpMode() {
        if(this.getClass().isAnnotationPresent(Flags.class)) {
            Flags flags = this.getClass().getAnnotation(Flags.class);

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

    void initCamera(){
        webcamName = hardwareMap.get(WebcamName.class, "Webcam 1");
        cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
    }

    void initDetector() {
        this.detector = new Detection(webcamName, cameraMonitorViewId);
        this.detector.init(5.32, 1932, 1932, 648, 648);
    }

    private void initCommon() {
//        initCamera();
//        initDetector();
    }

    private void run() {
        driveTrain.driveCM(50, 1, DriveTrain.Direction.FORWARD);
        driveTrain.turn(90, 1, 3, imu);
        driveTrain.driveCM(50, 1, DriveTrain.Direction.FORWARD);
        driveTrain.turn(0, 1, 3, imu);
        driveTrain.driveCM(50, 1, DriveTrain.Direction.FORWARD);
    }
}