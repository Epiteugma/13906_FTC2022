package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.z3db0y.flagship.DriveTrain;

import org.firstinspires.ftc.teamcode.Enums;
import org.firstinspires.ftc.teamcode.Flags;

@Flags(robotType = Enums.RobotType.REVVED_UP, alliance = Enums.Alliance.BLUE, side = Enums.Side.RIGHT)
@Autonomous(name = "Playground", group = "FTC22_Autonomous")
public class Playground extends AutonomousOpMode {

    @Override
    public void run() {
        openClaw(true);
        driveTrain.strafeCM(100, 1, DriveTrain.Direction.RIGHT);
    }

}
