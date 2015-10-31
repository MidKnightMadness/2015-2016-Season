package com.qualcomm.ftcrobotcontroller.common;

import com.qualcomm.robotcore.hardware.GyroSensor;

public class GyroWorkerThread extends Thread{

    private GyroSensor sensor;
    private boolean running = true;
    private double heading;

    public GyroWorkerThread(GyroSensor sensor){
        this.sensor = sensor;
    }

    @Override
    public void run() {
        try {
            while (running) {
                double gyroRot = sensor.getRotation() - 600;
                // Give it a tolerance of +- 2 degrees/sec
                if(Math.abs(gyroRot) <= 2)
                    gyroRot = 0;
                this.heading += gyroRot * 0.02;
                Thread.sleep(20, 0);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public synchronized double heading(){
        return this.heading;
    }

    public synchronized void calibrate(){
        this.heading = 0;
    }
}
