package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.TouchSensor;

/**
 * Created by Joshua on 10/29/2015.
 */
public class AtlerTouchSensorTest extends OpMode {

    TouchSensor touchSensor;

    @Override
    public void init() {
        touchSensor = hardwareMap.touchSensor.get("touchSensor");
    }

    @Override
    public void loop() {
        telemetry.addData("TouchSensor", touchSensor.isPressed());
    }
}
