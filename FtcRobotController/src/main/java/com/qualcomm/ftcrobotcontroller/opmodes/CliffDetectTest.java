package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.common.ODSHelper;
import com.qualcomm.ftcrobotcontroller.common.TKAOpmode;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;

public class CliffDetectTest extends TKAOpmode {

    private OpticalDistanceSensor ods;
    private ODSHelper odsHelper;
    @Override
    public void initialize() {
        ods = this.hardwareMap.opticalDistanceSensor.get("ods");
        odsHelper = new ODSHelper(ods);
    }

    @Override
    public void loop() {
        odsHelper.update();
        telemetry.addData("ODS", ods.getLightDetectedRaw());
        telemetry.addData("RunningAvg", odsHelper.runningAvg());
        telemetry.addData("OnCliff", odsHelper.onCliff());
    }
}
