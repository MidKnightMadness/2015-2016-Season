package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.common.RedBlueLinearOpMode;

public class RedBlueLinearExample extends RedBlueLinearOpMode{

    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();
        while(opModeIsActive()){
            telemetry.addData("Color", this.teamColor.toString());
        }
    }
}
