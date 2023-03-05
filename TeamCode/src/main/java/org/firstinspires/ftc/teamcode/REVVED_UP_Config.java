package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;

import com.z3db0y.flagship.pid.PIDCoeffs;

@Config
public class REVVED_UP_Config extends CommonConfig {
    public static PIDCoeffs backLeftVeloPID       = new PIDCoeffs(1, 0, 0);
    public static PIDCoeffs backRightVeloPID      = new PIDCoeffs(1, 0, 0);
    public static PIDCoeffs frontLeftVeloPID      = new PIDCoeffs(1, 0, 0);
    public static PIDCoeffs frontRightVeloPID     = new PIDCoeffs(1, 0, 0);
    public static PIDCoeffs backLeftTickPID       = new PIDCoeffs(1, 0, 0);
    public static PIDCoeffs backRightTickPID      = new PIDCoeffs(1, 0, 0);
    public static PIDCoeffs frontLeftTickPID      = new PIDCoeffs(1, 0, 0);
    public static PIDCoeffs frontRightTickPID     = new PIDCoeffs(1, 0, 0);
    public static PIDCoeffs angularPID            = new PIDCoeffs(1, 0, 0);
    public static DriveTrain driveTrain           = new DriveTrain(12, 28, 6000, 7.5);
    public static SlideConfig slideConfig         = new SlideConfig(28, 15, 4.75);
    public static ExtensionConfig extensionConfig = new ExtensionConfig(28, 20, 4.75);
    public static ServoConfig leftClawConfig      = new ServoConfig(0.65, 0.94);
    public static ServoConfig rightClawConfig     = new ServoConfig(0.65, 0.94);

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
