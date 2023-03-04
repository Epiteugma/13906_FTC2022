package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;

import com.z3db0y.flagship.pid.PIDCoeffs;

@Config
public class HDRIVE_Config extends CommonConfig {
    public static PIDCoeffs backLeftVeloPID = new PIDCoeffs(1, 0, 0);
    public static PIDCoeffs backRightVeloPID = new PIDCoeffs(1, 0, 0);
    public static PIDCoeffs frontLeftVeloPID = new PIDCoeffs(1, 0, 0);
    public static PIDCoeffs frontRightVeloPID = new PIDCoeffs(1, 0, 0);
    public static PIDCoeffs backLeftTickPID = new PIDCoeffs(1, 0, 0);
    public static PIDCoeffs backRightTickPID = new PIDCoeffs(1, 0, 0);
    public static PIDCoeffs frontLeftTickPID = new PIDCoeffs(1, 0, 0);
    public static PIDCoeffs frontRightTickPID = new PIDCoeffs(1, 0, 0);
    public static PIDCoeffs angularPID = new PIDCoeffs(1, 0, 0);

    public PIDCoeffs getBackLeftVeloPID() {
        return backLeftVeloPID;
    }

    public PIDCoeffs getBackRightVeloPID() {
        return backRightVeloPID;
    }

    public PIDCoeffs getFrontLeftVeloPID() {
        return frontLeftVeloPID;
    }

    public PIDCoeffs getFrontRightVeloPID() {
        return frontRightVeloPID;
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
