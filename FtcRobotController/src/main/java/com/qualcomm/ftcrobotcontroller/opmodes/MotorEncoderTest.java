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

        driveDistance(1000, 0.3F);

        sleep(500);

        driveDistance(4000, 0.3F);

        //leftFrontMotor.setPower(0);
        leftBackMotor.setPower(0);
        rightFrontMotor.setPower(0);
        //rightBackMotor.setPower(0);


    }

    void driveDistance(int encoderDistance, float power) {
        rightFrontMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        rightFrontMotor.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        while(rightFrontMotor.getCurrentPosition() < encoderDistance) {
            //leftFrontMotor.setPower(power);
            leftBackMotor.setPower(power);
            rightFrontMotor.setPower(power);
            //rightBackMotor.setPower(power);

            telemetry.addData("Right Front Encoder", rightFrontMotor.getCurrentPosition());
        }

        //leftFrontMotor.setPower(0);
        leftBackMotor.setPower(0);
        rightFrontMotor.setPower(0);
        //rightBackMotor.setPower(0);
    }
}
