package org.firstinspires.ftc.teamcode.autonomous.blue;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.z3db0y.flagship.Logger;

import org.firstinspires.ftc.teamcode.Enums;
import org.firstinspires.ftc.teamcode.Flags;
import org.firstinspires.ftc.teamcode.autonomous.AutonomousOpMode;

@Flags(robotType = Enums.RobotType.H_DRIVE, alliance = Enums.Alliance.BLUE, startingPosition = Enums.StartPosition.RIGHT)
@Autonomous(name="Blue Right", group="FTC22_Autonomous_Blue")
public class Right extends AutonomousOpMode {
    @Override
    public void run() {
        Logger.setTelemetry(telemetry);
        super.run();
        driveTrain.turn(-90, 0.3, imu);
        Logger.addData("Runmodes:");
        Logger.addData("frontLeft: " + frontLeft.getMode());
        Logger.addData("frontRight: " + frontRight.getMode());
        Logger.addData("backLeft: " + backLeft.getMode());
        Logger.addData("backRight: " + backRight.getMode());
        Logger.addData("frontLeft: ", String.valueOf(frontLeft.getVelocity()));
        Logger.addData("frontRight: ", String.valueOf(frontRight.getVelocity()));
        Logger.addData("backLeft: ", String.valueOf(backLeft.getVelocity()));
        Logger.addData("backRight: ", String.valueOf(backRight.getVelocity()));
        Logger.update();
    }
}
