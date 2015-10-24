package com.qualcomm.ftcrobotcontroller.common;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * OpMode that provides helper functions for various utilities such as grouping motors
 */
public abstract class TKAOpmode extends OpMode {

    private HashMap<String, ArrayList<DcMotor>> motorGroups = new HashMap<String, ArrayList<DcMotor>>();

    public abstract void initialize();

    @Override
    public void init() {
        populateMotors();
        populateMotorGroups();
        initialize();
    }

    /**
     * Populates all of the declared {@link com.qualcomm.robotcore.hardware.DcMotor DcMotors} and
     * all the declared {@link com.qualcomm.robotcore.hardware.Servo Servos} in the current class
     */
    private void populateMotors() {
        try {
            Field[] allFields = this.getClass().getDeclaredFields();
            for (Field f : allFields) {
                if (f.getDeclaringClass() == DcMotor.class) {
                    f.set(this, hardwareMap.dcMotor.get(f.getName().toLowerCase()));
                } else if (f.getDeclaringClass() == Servo.class) {
                    f.set(this, hardwareMap.servo.get(f.getName().toLowerCase()));
                }
            }
        } catch (Exception e) {
            // An error occurred. We should do something about this
        }
    }

    /**
     * Populate the {@link com.qualcomm.ftcrobotcontroller.common.TKAOpmode#motorGroups} hashmap
     * with motors for use in the {@link com.qualcomm.ftcrobotcontroller.common.TKAOpmode#runMotorGroup(String, double)}
     * and {@link com.qualcomm.ftcrobotcontroller.common.TKAOpmode#stopMotorGroup(String)}
     */
    private void populateMotorGroups() {
        try {
            Field[] allFields = this.getClass().getDeclaredFields();
            for (Field f : allFields) {

                if (f.getDeclaringClass() == DcMotor.class) {
                    MotorGroup mg;
                    if ((mg = f.getAnnotation(MotorGroup.class)) != null) {
                        String motorGroup = mg.value();
                        ArrayList<DcMotor> motors = this.motorGroups.get(motorGroup);
                        if (motors == null)
                            motors = new ArrayList<DcMotor>();
                        motors.add((DcMotor) f.get(this));
                    }
                }
            }
        } catch (Exception e) {
            // An error occurred. We should do something about this
        }
    }

    /**
     * Runs the motor with the {@link com.qualcomm.ftcrobotcontroller.common.MotorGroup}
     *
     * @param motorGroup The motor group to run
     * @param power      The power to set
     */
    protected void runMotorGroup(String motorGroup, double power) {
        ArrayList<DcMotor> motors = this.motorGroups.get(motorGroup);
        if (motors == null)
            return;
        for (DcMotor m : motors) {
            m.setPower(power);
        }
    }

    /**
     * Stops all the motors in the given motor group
     *
     * @param motorGroup The motor group to stop
     */
    protected void stopMotorGroup(String motorGroup) {
        runMotorGroup(motorGroup, 0);
    }
}
