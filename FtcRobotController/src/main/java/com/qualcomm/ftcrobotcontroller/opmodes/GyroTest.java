package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.common.GyroWorkerThread;
import com.qualcomm.ftcrobotcontroller.common.MotorGroup;
import com.qualcomm.ftcrobotcontroller.common.TKAOpmode;
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
        front_left = this.hardwareMap.dcMotor.get("front_left");
        front_left.setDirection(DcMotor.Direction.REVERSE);
        front_right.setDirection(DcMotor.Direction.REVERSE);
        back_left = this.hardwareMap.dcMotor.get("back_left");
        front_right = this.hardwareMap.dcMotor.get("front_right");
        back_right = this.hardwareMap.dcMotor.get("back_right");
        gyroSensor = this.hardwareMap.gyroSensor.get("gyro");
        gyro = new GyroWorkerThread(gyroSensor);
        gyro.start();
    }

    @Override
    public void loop() {
        switch (phase){
            case 0:
                // Turning
                double heading = this.gyro.heading();
                if(Math.abs(heading - 90) < 2){
                    this.phase = 1;
                    break;
                }
                front_left.setPower(0.5);
                back_left.setPower(0.5);
                front_right.setPower(-0.5);
                back_right.setPower(-0.5);
                break;
            case 1:
                front_left.setPower(0);
                back_left.setPower(0);
                front_right.setPower(0);
                back_right.setPower(0);
                break;
        }
        telemetry.addData("raw gyro", this.gyroSensor.getRotation());
        telemetry.addData("Heading", gyro.heading());
        telemetry.addData("phase", this.phase);
    }
}
