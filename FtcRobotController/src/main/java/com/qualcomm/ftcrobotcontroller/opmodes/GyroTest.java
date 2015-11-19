package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.common.GyroWorkerThread;
import com.qualcomm.ftcrobotcontroller.common.MotorGroup;
import com.qualcomm.ftcrobotcontroller.common.TKAOpmode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;

public class GyroTest extends TKAOpmode {

    GyroSensor gyroSensor;
    GyroWorkerThread gyro;

    @MotorGroup("left")
    private DcMotor front_left;
    @MotorGroup("right")
    private DcMotor front_right;
    @MotorGroup("left")
    private DcMotor back_left;
    @MotorGroup("right")
    private DcMotor back_right;

    int phase = 0;

    @Override
    public void initialize() {
        front_right.setDirection(DcMotor.Direction.REVERSE);
        back_right.setDirection(DcMotor.Direction.REVERSE);
        gyroSensor = this.hardwareMap.gyroSensor.get("gyro");
        gyro = new GyroWorkerThread(this, gyroSensor);
        gyro.start();
    }

    @Override
    public void loop() {
        switch (phase){
            case 0:
                // Turning
                double heading = this.gyro.heading();
                // +- 0.125 = 15
                // +- .25 = 32
                // y = 136x - 2
                if(heading >= 90 + correction(0.25)){
                    this.phase = 1;
                    return;
                }
                runMotorGroup("left", 0.25);
                runMotorGroup("right", -0.25);
                break;
            case 1:
                stopMotorGroup("left", "right");
                break;
        }
        telemetry.addData("raw gyro", this.gyroSensor.getRotation());
        telemetry.addData("Heading", gyro.heading());
        telemetry.addData("phase", this.phase);
    }
    private double correction(double speed){
        return (136 * Math.abs(speed)) -2;
    }
}
