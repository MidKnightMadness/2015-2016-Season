package com.qualcomm.ftcrobotcontroller.opmodes;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;

public class TreadBot extends OpMode {

    private DcMotor left;
    private DcMotor right;
    private DcMotor hangArm;
    private DcMotor plow;
    boolean encReset = false;
    boolean isHanging = false;
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
        left.setPower(gamepad1.left_stick_y);
        right.setPower(gamepad1.right_stick_y);
        telemetry.addData("left_power", left.getPower());
        telemetry.addData("right_power", right.getPower());
        telemetry.addData("hangArmPos", hangArm.getCurrentPosition());
        telemetry.addData("plowPos", plow.getCurrentPosition());
        updateArm();
        updatePlow();


    }



    private void updateArm() {


        if(!isHanging) {
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
        }

        //hang
        //TODO: Debug this
        if(gamepad2.b && gamepad2.start) {
            isHanging = true;
            hangArm.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
            hangArm.setTargetPosition(-3900);
            hangArm.setPower(1);
        }

        //deploy
        if (gamepad2.x) {
            hangArm.setTargetPosition(-16000);
            hangArm.setPower(1);
        }

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
        //game pad 2
        else if(gamepad2.y) {
            plow.setTargetPosition(plow.getCurrentPosition() + plowInc);
            plow.setPower(1);
        }
        else if(gamepad2.a) {
            plow.setTargetPosition(plow.getCurrentPosition() + plowInc);
            plow.setPower(-1);
        }
        //
        else {
            plow.setTargetPosition(plow.getCurrentPosition());
            plow.setPower(0);


        }
    }
}

