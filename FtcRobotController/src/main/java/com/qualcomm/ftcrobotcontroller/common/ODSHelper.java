package com.qualcomm.ftcrobotcontroller.common;

import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;

public class ODSHelper {
    private OpticalDistanceSensor sensor;
    private int loopCount = 1;
    private int odsReading = 0;
    private boolean onCliff = false;
    public ODSHelper(OpticalDistanceSensor sensor){
        this.sensor = sensor;
    }

    public boolean onCliff(){
        if(onCliff)
            return true;
        int trigger = runningAvg() - 20;
        if(trigger < 0)
            trigger = 0;
        return onCliff = sensor.getLightDetectedRaw() < trigger;
    }

    public void update(){
        if(!onCliff) {
            runningAvg();
        }
        if(sensor.getLightDetectedRaw() >= 10)
            onCliff = false;
    }

    public int runningAvg(){
        if(loopCount >= 600){
            // Reset
            loopCount = 1;
            odsReading = 0;
        }
        odsReading += sensor.getLightDetectedRaw();
        return odsReading / loopCount++;
    }
}
