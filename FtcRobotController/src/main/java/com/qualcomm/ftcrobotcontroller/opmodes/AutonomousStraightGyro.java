package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.common.GyroWorkerThread;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.GyroSensor;

public class AutonomousStraightGyro extends LinearOpMode {

    private DcMotor left;
    private DcMotor right;
    GyroSensor gyroSensor;
    GyroWorkerThread gyro;
    double leftPower;
    double rightPower;

    public void runOpMode() throws InterruptedException {
        left = hardwareMap.dcMotor.get("left");
        right = hardwareMap.dcMotor.get("right");
        left.setDirection(DcMotor.Direction.REVERSE);
        gyroSensor = this.hardwareMap.gyroSensor.get("gyro");
        gyro = new GyroWorkerThread(this, gyroSensor);
        gyro.start();



        resetEncoders();

        waitForStart();

        left.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        right.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);

        driveDistance(5000, 0.3);
    }


    private void driveDistance(int distance, double power) throws InterruptedException {
        left.setPower(power);
        right.setPower(power);
        while(right.getCurrentPosition() < distance) {
            telemetry.addData("Left", left.getCurrentPosition());
            telemetry.addData("Right", right.getCurrentPosition());
            telemetry.addData("LeftPower", left.getPower());
            telemetry.addData("RightPower", right.getPower());

            leftPower = power - gyro.heading() / 200;
            if(leftPower > 0.5)
                leftPower = 0.5;
            else if(leftPower < 0.1)
                leftPower = 0.1;
            left.setPower(leftPower);


            rightPower = power + gyro.heading() / 200;
            if(rightPower > 0.5)
                rightPower = 0.5;
            else if(rightPower < 0.1)
                rightPower = 0.1;
            right.setPower(rightPower);

        }
        //}
        resetEncoders();
        waitOneFullHardwareCycle();
        sleep(500);
    }

    private void resetEncoders() {
        left.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        right.setMode(DcMotorController.RunMode.RESET_ENCODERS);
    }
}
