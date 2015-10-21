package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by Joshua on 10/21/2015.
 */
public class AtlerTeleOpLesson5 extends OpMode {

    // declare motors
    DcMotor leftMotor;
    DcMotor rightMotor;

    @Override
    public void init() {
        // initialize motors
        leftMotor = hardwareMap.dcMotor.get("leftMotor");
        rightMotor = hardwareMap.dcMotor.get("rightMotor");

        // reverse a motor if necessary
        // leftMotor.setDirection(DcMotor.Direction.REVERSE);
        // rightMotor.setDirection(DcMotor.Directeion.REVERSE);
    }

    @Override
    public void loop() {
        // read values from the gamepad
        float leftY = gamepad1.left_stick_y;
        float rightY = gamepad1.right_stick_x;

        // set the power of the motors
        leftMotor.setPower(leftY);
        rightMotor.setPower(rightY);
    }
}
