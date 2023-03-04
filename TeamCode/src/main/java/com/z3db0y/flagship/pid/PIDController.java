package com.z3db0y.flagship.pid;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class PIDController {
    private Telemetry telem;
    private String TAG = "";

    double kP = 1;
    double kI = 0;
    double kD = 0;
    int ticksPerRev;
    DcMotor motor;

    double P = 0;
    double I = 0;
    double It = 0;
    double D = 0;
    double prevErr = 0;
    long lastT = 0;
    int lastPos = 0;
    int rpm = 0;

    public PIDController(double kP, double kI, double kD) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
    }

    public PIDController(PIDCoeffs coeffs) {
        this.kP = coeffs.kP;
        this.kI = coeffs.kI;
        this.kD = coeffs.kD;
    }

    public PIDController() {}

    public void setDebug(Telemetry telem, String tag) {
        this.telem = telem;
        this.TAG = tag;
    }

    public PIDController bind(DcMotor motor, int ticksPerRev, int rpm) {
        this.motor = motor;
        this.P = 0;
        this.I = 0;
        this.It = 0;
        this.D = 0;
        this.prevErr = 0;
        this.ticksPerRev = ticksPerRev;
        this.rpm = rpm;
        return this;
    }

    public PIDController updateCoeffs(double kP, double kI, double kD) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        return this;
    }

    public PIDController updateCoeffs(PIDCoeffs coeffs) {
        this.kP = coeffs.kP;
        this.kI = coeffs.kI;
        this.kD = coeffs.kD;
        return this;
    }

    public double getOutput(double power) {
        double dt = System.currentTimeMillis() - this.lastT;
        this.lastT += dt;
        double target = (power * this.ticksPerRev * this.rpm) / 60000 * dt;
        double actual = this.motor.getCurrentPosition() - this.lastPos;
        this.lastPos = this.motor.getCurrentPosition();
        double err = target - actual;
        this.P = this.kP * err;
        this.I = this.kI * err * (dt/1000);
        this.It += this.I;
        this.D = this.kD * (err - this.prevErr) / dt;
        this.prevErr = err;
        double out = this.P + this.It + this.D;
        out /= this.ticksPerRev * this.rpm / 60000.0 * dt;

        if(this.telem != null) {
            this.telem.addData(this.TAG + "/target", target);
            this.telem.addData(this.TAG + "/actual", actual);
            this.telem.addData(this.TAG + "/out", out);
        }

        return out;
    }

    public void run(double power) {
        this.motor.setPower(this.getOutput(power));
    }
}
