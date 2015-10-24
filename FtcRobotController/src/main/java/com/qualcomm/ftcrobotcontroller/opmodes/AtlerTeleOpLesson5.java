package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by Joshua on 10/21/2015.
 */
public class AtlerTeleOpLesson5 extends OpMode {

    // declare motors
    DcMotor leftFrontMotor;
    DcMotor leftBackMotor;
    DcMotor rightFrontMotor;
    DcMotor rightBackMotor;

    @Override
    public void init() {
        // initialize motors
        leftFrontMotor = hardwareMap.dcMotor.get("leftFrontMotor");
        leftBackMotor = hardwareMap.dcMotor.get("leftBackMotor");
        rightFrontMotor = hardwareMap.dcMotor.get("rightFrontMotor");
        rightBackMotor = hardwareMap.dcMotor.get("rightBackMotor");

        // reverse a motor if necessary
        leftFrontMotor.setDirection(DcMotor.Direction.REVERSE);
        leftBackMotor.setDirection(DcMotor.Direction.REVERSE);
        // rightFrontMotor.setDirection(DcMotor.Direction.REVERSE);
        // rightBackMotor.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void loop() {
        // read values from the gamepad
        float leftY = gamepad1.left_stick_y / 2;
        float rightY = gamepad1.right_stick_y / 2;

        // set the power of the motors
        leftFrontMotor.setPower(leftY);
        leftBackMotor.setPower(leftY);
        rightFrontMotor.setPower(rightY);
        rightBackMotor.setPower(rightY);
    }
}
