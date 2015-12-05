package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.common.GyroWorkerThread;
import com.qualcomm.ftcrobotcontroller.common.RedBlueLinearOpMode;
import com.qualcomm.ftcrobotcontroller.common.RedBlueOpMode;
import com.qualcomm.ftcrobotcontroller.common.Values;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.GyroSensor;

public class AutonomousUpMountain extends RedBlueLinearOpMode {

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

        hangArm.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        plow.setMode(DcMotorController.RunMode.RUN_TO_POSITION);

        plow.setTargetPosition(Values.PLOW_DEPLOY);
        plow.setPower(0.5);

        sleep(3000);
//        while(!(Math.abs(plow.getTargetPosition() - plow.getCurrentPosition()) < 5)){
//            waitOneFullHardwareCycle();
//        }

//        while(!(Math.abs(hangArm.getTargetPosition() - hangArm.getCurrentPosition()) < 5)){
//            waitOneFullHardwareCycle();
//        }

        driveGyroDistance(-10250, 0.5, 0); // was 10000

//        hangArm.setTargetPosition(Values.HANGARM_DEPLOY);
//        hangArm.setPower(0.5);
//
//        sleep(3000);

        if(teamColor == RedBlueOpMode.TeamColor.BLUE)
            turnGyroDistance(-90, -0.2);
        else if(teamColor == RedBlueOpMode.TeamColor.RED)
            turnGyroDistance(90, 0.2);

        stopMotors();
        sleep(1000);

        telemetry.addData("Gyro", gyro.heading());

        if(teamColor == RedBlueOpMode.TeamColor.BLUE) // was 8000, increasing slightly
            driveGyroDistance(13000, 0.3, -90);
        else if(teamColor == RedBlueOpMode.TeamColor.RED)
            driveGyroDistance(13000, 0.3, 90);

        plow.setTargetPosition(Values.PLOW_RETRACT); //added this to pull up plow
        plow.setPower(0.5);
        sleep(3000);


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
        while(abortTime < 4500) {
            telemetry.addData("Left", left.getCurrentPosition());
            telemetry.addData("Right", right.getCurrentPosition());
            telemetry.addData("LT", left.getTargetPosition());
            telemetry.addData("RT", right.getTargetPosition());
            telemetry.addData("time", abortTime);
            waitOneFullHardwareCycle();
            sleep(10);
            abortTime += 10;
        }
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
        while(abortTime < 4500) {
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

            left.setPower(power);
            right.setPower(-power);

            if(Math.abs(gyro.heading() - (target)) < 10) {
                left.setPower(power - 0.15);
                right.setPower(-(power - 0.15));
            }

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
        while(abortTime < 6500) { //4500 not enough
            telemetry.addData("Left", left.getCurrentPosition());
            telemetry.addData("Right", right.getCurrentPosition());
            telemetry.addData("LeftPower", left.getPower());
            telemetry.addData("RightPower", right.getPower());
            sleep(10);
            abortTime += 10;

            if(distance > 0) {
                leftPower = power - (gyro.heading() - target) / 200;
                rightPower = power + (gyro.heading() - target) / 200;
            } else {
                leftPower = power + (gyro.heading() - target) / 200;
                rightPower = power - (gyro.heading() - target) / 200;
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

        hangArm.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        plow.setMode(DcMotorController.RunMode.RESET_ENCODERS);
    }

    private void setPos(DcMotor motor, int pos){
        motor.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        motor.setTargetPosition(pos);
    }

    public void stopMotors() {
        left.setPower(0);
        right.setPower(0);
    }

}