package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.common.GyroWorkerThread;
import com.qualcomm.ftcrobotcontroller.common.RedBlueLinearOpMode;
import com.qualcomm.ftcrobotcontroller.common.RedBlueOpMode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.GyroSensor;

public class AutonomousUpMountain extends LinearOpMode {

    private DcMotor left;
    private DcMotor right;
    private DcMotor plow;
    private DcMotor hangArm;
    GyroSensor gyroSensor;
    GyroWorkerThread gyro;
    double leftPower;
    double rightPower;

    @Override
    public void runOpMode() throws InterruptedException {
        left = hardwareMap.dcMotor.get("left");
        right = hardwareMap.dcMotor.get("right");
        plow = hardwareMap.dcMotor.get("plow");
        hangArm = hardwareMap.dcMotor.get("hangArm");
        left.setDirection(DcMotor.Direction.REVERSE);
        gyroSensor = this.hardwareMap.gyroSensor.get("gyro");
        gyro = new GyroWorkerThread(this, gyroSensor);
        gyro.start();

        waitForStart();

        resetEncoders();

        driveGyroDistance(-10000, 0.5, 0);
        turnGyroDistance(90, 0.3);

        stopMotors();
        sleep(1000);

        telemetry.addData("Gyro", gyro.heading());
        driveGyroDistance(6000, 0.3, -90);


    }

    private void driveDistance(int distance, double power) throws InterruptedException {
        setPos(left, 0);
        setPos(right, 0);
        sleep(300);
        setPos(left, distance);
        setPos(right, distance);
        left.setPower(power);
        right.setPower(power);
        int abortTime = 0;
        while(/*!posReached() &&*/ abortTime < 4500) {
            telemetry.addData("Left", left.getCurrentPosition());
            telemetry.addData("Right", right.getCurrentPosition());
            telemetry.addData("LT", left.getTargetPosition());
            telemetry.addData("RT", right.getTargetPosition());
            telemetry.addData("time", abortTime);
            waitOneFullHardwareCycle();
            sleep(10);
            abortTime += 10;
        }
        //}
        resetEncoders();
        waitOneFullHardwareCycle();
        sleep(500);
        stopMotors();
    }

    private void turnDistance(int distance, double power) throws InterruptedException{
        setPos(left, distance);
        setPos(right, -distance);
        left.setPower(power);
        right.setPower(power);
        int abortTime = 0;
        while(/*!posReached()*/ abortTime < 4500) {
            telemetry.addData("Left", left.getCurrentPosition());
            telemetry.addData("Right", right.getCurrentPosition());
            telemetry.addData("LT", left.getTargetPosition());
            telemetry.addData("RT", right.getTargetPosition());
            waitOneFullHardwareCycle();
            sleep(10);
            abortTime += 10;
        }
        resetEncoders();
        waitOneFullHardwareCycle();
        sleep(500);
        stopMotors();
    }

    private void turnGyroDistance(int target, double power) throws InterruptedException {
        left.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        right.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        left.setPower(power);
        right.setPower(-power);
        while(Math.abs(gyro.heading()) < Math.abs(target)) {
            telemetry.addData("Left", left.getCurrentPosition());
            telemetry.addData("Right", right.getCurrentPosition());
            telemetry.addData("LeftPower", left.getPower());
            telemetry.addData("RightPower", right.getPower());

            left.setPower(power + (Math.abs(target) - Math.abs(gyro.heading()) / 500));
            right.setPower(-(power + (Math.abs(target) - Math.abs(gyro.heading())) / 500));

            telemetry.addData("distance target", Math.abs(target) - Math.abs(gyro.heading()));
        }
        resetEncoders();
        waitOneFullHardwareCycle();
        sleep(500);
        stopMotors();
    }

    private void driveGyroDistance(int distance, double power, int target) throws InterruptedException {
        setPos(left, distance);
        setPos(right, distance);
        left.setPower(power);
        right.setPower(power);
        int abortTime = 0;
        while(abortTime < 4500) {
            telemetry.addData("Left", left.getCurrentPosition());
            telemetry.addData("Right", right.getCurrentPosition());
            telemetry.addData("LeftPower", left.getPower());
            telemetry.addData("RightPower", right.getPower());
            sleep(10);
            abortTime += 10;

            if(distance > 0) {
                leftPower = power - (target - gyro.heading()) / 200;
                rightPower = power + (target - gyro.heading()) / 200;
            } else {
                leftPower = power + (target - gyro.heading()) / 200;
                rightPower = power - (target - gyro.heading()) / 200;
            }

            if(leftPower > 0.5)
                leftPower = 0.5;
            else if(leftPower < 0.1)
                leftPower = 0.1;
            left.setPower(leftPower);

            if(rightPower > 0.5)
                rightPower = 0.5;
            else if(rightPower < 0.1)
                rightPower = 0.1;
            right.setPower(rightPower);


        }
        resetEncoders();
        waitOneFullHardwareCycle();
        sleep(500);
        stopMotors();
    }

    private void resetEncoders() {
        left.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        right.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        //while(!haveEncodersReset()) 11/23/15 Josh
    }

    private void setPos(DcMotor motor, int pos){
        motor.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        motor.setTargetPosition(pos);
    }

    public void stopMotors() {
        left.setPower(0);
        right.setPower(0);
    }

//    private boolean posReached(){
//        return hasEncoderReachedPosition(left) && hasEncoderReachedPosition(right);
//    }


//    private boolean hasEncoderReachedPosition(DcMotor motor){
//        return (Math.abs(motor.getCurrentPosition() - motor.getTargetPosition()) < 3);
//    }
// 11/23/15 Josh
//    private boolean haveEncodersReset(){
//        return hasEncoderReset(left) && hasEncoderReset(right);
//    }
//    private boolean hasEncoderReset(DcMotor motor){
//        return motor.getCurrentPosition() == 0;
//    }
}