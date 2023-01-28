package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.z3db0y.flagship.DriveTrain;
import com.z3db0y.flagship.Logger;

import org.firstinspires.ftc.teamcode.Enums;
import org.firstinspires.ftc.teamcode.Flags;
import org.firstinspires.ftc.teamcode.TickUtils;
import org.firstinspires.ftc.teamcode.autonomous.AutonomousOpMode;

@Flags(robotType = Enums.RobotType.H_DRIVE, alliance = Enums.Alliance.BLUE, startingPosition = Enums.StartPosition.RIGHT)
@Autonomous(name="Right", group="FTC22_Autonomous")
public class Right extends AutonomousOpMode {

    public void left() {

    }

    public void center() {
        for(int i = 0; i < 2; i++) {
            driveTrain.turn(180, 1, imu, false);
            slideMotors.runToPosition(TickUtils.cmToTicks(15 - (i * 5), 28 * 18, slideGearRadius), 1);
            driveTrain.driveCM(51 +i*2, 0.9, DriveTrain.Direction.FORWARD);
            closeClaw();
            slideMotors.runToPosition(TickUtils.cmToTicks(77 + i*5, 28 * 18, slideGearRadius), 1);
//            slideMotors.runToPositionAsync(TickUtils.cmToTicks(50 + i * 5, 28 * 18, slideGearRadius), 1);
            driveTrain.driveCM(39 + i*2, 0.9, DriveTrain.Direction.BACKWARD);
            driveTrain.turn(-45, 1, imu, false);
            driveTrain.driveCM(15, 1, DriveTrain.Direction.FORWARD);
//            extension.runToPosition(TickUtils.cmToTicks(-22, 288, extensionGearRadius), 1);
            slideMotors.runToPosition(0, 1, false);
            openClaw();
//            extension.runToPosition(0, 1, false);
            driveTrain.driveCM(15, 1, DriveTrain.Direction.BACKWARD);
        }
        driveTrain.driveCM(3, 1, DriveTrain.Direction.BACKWARD);
    }

    public void right() {
//        driveTrain.driveCM(55, 1, DriveTrain.Direction.BACKWARD);
//        releaseRotatingBaseServo(true);
//        driveTrain.strafeCM(55, 1, DriveTrain.Direction.RIGHT);
//        driveTrain.turn(-90, 1, imu, false);
//        driveTrain.driveCM(5, 1, DriveTrain.Direction.FORWARD);
//        slideMotors.runToPosition(TickUtils.cmToTicks(-20, 28 * 18, slideGearRadius), 1);
//        rotatingBaseHelper.runTo(37, 0.6);
//        extension.runToPosition(-TickUtils.cmToTicks(40, 288, extensionGearRadius), 1);
//        closeClaw();
//        slideMotors.runToPosition(TickUtils.cmToTicks(25, 28 * 18, slideGearRadius), 1);
//        rotatingBaseHelper.runTo(-58, 0.6);
//        extension.runToPosition(TickUtils.cmToTicks(25, 288, extensionGearRadius), 1);
//        slideMotors.runToPosition(-TickUtils.cmToTicks(30, 28 * 18, slideGearRadius), 1);
//        openClaw();
//        extension.runToPosition(TickUtils.cmToTicks(4, 288, extensionGearRadius), 1);
//        driveTrain.strafeCM(10, 1, DriveTrain.Direction.RIGHT);
//        driveTrain.turn(-90, 1, imu, false);
    }

    @Override
    public void run() {
        rotatingBaseHelper.runToAsync(0, 1);
        Logger.setTelemetry(telemetry);
        super.run();
        slideMotors.runToPositionAsync(TickUtils.cmToTicks(95, 28 * 18, slideGearRadius), 1);
        driveTrain.strafeCM(160,1, DriveTrain.Direction.RIGHT);
//        extension.runToPosition(TickUtils.cmToTicks(-2, 288, 4.75/2), 1);
        sleep(450);
        slideMotors.runToPosition(TickUtils.cmToTicks(-20, 28 * 18, slideGearRadius), 1);
        slideMotors.runToPositionAsync(TickUtils.cmToTicks(-75, 28 * 18, slideGearRadius), 1);
        encloseClaw();
        driveTrain.driveCM(5, 1, DriveTrain.Direction.BACKWARD);
//        extension.runToPositionAsync(TickUtils.cmToTicks(2, 288, 4.75/2), 1);
        driveTrain.strafeCM(30, 1, DriveTrain.Direction.LEFT);
        switch(parkingPosition) {
            case LEFT:
                left();
                break;
            case CENTER:
                center();
                break;
            case RIGHT:
                right();
                break;
        }
    }
}
