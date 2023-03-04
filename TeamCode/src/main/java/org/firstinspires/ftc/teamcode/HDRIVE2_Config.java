package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;

import com.z3db0y.flagship.pid.PIDCoeffs;

@Config
public class HDRIVE2_Config extends CommonConfig {
    public static PIDCoeffs backLeftPID = new PIDCoeffs(1, 0, 0);
    public static PIDCoeffs backRightPID = new PIDCoeffs(1, 0, 0);
    public static PIDCoeffs frontLeftPID = new PIDCoeffs(1, 0, 0);
    public static PIDCoeffs frontRightPID = new PIDCoeffs(1, 0, 0);
    public static PIDCoeffs backLeftTickPID = new PIDCoeffs(1, 0, 0);
    public static PIDCoeffs backRightTickPID = new PIDCoeffs(1, 0, 0);
    public static PIDCoeffs frontLeftTickPID = new PIDCoeffs(1, 0, 0);
    public static PIDCoeffs frontRightTickPID = new PIDCoeffs(1, 0, 0);
    public static PIDCoeffs angularPID = new PIDCoeffs(1, 0, 0);

    public PIDCoeffs getBackLeftVeloPID() {
        return backLeftPID;
    }

    public PIDCoeffs getBackRightVeloPID() {
        return backRightPID;
    }

    public PIDCoeffs getFrontLeftVeloPID() {
        return frontLeftPID;
    }

    public PIDCoeffs getFrontRightVeloPID() {
        return frontRightPID;
    }

    public PIDCoeffs getBackLeftTickPID() {
        return backLeftTickPID;
    }

    public PIDCoeffs getBackRightTickPID() {
        return backRightTickPID;
    }

    public PIDCoeffs getFrontLeftTickPID() {
        return frontLeftTickPID;
    }

    public PIDCoeffs getFrontRightTickPID() {
        return frontRightTickPID;
    }

    public PIDCoeffs getAngularPID() {
        return angularPID;
    }
}
