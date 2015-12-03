package com.qualcomm.ftcrobotcontroller.common;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public abstract class RedBlueOpMode extends OpMode {

    public TeamColor teamColor;

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
            OpMode redOpMode = clazz.newInstance();
            OpMode blueOpMode = clazz.newInstance();
            Field teamColorField = clazz.getField("teamColor");
            teamColorField.setAccessible(true);
            teamColorField.set(redOpMode, TeamColor.RED);
            teamColorField.set(blueOpMode, TeamColor.BLUE);
            manager.register(String.format("[R] %s", opModeName), redOpMode);
            manager.register(String.format("[B] %s", opModeName), blueOpMode);
        } catch (Exception e) {
            // Fail silently
        }
    }

    public enum TeamColor {
        RED,
        BLUE
    }
}
