package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.common.RedBlueOpMode;

public class RedBlueExample extends RedBlueOpMode{

    @Override
    public void init() {

    }

    @Override
    public void loop() {
        RedBlueOpMode.TeamColor color = this.teamColor;
        telemetry.addData("Color", color);
    }
}
