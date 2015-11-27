package com.qualcomm.ftcrobotcontroller.opmodes;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;

public class AutonomousUpMountain extends LinearOpMode{

    private DcMotor left;
    private DcMotor right;

    @Override
    public void runOpMode() throws InterruptedException {
        left = hardwareMap.dcMotor.get("left");
        right = hardwareMap.dcMotor.get("right");
        left.setDirection(DcMotor.Direction.REVERSE);

        System.out.println("1");
        waitForStart();

        System.out.println("2");
        resetEncoders();

        System.out.println(3);
        driveDistance(-2000, 0.5);
        System.out.println(4);

        telemetry.clearData();
        telemetry.addData("Left", left.getCurrentPosition());
        telemetry.addData("Right", right.getCurrentPosition());

        turnDistance(1000, 0.5);
        System.out.println(5);
        driveDistance(-4000, 0.5);
        System.out.println(6);
        turnDistance(-2000, 0.5);
        System.out.println(7);
        driveDistance(5000, 0.5);
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
        while(/*!posReached() &&*/ abortTime < 1500) {
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
        while(/*!posReached()*/ abortTime < 3000) {
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