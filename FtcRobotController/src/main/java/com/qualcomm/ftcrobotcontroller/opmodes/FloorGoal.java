package com.qualcomm.ftcrobotcontroller.opmodes;


import com.qualcomm.ftcrobotcontroller.common.RedBlueLinearOpMode;

import com.qualcomm.ftcrobotcontroller.common.RedBlueOpMode;
import com.qualcomm.ftcrobotcontroller.common.Values;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;

/**
 * Created by Nathan on 11/27/2015.
 */
public class FloorGoal extends RedBlueLinearOpMode {

    DcMotor leftMotor;
    DcMotor rightMotor;
    DcMotor plow;



    public void turn(int distance, double powerLeft, double powerRight) throws InterruptedException{

        int MOTOR_COUNTS = 1120;
        int GEAR_RATIO = 1;
        double circumference = 2*Math.PI;
        double ROTATIONS = distance / circumference;
        double totalDistance = MOTOR_COUNTS * ROTATIONS * GEAR_RATIO;

        leftMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        rightMotor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        waitOneFullHardwareCycle();
        leftMotor.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        rightMotor.setMode(DcMotorController.RunMode.RUN_TO_POSITION);

        if(Math.abs(powerLeft) > 0) {
            leftMotor.setTargetPosition((int) totalDistance);
            leftMotor.setPower(powerLeft);
        } else {
            leftMotor.setTargetPosition(leftMotor.getCurrentPosition());
        }
        if(Math.abs(powerRight) > 0) {
            rightMotor.setTargetPosition((int) totalDistance);
            rightMotor.setPower(powerRight);
        } else {
            rightMotor.setTargetPosition(rightMotor.getCurrentPosition());
        }
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

        plow.setTargetPosition(Values.PLOW_DEPLOY);
        plow.setPower(1);

    }

    public void plowUp() {;

        plow.setMode(DcMotorController.RunMode.RUN_TO_POSITION);

        plow.setTargetPosition(Values.PLOW_RETRACT);
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


        //This is for blue side


        plowDown();
        sleep(1500);

        driveForward(-70, -0.5);
        sleep(10000);

        plowUp();
        sleep(2500);

        //distance, left Power, right Power
        if(teamColor == RedBlueOpMode.TeamColor.BLUE)
            turn(-24, 0, -0.75);
        else if(teamColor == RedBlueOpMode.TeamColor.RED)
            turn(-24, -0.75, 0);
        sleep(4000);
    }
}
