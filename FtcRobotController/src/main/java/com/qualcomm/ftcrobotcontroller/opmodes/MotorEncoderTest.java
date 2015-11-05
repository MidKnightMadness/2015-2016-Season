package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;

/**
 * Created by Joshua on 11/4/2015.
 */
public class MotorEncoderTest extends LinearOpMode{

    DcMotor leftFrontMotor;
    DcMotor leftBackMotor;
    DcMotor rightFrontMotor;
    DcMotor rightBackMotor;

    @Override
    public void runOpMode() throws InterruptedException {

        leftFrontMotor = hardwareMap.dcMotor.get("leftFrontMotor");
        leftBackMotor = hardwareMap.dcMotor.get("leftBackMotor");
        rightFrontMotor = hardwareMap.dcMotor.get("rightFrontMotor");
        rightBackMotor = hardwareMap.dcMotor.get("rightBackMotor");

        leftFrontMotor.setDirection(DcMotor.Direction.REVERSE);
        leftBackMotor.setDirection(DcMotor.Direction.REVERSE);

        leftFrontMotor.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);

        waitForStart();

        while(leftFrontMotor.getCurrentPosition() < 500) {
            leftFrontMotor.setPower(0.5);
            leftBackMotor.setPower(0.5);
            rightFrontMotor.setPower(0.5);
            rightBackMotor.setPower(0.5);

            telemetry.addData("Left Front Encoder", leftFrontMotor.getCurrentPosition());
        }

        leftFrontMotor.setPower(0);
        leftBackMotor.setPower(0);
        rightFrontMotor.setPower(0);
        rightBackMotor.setPower(0);


    }
}
