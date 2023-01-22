package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.z3db0y.flagship.Motor;

public class TicksToAngles {
    int ticksPerRevolution;
    Motor motor;

    public TicksToAngles(Motor motor, int ticksPerRevolution) {
        this.ticksPerRevolution = ticksPerRevolution;
        this.motor = motor;
    }

    private double normalizeAngle(double angle) {
        while(angle > 180) angle -= 360;
        while(angle < -180) angle += 360;
        return angle;
    }

    public double getAngle() {
        return normalizeAngle(motor.getCurrentPosition() * 360 / ticksPerRevolution);
    }

    public void runTo(double targetAngle, double power) {
        motor.setTargetPosition((int) (normalizeAngle(targetAngle) / 360 * ticksPerRevolution));
        motor.setMode(Motor.RunMode.RUN_TO_POSITION);
        motor.setPower(power);
        Log.i("Init Angle", String.valueOf(targetAngle));
        Log.i("Init TargetPosition", String.valueOf(motor.getTargetPosition()));
        Log.i("Init CurrentPosition", String.valueOf(motor.getCurrentPosition()));
        while(Math.abs(this.getAngle() - targetAngle) > 0) {
            Log.i("Current Angle", String.valueOf(this.getAngle()));
            Log.i("Target Angle", String.valueOf(targetAngle));
            Log.i("TargetPosition", String.valueOf(motor.getTargetPosition()));
            Log.i("CurrentPosition", String.valueOf(motor.getCurrentPosition()));
        }
        Log.i("Ended Position", String.valueOf(motor.getCurrentPosition()));
    }

    public void runToAsync(double angle, double power) {
        motor.setTargetPosition((int) (normalizeAngle(angle) / 360 * ticksPerRevolution));
        motor.setMode(Motor.RunMode.RUN_TO_POSITION);
        motor.setPower(power);
    }
}
