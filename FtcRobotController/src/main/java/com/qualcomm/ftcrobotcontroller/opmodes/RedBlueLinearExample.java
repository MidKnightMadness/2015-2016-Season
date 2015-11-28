package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.common.RedBlueLinearOpMode;
import com.qualcomm.ftcrobotcontroller.common.RedBlueOpMode;

public class RedBlueLinearExample extends RedBlueLinearOpMode{

    public RedBlueLinearExample(RedBlueOpMode.TeamColor color) {
        super(color);
    }

    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();
        while(opModeIsActive()){
            telemetry.addData("Color", this.teamColor.toString());
        }
    }
}
