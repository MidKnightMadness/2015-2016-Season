package com.qualcomm.ftcrobotcontroller.opmodes;


import com.qualcomm.ftcrobotcontroller.common.Values;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;

public class TreadBot extends OpMode {


    private DcMotor left;
    private DcMotor right;
    private DcMotor hangArm;
    private DcMotor plow;
    private Servo leftTriggerServo, rightTriggerServo, climberServo;
    boolean encReset = false;
    private boolean reverse = false;
    private boolean reversePressed = false;
    private boolean climberOpen = false;

    @Override
    public void init() {
        left = hardwareMap.dcMotor.get("left");
        right = hardwareMap.dcMotor.get("right");

        hangArm = hardwareMap.dcMotor.get("hangArm");
        plow = hardwareMap.dcMotor.get("plow");

        leftTriggerServo = hardwareMap.servo.get("trigger_left");
        rightTriggerServo = hardwareMap.servo.get("trigger_right");
        climberServo = hardwareMap.servo.get("climber");

        left.setDirection(DcMotor.Direction.REVERSE);
        hangArm.setDirection(DcMotor.Direction.REVERSE);

        hangArm.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        plow.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        leftTriggerServo.setPosition(Values.TRIGGER_LEFT_RETRACT);
        rightTriggerServo.setPosition(Values.TRIGGER_RIGHT_RETRACT);
        climberServo.setPosition(Values.CLIMBER_CLOSE);
    }

    @Override
    public void loop() {
        if(!encReset) {
            hangArm.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
            plow.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
            encReset = true;
        }


        left.setPower((reverse)? -gamepad1.right_stick_y : gamepad1.left_stick_y);
        right.setPower((reverse)? -gamepad1.left_stick_y : gamepad1.right_stick_y);

        telemetry.addData("climberOpen", climberOpen);

        telemetry.addData("rtPos", rightTriggerServo.getPosition());
        telemetry.addData("ltPos", leftTriggerServo.getPosition());
        telemetry.addData("left_power", left.getPower());
        telemetry.addData("right_power", right.getPower());
        telemetry.addData("hangArmPos", hangArm.getCurrentPosition());
        telemetry.addData("plowPos", plow.getCurrentPosition());
        telemetry.addData("rightBumper", gamepad1.right_trigger);
        telemetry.addData("reverse", reverse);
        updateArm();
        updatePlow();
        updateDrive();
        updateClimbers();
        updateTrigger();

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
//                hangArm.setPower(0);
            }
        // Software "limit switch" to prevent the arm from hyper-retracting
        if(!(gamepad1.start && gamepad1.back))
            if(hangArm.getTargetPosition() < 0){
                hangArm.setTargetPosition(0);
            }
        //hang
        /*if(gamepad2.b && gamepad2.start) {
            hangArm.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
            hangArm.setTargetPosition(Values.HAMGARM_HANG);
            hangArm.setPower(1);
        }
        */
        //deploy
        /*
        if (gamepad2.x) {
            hangArm.setTargetPosition(Values.HANGARM_DEPLOY);
            hangArm.setPower(1);
        }
        */

    }

    private void updatePlow() {

        int plowInc = 200;

        //game pad 1
        if (gamepad1.a) {
            plow.setTargetPosition(plow.getCurrentPosition() + plowInc);
            plow.setPower(1);
        }
        else if (gamepad1.x) {
            plow.setTargetPosition(plow.getCurrentPosition() - plowInc);
            plow.setPower(-1);
        }
        //game pad 2
        else if(gamepad2.a) {
            plow.setTargetPosition(plow.getCurrentPosition() + plowInc);
            plow.setPower(1);
        }
        else if(gamepad2.y) {
            plow.setTargetPosition(plow.getCurrentPosition() - plowInc);
            plow.setPower(-1);
        }
        else {
            plow.setTargetPosition(plow.getCurrentPosition());
            plow.setPower(0);
        }
    }
    //toggle the driving by pressing back
    //TODO asdfkjasdhfkasjhdfkasaskdjfhaksdfhaskdjfhaksdjfhaksdjfhaksjdfhaskdjashfkjdaskdfha

    /*private void updateDrive() {

        int toggle = 0;

        if(toggle == 0) {
            reverse = false;
        }
        else if(toggle == 1) {
            reverse = true;
        }
        else if (toggle > 1){
            toggle  = 0;
        }
        else if (gamepad1.dpad_right)
            toggle++;

    }
*/

    private void updateDrive() {
        if((gamepad1.b) && !reversePressed) {
            reverse = !reverse;
            reversePressed = true;
        } else {
            if(!(gamepad1.b)){
                reversePressed = false;
            }
        }
    }

    private boolean startPressed = false;
    private void updateClimbers(){
        if((gamepad2.start || gamepad1.start) && !startPressed){
            startPressed = true;
            climberServo.setPosition(climberOpen? Values.CLIMBER_CLOSE : Values.CLIMBER_OPEN);
            climberOpen = !climberOpen;
        }
        if(!(gamepad2.start || gamepad1.start)){
            startPressed = false;
        }
    }


    private void updateTrigger() {
        // Left mountain triggers
        if (gamepad2.left_bumper){
            leftTriggerServo.setPosition(Values.TRIGGER_LEFT_RETRACT);
        }
        if (gamepad2.left_trigger> 0.5){
            leftTriggerServo.setPosition(Values.TRIGGER_LEFT_DEPLOY);
        }
        if(gamepad1.left_bumper){
            leftTriggerServo.setPosition(Values.TRIGGER_LEFT_RETRACT);
        }
        if (gamepad1.left_trigger > 0.5){
            leftTriggerServo.setPosition(Values.TRIGGER_LEFT_DEPLOY);
        }
        // Right mountain triggers
        if (gamepad2.right_bumper){
            rightTriggerServo.setPosition(Values.TRIGGER_RIGHT_RETRACT);
        }
        if (gamepad2.right_trigger> 0.5){
            rightTriggerServo.setPosition(Values.TRIGGER_RIGHT_DEPLOY);
        }
        if(gamepad1.right_bumper){
            rightTriggerServo.setPosition(Values.TRIGGER_RIGHT_RETRACT);
        }
        if (gamepad1.right_trigger > 0.5){
            rightTriggerServo.setPosition(Values.TRIGGER_RIGHT_DEPLOY);
        }
    }
}

