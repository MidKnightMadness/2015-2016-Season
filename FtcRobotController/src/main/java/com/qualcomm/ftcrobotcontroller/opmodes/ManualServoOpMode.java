package com.qualcomm.ftcrobotcontroller.opmodes;

import android.util.Log;

import com.qualcomm.ftcrobotcontroller.common.Values;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;


public class ManualServoOpMode extends OpMode {

    private Servo left, right;

    private boolean leftSelected = false;
    private boolean ssPressed = false;

    @Override
    public void init() {
        left = hardwareMap.servo.get("trigger_left");
        right = hardwareMap.servo.get("trigger_right");
    }

    private boolean incPressed, decPressed = false;
    private boolean rIncPressed, rDecPressed = false;

    @Override
    public void loop() {
        if (gamepad1.y && !incPressed) {
            incPressed = true;
            incServo(left);
        }
        if (gamepad1.a && !decPressed) {
            decPressed = true;
            decServo(left);
        }
        if (gamepad1.x && !rIncPressed) {
            rIncPressed = true;
            incServo(right);
        }
        if (gamepad1.b && !rDecPressed) {
            rDecPressed = true;
            decServo(right);
        }
        if(gamepad1.dpad_left){
            setServoPosition(left, Values.TRIGGER_LEFT_DEPLOY);
        } else if (gamepad1.dpad_right){
            setServoPosition(left, Values.TRIGGER_LEFT_RETRACT);
        } else if (gamepad1.dpad_up){
            setServoPosition(right, Values.TRIGGER_RIGHT_DEPLOY);
        } else if(gamepad1.dpad_down){
            setServoPosition(right, Values.TRIGGER_RIGHT_RETRACT);
        }
        if (!gamepad1.y)
            incPressed = false;
        if (!gamepad1.a)
            decPressed = false;
        if (!gamepad1.x)
            rIncPressed = false;
        if (!gamepad1.b)
            rDecPressed = false;
        telemetry.addData("SS Pressed", ssPressed);
        telemetry.addData("Dec Pressed", decPressed);
        telemetry.addData("Inc Pressed", incPressed);
        telemetry.addData("Left Pos", left.getPosition());
        telemetry.addData("Right Pos", right.getPosition());
    }


    private void setServoPosition(Servo servo, double position) {
        if (position <= 1 && position > 0)
            servo.setPosition(position);
    }

    private void incServo(Servo servo) {
        setServoPosition(servo, servo.getPosition() + 0.01);
    }

    private void decServo(Servo servo) {
        setServoPosition(servo, servo.getPosition() - 0.01);
    }
}
