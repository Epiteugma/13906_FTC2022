package org.firstinspires.ftc.teamcode.autonomous;

import android.content.Context;
import android.util.Log;

import com.qualcomm.ftccommon.FtcEventLoop;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.z3db0y.flagship.DriveTrain;

import org.firstinspires.ftc.ftccommon.external.OnCreateEventLoop;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;

public class TickBasedNavigator {
    static FtcEventLoop eventLoop;

    Thread pollThread;
    DriveTrain driveTrain;
    double wheelRadius;
    double robotWidth;
    double prevLeftTickAvg = 0;
    double prevRightTickAvg = 0;
    double robotAngle = 90;
    DistanceUnit distanceUnit;
    Position position;

    Runnable tracker = () -> {
        try{
            while(!Thread.currentThread().isInterrupted() && eventLoop.getOpModeManager().getActiveOpMode() != null && ((LinearOpMode) eventLoop.getOpModeManager().getActiveOpMode()).opModeIsActive()) {
                Log.i("RobotState", eventLoop.getOpModeManager().getRobotState().name());
                this.update();
            }
        } catch (Exception ignored) {}
    };

    public boolean isTracking() {
        return pollThread != null && pollThread.isAlive();
    }

    public void startTracking() {
        if(isTracking()) return;
        this.pollThread = new Thread(this.tracker);
        this.pollThread.start();
    }

    public void stopTracking() {
        if(!isTracking()) return;
        pollThread.interrupt();
    }

    private void update() {
        int leftMotorCount = 0;
        int rightMotorCount = 0;
        double leftTickSum = 0;
        double rightTickSum = 0;
        double leftTPRSum = 0;
        double rightTPRSum = 0;

        for(DriveTrain.MotorWithLocation motor : driveTrain.motors) {
            switch(motor.location) {
                case FRONT_LEFT:
                case BACK_LEFT:
                case LEFT:
                    leftTickSum += motor.getCurrentPosition();
                    leftTPRSum += motor.getMotorType().getTicksPerRev();
                    leftMotorCount++;
                    break;
                case FRONT_RIGHT:
                case BACK_RIGHT:
                case RIGHT:
                    rightTickSum += motor.getCurrentPosition();
                    rightTPRSum += motor.getMotorType().getTicksPerRev();
                    rightMotorCount++;
                    break;
            }
        }

        double leftTickAvg = leftTickSum / leftMotorCount;
        double rightTickAvg = rightTickSum / rightMotorCount;

        double leftTPRAvg = leftTPRSum / leftMotorCount;
        double rightTPRAvg = rightTPRSum / rightMotorCount;

        double ticksPerRev = ((leftTPRAvg/leftMotorCount) + (rightTPRAvg/rightMotorCount)) / 2;
        double leftTickDelta = leftTickAvg - prevLeftTickAvg;
        double rightTickDelta = rightTickAvg - prevRightTickAvg;

        double avgTickDelta = (leftTickDelta + rightTickDelta) / 2;
//        robotAngle += (rightTickDelta - leftTickDelta) * ticksPerRev * 90;
        double rotationsPerDegree = (robotWidth * Math.PI) / (2 * Math.PI * wheelRadius) / 360;
        robotAngle += ((leftTickDelta / ticksPerRev) - (rightTickDelta / ticksPerRev)) / rotationsPerDegree;

        Log.i("TickBasedNavigator", "ticksPerRev: " + ticksPerRev);
        Log.i("TickBasedNavigator", "robotAngle: " + robotAngle);
        Log.i("TickBasedNavigator", "leftTickDelta: " + leftTickDelta);
        Log.i("TickBasedNavigator", "rightTickDelta: " + rightTickDelta);

        double xMult = Math.cos(robotAngle);
        double yMult = Math.sin(robotAngle);

        double xDelta = avgTickDelta / ticksPerRev * xMult;
        double yDelta = avgTickDelta / ticksPerRev * yMult;

        prevLeftTickAvg = leftTickAvg;
        prevRightTickAvg = rightTickAvg;

        position.x += xDelta;
        position.y += yDelta;
        position.acquisitionTime = System.nanoTime();
    }

    public TickBasedNavigator(DriveTrain driveTrain, double wheelRadius, double robotWidth, DistanceUnit distanceUnit) {
        this.driveTrain = driveTrain;
        this.wheelRadius = wheelRadius;
        this.robotWidth = robotWidth;
        this.distanceUnit = distanceUnit;

        this.position = new Position(distanceUnit, 0, 0, 0, 0);
    }

    public Position getPosition() {
        return position;
    }

    @OnCreateEventLoop
    public static void attachEventLoop(Context ctx, FtcEventLoop eventLoop) {
        TickBasedNavigator.eventLoop = eventLoop;
    }
}
