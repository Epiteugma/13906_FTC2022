package com.z3db0y.flagship.pid;

import com.qualcomm.hardware.bosch.BNO055IMU;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public class AngularPIDController {
    double kP = 1;
    double kI = 0;
    double kD = 0;

    String TAG;
    Telemetry telem;

    BNO055IMU imu;
    double P = 0;
    double I = 0;
    double It = 0;
    double D = 0;
    long lastT;
    double prevErr;

    public AngularPIDController() {}

    public AngularPIDController(double kP, double kI, double kD) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
    }

    public AngularPIDController(PIDCoeffs coeffs) {
        this.kP = coeffs.kP;
        this.kI = coeffs.kI;
        this.kD = coeffs.kD;
    }

    public void setDebug(Telemetry telem, String tag) {
        this.telem = telem;
        this.TAG = tag;
    }

    public void bind(BNO055IMU imu) {
        this.imu = imu;
        this.P = 0;
        this.I = 0;
        this.It = 0;
        this.D = 0;
        this.lastT = 0;
        this.prevErr = 0;
    }

    double normalizeAngle(double angle) {
        while(angle > 180) angle -= 360;
        while(angle < -180) angle += 360;
        return angle;
    }

    public AngularPIDController updateCoeffs(double kP, double kI, double kD) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        return this;
    }

    public AngularPIDController updateCoeffs(PIDCoeffs coeffs) {
        this.kP = coeffs.kP;
        this.kI = coeffs.kI;
        this.kD = coeffs.kD;
        return this;
    }

    public double getOutput(double target, int angle) {
        double dt = System.currentTimeMillis() - this.lastT;
        this.lastT += dt;
        Orientation orientation = imu.getAngularOrientation();
        double actual = angle == 1 ? orientation.firstAngle : angle == 2 ? orientation.secondAngle : angle == 3 ? orientation.thirdAngle : 0;
        double err =  normalizeAngle(target - actual);
        this.P = this.kP * err;
        this.I = this.kI * err * (dt/1000);
        this.It += this.I;
        this.D = this.kD * (err - this.prevErr) / dt;
        this.prevErr = err;
        double out = this.P + this.It + this.D;
        out /= 45;

        if(this.telem != null) {
            this.telem.addData(this.TAG + "/target", target);
            this.telem.addData(this.TAG + "/actual", actual);
            this.telem.addData(this.TAG + "/out", out);
        }

        return out;
    }
}
