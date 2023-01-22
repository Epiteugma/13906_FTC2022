package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.z3db0y.flagship.DriveTrain;

import org.firstinspires.ftc.teamcode.Enums;
import org.firstinspires.ftc.teamcode.Flags;
import org.firstinspires.ftc.teamcode.TickUtils;

@Flags(robotType = Enums.RobotType.H_DRIVE, alliance = Enums.Alliance.BLUE, startingPosition = Enums.StartPosition.RIGHT)
@Autonomous(name = "Playground", group = "FTC22_Autonomous")
public class Playground extends AutonomousOpMode {

    @Override
    public void run() {
//        rotatingBaseHelper.runTo(-90, 1);
//        sleep(3000);
//        rotatingBaseHelper.runTo(90, 1);
        while(!Thread.currentThread().isInterrupted()) {}
    }

}
