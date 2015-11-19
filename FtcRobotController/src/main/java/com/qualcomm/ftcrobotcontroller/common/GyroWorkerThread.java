package com.qualcomm.ftcrobotcontroller.common;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.GyroSensor;

public class GyroWorkerThread extends Thread{

    private GyroSensor sensor;
    private OpMode parent;
    private boolean running = true;
    private double heading;
    private double calibration;
    private double calib;
    private State state = State.UNKNOWN;
    private int calibrationRuns;

    public GyroWorkerThread(OpMode parent, GyroSensor sensor){
        this.sensor = sensor;
        this.parent = parent;
        calibrate();
    }

    @Override
    public void run() {
        try {
            while (running) {
                this.parent.telemetry.addData("State", this.state.toString());
                switch(state){
                    case CALIBRATING:
                        calib += sensor.getRotation();
                        this.calibrationRuns++;
                        if(calibrationRuns >= 20){
                            this.calibration = calib / calibrationRuns;
                            state = State.READING;
                            break;
                        }
                        Thread.sleep(10);
                        break;
                    case READING:
                        double gyRot = sensor.getRotation() - this.calibration;
                        if(Math.abs(gyRot) <= 2){
                            gyRot = 0;
                        }
                        this.heading += gyRot * 0.02;
                        Thread.sleep(20, 0);
                        break;
                }
                parent.telemetry.addData("Gyro", this.heading());
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
        this.calib = 0;
        this.calibrationRuns = 0;
        this.calibration = 0;
        this.state = State.CALIBRATING;
    }

    public enum State{
        UNKNOWN,
        CALIBRATING,
        READING
    }
}
