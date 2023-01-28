package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.z3db0y.flagship.DriveTrain;
import com.z3db0y.flagship.Motor;

import org.firstinspires.ftc.teamcode.Enums;
import org.firstinspires.ftc.teamcode.Flags;
import org.firstinspires.ftc.teamcode.TickUtils;

@Flags(robotType = Enums.RobotType.H_DRIVE, alliance = Enums.Alliance.BLUE, startingPosition = Enums.StartPosition.RIGHT)
@Autonomous(name = "Playground", group = "FTC22_Autonomous")
public class Playground extends AutonomousOpMode {

    @Override
    public void run() {
        openClaw(true);
        driveTrain.strafeCM(100, 1, DriveTrain.Direction.RIGHT);
    }

}
