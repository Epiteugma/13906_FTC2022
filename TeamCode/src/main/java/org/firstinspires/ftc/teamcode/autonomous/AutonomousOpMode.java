package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.z3db0y.flagship.Logger;
import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.z3db0y.flagship.pid.AngularPIDController;
import com.z3db0y.flagship.pid.TickPIDController;
import com.z3db0y.flagship.pid.VelocityPIDController;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.Common;
import org.firstinspires.ftc.teamcode.Enums;
import org.firstinspires.ftc.teamcode.Flags;
import org.firstinspires.ftc.teamcode.TickUtils;
import org.firstinspires.ftc.teamcode.autonomous.vision.sleeveRecognition.Detection;
import org.firstinspires.ftc.teamcode.autonomous.vision.sleeveRecognition.ParkingPosition;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.util.ArrayList;
import java.util.List;

public class AutonomousOpMode extends Common {

    public int easyOpenCvViewId;
    public Detection detector;
    public ParkingPosition defaultParkingPosition = ParkingPosition.CENTER;
    public ArrayList<AprilTagDetection> detections;
    public ParkingPosition parkingPosition;

    public void runOpMode() {
        if (flags != null) {

            if (flags.robotType() == Enums.RobotType.H_DRIVE2) this.initHDrive2();
            else if (flags.robotType() == Enums.RobotType.H_DRIVE) this.initHDrive();

            this.initCommon();
            waitForStart();
            this.run();
        } else throw new RuntimeException("This class is not annotated with @Flags!");
    }

    void initSleeveDetector(String deviceName, OpenCvCameraRotation camRot) {
        WebcamName webcam = hardwareMap.get(WebcamName.class, deviceName);
        easyOpenCvViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        this.detector = new Detection(webcam, easyOpenCvViewId);
        this.detector.init(5.32, 1932, 1932, 648, 648, camRot);
        this.detector.waitForCamera();
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

        Logger.setTelemetry(new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry()));
        closeClaw(false);
        initSleeveDetector(this.getClass().getAnnotation(Flags.class).side() == Enums.Side.LEFT ? "leftWebcam" : "rightWebcam", this.getClass().getAnnotation(Flags.class).side() == Enums.Side.LEFT ? OpenCvCameraRotation.UPSIDE_DOWN : OpenCvCameraRotation.UPRIGHT);
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
                return defaultParkingPosition;
            }
            Logger.update();
        }
        Logger.addData("Parking Position: " + parkingPosition.name());
        Logger.update();
        if(parkingPosition == null) return defaultParkingPosition;
        return parkingPosition;
    }

    enum PIDTaskType {
        TURN,
        STRAFE,
        DRIVE;
    }

    interface PIDCallback {
        void run();
    }

    class PIDTask {
        public PIDTaskType type;
        public double target;
        public double power;
        public int angle = 1;
        public PIDCallback onComplete;
        public boolean complete = false;

        public PIDTask(PIDTaskType type, double target, double power, PIDCallback onComplete) {
            this.type = type;
            this.target = target;
            this.power = power;
            this.onComplete = onComplete;
        }

        public PIDTask(PIDTaskType type, double target, double power) {
            this.type = type;
            this.target = target;
            this.power = power;
        }
    }

    public void run() {
        int ticksPerRev = 28 * 5;
        int maxRPM = 6000;
        double wheelRadius = 3.75;

        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        Logger.setTelemetry(telemetry);
        parkingPosition = sleeveDetection(1500);
        detector.stop();

        List<PIDTask> pidQueue = new ArrayList<>();
        pidQueue.add(new PIDTask(PIDTaskType.DRIVE, TickUtils.cmToTicks(90, ticksPerRev, wheelRadius), 0.4));
        pidQueue.add(new PIDTask(PIDTaskType.TURN, 90, 0.25));
//        pidQueue.add(new PIDTask(PIDTaskType.STRAFE, TickUtils.cmToTicks(30, 28 * 5, 4.5), 0.25));

        VelocityPIDController pidVeloBackLeft = new VelocityPIDController(config.getBackLeftVeloPID()).bind(backLeft, ticksPerRev, maxRPM);
        VelocityPIDController pidVeloBackRight = new VelocityPIDController(config.getBackRightVeloPID()).bind(backRight, ticksPerRev, maxRPM);
        VelocityPIDController pidVeloFrontLeft = new VelocityPIDController(config.getFrontLeftVeloPID()).bind(frontLeft, ticksPerRev, maxRPM);
        VelocityPIDController pidVeloFrontRight = new VelocityPIDController(config.getFrontRightVeloPID()).bind(frontRight, ticksPerRev, maxRPM);

        TickPIDController pidTickBackLeft = new TickPIDController(config.getBackLeftTickPID()).bind(backLeft, ticksPerRev, maxRPM);
        TickPIDController pidTickBackRight = new TickPIDController(config.getBackLeftTickPID()).bind(backRight, ticksPerRev, maxRPM);
        TickPIDController pidTickFrontLeft = new TickPIDController(config.getBackLeftTickPID()).bind(frontLeft, ticksPerRev, maxRPM);
        TickPIDController pidTickFrontRight = new TickPIDController(config.getBackLeftTickPID()).bind(frontRight, ticksPerRev, maxRPM);

        pidVeloBackLeft.setDebug(telemetry, "backLeftVelo");
        pidVeloBackRight.setDebug(telemetry, "backRightVelo");
        pidVeloFrontLeft.setDebug(telemetry, "frontLeftVelo");
        pidVeloFrontRight.setDebug(telemetry, "frontRightVelo");

        pidTickBackLeft.setDebug(telemetry, "backLeftTicks");
        pidTickBackRight.setDebug(telemetry, "backRightTicks");
        pidTickFrontLeft.setDebug(telemetry, "frontLeftTicks");
        pidTickFrontRight.setDebug(telemetry, "frontRightTicks");

        AngularPIDController angularPID = new AngularPIDController(config.getAngularPID()).bind(imu);
        angularPID.setDebug(telemetry, "IMU");

        PIDTask currentTask;
        double targetAngle = 0;
        for(int i = 0; i < pidQueue.size(); i++) {
            currentTask = pidQueue.get(i);
            backLeft.resetEncoder();
            backRight.resetEncoder();
            frontLeft.resetEncoder();
            frontRight.resetEncoder();

            while (!currentTask.complete && opModeIsActive()) {
                double angularOutput = angularPID.getOutput(targetAngle, 1);

                int leftSide = backLeft.getCurrentPosition() + frontLeft.getCurrentPosition();
                int rightSide = backRight.getCurrentPosition() + frontRight.getCurrentPosition();
                int sideSum = (leftSide + rightSide) / 2;

                switch (currentTask.type) {
                    case DRIVE:
                        if(Math.abs(sideSum) > Math.abs(currentTask.target * 2)) currentTask.complete = true;
                        else {
                            int mlt = currentTask.target < 0 ? -1 : 1;
//                            frontLeft.setPower(pidVeloFrontLeft.getOutput((currentTask.power - angularOutput) * mlt));
//                            frontRight.setPower(pidFrontRight.getOutput((currentTask.power + angularOutput) * mlt));
//                            backLeft.setPower(pidVeloBackLeft.getOutput((currentTask.power - angularOutput) * mlt));
//                            backRight.setPower(pidVeloBackRight.getOutput((currentTask.power + angularOutput) * mlt));
                            frontLeft.setPower(pidVeloFrontLeft.getOutput(Math.max(pidTickBackLeft.getOutput(currentTask.target) * currentTask.power - angularOutput, 0.25) * mlt));
                            frontRight.setPower(pidVeloFrontRight.getOutput(Math.max(pidTickBackLeft.getOutput(currentTask.target) * currentTask.power + angularOutput, 0.25) * mlt));
                            backLeft.setPower(pidVeloBackLeft.getOutput(Math.max(pidTickBackLeft.getOutput(currentTask.target) * currentTask.power - angularOutput, 0.25) * mlt));
                            backRight.setPower(pidVeloBackRight.getOutput(Math.max(pidTickBackLeft.getOutput(currentTask.target) * currentTask.power + angularOutput, 0.25) * mlt));
                        }
                        break;
                    case TURN:
                        double angle = imu.getAngularOrientation().firstAngle;
                        targetAngle = currentTask.target;
                        if(Math.abs(Math.abs(currentTask.target) - Math.abs(angle)) < 1) currentTask.complete = true;
                        else {
                            frontLeft.setPower(pidVeloFrontLeft.getOutput(-angularOutput * currentTask.power));
                            frontRight.setPower(pidVeloFrontRight.getOutput(angularOutput * currentTask.power));
                            backLeft.setPower(pidVeloBackLeft.getOutput(-angularOutput * currentTask.power));
                            backRight.setPower(pidVeloBackRight.getOutput(angularOutput * currentTask.power));
                        }
                        break;
                    case STRAFE:
                        if(Math.abs(sideSum) > Math.abs(currentTask.target * 2)) currentTask.complete = true;
                        else {
                            int mlt = currentTask.target < 0 ? -1 : 1;
                            frontLeft.setPower(pidVeloFrontLeft.getOutput((currentTask.power - angularOutput) * -mlt));
                            frontRight.setPower(pidVeloFrontRight.getOutput((currentTask.power + angularOutput) * mlt));
                            backLeft.setPower(pidVeloBackLeft.getOutput((currentTask.power - angularOutput) * mlt));
                            backRight.setPower(pidVeloBackRight.getOutput((currentTask.power + angularOutput) * -mlt));
                        }
                        break;
                }
                telemetry.update();
            }
        }
        while (opModeIsActive()) {
            telemetry.addData("exited", "---");
            pidVeloBackLeft.run(angularPID.getOutput(targetAngle, 1));
            pidVeloFrontLeft.run(angularPID.getOutput(targetAngle, 1));
            pidVeloBackRight.run(angularPID.getOutput(targetAngle, 1));
            pidVeloFrontRight.run(angularPID.getOutput(targetAngle, 1));
            telemetry.update();
//            pidVeloBackLeft.run(-angularPID.getOutput(targetAngle, 1));
//            pidVeloBackRight.run(angularPID.getOutput(targetAngle, 1));
//            pidVeloFrontLeft.run(-angularPID.getOutput(targetAngle, 1));
//            pidFrontRight.run(angularPID.getOutput(targetAngle, 1));
        }
    }
}