package com.qualcomm.ftcrobotcontroller.common;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;

import java.lang.reflect.Constructor;

public abstract class RedBlueOpMode extends OpMode {

    public TeamColor teamColor;

    public RedBlueOpMode(TeamColor color) {
        this.teamColor = color;
    }


    /**
     * Registers two OpModes (Red and blue) when given a RedBlue
     * <br/>
     * <b>WARNING: This method fails silently if there are any errors</b>
     *
     * @param opModeName The name of the {@link com.qualcomm.robotcore.eventloop.opmode.OpMode} to register
     * @param clazz      The class of the {@link com.qualcomm.robotcore.eventloop.opmode.OpMode}
     * @param manager    The {@link com.qualcomm.robotcore.eventloop.opmode.OpModeManager} passed in
     *                   from the {@link com.qualcomm.ftcrobotcontroller.opmodes.FtcOpModeRegister FtcOpModeRegister}
     */
    public static void register(String opModeName, Class<? extends OpMode> clazz, OpModeManager manager) {
        try {
            Constructor redConstructor = clazz.getConstructor(TeamColor.class);
            Constructor blueConstructor = clazz.getConstructor(TeamColor.class);
            OpMode redOpMode = (OpMode) redConstructor.newInstance(TeamColor.RED);
            OpMode blueOpMode = (OpMode) blueConstructor.newInstance(TeamColor.BLUE);
            manager.register(String.format("[RED] %s", opModeName), redOpMode);
            manager.register(String.format("[BLUE] %s", opModeName), blueOpMode);
        } catch (Exception e) {
            // Fail silently
        }
    }

    public enum TeamColor {
        RED,
        BLUE
    }
}
