package com.z3db0y.flagship;


import android.util.Log;

import androidx.annotation.NonNull;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorImplEx;
import org.firstinspires.ftc.teamcode.autonomous.vision.vuforia.VuforiaTracker;

import java.util.ArrayList;
import java.util.Arrays;

public class DriveTrain {
    public MotorWithLocation[] motors;
    Type type;
    double ticksPerRevolution;
    double wheelDiameter;
    double gearRatio;

    public enum Direction {
        FORWARD,
        BACKWARD,
        LEFT,
        RIGHT
    }

    public enum Type {
        MECANUM(new int[]{4}, new MotorWithLocation.Location[]{
                MotorWithLocation.Location.FRONT_LEFT,
                MotorWithLocation.Location.FRONT_RIGHT,
                MotorWithLocation.Location.BACK_LEFT,
                MotorWithLocation.Location.BACK_RIGHT
        }),
        TANK(new int[]{2, 4, 6, 8}, new MotorWithLocation.Location[]{
                MotorWithLocation.Location.LEFT,
                MotorWithLocation.Location.RIGHT
        }),
        X_DRIVE(new int[]{4}, new MotorWithLocation.Location[]{
                MotorWithLocation.Location.LEFT,
                MotorWithLocation.Location.RIGHT,
                MotorWithLocation.Location.FRONT,
                MotorWithLocation.Location.BACK
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
            RIGHT,
            FRONT,
            BACK;
        }

        public MotorWithLocation(DcMotorImplEx motor, Location location) {
            super(motor);
            this.location = location;
        }
    }

    public DriveTrain(@NonNull Type type, @NonNull MotorWithLocation[] motors, double wheelDiameter, int ticksPerRevolution, int gearRatio) {
        this.type = type;
        this.motors = motors;
        this.wheelDiameter = wheelDiameter;
        this.gearRatio = gearRatio;
        this.ticksPerRevolution = ticksPerRevolution * gearRatio;

        for (int num : type.validMotorCounts) {
            if (motors.length == num) {
                for (MotorWithLocation motor : motors) {
                    if (!Arrays.asList(type.motorLocations).contains(motor.location)) {
                        throw new IllegalArgumentException("Invalid motor location");
                    }
                }
                return;
            }
        }
        throw new IllegalArgumentException("Invalid motor locations for drive train type");
    }

    public void runTicks(int left, int right, double power) {
        for (MotorWithLocation motor : this.motors) {
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            switch (motor.location) {
                case LEFT:
                case BACK_LEFT:
                case FRONT_LEFT:
                    motor.setRelativeTargetPosition(left);
                    break;
                case RIGHT:
                case BACK_RIGHT:
                case FRONT_RIGHT:
                    motor.setRelativeTargetPosition(right);
                    break;
            }
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
        for(Motor motor : motors) motor.setPower((motor.getTargetPosition() < 0 ? -1 : 1) * power);
        while(this.isBusy()) {}
        for(Motor motor : motors) motor.setPower(0);
    }

    public void setVelocity(double fr, double fl, double br, double bl) {
        for (MotorWithLocation motor : this.motors) {
            switch (motor.location) {
                case FRONT_RIGHT:
                    motor.setVelocity(fr);
                    break;
                case FRONT_LEFT:
                    motor.setVelocity(fl);
                    break;
                case BACK_RIGHT:
                    motor.setVelocity(br);
                    break;
                case BACK_LEFT:
                    motor.setVelocity(bl);
                    break;
            }
//            Logger.addData("Setting motor " + motor.location + " velocity to " + motor.getVelocity());
        }
//        Logger.update();
    }

    public void setPower(double fr, double fl, double br, double bl) {
        for (MotorWithLocation motor : this.motors) {
            switch (motor.location) {
                case FRONT_RIGHT:
                    motor.setPower(fr);
                    break;
                case FRONT_LEFT:
                    motor.setPower(fl);
                    break;
                case BACK_RIGHT:
                    motor.setPower(br);
                    break;
                case BACK_LEFT:
                    motor.setPower(bl);
                    break;
            }
//            Logger.addData("Setting motor power of " + motor.location + " to " + motor.getPower());
        }
//        Logger.update();
    }

    public boolean isBusy() {
        int tickSum = 0;
        int targetTickSum = 0;
        int allowedError = 3;
        for (int i = 0; i < motors.length; i++) {
            tickSum += Math.abs(motors[i].getCurrentPosition());
            targetTickSum += Math.abs(motors[i].getTargetPosition());
        }
        return Math.abs((tickSum/this.motors.length) - (targetTickSum/this.motors.length)) > allowedError;
    }

    public void driveRobotCentric(double forward, double turn, double strafe) {
        for (MotorWithLocation motor : this.motors) {
            double result = 0;
            switch (motor.location) {
                case FRONT_LEFT:
                    result = forward - turn - strafe;
                    break;
                case FRONT_RIGHT:
                    result = forward + turn + strafe;
                    break;
                case BACK_LEFT:
                    result = forward - turn + strafe;
                    break;
                case BACK_RIGHT:
                    result = forward + turn - strafe;
                    break;
                case LEFT:
                    result = forward - turn;
                    break;
                case RIGHT:
                    result = forward + turn;
                    break;
                case FRONT:
                    result = -strafe - turn;
                    break;
                case BACK:
                    result = -strafe + turn;
                    break;
            }
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor.setPower(result);
//            Logger.addData("Setting motor velocity" + motor.location + " to " + motor.getVelocity());
        }
//        Logger.update();
    }

    // KEEP THE SAME HEADING ANGLE
    public void drive(int ticks, double velocity, Direction direction) {
        int left = 0;
        int right = 0;
        switch (direction) {
            case FORWARD:
                left = -ticks;
                right = -ticks;
                break;
            case BACKWARD:
                left = ticks;
                right = ticks;
                break;
        }
        runTicks(left, right, velocity);
    }

    public void driveCM(double cm, double velocity, Direction direction) {
        double wheelCirc = Math.PI * wheelDiameter;
        int ticks = (int) ((cm / wheelCirc) * ticksPerRevolution);
//        Logger.addData("DriveTrain", "Driving " + cm + " cm at " + velocity + " velocity");
//        Logger.update();
        drive(ticks, velocity, direction);
//        Logger.addData("DriveTrain", "Done driving " + cm + " cm at " + velocity + " velocity");
//        Logger.update();
    }

    public void strafe(int ticks, double power, Direction direction) {
        for(Motor motor : motors) motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        for(Motor motor : motors) motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        switch (direction) {
            case LEFT:
                motors[0].setRelativeTargetPosition(-ticks);
                motors[1].setRelativeTargetPosition(ticks);
                motors[2].setRelativeTargetPosition(ticks);
                motors[3].setRelativeTargetPosition(-ticks);

                break;
            case RIGHT:
                motors[0].setRelativeTargetPosition(ticks);
                motors[1].setRelativeTargetPosition(-ticks);
                motors[2].setRelativeTargetPosition(-ticks);
                motors[3].setRelativeTargetPosition(ticks);
                break;
        }
        for(Motor motor : motors) motor.setPower((motor.getTargetPosition() < 0 ? -1 : 1) * power);
        while(this.isBusy()) {}
        for(Motor motor : motors) motor.setPower(0);
    }

    public void strafeCM(double cm, double velocity, Direction direction) {
        int ticks = (int) (cm / (Math.PI * wheelDiameter) * ticksPerRevolution);
        Logger.addData("DriveTrain", "Strafing " + cm + " cm at " + velocity + " velocity");
        strafe(ticks, velocity, direction);
    }

    public double normalizeAngle(double angle) {
        while (angle > 180) {
            angle -= 360;
        }
        while (angle < -180) {
            angle += 360;
        }
        return angle;
    }

    public void turn(double target, double velocity, BNO055IMU imu, boolean relative) {
        double startAngle = imu.getAngularOrientation().firstAngle;
        target = normalizeAngle((relative ? startAngle : 0) + target);
        int direction = normalizeAngle(startAngle - target) > 0 ? 1 : -1;
        double current = normalizeAngle(imu.getAngularOrientation().firstAngle);
        double error = normalizeAngle(target - current);
        double turnVelo = Math.abs(error)/180 * velocity * direction;
        if(Math.abs(turnVelo) < 0.1 * velocity) turnVelo = 0.1 * velocity * direction;

        while (Math.abs(turnVelo) > 0.1 * velocity) {
            current = normalizeAngle(imu.getAngularOrientation().firstAngle);
            error = normalizeAngle(target - current);
            turnVelo = Math.abs(error)/45 * velocity * direction;
            if(Math.abs(turnVelo) < 0.1 * velocity) turnVelo = 0.1 * velocity * direction;
            driveRobotCentric(0, turnVelo, 0);
//            Logger.addData("DriveTrain", "Turning to " + target + " at " + velocity + " velocity");
//            Logger.addData("DriveTrain", "Current angle: " + current);
//            Logger.addData("DriveTrain", "Error: " + error);
//            Logger.addData("DriveTrain", "Turn: " + turnVelo);
//            Logger.update();
        }
        driveRobotCentric(0, 0, 0);
//        Logger.addData("DriveTrain", "Turned to " + target + " at " + velocity + " velocity");
//        Logger.update();
    }

    public void turn(double target, double velocity, BNO055IMU imu) {
        this.turn(target, velocity, imu, true);
    }

    // TODO: maybe add a drive field centric method
    public void driveFieldCentric(double forwardVelo, double turnVelo, double strafeVelo, double angle, double addAngle) {
        double gamepadAngle = normalizeAngle(Math.toDegrees(Math.atan2(-forwardVelo, strafeVelo)) - 90);
        Log.i("gamepadAngle", String.valueOf(gamepadAngle));
        double transformAngle = normalizeAngle(gamepadAngle - angle + addAngle);
        Log.i("transformAngle", String.valueOf(transformAngle));
        double pow = Math.sqrt(Math.pow(forwardVelo, 2) + Math.pow(strafeVelo, 2));
        forwardVelo = -Math.cos(Math.toRadians(transformAngle)) * pow;
        strafeVelo = -Math.sin(Math.toRadians(transformAngle)) * pow;
        this.driveRobotCentric(forwardVelo, turnVelo, strafeVelo);
    }

    public void driveFieldCentric(double forwardVelo, double turnVelo, double strafeVelo, double angle) {
        this.driveFieldCentric(forwardVelo, turnVelo, strafeVelo, angle, 0);
    }
}
