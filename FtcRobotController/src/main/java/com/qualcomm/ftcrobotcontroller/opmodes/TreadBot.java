package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.common.TKAOpmode;
import com.qualcomm.robotcore.hardware.DcMotor;

public class TreadBot extends TKAOpmode {

    private DcMotor left;
    private DcMotor right;

    @Override
    public void initialize() {
        right.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void loop() {
        telemetry.addData("Status: ", "IT LIVES!");
        left.setPower(gamepad1.left_stick_y);
        right.setPower(gamepad1.right_stick_y);
        telemetry.addData("left_power", left.getPower());
        telemetry.addData("right_power", right.getPower());
    }
}
