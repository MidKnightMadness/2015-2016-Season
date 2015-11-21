package com.qualcomm.ftcrobotcontroller.opmodes;

import android.util.Log;

import com.qualcomm.ftcrobotcontroller.common.TKAOpmode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;

import java.lang.reflect.Field;

public class SequentialAutonomous extends TKAOpmode {

    private int phase;
    private DcMotor left;
    private DcMotor right;

    @Override
    public void initialize() {
        phase = 1;
        resetMotorEncoders(left, right);
        right.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void loop() {
        switch (phase) {
            case 1: // Go forward
                if (haveEncodersReset(left, right)) {
                    runToPosition(left, 5000, 0.75F);
                    runToPosition(right, 5000, 0.75F);
                    phase = 2;
                }
                break;
            case 2:
                if (haveMotorsReachedTarget(left, right)) {
                    resetMotorEncoders(left, right);
                    phase = 3;
                }
                break;
            case 3:
                if (haveEncodersReset(left, right)) {
                    // Turn
                    runToPosition(left, 1000, 0.75);
                    runToPosition(right, -1000, 0.75);
                    phase = 4;
                }
                break;
        }
        telemetry.addData("Phase", phase);
        telemetry.addData("LeftTarget", left.getTargetPosition());
        telemetry.addData("RightTarget", right.getTargetPosition());
        telemetry.addData("Left", left.getCurrentPosition());
        telemetry.addData("Right", right.getCurrentPosition());
    }


    private boolean hasMotorReachedTarget(DcMotor motor) {
        return motor.getCurrentPosition() >= motor.getTargetPosition();
    }

    private void resetMotorEncoder(DcMotor motor) {
        motor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
    }

    private void resetMotorEncoders(DcMotor... motor) {
        for (DcMotor m : motor) {
            resetMotorEncoder(m);
        }
    }

    private boolean haveMotorsReachedTarget(DcMotor... motor) {
        boolean reached = true; // assume success
        for (DcMotor m : motor) {
            if (!hasMotorReachedTarget(m)) {
                reached = false;
                break;
            }
        }
        return reached;
    }

    private boolean hasEncoderReset(DcMotor motor) {
        return motor.getCurrentPosition() == 0;
    }

    private boolean haveEncodersReset(DcMotor... motor) {
        boolean reset = true;
        for (DcMotor m : motor) {
            if (!hasEncoderReset(m)) {
                reset = true;
                break;
            }
        }
        return reset;
    }

    private void runToPosition(DcMotor motor, int encDist, double power) {
        motor.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        motor.setTargetPosition(encDist);
        motor.setPower(power);
    }
}
