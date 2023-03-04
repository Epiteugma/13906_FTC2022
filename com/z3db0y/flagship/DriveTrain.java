package com.z3db0y.flagship;


import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorImplEx;

import java.util.Arrays;
import java.util.stream.Collectors;

public class DriveTrain {
    MotorWithLocation[] motors;
    Type type;

    public enum Type {
        MECANUM(new int[]{4}, new MotorWithLocation.Location[]{
            MotorWithLocation.Location.FRONT_LEFT,
            MotorWithLocation.Location.FRONT_RIGHT,
            MotorWithLocation.Location.BACK_LEFT,
            MotorWithLocation.Location.BACK_RIGHT
        }),
        TANK(new int[]{ 2, 4, 6, 8 }, new MotorWithLocation.Location[]{
            MotorWithLocation.Location.LEFT,
            MotorWithLocation.Location.RIGHT
        }),
        X_DRIVE(new int[]{4}, new MotorWithLocation.Location[]{
            MotorWithLocation.Location.FRONT_LEFT,
            MotorWithLocation.Location.FRONT_RIGHT,
            MotorWithLocation.Location.BACK_LEFT,
            MotorWithLocation.Location.BACK_RIGHT
        });

        public final int[] validMotorCounts;
        public final MotorWithLocation.Location[] motorLocations;

        Type(int[] validMotorCounts, MotorWithLocation.Location[] validLocations) {
            this.validMotorCounts = validMotorCounts;
            this.motorLocations = validLocations;
        }
    }

    public static class MotorWithLocation extends Motor {
        public Location location;

        public enum Location {
            FRONT_LEFT,
            FRONT_RIGHT,
            BACK_LEFT,
            BACK_RIGHT,
            LEFT,
            RIGHT;
        }

        public MotorWithLocation(DcMotorImplEx motor, Location location) {
            super(motor);
            this.location = location;
        }
    }

    public DriveTrain(@NonNull Type type, @NonNull MotorWithLocation[] motors) {
        this.type = type;
        this.motors = motors;

        for(int num : type.validMotorCounts) {
		if(motors.length == num) {
			for(MotorWithLocation motor : motors) {
                if(!Arrays.asList(type.motorLocations).contains(motor.location)) {
                    throw new IllegalArgumentException("Invalid motor location");
                }
            }
            return;
		}
	}
        throw new IllegalArgumentException("Invalid motor locations for drive train type");
    }

    public void driveRobotCentric(double forwardPower, double sidePower, double strafePower) {
        for(MotorWithLocation motor : this.motors) {
            double resultantPower = 0;
            switch(motor.location) {
                case FRONT_LEFT:
                    resultantPower = forwardPower - sidePower - strafePower;
                    break;
                case FRONT_RIGHT:
                    resultantPower = forwardPower + sidePower + strafePower;
                    break;
                case BACK_LEFT:
                    resultantPower = forwardPower - sidePower + strafePower;
                    break;
                case BACK_RIGHT:
                    resultantPower = forwardPower + sidePower - strafePower;
                    break;
                case LEFT:
                    resultantPower = forwardPower - sidePower;
                    break;
                case RIGHT:
                    resultantPower = forwardPower + sidePower;
                    break;
            }
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor.setPower(resultantPower);
        }
    }

    // TODO: maybe add a drive field centric method
}
