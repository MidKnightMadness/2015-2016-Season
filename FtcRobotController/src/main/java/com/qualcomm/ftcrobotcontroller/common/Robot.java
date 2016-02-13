package com.qualcomm.ftcrobotcontroller.common;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import java.lang.reflect.Field;

public class Robot extends Thread {

    private DcMotor drive_left;
    private DcMotor drive_right;
    private DcMotor hang_arm;
    private DcMotor plow;

    private Servo eyebrowLeft;
    private Servo eyebrowRight;
    private Servo climberServo;

    private GyroSensor gyro;
    private GyroWorkerThread gyroThread;

    private Mode driveMode;
    private boolean useGyro;
    private OpMode parent;

    private double leftPower;
    private double rightPower;
    private double power;
    private double gyroTarget;
    private boolean driveBackwards;
    private int leftTarget;
    private int rightTarget;

    private static final int TICKS_TO_CM = 89;

    public Robot(OpMode parent) {
        this.parent = parent;
        HardwareMap map = parent.hardwareMap;
        drive_left = map.dcMotor.get("left");
        drive_right = map.dcMotor.get("right");
        hang_arm = map.dcMotor.get("hangArm");
        plow = map.dcMotor.get("plow");

        eyebrowLeft = map.servo.get("trigger_left");
        eyebrowRight = map.servo.get("trigger_right");
        climberServo = map.servo.get("climber");
        gyro = map.gyroSensor.get("gyro");

        gyroThread = new GyroWorkerThread(parent, gyro);
        gyroThread.start();

        drive_left.setDirection(DcMotor.Direction.FORWARD);
        drive_right.setDirection(DcMotor.Direction.REVERSE);
        hang_arm.setDirection(DcMotor.Direction.FORWARD);
        plow.setDirection(DcMotor.Direction.FORWARD);

        plow.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        hang_arm.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        setDaemon(true);
        start();
        setName("RobotUpdaterThread");
    }

    public void driveDistanceTicks(int distance, double power) {
        driveMode = Mode.DRIVE;
        useGyro = false;
        drive_right.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        drive_left.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        // All relative
        drive_left.setTargetPosition(drive_left.getCurrentPosition() + distance);
        drive_right.setTargetPosition(drive_right.getCurrentPosition() + distance);
        leftTarget = drive_left.getTargetPosition();
        rightTarget = drive_right.getTargetPosition();
        drive_left.setPower(power);
        drive_right.setPower(power);
    }

    public void driveDistanceCm(int distance, double power){
        driveDistanceTicks(distance * TICKS_TO_CM, power);
    }

    public void driveStraightUsingGyro(int distance, double power) {
        this.power = power;
        this.gyroTarget = gyroThread.heading();
        this.driveMode = Mode.DRIVE;
        this.useGyro = true;
        this.leftPower = this.rightPower = power;
        this.driveBackwards = (distance < 0);
        drive_right.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        drive_left.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        drive_left.setTargetPosition(drive_left.getTargetPosition() + distance);
        drive_right.setTargetPosition(drive_right.getTargetPosition() + distance);
        drive_left.setPower(power);
        drive_right.setPower(power);
    }

    public void turn(double target, double power) {
        drive_left.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        drive_right.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        this.gyroTarget = target;
        drive_left.setPower(power);
        drive_right.setPower(power);
    }

    public boolean movementComplete() {
        switch (driveMode) {
            case DRIVE:
                return Math.abs(leftTarget- drive_left.getCurrentPosition()) < 20 &&
                        Math.abs(rightTarget - drive_left.getCurrentPosition()) < 20;
            case TURN:
                return Math.abs(gyroThread.heading() - gyroTarget) < 3;
            default:
                return true;
        }
    }

    public DcMotor getLeftDriveMotor() {
        return drive_left;
    }

    public DcMotor getRightDriveMotor() {
        return drive_right;
    }

    public DcMotor getHangArmMotor() {
        return hang_arm;
    }

    public DcMotor getPlow() {
        return plow;
    }

    public Servo getLeftEyebrow() {
        return eyebrowLeft;
    }

    public Servo getRightEyebrow() {
        return eyebrowRight;
    }

    public GyroSensor getGyro() {
        return gyro;
    }

    public Servo getClimberServo() {
        return climberServo;
    }

    public void initialize() {
        eyebrowLeft.setPosition(Values.TRIGGER_LEFT_RETRACT);
        eyebrowRight.setPosition(Values.TRIGGER_RIGHT_RETRACT);
        climberServo.setPosition(Values.CLIMBER_CLOSE);
    }

    public void deployPlow(){
        plow.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        plow.setTargetPosition(Values.PLOW_DEPLOY);
        plow.setPower(1);
    }

    public void retractPlow(){
        plow.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        plow.setTargetPosition(Values.PLOW_RETRACT);
        plow.setPower(1);
    }

    private void update() {
        if (driveMode != null)
            switch (driveMode) {
                case DRIVE:
                    if (useGyro) {
                        parent.telemetry.addData("Left", drive_left.getCurrentPosition());
                        parent.telemetry.addData("Right", drive_right.getCurrentPosition());
                        parent.telemetry.addData("LeftPower", drive_left.getPower());
                        parent.telemetry.addData("RightPower", drive_right.getPower());
                        if (driveBackwards) {
                            leftPower = power - (gyroThread.heading() - gyroTarget) / 200;
                            rightPower = power + (gyroThread.heading() - gyroTarget) / 200;

                            if (leftPower > 0.6)
                                leftPower = 0.6;
                            else if (leftPower < 0.1)
                                leftPower = 0.1;
                            drive_left.setPower(leftPower);

                            if (rightPower > 0.6)
                                rightPower = 0.6;
                            else if (rightPower < 0.1)
                                rightPower = 0.1;
                            drive_right.setPower(rightPower);
                        } else {
                            leftPower = power - (gyroThread.heading() - gyroTarget) / 200;
                            rightPower = power + (gyroThread.heading() - gyroTarget) / 200;

                            if (leftPower < -0.6)
                                leftPower = -0.6;
                            else if (leftPower > -0.1)
                                leftPower = -0.1;
                            drive_left.setPower(leftPower);

                            if (rightPower < -0.6)
                                rightPower = -0.6;
                            else if (rightPower > -0.1)
                                rightPower = -0.1;
                            drive_right.setPower(rightPower);
                        }
                    }
                    break;
            }
        // Automatic parent.telemetry maker thing
        try {
            for (Field f : getClass().getDeclaredFields()) {
                f.setAccessible(true);
                if (f.getType() == DcMotor.class) {
                    DcMotor m = (DcMotor) f.get(this);
                    parent.telemetry.addData(f.getName() + "_pos", m.getCurrentPosition());
                    parent.telemetry.addData(f.getName() + "_target", m.getTargetPosition());
                }
                if (f.getType() == Servo.class) {
                    Servo servo = (Servo) f.get(this);
                    parent.telemetry.addData(f.getName() + "_pos", servo.getPosition());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(driveMode != null)
            parent.telemetry.addData("Mode", driveMode.toString());
        parent.telemetry.addData("Joystick 1 Left (X,Y): ", parent.gamepad1.left_stick_x+", "+parent.gamepad1.left_stick_y);
        parent.telemetry.addData("Joystick 1 Right (X,Y): ", parent.gamepad1.right_stick_x+", "+parent.gamepad1.right_stick_y);
        parent.telemetry.addData("Joystick 2 Left (X,Y): ", parent.gamepad2.left_stick_x+", "+parent.gamepad2.left_stick_y);
        parent.telemetry.addData("Joystick 2 Right (X,Y): ", parent.gamepad2.right_stick_x+", "+parent.gamepad2.right_stick_y);
        parent.telemetry.addData("righttarget, lefttarget", leftTarget +","+rightTarget);
    }

    @Override
    public void run() {
        while (true) {
            update();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopDriveMotors() {
        drive_left.setPower(0);
        drive_right.setPower(0);
    }

    private enum Mode {
        TURN,
        DRIVE
    }
}
