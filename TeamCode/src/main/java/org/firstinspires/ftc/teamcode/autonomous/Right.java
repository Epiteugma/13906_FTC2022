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

    }

    public void right() {

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
