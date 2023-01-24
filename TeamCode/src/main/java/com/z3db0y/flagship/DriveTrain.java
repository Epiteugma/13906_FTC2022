package com.z3db0y.flagship;


import android.util.Log;

import androidx.annotation.NonNull;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorImplEx;
import org.firstinspires.ftc.teamcode.autonomous.vision.vuforia.VuforiaTracker;

import java.util.Arrays;

public class DriveTrain {
    public MotorWithLocation[] motors;
    Type type;
    double ticksPerRevolution;
    double wheelDiameter;
    double gearRatio;
    CorrectionHandler handler;
    public interface CorrectionHandler {
        void correct();
    }

    public void setCorrectionHandler(CorrectionHandler handler) {
        this.handler = handler;
    }

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

    public DriveTrain(@NonNull Type type, @NonNull MotorWithLocation[] motors, double wheelDiameter, double gearRatio) {
        this.type = type;
        this.motors = motors;
        this.wheelDiameter = wheelDiameter;
        this.gearRatio = gearRatio;
        this.ticksPerRevolution = 28 * gearRatio;

        for (int num : type.validMotorCounts) {
            if (motors.length == num) {
                for (MotorWithLocation motor : motors) {
                    if (!Arrays.asList(type.motorLocations).contains(motor.location)) {
                        throw new IllegalArgumentException("Invalid motor location");
                    }
                    motor.getMotorType().setTicksPerRev(ticksPerRevolution);
                }
                return;
            }
        }
        throw new IllegalArgumentException("Invalid motor locations for drive train type");
    }

    public void runTicks(int left, int right, double velocity) {
        for (MotorWithLocation motor : this.motors) {
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
            Logger.addData("Setting motor " + motor.location + " ticks to " + motor.getTargetPosition());
            motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            motor.setVelocity(motor.getMotorType().getAchieveableMaxTicksPerSecond() * velocity);
        }
        Logger.update();
        while(this.isBusy()){
            if(handler != null) handler.correct();
            for (MotorWithLocation motor : this.motors) {
                Logger.addData("Motor " + motor.location + " is at " + motor.getCurrentPosition() + " ticks");
                Logger.update();
            }
        }
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
            Logger.addData("Setting motor " + motor.location + " velocity to " + motor.getVelocity());
        }
        Logger.update();
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
            Logger.addData("Setting motor power of " + motor.location + " to " + motor.getPower());
        }
        Logger.update();
    }

    public boolean isBusy() {
        for (MotorWithLocation motor : this.motors) {
            if (motor.isBusy()) {
                int ticks = motor.getCurrentPosition();
                Logger.addData("DriveTrain", "Motor " + motor.location + " is busy at " + ticks + " ticks");
                return true;
            }
        }
        Logger.update();
        return false;
    }

    public void driveVuforia(double distance, double robotAngle, double velocity, VuforiaTracker vuforia) {
        for (MotorWithLocation motor : this.motors) {
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
        double dx = Math.asin(robotAngle) * distance;
        double dy = Math.acos(robotAngle) * distance;
        float[] target = vuforia.getLocation();
        target[0] += dx;
        target[1] += dy;
        float[] current = vuforia.getLocation();
        while(Math.abs(target[0] - current[0]) > 1 || Math.abs(target[1] - current[1]) > 1) {
            current = vuforia.getLocation();
            for (MotorWithLocation motor : this.motors) {
                switch (motor.location) {
                    case LEFT:
                    case BACK_LEFT:
                    case FRONT_LEFT:
                        motor.setVelocity(velocity * (target[0] - current[0]));
                        break;
                    case RIGHT:
                    case BACK_RIGHT:
                    case FRONT_RIGHT:
                        motor.setVelocity(velocity * (target[1] - current[1]));
                        break;
                }
            }
        }
        for (MotorWithLocation motor : this.motors) {
            motor.setVelocity(0);
        }
    }

    public void driveRobotCentric(double forwardVelo, double turnVelo, double strafeVelo) {
        for (MotorWithLocation motor : this.motors) {
            double resultantVelo = 0;
            switch (motor.location) {
                case FRONT_LEFT:
                    resultantVelo = forwardVelo - turnVelo - strafeVelo;
                    break;
                case FRONT_RIGHT:
                    resultantVelo = forwardVelo + turnVelo + strafeVelo;
                    break;
                case BACK_LEFT:
                    resultantVelo = forwardVelo - turnVelo + strafeVelo;
                    break;
                case BACK_RIGHT:
                    resultantVelo = forwardVelo + turnVelo - strafeVelo;
                    break;
                case LEFT:
                    resultantVelo = forwardVelo - turnVelo;
                    break;
                case RIGHT:
                    resultantVelo = forwardVelo + turnVelo;
                    break;
                case FRONT:
                    resultantVelo = -strafeVelo - turnVelo;
                    break;
                case BACK:
                    resultantVelo = -strafeVelo + turnVelo;
                    break;
            }
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor.setVelocity(motor.getMotorType().getAchieveableMaxTicksPerSecond() * resultantVelo);
            Logger.addData("Setting motor velocity" + motor.location + " to " + motor.getVelocity());
        }
        Logger.update();
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
        int ticks = (int) (cm / wheelCirc * ticksPerRevolution);
        Logger.addData("DriveTrain", "Driving " + cm + " cm at " + velocity + " velocity");
        Logger.update();
        drive(ticks, velocity, direction);
        Logger.addData("DriveTrain", "Done driving " + cm + " cm at " + velocity + " velocity");
        Logger.update();
    }

    public void strafe(int ticks, double velocity, Direction direction) {
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
        for(MotorWithLocation motor : motors) {
            motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            motor.setVelocity(motor.getMotorType().getAchieveableMaxTicksPerSecond() * velocity);
        }
        while(this.isBusy()) {

        }
        for(MotorWithLocation motor : motors) {
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor.setVelocity(0);
        }
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

    public void turn(double target, double velocity, BNO055IMU imu) {
        double startAngle = imu.getAngularOrientation().firstAngle;
        target = normalizeAngle(startAngle + target);
        int direction = startAngle - target > 0 ? 1 : -1;
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
            Logger.addData("DriveTrain", "Turning to " + target + " at " + velocity + " velocity");
            Logger.addData("DriveTrain", "Current angle: " + current);
            Logger.addData("DriveTrain", "Error: " + error);
            Logger.addData("DriveTrain", "Turn: " + turnVelo);
            Logger.update();
        }
        driveRobotCentric(0, 0, 0);
        Logger.addData("DriveTrain", "Turned to " + target + " at " + velocity + " velocity");
        Logger.update();
    }

    // TODO: maybe add a drive field centric method
    public void driveFieldCentric(double forwardVelo, double turnVelo, double strafeVelo, double angle) {
        double[] velocities = new double[4];
        velocities[0] = forwardVelo - turnVelo - strafeVelo;
        velocities[1] = forwardVelo + turnVelo + strafeVelo;
        velocities[2] = forwardVelo - turnVelo + strafeVelo;
        velocities[3] = forwardVelo + turnVelo - strafeVelo;

        double max = Arrays.stream(velocities).max().getAsDouble();
        double min = Arrays.stream(velocities).min().getAsDouble();

        if (max > 1 || min < -1) {
            double scale = 1 / Math.max(Math.abs(max), Math.abs(min));
            for (int i = 0; i < velocities.length; i++) {
                velocities[i] *= scale;
            }
        }

        for (int i = 0; i < velocities.length; i++) {
            velocities[i] *= Math.cos(angle);
        }

        for (MotorWithLocation motor : this.motors) {
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor.setVelocity(motor.getMotorType().getAchieveableMaxTicksPerSecond() * velocities[motor.location.ordinal()]);
        }
    }
}
