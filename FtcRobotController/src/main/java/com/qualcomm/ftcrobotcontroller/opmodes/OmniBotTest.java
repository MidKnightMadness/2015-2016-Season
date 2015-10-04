package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

public class OmniBotTest extends OpMode {

    private DcMotor front_left, front_right, back_left, back_right;

    @Override
    public void init() {
        front_left = hardwareMap.dcMotor.get("front_left");
        front_right = hardwareMap.dcMotor.get("front_right");
        back_left = hardwareMap.dcMotor.get("back_left");
        back_right = hardwareMap.dcMotor.get("back_right");
    }

    @Override
    public void loop() {
        float[] motorPwr = calculateMotorValues(gamepad1.left_stick_x, gamepad1.left_stick_y);
        front_left.setPower(motorPwr[0]);
        front_right.setPower(motorPwr[1]);
        back_left.setPower(motorPwr[2]);
        back_right.setPower(motorPwr[3]);
        // TODO: Maybe have one stick = omnidirectional & 2 sticks be tank-like
        telemetry.addData("front_left power", front_left.getPower());
        telemetry.addData("front_right_power", front_right.getPower());
        telemetry.addData("back_left_power", back_left.getPower());
        telemetry.addData("back_right_power", back_right.getPower());
    }


    /**
     * Calculates the motor powers for driving omnidirectionally <b>USING A SINGLE JOYSTICK</b>
     *
     * @param joyX The left X-value of the joystick
     * @param joyY The left Y-value of the joystick
     * @return A float array with the motor powers in the following order: front_left, front_right, back_left, back_right
     */
    private float[] calculateMotorValues(float joyX, float joyY) {
        float[] motorPower = new float[]{0, 0, 0, 0};
        joyX = scaleInput(joyX);
        joyY = scaleInput(joyY);
        // front_left
        motorPower[0] = joyX + joyY;
        // front_right
        motorPower[1] = -joyX + joyY;
        // back_left
        motorPower[2] = -joyX + joyY;
        // back_right
        motorPower[3] = joyX + joyY;

        // limit range
        for(int i = 0; i < motorPower.length; i++){
            Range.clip(motorPower[i], -1F, 1F);
        }
        return motorPower;
    }

    private float scaleInput(float input) {
        double[] scaleArray = {0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24, 0.30, 0.36, 0.43,
                0.50, 0.60, 0.72, 0.85, 1.0, 1.0};
        int index = (int) (input * 16);
        if (index < 0)
            index = -index;
        else if (index > 16)
            index = 16;
        float scaled;
        if(input < 0)
            scaled = (float) -scaleArray[index];
        else
            scaled = (float) scaleArray[index];
        return scaled;
    }
}
