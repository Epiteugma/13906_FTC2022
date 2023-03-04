package com.z3db0y.flagship.pid;

public class PIDCoeffs {

    public double kP = 1;
    public double kI = 0;
    public double kD = 0;

    public PIDCoeffs(double kP, double kI, double kD) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
    }

    public PIDCoeffs() {}
}
