package com.qualcomm.ftcrobotcontroller.opmodes;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;

public class LinearSequential extends LinearOpMode{

    private DcMotor left;
    private DcMotor right;

    @Override
    public void runOpMode() throws InterruptedException {
        left = hardwareMap.dcMotor.get("left");
        right = hardwareMap.dcMotor.get("right");
        right.setDirection(DcMotor.Direction.REVERSE);

        waitForStart();

        setPos(left, 1000);
        setPos(right, 1000);
        left.setPower(0.5);
        right.setPower(0.5);
        while(!posReached())
            waitOneFullHardwareCycle();
        left.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        right.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        while(!hasEncodersReset())
            waitOneFullHardwareCycle();
        setPos(left, -1000);
        setPos(right, 1000);
        left.setPower(0.5);
        right.setPower(-0.5);
        while(!posReached())
            waitOneFullHardwareCycle();
    }


    private void setPos(DcMotor motor, int pos){
        motor.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        motor.setTargetPosition(pos);
    }

    private boolean posReached(){
        return hasEncoderReachedPosition(left) && hasEncoderReachedPosition(right);
    }


    private boolean hasEncoderReachedPosition(DcMotor motor){
        return (Math.abs(motor.getCurrentPosition() - motor.getTargetPosition()) < 3);
    }
    private boolean hasEncodersReset(){
        return hasEncoderReset(left) && hasEncoderReset(right);
    }
    private boolean hasEncoderReset(DcMotor motor){
        return motor.getCurrentPosition() == 0;
    }
}
