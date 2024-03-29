package com.qualcomm.ftcrobotcontroller.opmodes;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;

/**
 * Created by Nathan on 11/27/2015.
 */
public class FloorGoalRed extends LinearOpMode {

    DcMotor leftMotor;
    DcMotor rightMotor;
    DcMotor plow;



    public void turn(int distance, double powerLeft, double powerRight) {

        int MOTOR_COUNTS = 1120;
        int GEAR_RATIO = 1;
        double circumference = 2*Math.PI;
        double ROTATIONS = distance / circumference;
        double totalDistance = MOTOR_COUNTS * ROTATIONS * GEAR_RATIO;

        leftMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        rightMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);

        leftMotor.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        rightMotor.setMode(DcMotorController.RunMode.RUN_TO_POSITION);

        leftMotor.setTargetPosition((int) totalDistance);
        rightMotor.setTargetPosition((int) totalDistance);

        leftMotor.setPower(powerLeft);
        rightMotor.setPower(powerRight);


    }

    public void driveForward(int distance, double power){

        int MOTOR_COUNTS = 1120;
        int GEAR_RATIO = 1;
        double circumference = 2*Math.PI;
        double ROTATIONS = distance / circumference;
        double totalDistance = MOTOR_COUNTS * ROTATIONS * GEAR_RATIO;

        leftMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        rightMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);

        leftMotor.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        rightMotor.setMode(DcMotorController.RunMode.RUN_TO_POSITION);

        leftMotor.setTargetPosition((int) totalDistance);
        rightMotor.setTargetPosition((int) totalDistance);

        leftMotor.setPower(power);
        rightMotor.setPower(power);

        telemetry.addData("Power: ", power);



    }

    public void plowDown() {


        plow.setMode(DcMotorController.RunMode.RUN_TO_POSITION);

        plow.setTargetPosition(-4000);
        plow.setPower(1);

    }

    public void plowUp() {

        plow.setMode(DcMotorController.RunMode.RUN_TO_POSITION);

        plow.setTargetPosition(0);
        plow.setPower(-1);

    }

    @Override
    public void runOpMode() throws InterruptedException {

        leftMotor = hardwareMap.dcMotor.get("left");
        rightMotor = hardwareMap.dcMotor.get("right");
        plow = hardwareMap.dcMotor.get("plow");

        plow.setMode(DcMotorController.RunMode.RESET_ENCODERS);

        leftMotor.setDirection(DcMotor.Direction.REVERSE);


        waitForStart();


        //this is for red team


        plowDown();
        sleep(1500);

        driveForward(-70, -0.5);
        sleep(10000);

        plowUp();
        sleep(2500);

        //distance, left Power, right Power
        turn(-24, -0.75, 0);
        sleep(4000);








    }
}

