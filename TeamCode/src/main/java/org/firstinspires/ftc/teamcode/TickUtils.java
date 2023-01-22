package org.firstinspires.ftc.teamcode;

public class TickUtils {
    public static int cmToTicks(double cm, double tpr, double wheelRadius) {
        return (int) (cm / (2 * Math.PI * wheelRadius) * tpr);
    }
}
