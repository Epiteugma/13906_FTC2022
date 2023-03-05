package org.firstinspires.ftc.teamcode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Flags {
    Enums.RobotType robotType();
    Enums.Alliance alliance() default Enums.Alliance.RED;
    Enums.Side side() default Enums.Side.LEFT;
    boolean debug() default false;
}
