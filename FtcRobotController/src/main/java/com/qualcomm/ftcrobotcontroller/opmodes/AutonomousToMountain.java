package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;

/**
 * Created by Joshua on 11/4/2015.
 */
public class AutonomousToMountain extends LinearOpMode{

    DcMotor leftMotor;
    DcMotor rightMotor;

    @Override
    public void runOpMode() throws InterruptedException {

        leftMotor = hardwareMap.dcMotor.get("leftBackMotor");
        rightMotor = hardwareMap.dcMotor.get("rightFrontMotor");

        leftMotor.setDirection(DcMotor.Direction.REVERSE);
        rightMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);

        telemetry.addData("Right Front Encoder", rightMotor.getCurrentPosition());

        waitForStart();

        rightMotor.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);

        telemetry.addData("Right Front Encoder", rightMotor.getCurrentPosition());

        driveDistance(1000, 0.3F);
        sleep(500);
        driveDistance(4000, 0.3F);

        leftMotor.setPower(0);
        rightMotor.setPower(0);

    }

    void driveDistance(int encoderDistance, float power) {
        rightMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        rightMotor.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        while(rightMotor.getCurrentPosition() < encoderDistance) {
            leftMotor.setPower(power);
            rightMotor.setPower(power);

            telemetry.addData("Right Front Encoder", rightMotor.getCurrentPosition());
        }

        leftMotor.setPower(0);
        rightMotor.setPower(0);
    }

    void turnDistance(int encoderDistance, float power) {
        rightMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        rightMotor.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        while(Math.abs(rightMotor.getCurrentPosition()) < Math.abs(encoderDistance)) {
            if(encoderDistance >= 0) {
                leftMotor.setPower(power);
                rightMotor.setPower(power);
            } else {
                leftMotor.setPower(-power);
                rightMotor.setPower(-power);
            }

        }
    }
}
