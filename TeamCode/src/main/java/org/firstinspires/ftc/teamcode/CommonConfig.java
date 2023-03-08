package org.firstinspires.ftc.teamcode;

import com.z3db0y.flagship.pid.PIDCoeffs;

public class CommonConfig {
    public static class ServoConfig {
        public double openPosition;
        public double closedPosition;

        public ServoConfig(double openPosition, double closedPosition) {
            this.openPosition = openPosition;
            this.closedPosition = closedPosition;
        }
    }
    public static class DriveTrain {
        public int gearRatio = 1;
        public int ticksPerRev = 28;
        public int maxRPM = 6000;
        public double wheelDiameterCM;

        public DriveTrain(int gearRatio, int ticksPerRev, int maxRPM, double wheelDiameterCM) {
            this.gearRatio = gearRatio;
            this.ticksPerRev = ticksPerRev;
            this.maxRPM = maxRPM;
            this.wheelDiameterCM = wheelDiameterCM;
        }
    }

    public static class SlideConfig {
        public int ticksPerRev = 28;
        public int gearRatio = 1;
        public double wheelDiameterCM;

        public SlideConfig(int ticksPerRev, int gearRatio, double wheelDiameterCM) {
            this.ticksPerRev = ticksPerRev;
            this.gearRatio = gearRatio;
            this.wheelDiameterCM = wheelDiameterCM;
        }
    }

    public static class ExtensionConfig {
        public int ticksPerRev = 28;
        public int gearRatio = 1;
        public double wheelDiameterCM;

        public ExtensionConfig(int ticksPerRev, int gearRatio, double wheelDiameterCM) {
            this.ticksPerRev = ticksPerRev;
            this.gearRatio = gearRatio;
            this.wheelDiameterCM = wheelDiameterCM;
        }
    }

    public PIDCoeffs getBackLeftVeloPID() {
        return null;
    }

    public PIDCoeffs getBackRightVeloPID() {
        return null;
    }

    public PIDCoeffs getFrontLeftVeloPID() {
        return null;
    }

    public PIDCoeffs getFrontRightVeloPID() {
        return null;
    }

    public PIDCoeffs getBackLeftTickPID() {
        return null;
    }

    public PIDCoeffs getBackRightTickPID() {
        return null;
    }

    public PIDCoeffs getFrontLeftTickPID() {
        return null;
    }

    public PIDCoeffs getFrontRightTickPID() {
        return null;
    }

    public PIDCoeffs getAngularPID() {
        return null;
    }

    public DriveTrain getDrivetrain() {
        return null;
    }

    public SlideConfig getSlideConfig() {
        return null;
    }
    public ExtensionConfig getExtensionConfig() {
        return null;
    }

    public ServoConfig getLeftClawConfig() {
        return null;
    }

    public ServoConfig getRightClawConfig() {
        return null;
    }
}
