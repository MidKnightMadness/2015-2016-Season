package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.common.GyroWorkerThread;
import com.qualcomm.ftcrobotcontroller.common.RedBlueLinearOpMode;
import com.qualcomm.ftcrobotcontroller.common.Values;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.Servo;

public class ClimberDumpAtler extends RedBlueLinearOpMode {

    private DcMotor left;
    private DcMotor right;
    private DcMotor plow;
    private DcMotor hangArm;
    GyroSensor gyroSensor;
    GyroWorkerThread gyro;
    Servo climberServo;
    double leftPower;
    double rightPower;

    @Override
    public void runOpMode() throws InterruptedException {
        left = hardwareMap.dcMotor.get("left");
        right = hardwareMap.dcMotor.get("right");
        plow = hardwareMap.dcMotor.get("plow");
        hangArm = hardwareMap.dcMotor.get("hangArm");
        left.setDirection(DcMotor.Direction.REVERSE);
        climberServo = hardwareMap.servo.get("climber"); //changed from climberServo
        gyroSensor = this.hardwareMap.gyroSensor.get("gyro");
        gyro = new GyroWorkerThread(this, gyroSensor);
        gyro.start();

        waitForStart();

        resetPlowAndHangArm();
        sleep(500);

        plowDown();
        sleep(1500);

        resetEncoders();
        sleep(2000);

        //driveForward(-70, -0.5);
        driveGyroDistance(-11250, 0.5, 0);

        resetEncoders();
        sleep(500);

        turnGyroDistance(-45, -0.3);
        resetEncoders();
        driveGyroDistance(-2000, 0.5, -45);

        resetEncoders();
        turnGyroDistance(-135, -0.3);

        resetEncoders();
        driveGyroDistance(3000, 0.5, -135);



    }

    public void driveForward(int distance, double power) {

        int MOTOR_COUNTS = 1120;
        int GEAR_RATIO = 1;
        double circumference = 2 * Math.PI;
        double ROTATIONS = distance / circumference;
        double totalDistance = MOTOR_COUNTS * ROTATIONS * GEAR_RATIO;

        left.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        right.setMode(DcMotorController.RunMode.RESET_ENCODERS);

        left.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        right.setMode(DcMotorController.RunMode.RUN_TO_POSITION);

        left.setTargetPosition((int) totalDistance);
        right.setTargetPosition((int) totalDistance);

        left.setPower(power);
        right.setPower(power);

        telemetry.addData("Power: ", power);
    }


    private void turnDistance(int distance, double power) throws InterruptedException {
        setPos(left, distance);
        setPos(right, -distance);
        left.setPower(power);
        right.setPower(power);
        int abortTime = 0;
        while (abortTime < 4500) {
            telemetry.addData("Left", left.getCurrentPosition());
            telemetry.addData("Right", right.getCurrentPosition());
            telemetry.addData("LT", left.getTargetPosition());
            telemetry.addData("RT", right.getTargetPosition());
            waitOneFullHardwareCycle();
            sleep(10);
            abortTime += 10;
        }
        resetEncoders();
        waitOneFullHardwareCycle();
        sleep(500);
        stopMotors();
    }

    private void turnGyroDistance(int target, double power) throws InterruptedException {
        left.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        right.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        left.setPower(power);
        right.setPower(-power);
        while (Math.abs(gyro.heading()) < Math.abs(target)) {
            telemetry.addData("Left", left.getCurrentPosition());
            telemetry.addData("Right", right.getCurrentPosition());
            telemetry.addData("LeftPower", left.getPower());
            telemetry.addData("RightPower", right.getPower());

            left.setPower(power);
            right.setPower(-power);

            if (Math.abs(gyro.heading() - (target)) < 20) {
                left.setPower(power - 0.15);
                right.setPower(-(power - 0.15));
            }

            telemetry.addData("distance target", Math.abs(target) - Math.abs(gyro.heading()));
        }
        resetEncoders();
        waitOneFullHardwareCycle();
        sleep(500);
        stopMotors();
    }

    public void plowDown() {


        plow.setMode(DcMotorController.RunMode.RUN_TO_POSITION);

        plow.setTargetPosition(Values.PLOW_DEPLOY);
        plow.setPower(0.5);

    }

    public void plowUp() {

        plow.setMode(DcMotorController.RunMode.RUN_TO_POSITION);

        plow.setTargetPosition(Values.PLOW_RETRACT);
        plow.setPower(-1);

    }

    private void resetEncoders() {
        left.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        right.setMode(DcMotorController.RunMode.RESET_ENCODERS);
    }

    private void resetPlowAndHangArm() {
        hangArm.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        plow.setMode(DcMotorController.RunMode.RESET_ENCODERS);
    }

    private void setPos(DcMotor motor, int pos) {
        motor.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        motor.setTargetPosition(pos);
    }

    public void stopMotors() {
        left.setPower(0);
        right.setPower(0);
    }

    private void driveGyroDistance(int distance, double power, int target) throws InterruptedException {
        setPos(left, distance);
        setPos(right, distance);
        left.setPower(power);
        right.setPower(power);
        int abortTime = 0;
        while (abortTime < 8000) { //4500 not enough
            telemetry.addData("Left", left.getCurrentPosition());
            telemetry.addData("Right", right.getCurrentPosition());
            telemetry.addData("LeftPower", left.getPower());
            telemetry.addData("RightPower", right.getPower());
            sleep(10);
            abortTime += 10;

            if (distance > 0) {
                leftPower = power - (gyro.heading() - target) / 200;
                rightPower = power + (gyro.heading() - target) / 200;
            } else {
                leftPower = power + (gyro.heading() - target) / 200;
                rightPower = power - (gyro.heading() - target) / 200;
            }

            if (leftPower > 0.5)
                leftPower = 0.5;
            else if (leftPower < 0.1)
                leftPower = 0.1;
            left.setPower(leftPower);

            if (rightPower > 0.5)
                rightPower = 0.5;
            else if (rightPower < 0.1)
                rightPower = 0.1;
            right.setPower(rightPower);


        }
    }
}