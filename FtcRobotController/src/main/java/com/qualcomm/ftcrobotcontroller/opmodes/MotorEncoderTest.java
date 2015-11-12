package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;

/**
 * Created by Joshua on 11/4/2015.
 */
public class MotorEncoderTest extends LinearOpMode{

    //DcMotor leftFrontMotor;
    DcMotor leftBackMotor;
    DcMotor rightFrontMotor;
    //DcMotor rightBackMotor;

    @Override
    public void runOpMode() throws InterruptedException {

        //leftFrontMotor = hardwareMap.dcMotor.get("leftFrontMotor");
        leftBackMotor = hardwareMap.dcMotor.get("leftBackMotor");
        rightFrontMotor = hardwareMap.dcMotor.get("rightFrontMotor");
        //rightBackMotor = hardwareMap.dcMotor.get("rightBackMotor");

        //leftFrontMotor.setDirection(DcMotor.Direction.REVERSE);
        leftBackMotor.setDirection(DcMotor.Direction.REVERSE);

        rightFrontMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);

        telemetry.addData("Right Front Encoder", rightFrontMotor.getCurrentPosition());

        waitForStart();

        rightFrontMotor.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);

        telemetry.addData("Right Front Encoder", rightFrontMotor.getCurrentPosition());

        while(rightFrontMotor.getCurrentPosition() < 1000) {
            //leftFrontMotor.setPower(0.3);
            leftBackMotor.setPower(0.3);
            rightFrontMotor.setPower(0.3);
            //rightBackMotor.setPower(0.3);

            telemetry.addData("Right Front Encoder", rightFrontMotor.getCurrentPosition());
        }

        //leftFrontMotor.setPower(0);
        leftBackMotor.setPower(0);
        rightFrontMotor.setPower(0);
        //rightBackMotor.setPower(0);


    }
}
