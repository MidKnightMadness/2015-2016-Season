package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.common.RedBlueOpMode;

public class RedBlueExample extends RedBlueOpMode{

    public RedBlueExample(TeamColor color) {
        super(color);
    }

    @Override
    public void init() {

    }

    @Override
    public void loop() {
        RedBlueOpMode.TeamColor color = this.teamColor;
        telemetry.addData("Color", color);
    }
}
