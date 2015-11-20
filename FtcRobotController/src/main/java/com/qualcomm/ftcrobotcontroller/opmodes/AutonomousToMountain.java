package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;

/**
 * Created by Joshua on 11/4/2015.
 */
public class AutonomousToMountain extends OpMode {

    DcMotor leftMotor;
    DcMotor rightMotor;

    @Override
    public void init() {
        leftMotor = hardwareMap.dcMotor.get("left");
        rightMotor = hardwareMap.dcMotor.get("right");
        leftMotor.setDirection(DcMotor.Direction.REVERSE);
        rightMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        leftMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
    }

    @Override
    public void start() {
        turnDistance(1000, 0.75F);
    }

    @Override
    public void loop() {
        telemetry.addData("Left", leftMotor.getCurrentPosition());
        telemetry.addData("Right", rightMotor.getCurrentPosition());
    }

    void driveDistance(int encoderDistance, float power) {
        rightMotor.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        leftMotor.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        rightMotor.setTargetPosition(encoderDistance);
        leftMotor.setTargetPosition(encoderDistance);
        rightMotor.setPower(power);
        leftMotor.setPower(power);
    }

    void turnDistance(int encoderDistance, float power) {
        rightMotor.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        leftMotor.setMode(DcMotorController.RunMode.RUN_TO_POSITION);

        leftMotor.setTargetPosition(encoderDistance);
        rightMotor.setTargetPosition(-encoderDistance);
        leftMotor.setPower(power);
        rightMotor.setPower(-power);
    }
}
