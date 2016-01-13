package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.common.Values;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;

public class ColorSensorTest extends LinearOpMode {

    ColorSensor colorSensor;



    @Override
    public void runOpMode() throws InterruptedException {
        colorSensor = hardwareMap.colorSensor.get("color");

        waitForStart();

        colorSensor.enableLed(false);

        while(opModeIsActive()) {
            telemetry.addData("Red", colorSensor.red());
            telemetry.addData("Blue", colorSensor.blue());

            if(colorSensor.blue() > colorSensor.red()) {
                telemetry.clearData();
                telemetry.addData("Status", "BLUE!!");
            }
            if (colorSensor.red() > colorSensor.blue()) {
                telemetry.clearData();
                telemetry.addData("Status", "RED!!");
            }
            waitOneFullHardwareCycle();
        }

    }

}