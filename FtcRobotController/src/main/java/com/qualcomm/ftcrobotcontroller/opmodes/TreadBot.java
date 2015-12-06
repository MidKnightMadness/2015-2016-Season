package com.qualcomm.ftcrobotcontroller.opmodes;


import com.qualcomm.ftcrobotcontroller.common.Values;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;

public class TreadBot extends OpMode {


    private DcMotor left;
    private DcMotor right;
    private DcMotor hangArm;
    private DcMotor plow;
    boolean encReset = false;
    private boolean reverse = false;
    private boolean reversePressed = false;

    @Override
    public void init() {
        left = hardwareMap.dcMotor.get("left");
        right = hardwareMap.dcMotor.get("right");

        hangArm = hardwareMap.dcMotor.get("hangArm");
        plow = hardwareMap.dcMotor.get("plow");
        right.setDirection(DcMotor.Direction.REVERSE);

        hangArm.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        plow.setMode(DcMotorController.RunMode.RESET_ENCODERS);
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

        telemetry.addData("left_power", left.getPower());
        telemetry.addData("right_power", right.getPower());
        telemetry.addData("hangArmPos", hangArm.getCurrentPosition());
        telemetry.addData("plowPos", plow.getCurrentPosition());
        telemetry.addData("rightBumper", gamepad1.right_trigger);
        telemetry.addData("reverse", reverse);
        updateArm();
        updatePlow();
        updateDrive();


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
                hangArm.setPower(0);
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
        if (gamepad1.y) {
            plow.setTargetPosition(plow.getCurrentPosition() + plowInc);
            plow.setPower(1);
        }
        else if (gamepad1.a) {
            plow.setTargetPosition(plow.getCurrentPosition() - plowInc);
            plow.setPower(-1);
        }
        else if (gamepad1.right_bumper){
            plow.setTargetPosition(plow.getCurrentPosition() + plowInc);
            plow.setPower(1);
        }
        else if (gamepad1.right_trigger > 0.5) {
            plow.setTargetPosition(plow.getCurrentPosition() - plowInc);
            plow.setPower(-1);
        }
        //game pad 2
        else if(gamepad2.y) {
            plow.setTargetPosition(plow.getCurrentPosition() + plowInc);
            plow.setPower(1);
        }
        else if(gamepad2.a) {
            plow.setTargetPosition(plow.getCurrentPosition() - plowInc);
            plow.setPower(-1);
        }
        else if (gamepad2.right_bumper){
            plow.setTargetPosition(plow.getCurrentPosition() + plowInc);
            plow.setPower(1);
        }
        else if (gamepad2.right_trigger > 0.5) {
            plow.setTargetPosition(plow.getCurrentPosition() - plowInc);
            plow.setPower(-1);
        }
        else {
            plow.setTargetPosition(plow.getCurrentPosition());
            plow.setPower(0);
        }
    }
    //toggle the driving by pressing the stick buttons
    private void updateDrive() {
        if((gamepad1.left_stick_button || gamepad1.right_stick_button) && !reversePressed) {
            reverse = !reverse;
            reversePressed = true;
        } else {
            if(!(gamepad1.left_stick_button || gamepad1.right_stick_button)){
                reversePressed = false;
            }
        }
    }
}

