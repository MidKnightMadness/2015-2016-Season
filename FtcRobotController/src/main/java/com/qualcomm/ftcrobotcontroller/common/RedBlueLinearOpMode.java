package com.qualcomm.ftcrobotcontroller.common;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public abstract class RedBlueLinearOpMode extends LinearOpMode{
    public RedBlueOpMode.TeamColor teamColor;

    public RedBlueLinearOpMode(RedBlueOpMode.TeamColor color){
        this.teamColor = color;
    }
}
