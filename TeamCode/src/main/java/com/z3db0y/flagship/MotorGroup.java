package com.z3db0y.flagship;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import java.util.ArrayList;
import java.util.Arrays;

public class MotorGroup {
    private ArrayList<Motor> motors;

    public MotorGroup(Motor... motors) {
        this.motors = new ArrayList<>(Arrays.asList(motors));
    }

    public MotorGroup() {
        this.motors = new ArrayList<>();
    }

    public void addMotor(Motor motor) {
        motors.add(motor);
    }

    public void removeMotor(Motor motor) {
        motors.remove(motor);
    }

    public void setPower(double power) {
        for(Motor motor : motors) {
            motor.setPower(power);
        }
    }

    public void setVelocity(double velocity) {
        for(Motor motor : motors) {
            motor.setVelocity(velocity);
        }
    }

    public void setVelocity(double velocity, AngleUnit unit) {
        for(Motor motor : motors) {
            motor.setVelocity(velocity, unit);
        }
    }

    public void setTargetPosition(int position) {
        for(Motor motor : motors) {
            motor.setTargetPosition(position);
        }
    }

    public void setMode(DcMotor.RunMode mode) {
        for(Motor motor : motors) {
            motor.setMode(mode);
        }
    }

    public void setHoldPosition(boolean holdPosition) {
        for(Motor motor : motors) {
            motor.setHoldPosition(holdPosition);
        }
    }

    public void setDirection(DcMotor.Direction direction) {
        for(Motor motor : motors) {
            motor.setDirection(direction);
        }
    }

    public void setZeroPowerBehavior(DcMotor.ZeroPowerBehavior behavior) {
        for(Motor motor : motors) {
            motor.setZeroPowerBehavior(behavior);
        }
    }

    public void runToPosition(int position, double power) {
        for(Motor motor : motors) motor.runToPositionAsync(position, power);
        for (Motor motor : motors) {
            while(!motor.atTargetPosition()) {}
        }
    }

    public void runToPositionAsync(int position, double power) {
        for(Motor motor : motors) {
            motor.runToPositionAsync(position, power);
        }
    }

    public int getTargetPosition() {
        int st = 0;
        int c = 0;
        for(Motor motor : motors) {
            st += motor.getTargetPosition();
            c++;
        }
        return st/c;
    }
}
