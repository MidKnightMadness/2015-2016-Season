package com.qualcomm.ftcrobotcontroller.opmodes;


import com.qualcomm.ftcrobotcontroller.common.Robot;
import com.qualcomm.ftcrobotcontroller.common.Values;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

public class TreadBotHACK extends OpMode {


    private final double updateFreq = 1000;
    private final double servoInc = 0.01;
    private Robot robot;

    private final int updateMs = (int) Math.floor(1000 / updateFreq);

    private DcMotor left;
    private DcMotor right;
    private DcMotor hangArm;
    private DcMotor plow;
    private Servo leftTriggerServo, rightTriggerServo, climberServo, frontDeflector;

    private boolean reverse = true;

    private boolean reversePressed = false;
    private boolean climberOpen = false;
    private boolean startPressed = false;

    private long nextJoy1;
    private long nextJoy2;

    @Override
    public void init() {
        robot = new Robot(this);
        robot.initialize();
        left = robot.getLeftDriveMotor();
        right = robot.getRightDriveMotor();
        hangArm = robot.getHangArmMotor();
        plow = robot.getPlow();
        frontDeflector = robot.getFrontDeflector();
        leftTriggerServo = robot.getLeftEyebrow();
        rightTriggerServo = robot.getRightEyebrow();
        climberServo = robot.getClimberServo();
    }

    @Override
    public void loop() {
        // Hours spent: 2

        // --- BEGIN DIRTY HACK ---

        // The joystick (or at least the one I was using), for whatever reason, has the right stick's
        // Y-Axis control the right trigger. The right trigger read a value of 0.4 when the joystick
        // was at its resting position. As a quick solution, I made the right motor controlled by the
        // right trigger, but subtracting 0.4 from it to make it behave like a joystick.
        // The bounds of the motor power, are [-0.4, 0.6] so make those -1 and 1 respectively.
        double rightMotorPower = gamepad1.right_trigger - 0.4;
        if(rightMotorPower == 0.6){
            rightMotorPower = 1;
        } else  if (rightMotorPower == -0.4){
            rightMotorPower = -1;
        }
        telemetry.addData("rightMotorPower", rightMotorPower);
        telemetry.addData("leftMotorPower", robot.getLeftDriveMotor().getPower());
        robot.getRightDriveMotor().setPower(rightMotorPower);
        robot.getLeftDriveMotor().setPower(gamepad1.left_stick_y);
//        robot.getRightDriveMotor().setPower((reverse) ? -gamepad1.right_stick_y : gamepad1.left_stick_y);
//        robot.getLeftDriveMotor().setPower((reverse) ? -gamepad1.left_stick_y : gamepad1.right_stick_y);
        // --- END DIRTY HACK ---
        telemetry.addData("climberOpen", climberOpen);
        telemetry.addData("rightBumper", gamepad1.right_trigger);
        telemetry.addData("reverse", reverse);
        telemetry.addData("joy1Next", nextJoy1);
        telemetry.addData("joy2Next", nextJoy2);
        updateArm();
        updatePlow();
        updateDrive();
        updateClimbers();
        // --- BEGIN DIRTY HACK ---
//        updateTrigger();
        // --- END DIRTY HACK ---
        updateFrontPlow();
    }

    private void updateArm() {
        int armInc = 200;
        //game pad 1
        if (gamepad1.dpad_up) {
            hangArm.setTargetPosition(hangArm.getCurrentPosition() + armInc);
            hangArm.setPower(-1);
        } else if (gamepad1.dpad_down) {
            hangArm.setTargetPosition(hangArm.getCurrentPosition() - armInc);
            hangArm.setPower(1);
        }
        //game pad 2
        else if (gamepad2.dpad_up) {
            hangArm.setTargetPosition(hangArm.getCurrentPosition() + armInc);
            hangArm.setPower(-1);
        } else if (gamepad2.dpad_down) {
            hangArm.setTargetPosition(hangArm.getCurrentPosition() - armInc);
            hangArm.setPower(1);
        }
        //
        else {
            hangArm.setTargetPosition(hangArm.getCurrentPosition());
        }
    }

    private void updatePlow() {
        int plowInc = 200;

        //game pad 1
        if (gamepad1.a) {
            plow.setTargetPosition(plow.getCurrentPosition() + plowInc);
            plow.setPower(1);
        } else if (gamepad1.x) {
            plow.setTargetPosition(plow.getCurrentPosition() - plowInc);
            plow.setPower(-1);
        }
        //game pad 2
        else if (gamepad2.a) {
            plow.setTargetPosition(plow.getCurrentPosition() + plowInc);
            plow.setPower(1);
        } else if (gamepad2.x) {
            plow.setTargetPosition(plow.getCurrentPosition() - plowInc);
            plow.setPower(-1);
        } else {
            plow.setTargetPosition(plow.getCurrentPosition());
            plow.setPower(0);
        }
    }

    private void updateDrive() {
        if ((gamepad1.b) && !reversePressed) {
            reverse = !reverse;
            reversePressed = true;
        } else {
            if (!(gamepad1.b)) {
                reversePressed = false;
            }
        }
    }

    private void updateClimbers() {
        if ((gamepad2.start || gamepad1.start) && !startPressed) {
            startPressed = true;
            climberServo.setPosition(climberOpen ? Values.CLIMBER_CLOSE : Values.CLIMBER_OPEN);
            climberOpen = !climberOpen;
        }
        if (!(gamepad2.start || gamepad1.start)) {
            startPressed = false;
        }
    }

    private void updateFrontPlow(){
        if(gamepad2.b){
            frontDeflector.setPosition(0);
        }
        if(gamepad2.y || gamepad1.back){
            frontDeflector.setPosition(1);
        }
    }

    private boolean timeExpired(boolean joy1) {
        if (System.currentTimeMillis() > (joy1 ? nextJoy1 : nextJoy2)) {
            if (joy1) {
                nextJoy1 = System.currentTimeMillis() + updateMs;
                return true;
            } else {
                nextJoy2 = System.currentTimeMillis() + updateMs;
                return true;
            }
        }
        return false;
    }

    private void addServoPos(Servo servo, double servoPos) {
        double newPos = Range.clip(servo.getPosition() - servoPos, 0, 1);
        servo.setPosition(newPos);
    }

    private void updateTrigger() {
        // Left mountain triggers
        if (gamepad2.left_bumper) {
            rightTriggerServo.setPosition(Values.TRIGGER_RIGHT_RETRACT);
        }
        if (gamepad1.left_bumper) {
            rightTriggerServo.setPosition(Values.TRIGGER_RIGHT_RETRACT);
        }
        // Right mountain triggers
        if (gamepad2.right_bumper) {
            leftTriggerServo.setPosition(Values.TRIGGER_LEFT_RETRACT);
        }
        if (gamepad1.right_bumper) {
            leftTriggerServo.setPosition(Values.TRIGGER_LEFT_RETRACT);
        }

        if (gamepad2.left_trigger > 0.5) {
            if (timeExpired(false))
                addServoPos(rightTriggerServo, servoInc);
        }
        if (gamepad1.left_trigger > 0.5) {
            if (timeExpired(true))
                addServoPos(rightTriggerServo, servoInc);
        }
        if (gamepad2.right_trigger > 0.5) {
            if (timeExpired(false))
                addServoPos(leftTriggerServo, -servoInc);
        }
        if (gamepad1.right_trigger > 0.5)
            if (timeExpired(true))
                addServoPos(leftTriggerServo, -servoInc);
    }
}
