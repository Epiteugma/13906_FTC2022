package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;

import com.z3db0y.flagship.pid.PIDCoeffs;

@Config
public class REVVED_UP_JR_Config extends CommonConfig {
    public static PIDCoeffs backLeftPID           = new PIDCoeffs(1, 0, 0);
    public static PIDCoeffs backRightPID          = new PIDCoeffs(1, 0, 0);
    public static PIDCoeffs frontLeftPID          = new PIDCoeffs(1, 0, 0);
    public static PIDCoeffs frontRightPID         = new PIDCoeffs(1, 0, 0);
    public static PIDCoeffs backLeftTickPID       = new PIDCoeffs(1, 0, 0);
    public static PIDCoeffs backRightTickPID      = new PIDCoeffs(1, 0, 0);
    public static PIDCoeffs frontLeftTickPID      = new PIDCoeffs(1, 0, 0);
    public static PIDCoeffs frontRightTickPID     = new PIDCoeffs(1, 0, 0);
    public static PIDCoeffs angularPID            = new PIDCoeffs(1, 0, 0);
    public static DriveTrain driveTrain           = new DriveTrain(15, 28, 6000, 7.5);
    public static SlideConfig slideConfig         = new SlideConfig(28, 15, 4.75);
    public static ExtensionConfig extensionConfig = new ExtensionConfig(28, 20, 4.75);
    public static ServoConfig leftClawConfig      = new ServoConfig(0.65, 0.94);
    public static ServoConfig rightClawConfig     = new ServoConfig(0.65, 0.94);

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

    public DriveTrain getDrivetrain() {
        return driveTrain;
    }

    public SlideConfig getSlideConfig() {
        return slideConfig;
    }

    public ExtensionConfig getExtensionConfig() {
        return extensionConfig;
    }

    public ServoConfig getLeftClawConfig() {
        return leftClawConfig;
    }

    public ServoConfig getRightClawConfig() {
        return rightClawConfig;
    }
}
