package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;

import com.z3db0y.flagship.pid.PIDCoeffs;

@Config
public class HDRIVE_Config {
    public static PIDCoeffs backLeftPID = new PIDCoeffs(1, 0, 0);
    public static PIDCoeffs backRightPID = new PIDCoeffs(1, 0, 0);
    public static PIDCoeffs frontLeftPID = new PIDCoeffs(1, 0, 0);
    public static PIDCoeffs frontRightPID = new PIDCoeffs(1, 0, 0);
}
