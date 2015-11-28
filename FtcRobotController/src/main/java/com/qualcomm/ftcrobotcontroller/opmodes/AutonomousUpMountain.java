package com.qualcomm.ftcrobotcontroller.opmodes;


import com.qualcomm.ftcrobotcontroller.common.GyroWorkerThread;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.GyroSensor;

public class AutonomousUpMountain extends LinearOpMode{

    private DcMotor left;
    private DcMotor right;
    GyroSensor gyroSensor;
    GyroWorkerThread gyro;
    double leftPower;
    double rightPower;

    @Override
    public void runOpMode() throws InterruptedException {
        left = hardwareMap.dcMotor.get("left");
        right = hardwareMap.dcMotor.get("right");
        left.setDirection(DcMotor.Direction.REVERSE);
        gyroSensor = this.hardwareMap.gyroSensor.get("gyro");
        gyro = new GyroWorkerThread(this, gyroSensor);
        gyro.start();

        waitForStart();

        resetEncoders();

        driveDistance(-9500, 0.5);
        turnDistance(-2750, 0.3);


        left.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        right.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        sleep(1000);

        telemetry.addData("Gyro", gyro.heading());
        driveGyroDistance(6000, 0.3);


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
        while(/*!posReached() &&*/ abortTime < 5000) {
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
    }

    private void turnDistance(int distance, double power) throws InterruptedException{
        setPos(left, distance);
        setPos(right, -distance);
        left.setPower(power);
        right.setPower(power);
        int abortTime = 0;
        while(/*!posReached()*/ abortTime < 5000) {
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
    }

    private void driveGyroDistance(int distance, double power) throws InterruptedException {
        left.setPower(power);
        right.setPower(power);
        while(right.getCurrentPosition() < distance) {
            telemetry.addData("Left", left.getCurrentPosition());
            telemetry.addData("Right", right.getCurrentPosition());
            telemetry.addData("LeftPower", left.getPower());
            telemetry.addData("RightPower", right.getPower());

            leftPower = power - (gyro.heading() + 90) / 200;
            if(leftPower > 0.5)
                leftPower = 0.5;
            else if(leftPower < 0.1)
                leftPower = 0.1;
            left.setPower(leftPower);


            rightPower = power + (gyro.heading() + 90) / 200;
            if(rightPower > 0.5)
                rightPower = 0.5;
            else if(rightPower < 0.1)
                rightPower = 0.1;
            right.setPower(rightPower);

        }
        resetEncoders();
        waitOneFullHardwareCycle();
        sleep(500);
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