package com.qualcomm.ftcrobotcontroller.common;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Motor group annotation for grouping multiple motors together.
 * <br>
 * Useful for running all drivetrain motors together
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface MotorGroup {

    String value();
}
